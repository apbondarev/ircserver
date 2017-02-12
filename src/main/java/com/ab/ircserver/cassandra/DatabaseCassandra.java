package com.ab.ircserver.cassandra;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import com.ab.ircserver.Database;
import com.ab.ircserver.Message;
import com.ab.ircserver.Room;
import com.ab.ircserver.User;
import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.CloseFuture;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import com.datastax.driver.core.Statement;
import com.google.common.base.Function;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class DatabaseCassandra implements Database {

    private final String address;
    private final String keyspace;
    private Cluster cluster;
    private Session session;

    public DatabaseCassandra(String address) {
        this.address = address;
        this.keyspace = "ircserver";
        connect();
    }

    private void connect() {
        QueryOptions options = new QueryOptions();
        options.setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);
        cluster = Cluster.builder()
                .addContactPoint(address)
                .withQueryOptions(options)
                .build();
        session = cluster.connect(keyspace);
    }

    void testConnection() {
        ListenableFuture<ResultSet> resultSet = session.executeAsync("select release_version from system.local");

        // Use transform with a simple Function to apply a synchronous computation on the result:
        ListenableFuture<String> version = Futures.transform(resultSet, new Function<ResultSet, String>() {
            public String apply(ResultSet rs) {
                return rs.one().getString("release_version");
            }
        });

        // Use a callback to perform an action once the future is complete:
        Futures.addCallback(version, new FutureCallback<String>() {
            public void onSuccess(String version) {
                System.out.printf("Cassandra version: %s%n", version);
            }

            public void onFailure(Throwable t) {
                System.out.printf("Failed to retrieve the version: %s%n", t.getMessage());
            }
        });
        
        try {
            version.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
    
    void execute(String query) {
        session.execute(query);
    }

    @Override
    public CompletionStage<User> findOrCreateUser(String name, byte[] password) {
        ListenableFuture<ResultSet> resultSet = session.executeAsync("select password from users where name=?", name);

        // Use transform with a simple Function to apply a synchronous computation on the result:
        ListenableFuture<User> user = Futures.transform(resultSet, new AsyncFunction<ResultSet, User>() {
            public ListenableFuture<User> apply(ResultSet rs) {
                if (!rs.isExhausted()) {
                    Row row = rs.one();
                    User value = new User(name, row.getBytes("password").array());
                    return Futures.immediateFuture(value);
                }
                String query = "insert into users (name, password) values (?,?) if not exists";
                Object[] params = new Object[] { name, ByteBuffer.wrap(password) };
                ListenableFuture<ResultSet> resultSetIfNotExists = session.executeAsync(query, params);
                return Futures.transform(resultSetIfNotExists, new Function<ResultSet, User>() {
                    @Override
                    public User apply(ResultSet rs) {
                        if (rs.wasApplied()) {
                            return new User(name, password);
                        } else {
                            Row row = rs.one();
                            return new User(name, row.getBytes("password").array());
                        }
                    }
                });
            }
        });

        return buildCompletableFutureFromListenableFuture(user);
    }

    @Override
    public CompletionStage<Room> findOrCreateRoom(String roomName) {
        ListenableFuture<ResultSet> resultSet = session.executeAsync("select username, message from rooms where name=? order by ts desc limit 10", roomName);

        // Use transform with a simple Function to apply a synchronous computation on the result:
        ListenableFuture<Room> user = Futures.transform(resultSet, new AsyncFunction<ResultSet, Room>() {
            public ListenableFuture<Room> apply(ResultSet rs) {
                if (!rs.isExhausted()) {
                    List<Message> messages = new ArrayList<>();
                    for (Row row : rs.all()) {
                        messages.add(new Message(row.getString("username"), row.getString("message")));
                    }
                    Room value = new Room(roomName, messages);
                    return Futures.immediateFuture(value);
                }
                String query = "insert into rooms (name) values (?) if not exists";
                ListenableFuture<ResultSet> resultSetIfNotExists = session.executeAsync(query, roomName);
                return Futures.transform(resultSetIfNotExists, new Function<ResultSet, Room>() {
                    @Override
                    public Room apply(ResultSet rs) {
                        if (rs.wasApplied()) {
                            return new Room(roomName);
                        } else {
                            Row row = rs.one();
                            return new Room(row.getString("name"));
                        }
                    }
                });
            }
        });

        return buildCompletableFutureFromListenableFuture(user);
    }

    @Override
    public CompletionStage<Void> save(Room room) {
        String query = "insert into rooms (name, ts, username, message) values (?, now(), ?, ?)";
        
        BatchStatement batch = new BatchStatement();
        for (Message m : room.lastMessages()) {
            Statement st = new SimpleStatement(query, room.name(), m.username(), m.text());
            batch.add(st);
        }
        ResultSetFuture resultSet = session.executeAsync(batch);
        
        ListenableFuture<Void> result = Futures.transform(resultSet, new Function<ResultSet, Void>() {
            @Override
            public Void apply(ResultSet input) {
                return null;
            }
        });
        
        return buildCompletableFutureFromListenableFuture(result);
    }

    @Override
    public CompletionStage<Void> close() {
        if (cluster != null) {
            CloseFuture closeFuture = cluster.closeAsync();
            cluster = null;
            return buildCompletableFutureFromListenableFuture(closeFuture);
        }
        return CompletableFuture.completedFuture(null);
    }
    
    static <T> CompletableFuture<T> buildCompletableFutureFromListenableFuture(  
        final ListenableFuture<T> listenableFuture) {  
        //create an instance of CompletableFuture  
        CompletableFuture<T> completable = new CompletableFuture<T>() {  
            @Override  
            public boolean cancel(boolean mayInterruptIfRunning) {  
                // propagate cancel to the listenable future  
                boolean result = listenableFuture.cancel(mayInterruptIfRunning);  
                super.cancel(mayInterruptIfRunning);  
                return result;  
            }  
        };  
  
        // add callback  
        Futures.addCallback(listenableFuture, new FutureCallback<T>() {  
            @Override  
            public void onSuccess(T result) {  
                completable.complete(result);  
            }  
  
            @Override  
            public void onFailure(Throwable t) {  
                completable.completeExceptionally(t);  
            }  
        });  
        return completable;  
    } 

}
