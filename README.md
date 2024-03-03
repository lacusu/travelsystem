Assumption 
- This is a Travel System which can be developed later on. The structure and codes allow for maintenance
- A **Trip** is identified by **company id** and **bus id**
- A Touch Off is identified by **company id ** and **bus id** and it's the latest after the date time of Touch On
- A Touch Off is not identified by any Touch On that is marked as invalid touch
- A Trip will be marked as unprocessable as PAN mismatched if there are different PANs between Touch On and Touch Off itself

How to Run
Apache Maven 3.6.3
Java version: 17

mvn spring-boot:run


How to Test
There is an example input file touchData.csv located at src/main/resources/datafiles/input/touchData.csv. We can modify this file to test more cases if needed
The out files will be located in src/main/resources/datafiles/out/
Steps 
1. Modify the input file: src/main/resources/datafiles/input/touchData.csv
2. Run the application
mvn spring-boot:run
3. Verify the output files: src/main/resources/datafiles/out/

Note: Input and Output file paths can be modified in src/main/resources/application.yml 

Solution Overall:
From the data in the file I preprocess each input data field for modeling it and persist into a kind of DB and I am using H2 DB as in-memory storage which can replace keeping data in Java variables or objects. And with this Database Storage then I can take advantage of HQL for access and collecting data as required. Every major implementation focuses on Database entity then we can replace the H2 with MySQL or any relational DBs for later on
Decoupling the logic to multiple service classes can be easy to change, develop, and replace if needed
Provided Unit Tests for major functions 

What I am using
1. Sprint Boot 3x which is compatible with Java 17
2. Project Lombok
   Project Lombok is a Java library that automatically plugs into your editor and builds tools, spicing up your Java.
Never write another getter or equals method again, with one annotation your class has a fully featured builder, Automates your logging variables, and much more.
4. Spring Data
  	Its purpose is to unify and easy access to the different kinds of persistence stores, both relational database systems and NoSQL data stores.
5. liquibase
   database schema change management solution which enables you to manage revisions of your database changes easily
7. h2database
   H2 is an open-source lightweight Java database. It can be embedded in Java applications or run in the client-server mode. Mainly, the H2 database can be configured to run as an in-memory database, which means that data will not persist on the disk
9. mockito
    Tasty mocking framework for unit tests in Java
