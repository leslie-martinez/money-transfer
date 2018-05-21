# money-transfer
Money Transfer Project

## Requirements & tools


- Java 8
- Maven 3+
- Embedded database : H2 1.4.196
- Embedded server : Jetty 9.2.3.v20140905
- Unit and Integration Testing : JUnit 4.11


## Usage

Package the executable application with:

    mvn clean compile package

And execute:

    mvn exec:java

The server is now listening at `http://localhost:8080/`.

You can now test the endpoints described below:

## Endpoints

| Endpoint                    | Method   | Payload          | Call example                              | Return                                      |
|:----------------------------|:---------|:-----------------|:------------------------------------------|:--------------------------------------------|
| /accounts                   |GET       |                  |   http://localhost:8080/accounts          | `200 OK`, `404 NOT FOUND`              |
| /accounts/{accountNo}       | GET      |                  |    http://localhost:8080/accounts/1234    | `200 OK`, `404 NOT FOUND`             |
| /accounts/{accountNo}       | DELETE      |                  |    http://localhost:8080/accounts/1234    | `204 NO CONTENT`, `400 BAD REQUEST`, `404 NOT FOUND`             |
| /accounts/{accountNo}/balance       | GET      |                  |    http://localhost:8080/accounts/1234/balance    | `200 OK`, `404 NOT FOUND`             |
| /transfers                  | GET      |                  |   http://localhost:8080/transfers         | `200 OK`, `404 NOT FOUND`              |
| /transfers/to/{accountNo}   | GET      |                  |http://localhost:8080/transfers/to/1234    | `200 OK`, `404 NOT FOUND`            |
| /transfers/from/{accountNo} | GET      |                  |http://localhost:8080/transfers/from/1234  | `200 OK`, `404 NOT FOUND`            |
| /transfers                  | POST     | { "sourceAccountNo":89012345678, "destinationAccountNo":12345678901, "transferAmount":100, "transferCurrencyCode":"EUR" } | http://localhost:8080/transfers  | `200 OK` , `404 NOT FOUND`, `400 BAD REQUEST` |
| /rates                  | GET      |                  |   http://localhost:8080/rates         | `200 OK`, `404 NOT FOUND`              |
| /rates/effective                  | GET      |                  |   http://localhost:8080/rates/effective         | `200 OK`, `404 NOT FOUND`              |
| /rates/{rate}                  | PUT      |    { "rate":1.23, "effectiveDt":"2018-05-30" }              |   http://localhost:8080/rates/1234         | `200 OK`, `500 INTERNAL SERVER ERROR`              |
| /rates/query?sourceCurrency={sourceCurrency}&destinationCurrency={destinationCurrency}| GET      |    |   http://localhost:8080/rates/query?sourceCurrency=EUR&destinationCurrency=SGD        | `200 OK`, `404 NOT FOUND`              |


## Real Life missing checks and features

In order to keep it simple, this application gives a working money transfer API, as close to real life scenario as it can get.
However, some features are missing : 

- Customer Management
- Authentication
- Authorization
- Real storage
- Real-time currency rates
- Transaction management
- Some input validations (negative amounts, valid email address etc.)
- Customer notification
- PATCH, etc.
- etc.


