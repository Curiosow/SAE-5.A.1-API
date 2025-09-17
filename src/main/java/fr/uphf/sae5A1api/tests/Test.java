package fr.uphf.sae5A1api.tests;

import fr.uphf.sae5A1api.data.actions.ActionHandball;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Test {

    public static void main(String[] args) {
        CSVReading csvReading = new CSVReading();
        List<ActionHandball> data = csvReading.readActions("/Users/oscarbouttier/Documents/Cours/BUT3/SAE 5.A.1/R5A12-API/src/main/java/fr/uphf/sae5A1api/tests/stats.xlsx", "Données brutes");

        Map<String, Double> tauxReussiteParJoueuse = data.stream()
                .collect(Collectors.groupingBy(
                        ActionHandball::getJoueuse,
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            long tirs = list.stream().filter(a -> !a.getResultat().isEmpty()).count();
                            long buts = list.stream().filter(a -> "But".equalsIgnoreCase(a.getResultat())).count();
                            return tirs > 0 ? (buts * 100.0 / tirs) : 0.0;
                        })
                ));
        System.out.println(tauxReussiteParJoueuse);

        System.out.println(" -- ");
        Map<String, Long> repartitionResultats = data.stream()
                .collect(Collectors.groupingBy(ActionHandball::getResultat, Collectors.counting()));
        System.out.println(repartitionResultats);
        System.out.println(" -- ");

        Map<String, Double> tempsMoyenParJoueuse = data.stream()
                .collect(Collectors.groupingBy(
                        ActionHandball::getJoueuse,
                        Collectors.averagingDouble(ActionHandball::getDuree)
                ));
        System.out.println(tempsMoyenParJoueuse);
        System.out.println(" -- ");


    }

}
