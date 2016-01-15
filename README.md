# ticket-service
ticket service component provides functionality to find available seats, to hold seats for a certain configurable period of time and reserves group of seats.

**Tech Stack**
```
Java 1.8
Maven 3.3.3
```

**Steps to build and run tests**
```
mvn clean install (install runs the Junits)
```

**Assumptions**
```
This implementation is not a distributed solution and assumed to execute the solution on a single JVM instance.
Seats will be on hold for 2 seconds and it is configurable.
```

