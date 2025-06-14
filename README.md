# Bulklet Demo Spring Boot Project

This repository contains a minimal Maven-based Spring Boot application. The project includes the Microsoft SQL Server JDBC driver as a dependency.

## Building and Running

Ensure you have Java 17 and Maven installed. The following commands install Java 17 and Maven on Debian/Ubuntu systems:

```bash
sudo apt-get update
sudo apt-get install -y openjdk-17-jdk
sudo apt-get install -y maven
```

After Maven is available, build the project with:

```bash
mvn package
```

To start the application, run:

```bash
mvn spring-boot:run
```
