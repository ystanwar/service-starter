# service-starter
This is a sample starter services incorporating [best practices](https://github.com/cd-jump-start/service-starter/blob/master/best-practices.md) for writing services in spring-boot.
Uses PostgreSQL database. You can [Get Started here](https://github.com/cd-jump-start/service-starter/blob/master/best-practices.md)


This repo contains the following:  
- PaymentApi - the sample service with basic payment flow for demo purposes. 
  - Exposes a payment REST API that receives a payment request and performs actions to complete a payment transaction. 
  - Has a database that holds the payments info in a Payment table.
  - Has a BankInfo table that holds basic info on a Bank and the URLs it provides for validating accounts and making payments. 
  - Calls another dependency "bankservice" to validate accounts passed in the payment request and do the transactions (this bankservice stub is in another repository available [here](https://github.com/cd-jump-start/service-starter) )

The PaymentApi takes connectionString from externalised configuration - can be specified via external file, command line or environment variables 

You can [Get Started here](https://github.com/cd-jump-start/service-starter/blob/master/best-practices.md)


