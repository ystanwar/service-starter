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
```
git clone https://github.com/cd-jump-start/service-starter.git
cd service-starter/
./gradlew -Pgdusername=<<your_github_username>> -Pgdtoken=<<your_github_token>> build
```

## Building the code
### In CLI
./gradlew build

### In IDE
Open the service-starter project folder in IntelliJ
•	Choose "Import Project from external mode" -> "Gradle"
•	After the project is loaded and indexed, Build->Rebuild the project

## Configuring database
- Create a database in postgres by running the following script
```
src/main/resources/createdb.sh
```
## Start the application
*  Run the following command
```
./gradlew -Dspring.profiles.active=dev bootrun
```
* From IDE, Run the PaymentApplication.java
```
Set this in VM Options of "Run Configuration" in your IDE
-Dspring.profiles.active=dev
```

* Check the database you have configured. There will be 2 new empty tables – bankInfo and payment. Check and confirm the same

## Setting up seed data
- Download the jar file from gitPackages -> BankInfoSeed-1.1-20200415.074232-1.jar
- run the Service starter- ./gradlew bootRun
- run jar file (by going to the jar file path)-> java -jar BankInfoSeed-1.1-20200415.074232-1.jar java -jar BankInfoSeed-1.1-20200415.074232-1.jar jsonFilePathContainingBankData http://localhost:8080/bankinfo
- json File Data should be :->
 [
   {
     "bankCode": "HDFC",
     "url": "http://localhost:8082"
   }
 ]  
  
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


 


