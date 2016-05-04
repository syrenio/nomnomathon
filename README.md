# nomnomathon
Workflow Management and Process Management Project

# test order via REST Endpoint

> curl -H "Content-Type: application/json" -X POST -d '{"type":"SMS","text":"hungry"}' http://localhost:8080/api/orders

or

> curl -H "Content-Type: application/json" -X POST -d '{"type":"REGULAR","text":"hungry"}' http://localhost:8080/api/orders
