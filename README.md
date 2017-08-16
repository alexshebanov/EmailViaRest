# EmailViaRest
RESTful service, which handles orders for sending emails.

Techologies i used to develop this service:
Java 1.8
Spring Boot
Spring Data
Spring Security
Spring Mail
Jackson
MySQL Server

WARNING! For succesful building and start the app you should firstly
configure \src\main\resources\application.properties file. Because it contains
information of smtp host and credentials. During testing i used my gmail credentials.
As this applicaton uses database, you should configure db connection properties in
the same file. Dump of my database is located in the same directory.
Without props build on travis will fail.

In the table 'user' you can find user information and role.

In the table 'order_characteristics' you can find information about customers
and how many times they made orders.

I've enabled Spring Security and added some features for admin.
For example, by the '/refresh' GET request, admin can refresh current
service load.

https://travis-ci.org/alexshebanov/EmailViaRest.svg?branch=master
