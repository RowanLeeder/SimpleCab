# SimpleCab

Written for a code test.

> Cab Data Researcher is a company that provides insights on the open data about NY cab trips
> Cab trips in NY are public available as csv downloadable files. In order to make it more useful we want to wrap the data in a public API.
> 
> Data format is as follow:
>     medallion, 
>     hack_license, 
>     vendor_id, 
>     rate_code, 
>     store_and_fwd_flag, 
>     pickup_datetime, 
>     dropoff_datetime, 
>     passenger_count, 
>     trip_time_in_secs, 
>     trip_distance
>     
> The medallion is the cab identification
> 
> Our API should provide a way to query how many trips a particular cab (medallion) has made given a particular pickup date ( using pickup_datetime and only considering the date part)
> 
> The API must receive one or more medallions and return how many trips each medallion has made.
> Considering that the query creates a heavy load on the database, the results must be cached.
> The API must allow user to ask for fresh data, ignoring the cache. There must be also be a method to clear the cache.


## Prerequisites

Java 8, Maven and a running MySQL server with write access.

## Setup

Initialise the database:

```
unzip ny_cab_data_cab_trip_data_full.sql.zip
mysql -e 'create database ny_cab_data'
mysql ny_cab_data < ny_cab_data_cab_trip_data_full.sql
```

Set the MySQL user details in ```server/src/main/resources/application.properties```

```
spring.datasource.username=...
spring.datasource.password=...
```

Then build the packages.

```
mvn package
```

## Server

Start the server: ```java -jar server/target/server-1.0-SNAPSHOT.jar```

## Client

Use the client to communicate with the server. Passing `-h` or `--help` will display the help documentation. 

The default host is `http://localhost:8080`, which can be changed via the `--host` argument.

Cab trip count example:
```
java -jar client/target/client-1.0-SNAPSHOT.jar counts 2013-12-30 84F1B1B17DA76D79A1C908AD330D97B8 00FD1D146C1899CEDB738490659CAD30
{
  "00FD1D146C1899CEDB738490659CAD30": 27,
  "84F1B1B17DA76D79A1C908AD330D97B8": 24
}
```

Raise the `--ignore-cache` flag after `counts` to force the server to respond with fresh data

To clear the entire cache, use `java -jar client/target/client-1.0-SNAPSHOT.jar clear-cache` 


## Running the tests

JUnit tests can be run via `mvn test`

