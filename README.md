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

### Diagrama de Despliegue
<img src="https://lh4.googleusercontent.com/eOWlWQn9fkf-cl8Ar9DtET9gV1bqeN_pI1jZm77tMPEeB5N3-6RzZOqTcGa0Pf9NaYi_2KgZ3JHHi9QrPIL4=w1885-h964"/>

### How to build and run

To Build run:
```./gradlew build -x test```

Create docker image:

```docker build --build-arg JAR_FILE=build/libs/\*-SNAPSHOT.jar -t aica/signpdf:1.0.0 .```

Environment Variables:
* <b>SIGNPDF_SERVER_PORT</b>: define the exposed port
* <b>SIGNPDF_POSTGRES_URL</b>: database url connection
* <b>SIGNPDF_POSTGRES_USER</b>: database username credential
* <b>SIGNPDF_POSTGRES_PASS</b>: database username credential
* <b>SIGNPDF_MS_DOCS</b>: base uri to ms documentos
* <b>SIGNPDF_PKI_CRL_PATH</b>: uri to softel pki crl
* <b>SIGNPDF_AICA_PROXY_IP</b>: aica proxy ip
* <b>SIGNPDF_AICA_PROXY_PORT</b>: aica proxy port

Run service: ```docker-compose up```

### Docs
To review OpenApi Docs please open the next link: [http://localhost:8030/swagger-ui-custom.html](http://localhost:8030/swagger-ui-custom.html)
