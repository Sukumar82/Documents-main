3di Springboot Project
A brief, one-sentence description of what this Spring Boot application does.
Features
Spring Boot 3.x
Maven for dependency management
RESTful API endpoints
Unit testing with JUnit 5

Prerequisites
Java JDK (version 17 or higher recommended)
Maven 3.6+
Your favorite IDE (IntelliJ IDEA or Eclipse)
MySQL

Assessment Instructions

- Update the Accounts controller to display transactions between start and end date.
  This should be sent to the backend including the existing form submission data
  The start and end date to only load transactions where the initiation date is between the start and end date. The start and end dates can be inclusive of the data returned. E.g. if start date is 2019-11-04 then it should also include records which have the initiation date 2019-11-04.
  You will need to update the method in the transaction controller, transaction service and transaction repository and possibly and additional utility classes these methods use.

The backend will require a MySQL server. The resources folder contains the schema and data you will need to import before you run the application. You will only need to import schema.sql and can ignore data.sql.


Setup Instructions
1. IntelliJ IDEA
   Open Project: Launch IntelliJ and select File > Open. Navigate to the project folder and select the pom.xml file.
   Import as Project: When prompted, choose Open as Project. IntelliJ will automatically detect the Maven configuration and download dependencies.
   SDK Configuration: Ensure the Project SDK is set to your installed JDK (File > Project Structure > Project).
   Run: Find the main class (annotated with @SpringBootApplication), right-click the main method, and select Run 'Application'.

2. Eclipse (with Spring Tools 4)
   Import Project: Go to File > Import... > Maven > Existing Maven Projects.
   Select Directory: Browse to the root folder containing pom.xml, ensure the project is checked, and click Finish.
   Update Maven: If dependencies don't load, right-click the project and select Maven > Update Project....
   Run: Right-click the project or the main application class and select Run As > Spring Boot App (or Java Application)

