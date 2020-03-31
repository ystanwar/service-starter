#!/bin/bash

#Set the value of variable
database="paymentdb"
user="paymentuser"
password="password"

echo "Creating database $database"
psql -c "create database $database"
echo "Creating user $user"
psql -d $database -c "create user $user with encrypted password '$password'"
echo "Granting user to database"
psql -d $database -c "grant all privileges on database $database to $user"

echo "Done!"