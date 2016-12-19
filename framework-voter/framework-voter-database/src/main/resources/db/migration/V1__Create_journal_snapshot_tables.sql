DROP TABLE IF EXISTS journal;

CREATE TABLE IF NOT EXISTS journal (
  ordering SERIAL,
  persistence_id VARCHAR(255) NOT NULL,
  sequence_number BIGINT NOT NULL,
  deleted BOOLEAN DEFAULT FALSE,
  tags VARCHAR(255) DEFAULT NULL,
  message BLOB NOT NULL,
  PRIMARY KEY(persistence_id, sequence_number)
);

DROP TABLE IF EXISTS snapshot;

CREATE TABLE IF NOT EXISTS snapshot (
  persistence_id VARCHAR(255) NOT NULL,
  sequence_number BIGINT NOT NULL,
  created BIGINT NOT NULL,
  snapshot BLOB NOT NULL,
  PRIMARY KEY (persistence_id, sequence_number)
);

DROP TABLE IF EXISTS read_side_offsets;

CREATE TABLE read_side_offsets (
  read_side_id VARCHAR(255), tag VARCHAR(255),
  sequence_offset bigint, time_uuid_offset char(36),
  PRIMARY KEY (read_side_id, tag)
)
