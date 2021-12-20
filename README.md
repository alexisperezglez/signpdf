# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.5.6/gradle-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.5.6/gradle-plugin/reference/html/#build-image)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/2.5.6/reference/htmlsingle/#using-boot-devtools)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/2.5.6/reference/htmlsingle/#production-ready)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

### How to build and run
To Build run:
```./gradlew build -x test```

Create docker image: 

```docker build --build-arg JAR_FILE=build/libs/\*-SNAPSHOT.jar -t aica/signpdf:1.0.0 .```

Run service: ```docker-compose up```

### Docs
To review OpenApi Docs please open the next link: [http://localhost:8030/swagger-ui-custom.html](http://localhost:8030/swagger-ui-custom.html)
