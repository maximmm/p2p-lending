# p2p-lending

#### Microservice consists of six endpoints:

1. Client registration

POST http://localhost:8080/registration

Valid request example:
```
{
	"username" : "dummyClient",
	"password" : "password",
	"passwordRepeated" : "password",
	"personalId" : "1234567894"
}
```
After successful registration, the 'Authorization' header with Bearer token will be send back with response.

2. Client login

POST http://localhost:8080/login

Valid request example:
```
{
	"username" : "dummyClient",
	"password" : "password"
}
```
After successful login, the 'Authorization' header with Bearer token will be send back with response. 

3. Receive a loan from loan issuing company (originator)

POST http://localhost:8080/originator/loan

Valid request example:
```
{
	"loanNumber" : "1234559",
	"originator" : "DummyOne",
	"amount" : "1400.00",
	"startDate" : "15-07-2017",
	"dueDate" : "17-04-2019"
}
```
Please note, that in order to execute requests to http://localhost:8080/originator/** endpoints, your IP should be in application.properties in 'security.originators.ip-addresses' list. By default localhost as IPv6 is there.

4. Receive a payment from loan issuing company (originator)

POST http://localhost:8080/originator/payment

Valid request example:
```
{
	"loanNumber" : "1234559",
	"originator" : "DummyOne",
	"paymentAmount" : "200.00"
}
```
Please note, that in order to execute requests to http://localhost:8080/originator/** endpoints, your IP should be in application.properties in 'security.originators.ip-addresses' list. By default localhost as IPv6 is there.

5. Client invest in loan

POST http://localhost:8080/loan/investment

Valid request example:
```
{
	"loanNumber" : "1234558",
	"originator" : "DummyOne",
	"investmentAmount" : "400.00"
}
```
Please note, that in order to execute requests to http://localhost:8080/loan/** endpoints you should provide Bearer token in 'Authorization' header. The one you have received after registration/login procedure.

6. Expose available loans for the investment

GET http://localhost:8080/loan/investment/all?page=0&size=10

Please note, that in order to execute requests to http://localhost:8080/loan/** endpoints you should provide Bearer token in 'Authorization' header. The one you have received after registration/login procedure.

## H2 Console (embedded db):

http://localhost:8080/h2/

Username: sa

Password: 

JDBC URL: jdbc:h2:mem:test
