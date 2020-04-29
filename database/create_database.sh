#!/bin/bash

psql -U postgres -a -f /docker-entrypoint-initdb.d/database/create.sql

psql -U semillas -d semillas -a -f /docker-entrypoint-initdb.d/database/schema.sql

psql -U semillas -d semillas -a -f /docker-entrypoint-initdb.d/database/initial_data.sql