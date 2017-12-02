CREATE KEYSPACE BDTASK
WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE BDTASK.event (
  id uuid,
  time timestamp,
  PRIMARY KEY (id)
);
