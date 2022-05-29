# MyPark – A Parking Reservation System (SoPra Group 03 FS22)


## Introduction

To date, there is no uniform system for reservation and digital check-in in parking garages in Zurich.
We have all been to Zurich before and were looking for a parking space. On the one hand, it was unknown
where exactly the next parking garage was, and on the other hand, it was often already full when you
wanted to check in. In addition, the search for an empty parking space not only strains the nerves of motorists
but also leads to increased traffic congestion and ecologically undesirable pollution.
<br />

Our application - called **MyPark** - tackles this problem by providing a web-based user interface that allows registered users to see
in real-time which car parks have free parking spaces. These car parks can be explored either on an interactive map or
in tabular form. MyPark not only allows users to check-in into a car park (and thereby avoiding queuing) but also
to reserve a parking space in advance at the car park of their choice.
<br />

The necessary data for this project is provided by [Parkleitsystem Stadt Zürich (PLS)](https://www.pls-zh.ch/info.jsp) and can
be accessed via an [RSS feed](https://www.pls-zh.ch/plsFeed/rss). PLS tracks the empty parking spaces in the city of Zurich and
updates the number of empty parking spaces in real-time. In addition, we use Leaflet (OpenStreetMap) API
to show the car parkings on an interactive map. 

## Technologies

For the backend, this project used Java with the following frameworks:

- Spring Boot
- Gradle
- JPA
- Heroku
- SonarCube

## High-level Components

- [`CarparkService`](https://github.com/sopra-fs22-group-03/sopra-fs22-group-03-server/blob/master/src/main/java/ch/uzh/ifi/hase/soprafs22/service/CarparkService.java):
    This class has several important responsibilities. First, it handles the parsing of the external RSS data flow and
    storage of this information to our internal database. Second, this class is responsible as a worker for the Check-In
    and Check-out functionality of our application. Third, it serves as a worker for several functionalities related to
    the carparks.
- [`NotificationService`](https://github.com/sopra-fs22-group-03/sopra-fs22-group-03-server/blob/master/src/main/java/ch/uzh/ifi/hase/soprafs22/service/NotificationService.java):
    This class implements our real-time collaborative feature. It creates new notifications and handles the corresponding
    response sent by the other user.
- [`BillingController`](https://github.com/sopra-fs22-group-03/sopra-fs22-group-03-server/blob/master/src/main/java/ch/uzh/ifi/hase/soprafs22/controller/BillingController.java):
    This class is responsible to handle all incoming requests related to the billings. Among other things, this class is
    thus responsible for the "business logic" part of the real-time collaborative feature: it handles the incoming split
    request of one user and distributes the work do be done to the various service classes (e.g., to the NotificationService
    class mentioned above).

## Launch & Deployment

### Getting started with Spring Boot

-   Documentation: https://docs.spring.io/spring-boot/docs/current/reference/html/index.html
-   Guides: http://spring.io/guides

### Building with Gradle

You can use the local Gradle Wrapper to build the application.

-   macOS: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

#### Build

```bash
./gradlew build
```

#### Run

```bash
./gradlew bootRun
```

#### Test

```bash
./gradlew test
```

#### Development Mode

You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed and you save the file.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

### API Endpoint Testing

Upon request, developers can be added to the existing [Postman](https://www.getpostman.com) workspace of this project. Therein, many API tests for
the endpoints of this application are already written. Future developers can build upon these existing tests.


## Roadmap

- Establish a connection of this prototype to the city of Zurich / to the actual carparks
- Implement a real payment system (most probably also including Twint as a payment method)
- Develop a mobile application such that users can easily check-in into carparks via mobile app

## Authors & Acknowledgements

-   [Noah Mamie](https://github.com/nmamie)
-   [Elias Schuhmacher](https://github.com/e-schuh)
-   [Kilian Sennrich](https://github.com/ksennr)
-   [Richard Specker](https://github.com/rspecker)

## License

[Apache License, 2.0](./LICENSE)

