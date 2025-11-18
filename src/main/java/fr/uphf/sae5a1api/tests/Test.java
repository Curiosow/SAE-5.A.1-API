package fr.uphf.sae5a1api.tests;

import fr.uphf.sae5a1api.SAE5A1ApiApplication;
import fr.uphf.sae5a1api.data.HikariConnector;
import fr.uphf.sae5a1api.data.impl.actions.ActionHandball;
import fr.uphf.sae5a1api.data.sql.managers.actions.ActionManager;

import java.util.List;
import java.util.UUID;

public class Test {

    public static void main(String[] args) {
        CSVReading csvReading = new CSVReading();
        List<ActionHandball> data = csvReading.readActions("/Users/oscarbouttier/Documents/Cours/BUT3/SAE 5.A.1/R5A12-API/src/main/java/fr/uphf/sae5A1api/tests/stats3.csv", "stats3");

        try {
            SAE5A1ApiApplication.setHikariConnector(HikariConnector.create("87.106.121.50", "leswinners", "kelawin", "postgres", 5432));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (ActionHandball datum : data) {
            ActionManager.saveAction(datum);
        }
    }

}
