package fr.uphf.sae5A1api.data.executor;

import fr.uphf.sae5A1api.data.HikariConnector;

import java.sql.Connection;

/**
 * Class to execute SQL request
 */
public class DatabaseExecutor {

    /**
     * Execute a SQL query that return a value
     *
     * @param executor Executor
     * @param <T>      Type of value desired
     * @return Instance of type desired obtained by the request
     */
    public static <T> T executeQuery(HikariConnector connector, QueryExecutor<T> executor) {
        T value = null;
        try (Connection connection = connector.getConnection()) {
            value = executor.perform(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static void executeVoidQuery(HikariConnector connector, QueryVoidExecutor executor) {
        try (Connection connection = connector.getConnection()) {
            executor.perform(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

