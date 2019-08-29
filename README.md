# transaction-authorization

This is a simple transaction authorization service, developed as an exercise for Nubank's hiring process.

---
## **Installation**

To install the project simply unzip the attached package. 
It should contain all the relevant files for the project, including source, tests, Dockerfile and an artifact.

For building this project it will also be essencial to have leiningen working.

---
## **Build**

This project uses Leiningen as a manager for the project.

Therefore you can run unit and integration tests via (more about this [here](#tests))
```
$ lein midje
```
and build your uberjar with
```
$ lein uberjar
```

---
## **Dependencies**

WRITE ABOUT DEPENCENCIES HERE

---
## **Firing up the server**

You can either run this service locally or via docker, being docker the preferred way.

### **Docker**

This method assumes a working Docker installation, if that is not the case refer to [the local usage guide](#local).

First, using an UNIX terminal access the project's docker folder, it contains both the Dockerfile which describes the build process and the transaction-authorization uberjar, which will be deployed to the docker instance.

Once in the correct folder run the following command
```
$ docker run --rm $(docker build -q .)
```

This will pull an alpine linux image from the hub with openjdk8 installed, set the default port via an environment variable and run the application.
The current instance ip and running port should appear, along with the startup information provided by http-kit in your terminal.

If you wish to keep the built instance in your local docker be sure to remove the `--rm` tag from the command above.

### **Local**

The local method for running the project needs only a working JRE.

To start the service you need either to build the project and enter the *target/uberjar* directory **OR** enter the *docker* folder, which contains a built artifact.

Once in the correct folder run
```
$ java -jar transaction-authorization-1.0.0-SNAPSHOT-standalone.jar
```

This will start the server and displayed on your terminal the exposed port alongside with your ip and startup information provided by http-kit.

---
## **Consuming the service**

The endpoints created by this service are

> POST /accounts

> POST /transactions

As soon as the service starts running it will display both the current IP address and the exposed port.
From there you can use any application such as Postman or even curl to consume the service.


Note that the expected http verb is POST for both routes and you should provide a body such as the examples below

*new account example*

```json
{
	"account": {
		"activeCard": true,
		"availableLimit": 16000
	}
}
```

*new transaction example*

```json
{
	"transaction": {
		"merchant": "Burger King",
		"amount": 14.99,
		"time": "2019-09-13T02:45:12.000Z"
	}
}
```

---
## **Tests**

WRITE ABOUT SELVAGE FLOW AND MIDJE (maybe about non mocked db)

---
## **File architecture**

WRITE ABOUT LOGIC | ADAPTER | DB

## **Database**