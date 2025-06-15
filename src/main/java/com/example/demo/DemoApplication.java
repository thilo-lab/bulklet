package com.example.demo;

import com.microsoft.sqlserver.jdbc.SQLServerBulkCopy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

    @Autowired
    private ConfigurableApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        executeSqlTasks();
        int exitCode = SpringApplication.exit(context);
        System.exit(exitCode);
    }

    /**
     * Executes all SQL tasks used by the demo application. Extracted for easier
     * testing.
     */
    void executeSqlTasks() throws Exception {
        String url = "jdbc:sqlserver://localhost:1433;encrypt=true;trustServerCertificate=true";
        Properties props = new Properties();
        props.put("user", "sa");
        props.put("password", "YourStrongPassword123");

        log.info("Connecting to SQL Server at localhost:1433");
        try (Connection sourceConn = DriverManager.getConnection(url, props);
             Connection targetConn = DriverManager.getConnection(url, props)) {

            try (Statement stmt = sourceConn.createStatement()) {
                log.info("Creating source table and inserting sample data");
                stmt.executeUpdate("IF OBJECT_ID('quelle', 'U') IS NOT NULL DROP TABLE quelle");
                stmt.executeUpdate("CREATE TABLE quelle (id INT IDENTITY(1,1) PRIMARY KEY, name NVARCHAR(100))");
                stmt.executeUpdate("INSERT INTO quelle (name) VALUES ('Alice'), ('Bob'), ('Charlie')");
            }

            try (Statement stmt = targetConn.createStatement()) {
                log.info("Creating target table");
                stmt.executeUpdate("IF OBJECT_ID('ziel', 'U') IS NOT NULL DROP TABLE ziel");
                stmt.executeUpdate("CREATE TABLE ziel (id INT IDENTITY(1,1) PRIMARY KEY, name NVARCHAR(100))");
            }

            try (Statement stmt = sourceConn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id, name FROM quelle")) {
                log.info("Copying data to target table using bulk API");
                SQLServerBulkCopy bulkCopy = new SQLServerBulkCopy(targetConn);
                bulkCopy.setDestinationTableName("ziel");
                bulkCopy.writeToServer(rs);
                bulkCopy.close();
            }
            log.info("SQL tasks completed");
        }
    }
}
