# Delivery Order Price Calculator service (DOPC)

This is a project for Wolt company as part of their recruitment process.

### Summary
DOPC is an imaginary backend service which is capable of 
calculating the total price and price breakdown of a delivery order. 
DOPC integrates with the Home Assignment API to fetch venue related data required to
calculate the prices. The term venue refers to any kind of restaurant / shop / store that's in Wolt.
The potential clients of DOPC might be other backend services, 
Wolt's consumer mobile apps, or even some third parties which integrate with Wolt.


### Notes
All the money related information (prices, fees, etc) are in the lowest denomination of the local currency. 
In euro countries they are in cents, and in Sweden they are in öre.

There are three venue slugs (the first entry parameter venue_slug) available for this calculator:

* home-assignment-venue-helsinki
* home-assignment-venue-stockholm
* home-assignment-venue-berlin


### Installation
This calculator software run with gradle and Kotlin 2.0.021 and springboot 3.3.4. 
There is a frontend of Swagger UI which make it easy to test the endpoint of calculator
After installing gradle and Kotlin, run the application with `./gradlew bootRun` and open the localhost 
with specified port mentioned in commandline which the default of it is 8080:
"http://localhost:8080/swagger-ui/index.html"

### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/3.3.4/gradle-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.3.4/gradle-plugin/packaging-oci-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.3.4/reference/htmlsingle/index.html#web)
* [Rest Repositories](https://docs.spring.io/spring-boot/docs/3.3.4/reference/htmlsingle/index.html#howto.data-access.exposing-spring-data-repositories-as-rest)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing JPA Data with REST](https://spring.io/guides/gs/accessing-data-rest/)
* [Accessing Neo4j Data with REST](https://spring.io/guides/gs/accessing-neo4j-data-rest/)
* [Accessing MongoDB Data with REST](https://spring.io/guides/gs/accessing-mongodb-data-rest/)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

