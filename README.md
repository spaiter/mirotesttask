# Widgets REST API.
A REST API that can do CRUD and filtering operations with widgets.

## Requirements
- java 11
- Maven 3.6.3

## Configuration

#### Preparation
To create local configuration copy example config file and change it if necessary.
Configuration folder name - `config`.

```
cp ./config/application.example.yml ./config/application.yml
```
 
#### API rate limit
There is ability to change API rate limit without application reloading.

1. Change rate limits in configuration file.
2. Make POST request to `/actuator/refresh`.

Request example with curl:
```
curl --location --request POST 'localhost:8080/actuator/refresh'
```

After this, API rate limits will be updated in runtime.

## Launch
API starts on `8080` port.
To run API use the following command:
```
mvn spring-boot:run
```

## Endpoints
After app launch the OpenAPI descriptions will be available at the path `/api-docs`.

## Testing
To run all tests and get report:
 ```
mvn test jacoco:report
 ```
Report folder `./target/site/jacoco`.

## TODO
- move shifting logic from repository to service, because it's a part of business logic.
- refactor whole app to clean architecture. Examples: https://medium.com/swlh/clean-architecture-java-spring-fea51e26e00, https://medium.com/slalom-build/clean-architecture-with-java-11-f78bba431041
- refactor entities and dto creation via factories.
- create a sql query that will return the z-index of the last widget to be offset. (same logic as in my implementation repository right now).
- fix all checklist errors.