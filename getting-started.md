# Getting Started
## Prerequisites
-Java JDK

-Postgres database

-gradle

-Java IDE

## Architecture
Payment service (the main reference service)
-uses Postgres DB
-calls BankService (available in service-starter-utilities repo)
 
You need to have both the database and the BankService running for payment service to be successfully working. 
Follow the below instructions to set them up

## Getting the code
$>> git clone https://github.com/cd-jump-start/service-starter.git

$>> cd service-starter/

$>>./gradlew build

## Building the code
### In CLI
./gradlew build --refresh-dependencies

### In IDE
Open the service-starter project folder in IntelliJ
•	Choose "Import Project from external mode" -> "Gradle"
•	After the project is loaded and indexed, Build->Rebuild the project

### Configuring database
- Open config.sh in root folder
  - Check the connection properties and ensure it matches with your local postgres server connection properties
  -The default database name used is “postgres”. Either create a database in your local server with this name or create a database with any name and update the name in the above two locations.
- Run the config.sh
- Run the application 
  - from IntelliJ 
  - or ./gradlew bootrun from commandline
- Check the database you have configured
  -There will be 2 new empty tables – bankInfo and payment. Check and confirm the same

