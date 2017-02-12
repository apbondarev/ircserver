CREATE KEYSPACE IF NOT EXISTS ircserver
WITH replication = {
	'class' : 'SimpleStrategy',
	'replication_factor' : 1
};

CREATE TABLE IF NOT EXISTS ircserver.users (
	name text,
	password blob,
	PRIMARY KEY (name)
);

CREATE TABLE IF NOT EXISTS ircserver.rooms (
	name text,
	ts timeuuid,
	username text,
	message text,
	PRIMARY KEY (name, ts)
) WITH CLUSTERING ORDER BY (ts DESC);