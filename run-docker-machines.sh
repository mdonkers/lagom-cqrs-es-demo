#!/bin/bash

del_stopped(){
  local name=$1
  local state=$(docker inspect --format "{{.State.Running}}" $name 2>/dev/null)

  if [[ "$state" == "false" ]]; then
    docker rm $name
  fi
}

## MariaDB
del_stopped akka-mariadb
docker run --name akka-mariadb -v /tmp/docker/mariadb:/var/lib/mysql -p 3306:3306 -e MYSQL_DATABASE=FRAMEWORKDB \
  -e MYSQL_USER=COFFEE -e MYSQL_PASSWORD=secret-coffee-pw -e MYSQL_ROOT_PASSWORD=secret-root-pw -d mariadb:latest
