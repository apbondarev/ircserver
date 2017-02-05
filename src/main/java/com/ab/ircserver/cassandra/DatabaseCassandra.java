package com.ab.ircserver.cassandra;

import java.util.concurrent.CompletableFuture;

import com.ab.ircserver.Database;
import com.ab.ircserver.Room;
import com.ab.ircserver.User;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.google.common.base.Function;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class DatabaseCassandra implements Database {

    private Cluster cluster;
    private ListenableFuture<Session> session;

    public DatabaseCassandra() {
        connect();
    }

    private void connect() {
        QueryOptions options = new QueryOptions();
        options.setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);
        cluster = Cluster.builder()
                .addContactPoint("192.168.1.102")
                .withQueryOptions(options)
                .build();
        session = cluster.connectAsync();
    }

    void testConnection() {
        // Use transform with an AsyncFunction to chain an async operation after another:
        ListenableFuture<ResultSet> resultSet = Futures.transform(session, new AsyncFunction<Session, ResultSet>() {
            public ListenableFuture<ResultSet> apply(Session session) throws Exception {
                return session.executeAsync("select release_version from system.local");
            }
        });

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
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public CompletableFuture<User> findOrCreateUser(String name, byte[] password) {
        return null;
    }

    @Override
    public CompletableFuture<Room> findOrCreateRoom(String roomName) {
        return null;
    }

    @Override
    public CompletableFuture<Void> save(Room room) {
        return null;
    }

    @Override
    public CompletableFuture<Void> close() {
        if (cluster != null) {
            cluster.close();
            cluster = null;
        }
        return null;
    }

}
