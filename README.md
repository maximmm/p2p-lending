# p2p-lending

#### Microservice consists of six endpoints:

Client registration:
1. POST http://localhost:8080/registration
Example request:
```
{
	"username" : "username",
	"password" : "password",
	"passwordRepeated" : "password",
	"personalId" : "1234567894"
}
```
After successful registration, the AUTHORIZATION header with Bearer token will be send back with response. 

2. POST http://localhost:8080/login
Example request:
```
{
	"username" : "borislav",
	"password" : "password"
}
```
After successful login, the AUTHORIZATION header with Bearer token will be send back with response. 

3. POST http://localhost:8080/originator/loan
4. POST http://localhost:8080/originator/payment
5. POST http://localhost:8080/loan/investment
6. GET http://localhost:8080/loan/investment/all?page=0&size=10
