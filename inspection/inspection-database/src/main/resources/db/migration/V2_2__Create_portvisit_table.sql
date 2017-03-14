DROP TABLE IF EXISTS portvisit;

CREATE TABLE IF NOT EXISTS portvisit (
  id              MEDIUMINT NOT NULL AUTO_INCREMENT,
  ucrn            VARCHAR(18) NOT NULL,
  dt_start        DATETIME,
  dt_end          DATETIME,
  status          VARCHAR(10),
  previous_port   VARCHAR(255),
  next_port       VARCHAR(255),
  ship_id         MEDIUMINT NOT NULL,
  PRIMARY KEY (id)
);

INSERT INTO portvisit
    (ucrn, dt_start, dt_end, status, previous_port, next_port, ship_id)
  VALUES
    ('NLRTM170001001', '2017-01-15T11:30:45', '2017-01-17T11:30:45', 'closed', 'Antwerp', 'Amsterdam', 63),
    ('NLRTM170001002', '2017-01-15T11:30:45', '2017-01-17T11:30:45', 'closed', 'Antwerp', 'Amsterdam', 62),
    ('NLRTM170001003', '2017-01-15T11:30:45', '2017-01-17T11:30:45', 'closed', 'Antwerp', 'Amsterdam', 64),
    ('NLRTM170001004', '2017-01-15T11:30:45', '2017-01-17T11:30:45', 'closed', 'Amsterdam', 'New York', 63),
    ('NLRTM170001005', '2017-01-15T11:30:45', '2017-01-17T11:30:45', 'closed', 'New York', 'Gothenburg', 63),
    ('NLRTM170001006', '2017-01-15T11:30:45', '2017-01-17T11:30:45', 'closed', 'Amsterdam', 'Cork Harbour', 64),
    ('NLRTM170001007', '2017-01-15T11:30:45', '2017-01-17T11:30:45', 'open', 'Gothenburg', 'Lisbon', 63),
    ('NLRTM170001008', '2017-01-15T11:30:45', '2017-01-17T11:30:45', 'closed', 'Halifax', 'Durban', 65),
    ('NLRTM170001009', '2017-01-15T11:30:45', '2017-01-17T11:30:45', 'closed', 'Cork Harbour', 'Antwerp', 64),
    ('NLRTM170001010', '2017-01-15T11:30:45', '2017-01-17T11:30:45', 'open', 'Durban', 'Chennai', 65),
    ('NLRTM170001011', '2017-01-15T11:30:45', '2017-01-17T11:30:45', 'open', 'Antwerp', 'Amsterdam', 64);
