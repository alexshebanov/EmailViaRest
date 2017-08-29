# EmailViaRest
RESTful service, which handles orders for sending emails.

Techologies i used to develop this service:
Java 1.8,
Spring Boot,
Spring Data,
Spring Security,
Spring Mail,
Jackson,
MySQL Server.

I used Gmail as SMTP server and Amazon RDS as datasource.

In the table 'user' you can find user information and role.

In the table 'order_characteristics' you can find information about customers
and how many times they made orders.

I've enabled Spring Security and added some features for admin.
For example, by the '/refresh' GET request, admin can refresh current
service load. By the '/refresh-senders-limit?customer="customersname"' GET request admin
can refresh orders made by selected customer per last hour.
And by reaching '/set-volume?count="count"' GET request admin can change maximum
order volume for service (as was required).

[![Build Status](https://travis-ci.org/alexshebanov/EmailViaRest.svg?branch=master)](https://travis-ci.org/alexshebanov/EmailViaRest)
