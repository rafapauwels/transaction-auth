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

On production this project dependencies are mainly `data.json` for json parsing, `http-kit` for serving the endpoints and `compojure` for routing inside the application.

In development it is necessary both `midje` and `selvage` for unit and integration testing.

---
## **Running the server**

You can either start this service locally or via docker, being docker the preferred way.

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

Midje was the unit testing framework choosen, I didn't unit test unpure functions since the integration tests via flow provided an unmocked environment for testing such functions.

Integration tests used `selvage.flow` alongside with `midje` for providing an end-to-end testing environment for account creation and for the transaction authorization flow.

Since the database is volatile, in-memory only and the whole service does not rely on outside resources the only mocked parts were the inputs, which are mocked as a map such as

```clojure
{:body (.getBytes (json))}
```  

### **Running the tests**
```
lein midje
```

---
## **File architecture**
The functions are distributed in a manner that tries to follow the hexagonal architecture

### **Logic**
Encapsulates all the business rules. That includes what is an account, what is a transaction and what are the violations that can happen in an authorization flow.

All functions here are pure, that is, it doesn't cause side-effects neither throw exceptions.

### **Adapter**
Holds conversions between formats. In this case that is the input json to a clojure map and the otherway around. Also responsible for stopping any input that is not a json.

### **Controller**
Orquestrate all the layers, is core between the requests and business rules. 

### **Server**
The entry point, responsible for starting the server, defining the endpoints and properly routing the requests.

### **Port**
The gateway between components like a database, kafka, mq or any other outside service.

### **Util**
Handles some of the necessary java interops for handling zoned datetimes.

### **Validator**
Defines what is a valid request, must be clojure map which contains given keys

### **Db**
In-memory database. [More below](#database).

---
## **Database**

The in-memory database holds four refs:

>account

>transactions history

>violations history

>current violation

which works in a similar way to tradicional tables on a SQL database. 
The db namespace also holds the functions get!, patch!, post! and delete!

**get** enables querying via `deref`

**post** provides a way to create and store new resources

**patch** alters the content of a resource, in this case it is used to alter an account's limit

**delete** is used for the violation stashing process and deleting the mock account created during integration tests.

### **Violation stash**
Every violation is stored forever*, without exceptions. 

So to avoid an overhead treating which exceptions ocurred during each transaction, everytime a violation is detected it is **post** to the current violation ref and later, when our controller starts stiching the request answer it moves everything from the current violation to the violation history array.

##### *Forever being the lifetime of our service.