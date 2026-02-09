package fr.uphf.sae5a1api;

import fr.uphf.sae5a1api.data.HikariConnector;
import fr.uphf.sae5a1api.runnable.GetRankingTask;
import fr.uphf.sae5a1api.runnable.GetRencontreTask;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
public class SAE5A1ApiApplication implements CommandLineRunner {

    @Getter
    @Setter
    private static HikariConnector hikariConnector;

    @Getter
    private static final Logger logger = Logger.getLogger(SAE5A1ApiApplication.class.getName());

    @Getter
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Value("${db.host}")
    private String dbHost;

    @Value("${db.user}")
    private String dbUser;

    @Value("${db.password}")
    private String dbPassword;

    @Value("${db.name}")
    private String dbName;

    @Value("${db.port}")
    private int dbPort;

    public static void main(String[] args) {
        logger.log(Level.INFO, "Starting Spring App...");
        SpringApplication.run(SAE5A1ApiApplication.class, args);
        logger.log(Level.FINE, "Started Spring App.");
    }

    @Override
    public void run(String... args) {
        logger.log(Level.INFO, "Starting WEB-API...");

        logger.log(Level.INFO, "Connecting to database with configuration file...");
        try {
            hikariConnector = HikariConnector.create(dbHost, dbUser, dbPassword, dbName, dbPort);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création du HikariConnector", e);
        }

        try {
            if (hikariConnector.getConnection() != null && !hikariConnector.getConnection().isClosed()) {
                logger.log(Level.FINE, "Connected to database.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la vérification de la connexion à la base", e);
        }

        logger.log(Level.INFO, "Starting runnables");
        GetRankingTask rankingTask = new GetRankingTask();
        scheduler.scheduleAtFixedRate(rankingTask, 1, 60 * 12, TimeUnit.MINUTES);
        rankingTask.run();

        GetRencontreTask rencontreTask = new GetRencontreTask();
        scheduler.scheduleAtFixedRate(rencontreTask, 1, 60 * 30, TimeUnit.MINUTES);
        rencontreTask.run();
        logger.log(Level.FINE, "Started runnables.");

        logger.log(Level.FINE, "Started WEB-API. Welcome on board!");

        HttpClient client = HttpClient.newHttpClient();
    }

}
