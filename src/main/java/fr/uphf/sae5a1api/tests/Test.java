package fr.uphf.sae5a1api.tests;

import fr.uphf.sae5a1api.SAE5A1ApiApplication;
import fr.uphf.sae5a1api.data.actions.ActionHandball;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Test {

    public static void main(String[] args) {
        CSVReading csvReading = new CSVReading();
        List<ActionHandball> data = csvReading.readActions("/Users/oscarbouttier/Documents/Cours/BUT3/SAE 5.A.1/R5A12-API/src/main/java/fr/uphf/sae5A1api/tests/stats.xlsx", "Données brutes");

        /*Map<String, Double> tauxReussiteParJoueuse = data.stream()
                .collect(Collectors.groupingBy(
                        ActionHandball::getJoueuse,
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            long tirs = list.stream().filter(a -> !a.getResultat().isEmpty()).count();
                            long buts = list.stream().filter(a -> "But".equalsIgnoreCase(a.getResultat())).count();
                            return tirs > 0 ? (buts * 100.0 / tirs) : 0.0;
                        })
                ));

        SAE5A1ApiApplication.getLogger().log(Level.INFO, tauxReussiteParJoueuse + "");*/

        SAE5A1ApiApplication.getLogger().log(Level.INFO, " -- ");
        Map<String, Long> repartitionResultats = data.stream()
                .collect(Collectors.groupingBy(ActionHandball::getResultat, Collectors.counting()));
        SAE5A1ApiApplication.getLogger().log(Level.INFO, repartitionResultats + "");
        SAE5A1ApiApplication.getLogger().log(Level.INFO, " -- ");

        /*Map<String, Double> tempsMoyenParJoueuse = data.stream()
                .collect(Collectors.groupingBy(
                        ActionHandball::getJoueuse,
                        Collectors.averagingDouble(ActionHandball::getDuree)
                ));
        SAE5A1ApiApplication.getLogger().log(Level.INFO, tempsMoyenParJoueuse + "");
        */
        SAE5A1ApiApplication.getLogger().log(Level.INFO, " -- ");


    }

}
