# nomnomathon
Workflow Management and Process Management Project

## What it is

Nomnomathon is a virtual delivery service that makes exhaustive use of Apache Camel.
It was created for a lecture on University in sommerterm 2016.

## The technology stack

We use the following software:

* Spring
** Boot
** Data
* Apache Camel
** see the pom for used components
* H2 DB for inmemory relational data
* mongodb for documents
* SMPPSim for sending SMS

#How to run it

## Requirements

U need this softwarestack to run it:

* Java 8 JDK (off course)
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

## Command for execution

Execute mvn spring-boot:run in the main project folder.


# test order via REST Endpoint

> curl -H "Content-Type: application/json" -X POST -d '{"type":"SMS","text":"hungry","phoneNumber":"+4368012345678"}' http://localhost:8080/api/orders

or

> curl -H "Content-Type: application/json;" -H "Authorization: Basic YmVybmQ6bm9tbm9t" -X POST -d '{"type":"REGULAR","text":"hungry","dishes":["pizza", "wurst"]}' http://localhost:8080/api/orders

or

> curl -H "Content-Type: application/json" -H "Authorization: Basic YmVybmQ6bm9tbm9t" -X POST -d "{\"type\":\"REGULAR\",\"text\":\"hungry\",\"phoneNumber\":\"+4368012345678\",\"dishes\":[\"pizza\", \"wurst\"]}" http://localhost:8080/api/orders


## Requests that will show error messages

Unknown property 'tex'

> curl -H "Content-Type: application/json;" -H "Authorization: Basic YmVybmQ6bm9tbm9t" -X POST -d '{"type":"REGULAR","tex":"hungry"}' http://localhost:8080/api/orders

Invalid format exception

> curl -H "Content-Type: application/json;" -H "Authorization: Basic YmVybmQ6bm9tbm9t" -X POST -d '{"type":"UPS!","text":"hungry"}' http://localhost:8080/api/orders

## Update restaurant data via REST Endpoint

> curl -H "Content-Type: application/json" http://localhost:8080/api/updateResData --data-binary @panucis_pizza.json


## more examples

### shrimp not found, REJECTED_NO_RESTAURANTS
curl -H "Content-Type: application/json" -H "Authorization: Basic DEIN-AUTHCODE" -X POST -d "{\"type\":\"REGULAR\",\"text\":\"hungry\",\"phoneNumber\" :\"+4368012345678\",\"dishes\":[\"shrimp\"]}" http://localhost:8080/api/orders

### wurst not available, REJECTED_NO_RESTAURANTS
curl -H "Content-Type: application/json" -H "Authorization: Basic DEIN-AUTHCODE" -X POST -d "{\"type\":\"REGULAR\",\"text\":\"hungry\",\"phoneNumber\":\"+4368012345678\",\"dishes\":[\"pizza\", \"wurst\"]}" http://localhost:8080/api/orders

### verfuegbar, FULLFILLED
curl -H "Content-Type: application/json" -H "Authorization: Basic DEIN-AUTHCODE" -X POST -d "{\"type\":\"REGULAR\",\"text\":\"hungry\",\"phoneNumber\":\"+4368012345678\",\"dishes\":[\"pizza\", \"pasta\"]}" http://localhost:8080/api/orders

### SMS random, FULLFILLED
curl -H "Content-Type: application/json" -X POST -d "{\"type\":\"SMS\",\"text\":\"hungry\",\"phoneNumber\":\"+DEINE-NUMMER\"}" http://localhost:8080/api/orders
