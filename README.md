# Travel System

## Assumptions
- This Travel System is designed with flexibility for future development and maintenance.
- A **Trip** is uniquely identified by the combination of **company id** and **bus id** and a pair of Touch On and Touch Off.
- A Touch Off event is identified by the combination of **company id** and **bus id**, occurring after the timestamp of the corresponding Touch On event.
- Touch Off events are not associated with any Touch On events then it's invalid data
- A Trip will be flagged as unprocessable if there is a mismatch in Primary Account Numbers (PAN) between the Touch On and Touch Off events.

## How to Run
- **Requirements:** Apache Maven 3.6.3, Java version 17
- Run the application using Maven:
 ```
   mvn spring-boot:run
   ```

## How to Test
- An example input file, `touchData.csv`, is provided at `src/main/resources/datafiles/input/touchData.csv`. Modify this file to test additional cases if needed.

  **touchData.csv**

  ![image](https://github.com/lacusu/travelsystem/assets/7995583/325e9ad5-b19f-4023-bc00-1c1ff08250e3)

- Output files will be generated in `src/main/resources/datafiles/out/`.

  **trips.csv**

  ![image](https://github.com/lacusu/travelsystem/assets/7995583/dc2e3fbc-c30f-42d3-a407-66fc013f5674)

  **tripSummary.csv**

  ![image](https://github.com/lacusu/travelsystem/assets/7995583/a3914470-6af6-4eb3-bcfb-75288c12b051)

  **unprocessableTouchData.csv**

  ![image](https://github.com/lacusu/travelsystem/assets/7995583/300b479f-5d48-45b4-8da6-3c30d3ff2c77)


- Follow these steps:
1. Modify the input file: `src/main/resources/datafiles/input/touchData.csv`.
2. Run the application:
   ```
   mvn spring-boot:run
   ```
3. Verify the output files: `src/main/resources/datafiles/out/`.

_Note: Input and output file paths can be configured in `src/main/resources/application.yml`._
## Solution Overview
The solution preprocesses each input data field for modeling and persists it into an in-memory database (H2 DB). This approach replaces storing data in Java variables or objects, providing efficient storage and retrieval capabilities. By leveraging HQL (Hibernate Query Language), the application can easily access and collect data as required. 
The architecture emphasizes decoupling the logic into multiple service classes, facilitating ease of change, development, and replacement when needed. 
Additionally, comprehensive unit tests have been provided for major functions.

## Technologies Used
1. **Spring Boot 3.x**: Compatible with Java 17, offering streamlined development and configuration.
2. **Project Lombok**: Automates repetitive Java boilerplate tasks, improving code readability and conciseness.
3. **Spring Data**: Simplifies access to various persistence stores, including relational databases.
4. **Liquibase**: Manages database schema changes, providing version control and seamless migration.
5. **H2 Database**: H2 is an open-source lightweight Java database. It can be embedded in Java applications or run in the client-server mode. Mainly, the H2 database can be configured to run as an in-memory database, which means that data will not persist on the disk
6. **Mockito**: Mocking framework for unit testing in Java, facilitating the creation of test doubles and verifying interactions.
