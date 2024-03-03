Assumtion 
- A **Trip** is identified by **company id** and **bus id**
- A Touch Off is identified by **company id ** and **bus id** and it's the latest after the data time of Touch On
- A Touch Off is not indetified by any Touch On that marked as invalid touch
- A Trip will be marked as unprocessable if there are different PAN between Touch On and Touch Off itselft
- 

How to Run
Java version: 18

mvn spring-boot:run


How to Test
