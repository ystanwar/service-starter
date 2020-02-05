#!/bin/bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/postgres
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres

export FLYWAY_ENABLED=true
export FLYWAY_LOCATIONS=classpath:/db/migration

