mBuddy
======

Introduction
------------

mBuddy is standalone application that runs an embedded smtp server.  It is
designed to be used to support integration level testing scenarios for
systems with email components.

The emails sent sent by the integration environment can be captured and made
available to a RESTful or Web API (or in future forwarded on to a live account).

License
-------

mBuddy is licensed under the Apache License, version 2.0.  See the LICENSE
file for details.

Quick Start
-----------

todo


Building
--------

mBuddy is built using maven (http://maven.apache.org).  To build the application
run:

    $ mvn clean package

To execute the system test suite, run:

    $ mvn clean test -Psystem


Feedback
--------

Feedback and contributions are welcome!  Please contact:

daniel.ostermeier@gmail.com

or simply fork away!
