# Getting Started
## Prerequisites
- Java JDK

- Postgres database

- gradle

- Docker

- Java IDE

## Architecture
Payment service (the main reference service)
-uses Postgres DB
-calls BankService (available in service-starter-utilities repo)
 
You need to have both the database and the BankService running for payment service to be successfully working. 
Follow the below instructions to set them up

## Getting the code
$>> git clone https://github.com/cd-jump-start/service-starter.git

$>> cd service-starter/

$>>./gradlew build --refresh-dependencies

## Building the code
### In CLI
./gradlew build --refresh-dependencies

### In IDE
Open the service-starter project folder in IntelliJ
•	Choose "Import Project from external mode" -> "Gradle"
•	After the project is loaded and indexed, Build->Rebuild the project

## Configuring database
- Open config.sh in root folder
  - Check the connection properties and ensure it matches with your local postgres server connection properties
  -The **default database name used is “postgres”**. Either create a database in your local server with this name or create a database with any name and update the name in the above two locations.
- **Run the config.sh**
- **Run the application in the same shell** where you ran config.sh
  - from IntelliJ 
  - or ./gradlew bootrun from commandline
- **Check the database you have configured**
  -There will be 2 new empty tables – bankInfo and payment. Check and confirm the same

## Setting up seed data
- Do a POST to http://localhost:8080/bankinfo with the following json

{
	"bankCode": "HDFC",
	"url": "http://localhost:8082"
}

- You should get a 201 response with following body

{
    "id": 1,
    "bankCode": "HDFC",
    "url": "http://localhost:8082"
}

## Running dependencies
### The payment service will require to connect to "BankService"
Follow below steps to get "BankService" running on your local machine 

 *(NOTE: This assumes Postgres db running on localhost:5432. Update URI in the below mentioned docker run command if different)*
- First get a github personal access token
  - [How to get a github personal token](https://help.github.com/en/github/authenticating-to-github/creating-a-personal-access-token-for-the-command-line)
- Authenticate with github packages
  - docker login -u USERNAME -p TOKEN docker.pkg.github.com
- Get the docker image for "BankService"
  - docker pull docker.pkg.github.com/cd-jump-start/service-dep-bankservice/bankservice:latest
 - Run the "BankService" in docker container
  - docker run  -p 8082:8082 -e SPRING_PROFILES_ACTIVE=dev -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/postgres docker.pkg.github.com/cd-jump-start/service-dep-bankservice/bankservice:latest

- In postman or browser GET http://localhost:8082/checkDetails?accountNumber=12345&ifscCode=HDFC1234
  - This should return 200 response with empty body

*NOTE: This is the service that will validate the accounts before payment. Any account-IfscCode combination that you will given in the payment request (json sample given above) should be in the “accounts” table of the database you configured to be used by this service*

### The payment service will require to connect to "Fraud-check Service"
Follow below steps to get "FraudService" running on your local machine 

- First get a github personal access token
  - [How to get a github personal token](https://help.github.com/en/github/authenticating-to-github/creating-a-personal-access-token-for-the-command-line)
- Authenticate with github packages
  - docker login -u USERNAME -p TOKEN docker.pkg.github.com
- Get the docker image for "FraudService"
  - docker pull docker.pkg.github.com/cd-jump-start/service-dep-fraudservice/fraudservice:latest
 - Run the "FraudService" in docker container
  - docker run -p 8083:8083 -e SPRING_PROFILES_ACTIVE=dev docker.pkg.github.com/cd-jump-start/service-dep-fraudservice/fraudservice:latest


## Testing the reference service - /payment 
- Run the service-starter application (./gradlew bootRun as mentioned above) 
- Do a POST to http://localhost:8080/payments with json body as follows:

{
	"amount" : 10001,
	"payee": {
		"name":"Ethan",
		"accountNumber":12345,
		"ifscCode":"HDFC1234"
	},
	"beneficiary": {
		"name":"May",
		"accountNumber":67890,
		"ifscCode":"HDFC1234"
	}
}

  - The above POST should return a 201 response with following content

{
    "statusMessage": "Payment done successfully",
    "paymentId": 1
}

- Do a GET http://localhost:8080/payments
  - This should return 200 response with following body
  [
    {
        "id": 1,
        "amount": 10001,
        "beneficiaryName": "May",
        "beneficiaryAccountNumber": 67890,
        "beneficiaryIfscCode": "HDFC1234",
        "payeeName": "Ethan",
        "payeeAccountNumber": 12345,
        "payeeIfscCode": "HDFC1234",
        "status": "success"
    }
]

**The service is now built and running fine in your setup. Explore, reference and reuse!!!**


 


