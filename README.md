
# **Xpense-Tracker**

Xpense Tracker is an application developed for tracking expenses of an user through his/her bank statements. User can
upload bank statement and provide mapping of the bank statement with the system required headers.
For eg: The application requires necessary information like transactiondate, debit, credit, closing balance, bank reference no, 
and closing balance. But the application isn't intelligent (as of now) to detect these fields from the bank statement, 
instead the user needs to explicitely tell which maps to these details.

Once this information is set, then the application can extract the required data from the statement and populate it for you.


## **Tech Stack**

The Application has a Springboot backend, React frontend and Postgresql Database.
Application is a monolith

### **How to run locally**

### Branch
`main` branch contains the completed features   
`develop` branch contains the development code - This should not be changed. All other branches should be merged to `develop` and ultimately to `main`

The Xpense-Tracker backend requires a Postgresql database to run with it. 
A docker-compose file is already available in the repository for running the postgresql database for you. To execute this,
you need to have docker-compose installed on your system.

**Start PostgreSQL**  

`cd docker/`  
`docker-compose up -d`

**Stop PostgreSQL**  

`docker-compose down`

If you have started the Postgresql server, then you can directly start up your application by running the Java class
`XpenseTrackerApplication` in path `src/main/java/com/xperia/xpense_tracker`


### How to Docker Deploy

Currently the application is deployed using [Render](www.render.com) for cost cutting purposes. 
A Dockerfile is already created with the required details.  
If you are deploying to new environment, follow the instructions

1. Go to Dockerfile
2. Update the Environment variables (Spring datasource) with the new details

### Flyway
We are using Flyway for the database migration. Flyway dependency is added in the pom.xml and also the plugin is configured for running this using maven
If there is a need to update the database schema, then add a new migration file `V{version_number}__{description}.sql` and then execute 
`mvn flyway:migrate`

1. Install flyway on your machine using the command `brew install flyway`
2. Execute the command `flyway -url=jdbc:postgresql://localhost:5432/xpense_tracker -user=xpense_admin -password=xpenseTracker1234 baseline`
