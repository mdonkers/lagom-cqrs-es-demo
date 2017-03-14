# lagom-cqrs-es-demo
Demo project for CQRS / ES with Lagom presentation

# Lagom
To run the demo application, execute `mvn lagom:runAll`
Or for the Scala sbt variant execute `sbt runAll`

# Database
MariaDB is used as SQL store for storing read-side data.
It is setup and can be used by running the `run-docker-machines.sh` script,
which will start a Docker container running MariaDB.

Data for the database is by default saved in /tmp/docker/mariadb

To initialize the database;
With Maven

    mvn flyway:migrate

With Sbt

    sbt inspection-database/flywayMigrate

When a migrate fails, run the following command

    sbt inspection-database/flywayRepair


