package fr.uphf.sae5a1api;

import fr.uphf.sae5a1api.data.HikariConnector;
import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

@SpringBootApplication
public class SAE5A1ApiApplication {

    @Getter
    private static HikariConnector hikariConnector;

    public static void main(String[] args) {
        System.out.println("Starting WEB-API...");

        System.out.println("Connecting to database...");
        try {
            hikariConnector = HikariConnector.create("87.106.121.50", "leswinners", "kelawin", "postgres", 5432);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            if(hikariConnector.getConnection() != null && !hikariConnector.getConnection().isClosed())
                System.out.println("Connected to database.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Starting Spring App...");
        SpringApplication.run(SAE5A1ApiApplication.class, args);
        System.out.println("Started Spring App.");
        System.out.println("Started WEB-API. Welcome on board!");
    }

}
