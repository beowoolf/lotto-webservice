# lotto-webservice

Web lottery application in Spring Boot, based on a modular monolith, hexagonal architecture. Technologies used in the
project: Java 17, Maven, Git, Spring Boot, Spring Data, MongoDB, Junit, AssertJ, Mockito, Testcontainers, MockMvc,
WireMock, Docker, REST API, GitHub

## Main commands

```bash
# image creation
$ docker build -f Dockerfile -t lotto-webservice .

# starting the image
$ docker run -d -p 8080:8080 lotto-webservice

# image composing
$ docker-compose up -d
```
