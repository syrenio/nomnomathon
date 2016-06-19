# NomNomathon
Workflow Management and Process Management Project

## Introduction

Nomnomathon is a virtual delivery service that makes exhaustive use of Apache Camel.
It was created for a lecture on University in sommerterm 2016.

## The technology stack

We use the following software:

* Spring
  * Boot
  * Data
* Apache Camel
  * see the pom for used components
* H2 DB for inmemory relational data
* mongodb for documents
* SMPPSim for sending SMS

# Getting Started

## Requirements

You need this softwarestack to run it:

* Java 8 JDK
* maven 3
* mongodb
* SMPPsim with default settings http://www.seleniumsoftware.com/downloads.html

## Define your own properties

By default mongodb is expected to be listening on localhost.

If you want to run anywhere else create your own properties file in src/main/ressources with a profile name inside. (application-<<PROFILENAME>>.properties) and set mongoDB.host to the desired adress.
You can also change the desired log-level there.

Set the maven property spring.profiles.active to <<PROFILENAME>> to make use of it. (by appending -Dspring.profiles.active=<<PROFILENAME>> on command line)
The selected profile should be shown 

If you want to start mongodb in docker use this command: docker run --name mymongo -p 27017:27017 -d mongo:3.2

## Setup and execution
* Adapt Testusers in DatabaseSeeder.java
  * Base64 encode: username:password for testing (e.g. https://www.base64encode.org/)
* Start mongodb and SMPPsim
* Execute mvn spring-boot:run in the main project folder.


## Test via REST Endpoint

replace YOUR_PHONENUMBER or YOUR_AUTH_CODE

### hungry SMS OrderRequest - get meal under 20€
> curl -H "Content-Type: application/json" -X POST -d "{\"type\":\"SMS\",\"text\":\"hungry\",\"phoneNumber\":\"+YOUR_PHONENUMBER\"}" http://localhost:8080/api/orders

### available, FULLFILLED
> curl -H "Content-Type: application/json" -H "Authorization: Basic YOUR_AUTH_CODE" -X POST -d "{\"type\":\"REGULAR\",\"text\":\"hungry\",\"phoneNumber\":\"+YOUR_PHONENUMBER\",\"dishes\":[\"pizza\", \"pasta\"]}" http://localhost:8080/api/orders

### shrimp not found, REJECTED_NO_RESTAURANTS
> curl -H "Content-Type: application/json" -H "Authorization: Basic YOUR_AUTH_CODE" -X POST -d "{\"type\":\"REGULAR\",\"text\":\"hungry\",\"phoneNumber\" :\"+YOUR_PHONENUMBER\",\"dishes\":[\"shrimp\"]}" http://localhost:8080/api/orders

### wurst not available, REJECTED_NO_RESTAURANTS
> curl -H "Content-Type: application/json" -H "Authorization: Basic YOUR_AUTH_CODE" -X POST -d "{\"type\":\"REGULAR\",\"text\":\"hungry\",\"phoneNumber\":\"+YOUR_PHONENUMBER\",\"dishes\":[\"pizza\", \"wurst\"]}" http://localhost:8080/api/orders

### frank nomoney has no money , REJECTED_INVALID_PAYMENT
> curl -H "Content-Type: application/json" -H "Authorization: Basic ZnJhbms6bm9tb25leQ==" -X POST -d "{\"type\":\"REGULAR\",\"text\":\"hungry\",\"phoneNumber\":\"+4368000000000\",\"dishes\":[\"pizza\", \"pasta\"]}" http://localhost:8080/api/orders

### unknown property 'tex'
> curl -H "Content-Type: application/json;" -H "Authorization: Basic ZnJhbms6bm9tb25leQ==" -X POST -d "{\"type\":\"REGULAR\",\"tex\":\"hungry\"}" http://localhost:8080/api/orders

### invalid format exception
> curl -H "Content-Type: application/json;" -H "Authorization: Basic ZnJhbms6bm9tb25leQ==" -X POST -d "{\"type\":\"UPS!\",\"text\":\"hungry\"}" http://localhost:8080/api/orders

### update restaurant data via REST Endpoint
> curl -H "Content-Type: application/json" http://localhost:8080/api/updateResData --data-binary @panucis_pizza.json

```
panucis_pizza.json
{

	"name": "Panucis Pizza",
	"_id": 1,
	"location": "1150",
	"opening": "10:00",
	"closing": "22:00",
	"categories": [

		"italian",
		"american"

	],

	"menu": [{
		"name": "pizza",
		"price": 22.99
	}, {
		"name": "pasta",
		"price": 11.99
	}, {
		"name": "burger",
		"price": 33.99
	}]
}
```

