[![Build Status](https://travis-ci.org/smpavlenko/docker-file-server.svg?branch=master)](https://travis-ci.org/smpavlenko/docker-file-server)

# Docker File Server
##### HTTP web server in Java, using only the standard JDK

This repository contains code to build and deploy simple web server which allows to traverse through user defined directory using web client.

Main capabilities
* The server is implemented using java standard *HttpServer*, *HttpHandler*, *HttpExchange* classes from *com.sun.net.httpserver* package
* The server implementation is using *apache log4j*, *apache commons-codec*, *lombok* libraries
* The server writes logs to stdout console
* The server is delivered inside a Docker container
* The server handles HTTP GET and HEAD requests. Other requests handling is not implemented
* The server handles properly HTTP *ETag*, *If-Match*, *If-Non-Match*, *If-Modified-Since* headers according to HTTP standards 
* The server could properly handle *Keep-Alive* request headers and reuses established connection

## Architecture
HTTP web server in Java, using only the standard JDK without using existing HTTP server frameworks. The server serves static files and directories from a user-specified root directory. It also gives possibility to discover subdirectories.

![architecture](assets/architecture.png)

## Prerequisites
To build, deploy and verify application you must have installed
* bash or shell console
* java version 11
* maven
* docker
* http client (web browser, Postman, curl)

## Repository Structure
```bash
.
├── README.md
├── Dockerfile
├── install.sh
├── acceptance.sh
├── pom.xml
├── .travis.yml
├── assets
└── src
    ├── main
    │   ├── java
    │   │   ├── com.pavlenko.controller
    │   │   ├── com.pavlenko.dto
    │   │   ├── com.pavlenko.processor
    │   │   ├── com.pavlenko.service
    │   │   ├── com.pavlenko.util
    │   │   │   
    │   │   ├── Application.java
    │   │   ├── Module.java
    │   │   └── Server.java
    │   └── resources
    │       ├── templates
    │       │   └── dir_template.html
    │       ├── 404_response.html
    │       ├── 405_response.html
    │       └── log4j2.xml
    │
    └── test
        └── java

```

**assets** dir with description images

**Application** main application, starting point of the server

**Server** Server implementation

**Module** instantiation of all necessary beans 

**templates/dir_template.html** html template for list files response

**404_response.html** HTML response for 404 Not Found HTTP code

**405_response.html** HTML response for 405 Method Not Allowed HTTP code

**log4j2.xml** log4j2 xml configuration

**install.sh** build application, create docker and run it

**acceptance.sh** primitive acceptance tests

## Building, deployment and starting server
To build java application, run in console 
```bash
mvn clean install
```


To run application locally, run
```bash
java -jar target/docker-file-server.jar $root_dir
```
where *$root_dir* is user-specified root directory to observe, e.g.:
```bash
java -jar target/docker-file-server.jar $(pwd)
```


To build docker container, run in console 
```bash
docker build -t docker-file-server .
```
where *docker-file-server* is arbitrary name of container


To run docker container, run in console 
```bash
docker run -it --rm -p 8080:8080 -v $root_dir:/root_dir --name docker-file-server docker-file-server /root_dir
```
where *$root_dir* is user-specified root directory to observe, e.g.:
```bash
docker run -it --rm -p 8080:8080 -v $(pwd):/root_dir --name docker-file-server docker-file-server /root_dir
```


To build and deploy all at once, run
```bash
./install.sh
```

Application logs will be written to console

## Verification
To experience application working, type *localhost:8080* in web browser

![demo](assets/browser.png)

To send HEAD requests or modify *If-Match*, *If-Non-Match*, *If-Modified-Since* request headers, use Postman or curl
```bash
sergiipavlenko$ curl --head http://localhost:8080/README.md
HTTP/1.1 200 OK
Date: Sun, 30 Aug 2020 13:09:00 GMT
Last-modified: Sun Aug 30 15:08:47 CEST 2020
Content-type: text/plain
Etag: "7c38bf8706e94b40a155c2aadd0d707d"
Content-length: 5039
```

Example of POST(unsupported) request
```bash
sergiipavlenko$ curl -X POST --head http://localhost:8080/README.md
HTTP/1.1 405 Method Not Allowed
Date: Sun, 30 Aug 2020 13:41:15 GMT
Content-type: text/html
Content-length: 224
```

Example of 404 Not Found request
```bash
sergiipavlenko$ curl --head http://localhost:8080/unknown
HTTP/1.1 404 Not Found
Date: Sun, 30 Aug 2020 13:09:49 GMT
Content-type: text/html
```

Example of request with *If-Match* header
```bash
sergiipavlenko$ curl --head http://localhost:8080/README.md --header 'If-Match: "0f46357eafa5c719e8e3bf277a993e07"'
HTTP/1.1 304 Not Modified
Date: Sun, 30 Aug 2020 13:08:29 GMT
Last-modified: Sun Aug 30 15:08:22 CEST 2020
Content-type: text/plain
Etag: "1a28a3c5ccebec9db44a4e0634762016"
Content-length: 5039
```

Example of request with *If-Modified-Since* header. Date must pass standard java Date.toString() pattern
```bash
sergiipavlenko$ curl --head http://localhost:8080/README.md --header 'If-Modified-Since: Sun Aug 30 13:35:22 CEST 2050'
HTTP/1.1 304 Not Modified
Date: Sun, 30 Aug 2020 13:02:45 GMT
Last-modified: Sun Aug 30 14:59:52 CEST 2020
Content-type: text/plain
Etag: "9e60ce9a14ff316c302f6fe6f5462052"
Content-length: 5049
```

To run simple primitive acceptance tests, run in console
```bash
./acceptance.sh
```
server must be up and running, and http://localhost:8080/README.md must be reachable. Result should be
```bash
sergiipavlenko$ ./acceptance.sh 
Asserting requests to http://localhost:8080/README.md
Completed
```