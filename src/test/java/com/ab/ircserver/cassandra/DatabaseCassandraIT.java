package com.ab.ircserver.cassandra;

import org.junit.Test;

public class DatabaseCassandraIT {

    @Test
    public void testDatabaseCassandra() {
        DatabaseCassandra db = null;
        try {
            db = new DatabaseCassandra();
            db.testConnection();
        } finally {
            if (db != null) db.close();
        }
    }

}
