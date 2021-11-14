[![Language: Java](https://img.shields.io/badge/Language-Java-blue.svg "Language: Java")](https://www.java.com/)
[![Version: 1.0.0](https://img.shields.io/badge/Version-1.0.0-yellow.svg "Version: 1.0.0")](https://gitlab.com/openease/java-microservice-example/-/tags)

# Java Microservice Example

1. [Description](#description)
1. [Requirements](#requirements)
1. [Java & Docker Build](#java--docker-build)
    1. [Run Micro-service Application Locally](#run-micro-service-application-locally)
    1. [Run Application In Docker Container](#run-application-in-docker-container)
1. [Configuration, Environment, & Start-up](#configuration-environment--start-up)
1. [Logging](#logging)
1. [Internationalization (i18n)](#internationalization-i18n)
1. [API Design](#api-design)
    1. [HTTP Methods & CRUD](#http-methods--crud)
    1. [Versioning](#versioning)
    1. [URL Conventions](#url-conventions)
    1. [Request Object (JSON) Validation](#request-object-json-validation)
    1. [Response Object (JSON) Conventions](#response-object-json-conventions)


## Description

This project is an example of how to organize, architect, design, develop, and build a Java micro-services project.

**Objectives:**
- use pragmatic Java frameworks
  - [Spring Boot 2](https://spring.io/projects/spring-boot)
    - [Spring 5](https://spring.io/)
    - [Spring Data - Elasticsearch](https://spring.io/projects/spring-data-elasticsearch)
    - [Spring Security](https://spring.io/projects/spring-security)
    - [Thymeleaf](https://www.thymeleaf.org/)
  - [Log4J 2](https://logging.apache.org/log4j/2.x/)
- use pragmatic data stores
  - [Elasticsearch](https://www.elastic.co/elasticsearch/)
- use [YAML](https://yaml.org/) for configuration for everything (where possible)
- implement a pragmatic API design
  - leverage HTTP Create/Read/Update/Delete (CRUD) operations
  - standardize API request and response objects
- use containerization for all micro-services
  - [Docker](https://www.docker.com/)
- ensure build artifacts are environment-agnostic
  - application run-time determines how application starts based on the environment passed in
- use an [effective Java build tool](https://maven.apache.org/) and plugins to optimize entire build process
  - [Docker Maven Plugin](https://github.com/fabric8io/docker-maven-plugin)
  - [Front-end Maven Plugin](https://github.com/eirslett/frontend-maven-plugin)
- use a well-managed Java Development Kit (JDK)
  - [Amazon Corretto](https://docs.aws.amazon.com/corretto/)
- avoid maintaining micro-service compatibility matrix of services that interact with each other through a (Git) mono-repo using version parity across all micro-services (in the build and release process)
    - store all micro-services in a single Git repo, release them all at the same time, regardless if a service changed or not
    - this comes at the expense of needlessly releasing certain services if they have not changed but the trade-off is worth it because it is more valuable to _avoid_ tracking service compatibility than trying to _avoid_ releasing something because a version number might change


## Requirements

- [Java 11](https://adoptopenjdk.net/) (JDK 11 - latest version)
  - [Amazon Corretto](https://docs.aws.amazon.com/corretto/)
- [Maven 3](http://maven.apache.org/) (3.8.x - latest version)
- [Docker](https://www.docker.com/) (latest version)
  - Docker Compose (latest version)


## Java & Docker Build

Build the Java & Docker artifacts locally:
```shell script
$ cd <project-root>
$ mvn clean install
```

Build the Java & Docker artifacts locally without running tests:
```shell script
$ cd <project-root>
$ mvn clean install -Dmaven.test.skip=true
```


### Run Micro-service Application Locally

> NOTE: in order to run an application locally, outside of a Docker container _and_ within a Docker container, the following needs to be added to: `/etc/hosts`
> ```
> # Allow the same context to work on the host and the container:
> 127.0.0.1   host.docker.internal
> 127.0.0.1   kubernetes.docker.internal
> ```

```shell script
$ cd <project-root>/service/<service-name>
```

Run a local instance on the existing web app project:
```shell script
$ mvn clean spring-boot:run
```

which is the equivalent to:
```shell script
$ mvn clean spring-boot:run -P env-local
```

The application starts with the default environment set to "local". To change the environment that the application
starts as, a Maven profile needs to be passed in on the command-line:

start the application locally _as if_ it was in the DEV environment:
```shell script
$ mvn clean spring-boot:run -P env-dev
```

> NOTE:
> - this is to _mimic_ the application starting up in DEV
> - use <kbd>Ctrl</kbd>+<kbd>c</kbd> to stop the application

start the application locally _as if_ it was in the PROD environment:
```shell script
$ mvn clean spring-boot:run -P env-prod
```

> NOTE:
> - this is to _mimic_ the application starting up in PROD
> - use <kbd>Ctrl</kbd>+<kbd>c</kbd> to stop the application

Check the running micro-service application (change port 8080 to appropriate start-up port):
- application home page:
  - http://localhost:8080/
- default active Spring actuators:
  - http://localhost:8080/actuator/health
    - basic system health endpoint
  - http://localhost:8080/actuator/info
    - basic info about application (smoke test endpoint)
  - http://localhost:8080/actuator/monitoring
    - JavaMelody monitoring endpoint
    - secured with Basic Auth:
      - username: `admin`
      - password: _git commit hash associated with build_
- custom info endpoints:
  - http://localhost:8080/info/_status
    - detailed info about application (smoke test endpoint)
  - http://localhost:8080/info/_verifyUtf8?q=%C3%A4%C3%B6%C3%BC
    - check application has properly started in UTF-8 mode
    - should produce `äöü` in the response
  - http://localhost:8080/info/_verifyError4xx
    - check that HTTP 4xx errors are being properly handled
  - http://localhost:8080/info/_verifyError5xx
    - check that HTTP 5xx errors are being properly handled

### Run Micro-service Application In Docker Container

#### Start the application's Docker container(s) from a local build:

Example:
```shell script
$ cd <project-root>
$ mvn clean install
$ cd target/deploy/compose/
$ OPENEASE_ENV=dev docker-compose up
```

> NOTE: use <kbd>Ctrl</kbd>+<kbd>c</kbd> (twice) to stop the Docker Compose runtime

#### Stop & clean-up the application's Docker container(s) from a local build:

Example:
```shell script
$ cd <project-root>/target/deploy/compose/
$ docker-compose down --remove-orphans
```

#### Interactively debug a running Docker container:

Example:

- find the running Docker container ID:
  ```shell script
  $ docker ps -a
  ```
- put the Docker container ID into the Docker exec command
  ```shell script
  $ docker exec -it <container-id> /bin/bash
  ```


## Configuration, Environment, & Start-up

- all configurations have been implemented using YAML (where possible)
- the application starts up based on the environment variable (`OPENEASE_ENV`) that is passed in on command-line:
  ```shell script
  $ OPENEASE_ENV=dev docker-compose up
  ```
  - the `OPENEASE_ENV` environment variable is passed into the Docker container, and eventually reaches this java command inside the `Dockerfile`:
    ```shell script
    java ${JVM_OPTS} -Dcom.openease.env=${OPENEASE_ENV} -Dlog4j2.configurationFile=log4j2-${OPENEASE_ENV}.yaml -jar ...
    ```
  - the `OPENEASE_ENV` environment variable must be set to a value of the `com.openease.common.Env` enum (lowercase)
  - the `com.openease.env` Java system property sets the value of `com.openease.common.config.Config.env` to the `com.openease.common.Env` Java enum value
  - the `com.openease.env` Java system property is a core component of driving how the application starts
  - Spring profiles are activated based this Java system property:
    ```yaml
    spring:
      profiles:
        active: "${@environment.systemProperty@}"
    ```
  - Maven does resource filtering on `@environment.systemProperty@` which resolves to `com.openease.env`:
    ```yaml
    spring:
      profiles:
        active: "${com.openease.env}"
    ```
  - Spring then injects the value into: `active: "${com.openease.env}"`
  - application YAML config file names specific to an environment (i.e. `application-{env}.yaml`) must follow the `com.openease.common.Env` enum values (lowercase)
    - example:
      - `application-local.yaml`
      - `application-dev.yaml`
      - `application-prod.yaml`
      - _etc._
    - `application.yaml` contains configuration that is environment-agnostic and is _always_ loaded
    - all of these `application*.yaml` files are packaged inside the application JAR/WAR (they are baked in)
    - Spring Boot's active profiles value controls which of the packaged environment-specific application YAML configuration files gets loaded
    - external configuration file is needed to override the configuration (`application*.yaml`) files packaged inside the application JAR/WAR (defaults) that gets loaded on start-up
      - must live in a directory named `config` next to the application JAR/WAR
        - example: `./config/application.yaml`
      - ideally mounted as a container volume relative to application in location: `${OPENEASE_HOME}/config/application.yaml` (`/opt/openease/config/application.yaml`)
        - example: if the external configuration file lives in the directory `/user/dev/app/config` then there are various options to volume mount the file:
          - Option 1: Docker CLI: `docker run ... -v /user/dev/app/config:/opt/openease/config ...`
          - Option 2: Docker Compose:
            ```yaml
            ...
            services:
              ...        
              app:
                ...
                volumes:
                  - "/user/dev/app/config:/opt/openease/config"
            ...
            ```
- by default, the application will start as LOCAL ("local") environment, this is for the convenience of developers


## Logging

- logging uses the Log4J (v2.x) framework: https://logging.apache.org/log4j/2.x/
- logging configuration is determined by the environment variable that is passed in on command-line (see `Dockerfile`)
  ```shell script
  $ java ${JVM_OPTS} -Dcom.openease.env=${OPENEASE_ENV} -Dlog4j2.configurationFile=log4j2-${OPENEASE_ENV}.yaml -jar ...
  ```
  - the `log4j2.configurationFile` Java system property determines which logging configuration file to use
    - example:
      - `log4j2-local.yaml`
      - `log4j2-dev.yaml`
      - `log4j2-prod.yaml`
      - _etc._
    - `log4j2.yaml` contains a default configuration that must exist and should _not_ be modified


## Internationalization (i18n)

- internationalized messages live at every layer of the application (data, manager, controller)
- message bundles that control the language messages are loaded by the `com.openease.common.web.lang.BaseI18nConfig` Java class and its derivative sub-classes
- to see this in action, start the application and go to: http://localhost:8080/
  - change your browser language setting to French and refresh that page (it will reload with French messages)


## API Design

The API design is composed of several elements:
- HTTP access design
- versioning
- URL conventions
- request & response objects

### HTTP Methods & CRUD

- API follows the HTTP specification for the **Create**, **Read**, **Update**, and **Delete** (CRUD) operations on objects
  - **CREATE** maps to `HTTP POST`
  - **READ** maps to `HTTP GET`
  - **UPDATE** maps to `HTTP PUT`
  - **DELETE** maps to `HTTP DELETE`

### Versioning

- API versioning is completely agnostic from the application's build artifact versioning
- API versioning is done at the URL level as a prefix to API endpoints
- versioning prefix starts with a 'v' and is followed by a positive integer
  - example: `HTTP GET /v1/cars`

> NOTE:
> - it does _not_ make sense to version an API using X.Y.Z (traditional Semantic Versioning)
>   - example: `HTTP GET /v1.3.2/cars`
> - only a major version number (X) is necessary to version an API
>   - example: `HTTP GET /v1/cars`

### URL Conventions

- URLs are constructed such that the API endpoint represents a collection of objects therefore the endpoints should be a plural noun
  - example: if you have a collection of Car objects then the API endpoint should be: `/v1/cars`
    - CREATE one Car:
      - `HTTP POST /v1/cars`
        - with JSON payload
    - READ all Cars:
      - `HTTP GET /v1/cars`
    - READ one Car with ID 7621:
      - `HTTP GET /v1/cars/7621`
    - UPDATE one Car with ID 7621:
      - `HTTP PUT /v1/cars/7621`
        - with JSON payload
    - DELETE all Cars:
      - `HTTP DELETE /v1/cars`
    - DELETE one Car with ID 7621:
      - `HTTP DELETE /v1/cars/7621`
- if you need a new verb that does _not_ fall into the above CRUD operations then you should:
  - use `HTTP POST`
  - append the verb at the end of the API endpoint with an underscore prefix
  - example: search a collection of Cars
    - `HTTP POST /v1/cars/_search`
      - with JSON payload (containing the search query)
- do _not_ use URL querystrings in any API URLs
- for endpoints that are not necessarily reflective of "acting on a collection of objects", they should still be constructed _as if_ they were acting on a collection of objects
  - example:
    - suppose you have an endpoint that sends an email to a recipient
      - this is not a collection of objects (you are not acting on a collection of emails)
      - you should still construct the API endpoint _as if_ you are acting on a collection of emails
      - sending an email can be thought of "creating an email" and the API endpoint should be: `HTTP POST /v1/emails` (with JSON payload)

### Request Object (JSON) Validation

- request objects are in JSON format, and the request validation is done with the Spring-integrated validation annotations on the corresponding Java objects and controller methods
- validation errors are dealt with in a pragmatic manner, and validation messages are i18n-compliant

### Response Object (JSON) Conventions

- all success and error response objects are JSON that follow a standard structure where the status object always exists:
  ```json
  {
    "status" : {
      "code" : ...,
      "internalCode" : ...,
      "message" : ...,
      "additionalInfo" : ...,
      "serverTimestamp" : ...
    }
  }
  ```
- example:
  - success response object (JSON):
    ```json
    {
      "status" : {
        "code" : 200,
        "internalCode" : 0,
        "message" : "OK",
        "additionalInfo" : null,
        "serverTimestamp" : 1234567890123
      },
      "result" : ...
    }
    ```
    - where the `result` attribute is _any_ type of value
  - error response object (JSON):
    ```json
    {
      "status" : {
        "code" : 418,
        "internalCode" : 0,
        "message" : "Sample HTTP 4xx error message",
        "additionalInfo" : "I'm a teapot",
        "serverTimestamp" : 1234567890123
      }
    }
    ```
    - where there is only the `status` object
