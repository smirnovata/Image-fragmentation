DROP TABLE IF EXISTS HO_IF_FILE;
DROP TABLE IF EXISTS HO_PROCESSING;
DROP TABLE IF EXISTS HO_SESSION;
DROP TABLE IF EXISTS HO_STATUS;
DROP SEQUENCE IF EXISTS status_id_seq;
DROP SEQUENCE IF EXISTS file_id_seq;
DROP SEQUENCE IF EXISTS session_id_seq;
DROP SEQUENCE IF EXISTS processing_id_seq;

CREATE SEQUENCE status_id_seq;
CREATE SEQUENCE file_id_seq;
CREATE SEQUENCE session_id_seq;
CREATE SEQUENCE processing_id_seq;

CREATE TABLE HO_STATUS (
  status_id INT NOT NULL,
  name VARCHAR(120),
  PRIMARY KEY (status_id)
);


CREATE TABLE HO_SESSION (
  session_id INT NOT NULL,
  session_name uuid NOT NULL,
  creating_date VARCHAR NOT NULL,
  PRIMARY KEY (session_id),
  UNIQUE (session_id)
);

CREATE TABLE HO_PROCESSING (
  processing_id INT NOT NULL,
  session_id INT NOT NULL,
  status_id INT NOT NULL,
  form_numbers INT NOT NULL,
  form_height INT NOT NULL,
  form_try_seg BOOLEAN NOT NULL,
  form_try_orig_si VARCHAR NOT NULL,
  form_background INT NOT NULL,
  PRIMARY KEY (processing_id),
  FOREIGN KEY (session_id)  REFERENCES HO_SESSION (session_id),
  FOREIGN KEY (status_id)  REFERENCES HO_STATUS (status_id)
);

CREATE TABLE HO_IF_FILE (
  file_id INT NOT NULL,
  session_id INT NOT NULL,
  file_name VARCHAR(400) NOT NULL,
  PRIMARY KEY (file_id),
  FOREIGN KEY (session_id)  REFERENCES HO_SESSION (session_id)
);

INSERT INTO HO_STATUS VALUES
    (nextval('status_id_seq'), 'UNDEFINED'),
    (nextval('status_id_seq'), 'IN_PROGRESS'),
    (nextval('status_id_seq'), 'FINISHED'),
    (nextval('status_id_seq'), 'FAILED');

