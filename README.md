# Bulklet Demo Spring Boot Project

This repository contains a minimal Spring Boot command-line application. The project includes the Microsoft SQL Server JDBC driver as a dependency and demonstrates copying data between two tables using the SQL Server bulk API.

## Building and Running

Ensure you have Java 21 and Maven installed. The following commands install Java 21 and Maven on Debian/Ubuntu systems:

```bash
sudo apt-get update
sudo apt-get install -y openjdk-21-jdk
sudo apt-get install -y maven
```

After Maven is available, build the project with:

```bash
mvn package
```

Before running the application, ensure a SQL Server instance is accessible at `localhost` using the credentials `sa` / `YourStrongPassword123`.

To start the application, run:

```bash
java -jar target/demo-0.0.1-SNAPSHOT.jar
```
