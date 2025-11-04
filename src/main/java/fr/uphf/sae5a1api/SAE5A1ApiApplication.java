package fr.uphf.sae5a1api;

import fr.uphf.sae5a1api.data.HikariConnector;
import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
public class SAE5A1ApiApplication {

    @Getter
    private static HikariConnector hikariConnector;

    @Getter
    private static final Logger logger = Logger.getLogger(SAE5A1ApiApplication.class.getName());

    public static void main(String[] args) {
        logger.log(Level.INFO, "Starting WEB-API...");

        logger.log(Level.INFO, "Connecting to database...");
        try {
            hikariConnector = HikariConnector.create("87.106.121.50", "leswinners", "kelawin", "postgres", 5432);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            if(hikariConnector.getConnection() != null && !hikariConnector.getConnection().isClosed())
                logger.log(Level.FINE, "Connected to database.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        logger.log(Level.INFO, "Starting Spring App...");
        SpringApplication.run(SAE5A1ApiApplication.class, args);
        logger.log(Level.FINE, "Started Spring App.");
        logger.log(Level.FINE, "Started WEB-API. Welcome on board!");
    }

}
