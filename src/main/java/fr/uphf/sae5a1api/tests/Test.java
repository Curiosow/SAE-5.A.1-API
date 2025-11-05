package fr.uphf.sae5a1api.tests;

import fr.uphf.sae5a1api.data.impl.actions.ActionHandball;

import java.util.List;

public class Test {

    public static void main(String[] args) {
        CSVReading csvReading = new CSVReading();
        List<ActionHandball> data = csvReading.readActions("/Users/oscarbouttier/Documents/Cours/BUT3/SAE 5.A.1/R5A12-API/src/main/java/fr/uphf/sae5A1api/tests/stats2.csv", "Stats Bruts J1 ATH Sambre");
        for (ActionHandball datum : data) {
            System.out.println(datum);
        }
    }

}
