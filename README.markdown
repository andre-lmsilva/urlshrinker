URL Shrinker
================================================

This module offers a set of end-points to shorten URLs to a smaller and convenient form. The application stores the
original URLs into an embedded, in-memory relational database. The short aliases are generated encoding the sequential
primary key of the persisted URLs to a base 62 number, represented using alphanumeric characters from a random generated
list. This approach was though to:

* Be simple to implement and fast to encode/decode values.
* Provide a reversible and simple way to encode the primary key of the registered URLs.
* Be URL safe.
* Ensure the uniqueness and avoid clashing.

Another alternatives where explored like:

1) Use an external library to generate the hashes, with the drawback of demonstrate less proficiency in Java.

   
2) Adopt a similar approach as git for commit hashes, exposing the first _n_ characters of the SHA-1 hash. After a certain
number of created elements, the probability of clashing between the hashes start to increase to a significant rate, which
required a fallback approach to avoid it. However, this fallback approach would require more database lookups along the
time, affection the application performance along the time.
   
   
3) Use Base64 hash, which didn't appear to be a good solution once that it can (a) have the sequential nature of the
primary keys inferred after few tries and (b) two Base64 encoded values can occasionally point to the same binary value.
   
## Prerequisites

In order to build, test and run the application, you will require to have installed and configured in your local
machine the following components:

| Component | Version | Purpose |
|-----------|---------|---------|
| JDK (any distribution) | 11 or newer | Required to build and run the application. |
| Apache Maven | 3.6.3 or newer | Used to manage the application dependencies and automate some building tasks. |

The installation and configuration of those components are beyond the scope of this document.

## Getting Start

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes

## Build and Running Locally

Using Maven, build the application should be as straight forward as:

```bash
mvn clean install
```

The above command will:

* Compile all the sources, including test classes.
* Copy resources to the classpath.
* Execute all unit tests (using maven-surefire-plugin).
* All database migrations are applied against test database (using flyway).
* Execute all integration tests (using maven-failsafe-plugin).  
* Generate the coverage report (using jacoco-maven-plugin).
* Package all the classes and resources into an executable JAR file located on ```target/urlshrinker-0.1.0-SNAPSHOT.jar```.

To run the application, we can use the following command:

```bash
java -jar target/urlsrinker-0.1.0-SNAPSHOT.jar -Dspring.profiles.active=dev
```

During the application bootstrap, the database migrations will be automatically applied, taking advantage of _Spring Boot_
integration with _flyway_. The migration folder is located on ```src/main/resources/db/migration```.

After start, the application will be by default available on the TCP port ```8080```.

## Usage

Once that the application is up and running, we can create a new URL using the following command:

```bash
curl -X POST http://localhost:8080/api/v1/url_entry -d '{"urlAddress":"http://www.google.com"}' -H "Content-Type: application/json" -i
```

Which should generate a response like bellow:

```bash
HTTP/1.1 201
Content-Type: application/json
Transfer-Encoding: chunked
Date: Fri, 20 Aug 2021 11:01:12 GMT

{"urlAlias":"http://localhost:8080/Wa0Z"}
```

To try the redirection of URL, paste the value of the _urlAlias_ attribute in your browser like:

```
http://localhost:8080/Wa0Z
```

You should be redirected to the original URL.

## Test Coverage

The test coverage is assessed using JaCoCo plugin, ignoring not useful code e.g. the boilerplate code used to start
the Spring Boot application inside the _main()_ method. The report is generated during the building process and can
be found on ```target/site/jacoco/index.html```.

The coverage report was indicating __100% of coverage__ when this document was written.

## Docker Deployment Instructions

As requested, this project has a _Dockerfile_ capable to produce a Docker image ready to be deployed. The _Dockerfile_
can be found in the application root folder and has few environment variables that need to be configured as desired:

| Environment Variable Name | Default Value | Purpose |
|---------------------------|---------------|---------|
| DATABASE_URL              | jdbc:hsqldb:mem:pro_urlshrinker;DB_CLOSE_DELAY=-1 | JDBC URL for database connection |
| DATABASE_USER             | sa | Database user used to connect with the database. |
| DATABASE_PASSWORD         | ** empty ** | Database user password used to connect with the database. |
| APP_BASE_URL              | http://localhost:8080 | Root application URL. Used to concatenate with the generate shorter alias. |

The image can be built with the following command executed from the project's root folder:

```bash
docker build -t urlshrinker .
```

And executed using the command:

```bash
docker run --name urlshrinker -p 8080:8080 -d urlshrinker
```

## Swagger Documentation

There is a non-comprehensive _Swagger Documentation_ available in the URL [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html).


