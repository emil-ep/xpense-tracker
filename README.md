
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
3. Also update the ENVIRONMENT VARIABLES of backend service in Render with the updated values of SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME, SPRING_DATASOURCE_PASSWORD

If any of the above steps are not done, you are fucked when updating the database

### Flyway
We are using Flyway for the database migration. You can install flyway CLI using `brew install flyway`
If there is a need to update the database schema, then add a new migration file `V{version_number}__{description}.sql` and then execute 
go to the directory `db-migrations` and execute `flyway repair migrate -configFiles=./dev/dev.conf -locations=filesystem:./`

### Prometheus

Setting up prometheus - https://medium.com/simform-engineering/revolutionize-monitoring-empowering-spring-boot-applications-with-prometheus-and-grafana-e99c5c7248cf#id_token=eyJhbGciOiJSUzI1NiIsImtpZCI6ImJhYTY0ZWZjMTNlZjIzNmJlOTIxZjkyMmUzYTY3Y2M5OTQxNWRiOWIiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiIyMTYyOTYwMzU4MzQtazFrNnFlMDYwczJ0cDJhMmphbTRsamRjbXMwMHN0dGcuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiIyMTYyOTYwMzU4MzQtazFrNnFlMDYwczJ0cDJhMmphbTRsamRjbXMwMHN0dGcuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDczNjUxNzIyMDY1NjI4MTA2MjEiLCJlbWFpbCI6ImVtaWxwcmFkZWVwQGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYmYiOjE3NDgxNzcxODQsIm5hbWUiOiJFbWlsIHByYWRlZXAiLCJwaWN0dXJlIjoiaHR0cHM6Ly9saDMuZ29vZ2xldXNlcmNvbnRlbnQuY29tL2EvQUNnOG9jSUcxTjNlRXVhb0RyQjN0czhJeFlvN0E4TG9fUTYwY3JwLUlzZUdWaTdyQVJ4UW9naXpVQT1zOTYtYyIsImdpdmVuX25hbWUiOiJFbWlsIiwiZmFtaWx5X25hbWUiOiJwcmFkZWVwIiwiaWF0IjoxNzQ4MTc3NDg0LCJleHAiOjE3NDgxODEwODQsImp0aSI6IjA3NGM5MjY2Nzg3OGRhYWI3NGRhOWU5NGExNTNlYzcyZmQyNmY3ZGMifQ.gCIZfqttc5j0-lXe0-2B_lajPNzrxRiB6Yk-BHiR2HTlke0JySxRg-fsBie51EgzhhIQN-8N-3P3L-eEH5l2p-FVURjjTMubsdMTJmHO4gcNp416GADRlyHjcA6CNq3ZJHe0c6jLuvd_g04aKXoV19l5-oeR8QHpO-mRIdz3ZfJArALGV9Q9SGpMgqGKS1h8PHbGs-MllTbG33wPPv5UbiBmD_lMygHZXjpgDmBwOxFNK9DSj1j91TkXhVXYW-eNxJ02IjIGd-04Ish59APkVX_qpdYUm0gW8OMq9DAZ0IaLEcc7plGQwPCKcJPVW4S2THz0abhYYlqCwB7AjIg9IQ

### Running prometheus and Grafana for your application

The application exposes actuator endpoints and we can make use of Prometheus and Grafana to visualise application metrics<br>
In order to run prometheus and Grafana, you can navigate to `monitoring` directory and execute<br>

`docker-compose up`

Prometheus will be available at endpoint http://localhost:9090 <br>
Grafana will be available at endpoint http://localhost:3002 <br>


### Kafka & Zookeeper

Setting up kafka and zookeeper - https://medium.com/@erkndmrl/kafka-cluster-with-docker-compose-5864d50f677e

The Xpense-Scheduler application requires Kafka to exchange messages with the consumers. For running and setting Kafka 
on your machine, you need to run the `docker-compose up` inside the folder `docker/kafka`. 
This will setup Kafka, Zookeeper and kafka-ui. <br>

You can view the kafka-ui using http://localhost:8087 <br>

# LATEST UPDATE!

Application now supports .xls format in raw. You don't have to remove any junk data from the file. Application now 
intelligently parses data from rows and columns. This saves a lot of time of the user. 
Also application with AI intelligence matches the statement headers with the application header, which is a huge 
step in automation

# COMING SOON!







