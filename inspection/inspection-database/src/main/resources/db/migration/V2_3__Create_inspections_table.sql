DROP TABLE IF EXISTS inspections;

CREATE TABLE IF NOT EXISTS inspections (
  id              MEDIUMINT NOT NULL AUTO_INCREMENT,
  ucrn            VARCHAR(18) NOT NULL,
  dt_inspected    DATETIME DEFAULT CURRENT_TIMESTAMP,
  employee        VARCHAR(48),
  remarks         VARCHAR(512),
  PRIMARY KEY (id)
);

INSERT INTO inspections
(ucrn, dt_inspected, employee, remarks)
VALUES
  ('NLRTM170001004', '2017-01-01T14:30:45', 'Jan', 'Tanks were not sealed correctly'),
  ('NLRTM170001005', '2017-01-02T14:30:45', 'Leon', 'Checking tanks again, not cleaned properly and venting not allowed'),
  ('NLRTM170001005', '2017-01-03T14:30:45', 'Jan', NULL),
  ('NLRTM170001006', '2017-01-04T14:30:45', 'Kees', NULL),
  ('NLRTM170001008', '2017-01-05T14:30:45', 'Mike', NULL),
  ('NLRTM170001008', '2017-01-06T14:30:45', 'Mike', 'Venting substances not allowed in this location'),
  ('NLRTM170001009', '2017-01-07T14:30:45', 'Jan', 'Ship not properly anchored. Paperwork not in order'),
  ('NLRTM170001009', '2017-01-08T14:30:45', 'Gregor', NULL),
  ('NLRTM170001009', '2017-01-21T14:30:45', 'David', 'Paperwork not in order, certificates expired'),
  ('NLRTM170001010', '2017-01-22T14:30:45', 'David', 'Two types of dangerous cargo loaded next to each other'),
  ('NLRTM170001011', '2017-01-23T14:30:45', 'Jan', 'Cargo not properly fixated'),
  ('NLRTM170001011', '2017-01-25T14:30:45', 'Kees', NULL);
