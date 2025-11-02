package fr.uphf.sae5a1api.data.sql.managers.data; // (Adaptez le package)

import fr.uphf.sae5a1api.data.HikariConnector;
import fr.uphf.sae5a1api.data.sql.executor.DatabaseExecutor;
import fr.uphf.sae5a1api.data.actions.ActionHandball; // (Importez votre POJO corrigé)

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Gère la logique BDD pour les actions de match (style UserManager).
 */
public class HandballDataManager {

    // --- TABLES (style UserManager) ---
    public static final String EVENT_TABLE = "evenements";
    public static final String PLAYER_TABLE = "players";

    // --- REQUESTS (style UserManager) ---
    public static final String INSERT_EVENT = "INSERT INTO " + EVENT_TABLE +
            " (match_id, joueur_id, timestamp_ms, duree_action_ms, categorie_action, resultat, " +
            " secteur, contexte_defense, detail_action, detail_enclenchement) " +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String FIND_PLAYER_BY_NAME = "SELECT id FROM " + PLAYER_TABLE +
            " WHERE nom_csv = ? AND team_id = ?"; // AJOUTÉ: AND team_id = ?

    // Requête pour la nouvelle méthode UPSERT
    public static final String INSERT_PLAYER = "INSERT INTO " + PLAYER_TABLE +
            " (id, team_id, first_name, last_name, nom_csv, jersey_number, weight_kg, height_cm, is_active, created_at, updated_at) " +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

    public static final String UPDATE_PLAYER = "UPDATE " + PLAYER_TABLE +
            " SET jersey_number = ?, weight_kg = ?, height_cm = ?, updated_at = CURRENT_TIMESTAMP " +
            " WHERE id = ?";


    /**
     * Importe une liste d'actions en batch (rapide).
     * C'est l'équivalent de votre createPlayer, mais pour une liste.
     */
    public static void importActionsEnBatch(List<ActionHandball> actions) {
        if (actions == null || actions.isEmpty()) {
            return;
        }

        // On utilise executeVoidQuery comme dans UserManager
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            connection.setAutoCommit(false); // Mode transaction

            try (PreparedStatement statement = connection.prepareStatement(INSERT_EVENT)) {

                for (ActionHandball action : actions) {
                    // On définit les "?" comme dans createPlayer
                    statement.setObject(1, action.getMatchId());
                    statement.setObject(2, action.getJoueurId()); // setObject gère bien les null
                    statement.setObject(3, action.getTimestampMs());
                    statement.setObject(4, action.getDureeActionMs());
                    statement.setString(5, action.getCategorieAction());
                    statement.setString(6, action.getResultat());
                    statement.setString(7, action.getSecteur());
                    statement.setString(8, action.getContexteDefense());
                    statement.setString(9, action.getDetailAction());
                    statement.setString(10, action.getDetailEnclenchement());

                    statement.addBatch(); // Ajoute à la file d'attente
                }

                statement.executeBatch(); // Exécute tout d'un coup
                connection.commit(); // Valide la transaction

            } catch (SQLException e) {
                connection.rollback(); // Annule en cas d'erreur
                throw e;
            } finally {
                connection.setAutoCommit(true); // Réactive l'auto-commit
            }
        });
    }

    /**
     * Outil pour trouver l'UUID d'un joueur (style buildCoach).
     * Doit être appelé à l'intérieur d'une requête DatabaseExecutor
     * car il a besoin d'une connexion active.
     */
    public static UUID findJoueurId(Connection conn, String nomJoueur, UUID teamId) throws SQLException {
        if (nomJoueur == null || nomJoueur.isEmpty() || teamId == null) { // AJOUTÉ: teamId null check
            return null;
        }

        List<UUID> foundIds = new ArrayList<>();
        // On utilise la template FIND_PLAYER_BY_NAME (maintenant mise à jour)
        try (PreparedStatement statement = conn.prepareStatement(FIND_PLAYER_BY_NAME)) {
            statement.setString(1, nomJoueur);
            statement.setObject(2, teamId); // AJOUTÉ: paramètre teamId

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) { // Boucle au cas où il y aurait des doublons
                    foundIds.add((UUID) rs.getObject("id"));
                }
            }
        }

        // Gérer les cas
        if (foundIds.size() == 1) {
            return foundIds.get(0); // Cas idéal
        } else {
            if (foundIds.size() > 1) System.err.println("Ambiguité: Plusieurs joueurs trouvés pour '" + nomJoueur + "'.");
            else System.err.println("Non trouvé: Aucun joueur pour '" + nomJoueur + "' dans l'équipe " + teamId); // Message mis à jour
            return null;
        }
    }

    /**
     * NOUVELLE MÉTHODE: Adapte la logique de main.go (UPSERT) pour une ligne CSV.
     * Cette méthode doit être appelée DANS UNE TRANSACTION gérée par l'appelant
     * (par exemple, le service qui lit le fichier CSV).
     *
     * @param conn Connexion SQL transactionnelle
     * @param teamId L'UUID de l'équipe
     * @param nomCsv Le nom complet du CSV (ex: "Jean Dupont")
     * @param jerseyNumber Le numéro de maillot
     * @param weightKg Le poids
     * @param heightCm La taille
     * @return true si un joueur a été créé, false s'il a été mis à jour (ou erreur)
     * @throws SQLException Si la requête échoue
     */
    public static boolean upsertPlayerFromCsv(Connection conn, UUID teamId, String nomCsv, int jerseyNumber, int weightKg, int heightCm) throws SQLException {

        // 1. CHERCHER le joueur (logique de main.go)
        // On réutilise la méthode findJoueurId mise à jour
        UUID existingPlayerId = findJoueurId(conn, nomCsv, teamId);

        if (existingPlayerId == null) {
            // --- 2. CRÉER le joueur (s'il n'existe pas) ---

            // Gérer la séparation prénom/nom
            String firstName, lastName;
            String[] parts = nomCsv.split(" ", 2);
            firstName = parts[0];
            lastName = (parts.length > 1) ? parts[1] : ""; // Ou une valeur par défaut

            try (PreparedStatement statement = conn.prepareStatement(INSERT_PLAYER)) {
                statement.setObject(1, UUID.randomUUID()); // Générer un nouvel ID
                statement.setObject(2, teamId);
                statement.setString(3, firstName);
                statement.setString(4, lastName);
                statement.setString(5, nomCsv);
                statement.setInt(6, jerseyNumber);
                statement.setInt(7, weightKg);
                statement.setInt(8, heightCm);

                statement.executeUpdate();
                return true; // Créé
            }

        } else {
            // --- 3. METTRE À JOUR le joueur (s'il existe) ---
            try (PreparedStatement statement = conn.prepareStatement(UPDATE_PLAYER)) {
                statement.setInt(1, jerseyNumber);
                statement.setInt(2, weightKg);
                statement.setInt(3, heightCm);
                statement.setObject(4, existingPlayerId); // L'ID trouvé

                statement.executeUpdate();
                return false; // Mis à jour
            }
        }
    }


    /**
     * Récupère les données pour la heatmap (style login).
     */
    public static List<Map<String, Object>> getHeatmap(UUID matchId, UUID joueurId) {

        // On construit la requête dynamiquement car joueurId est optionnel
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT secteur, COUNT(*) AS total FROM ");
        sql.append(EVENT_TABLE);
        sql.append(" WHERE match_id = ? AND secteur IS NOT NULL AND secteur != '' ");
        sql.append(" AND resultat IN ('But', 'Ar GB', 'Arret NC', 'HC', 'tir raté NC') "); // Que les tirs

        if (joueurId != null) sql.append("AND joueur_id = ? ");
        sql.append("GROUP BY secteur");

        // On utilise executeQuery comme dans UserManager.login
        return DatabaseExecutor.executeQuery(HikariConnector.get(), connection -> {
            List<Map<String, Object>> results = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {

                statement.setObject(1, matchId);
                if (joueurId != null) statement.setObject(2, joueurId);

                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        // On "build" la ligne de résultat
                        Map<String, Object> row = new HashMap<>();
                        row.put("secteur", rs.getString("secteur"));
                        row.put("total", rs.getInt("total"));
                        results.add(row);
                    }
                }
            }
            return results;
        });
    }

    /**
     * Récupère les stats résumées (style login).
     */
    public static Map<String, Object> getSummaryStats(UUID matchId, UUID joueurId) {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("  COUNT(*) AS total_actions, ");
        sql.append("  SUM(CASE WHEN resultat = 'But' THEN 1 ELSE 0 END) AS total_buts, ");
        sql.append("  SUM(CASE WHEN resultat = 'PDB' THEN 1 ELSE 0 END) AS total_pertes_de_balle, ");
        sql.append("  SUM(CASE WHEN resultat = 'Ar GB' THEN 1 ELSE 0 END) AS total_arrets_gb ");
        // ... ajoutez d'autres SUM(...) AS ... ici ...
        sql.append("FROM ");
        sql.append(EVENT_TABLE);
        sql.append(" WHERE match_id = ? ");

        if (joueurId != null) sql.append("AND joueur_id = ? ");

        return DatabaseExecutor.executeQuery(HikariConnector.get(), connection -> {
            try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {

                statement.setObject(1, matchId);
                if (joueurId != null) statement.setObject(2, joueurId);

                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        // On "build" le résultat (style buildCoach)
                        Map<String, Object> stats = new HashMap<>();
                        stats.put("total_actions", rs.getInt("total_actions"));
                        stats.put("total_buts", rs.getInt("total_buts"));
                        stats.put("total_pertes_de_balle", rs.getInt("total_pertes_de_balle"));
                        stats.put("total_arrets_gb", rs.getInt("total_arrets_gb"));
                        // ... récupérez vos autres stats ici ...
                        return stats;
                    }
                }
            }
            return null; // ou une Map vide
        });
    }

    public static Map<String, Object> getPlayerProfileStats(UUID joueurId, UUID matchId) {

        // --- CORRECTION ---
        // Liste des tirs EXACTEMENT comme votre guide
        final String LISTE_TIRS = "('But', 'Ar GB', 'HC', 'Arret NC', 'tir raté NC')";

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");

        // --- 1. Stats Clés (Basées sur votre guide) ---
        sql.append("  SUM(CASE WHEN resultat = 'But' THEN 1 ELSE 0 END) AS total_buts, ");
        // Basé sur evenements.csv, 'Passe D' est stocké dans detail_action
        sql.append("  SUM(CASE WHEN detail_action = 'Passe D' THEN 1 ELSE 0 END) AS total_passes_d, ");
        sql.append("  SUM(CASE WHEN resultat = 'PDB' THEN 1 ELSE 0 END) AS total_pdb, ");
        sql.append("  SUM(CASE WHEN resultat IN " + LISTE_TIRS + " THEN 1 ELSE 0 END) AS total_tirs, ");

        // --- 2. Détail Attaque (Basé sur votre guide) ---
        sql.append("  SUM(CASE WHEN resultat IN ('Ar GB', 'Arret NC') THEN 1 ELSE 0 END) AS tirs_arretes, ");
        sql.append("  SUM(CASE WHEN resultat IN ('HC', 'tir raté NC') THEN 1 ELSE 0 END) AS tirs_rates, ");

        // --- Efficacité par Zone (Buts) ---
        // (La logique 7m est basée sur 'secteur' car 'Jets de 7m' n'est pas dans evenements.csv)
        sql.append("  SUM(CASE WHEN resultat = 'But' AND secteur = '6m' THEN 1 ELSE 0 END) AS buts_6m, ");
        sql.append("  SUM(CASE WHEN resultat = 'But' AND secteur LIKE '9m%' THEN 1 ELSE 0 END) AS buts_9m, ");
        sql.append("  SUM(CASE WHEN resultat = 'But' AND secteur IN ('ALG', 'ALD') THEN 1 ELSE 0 END) AS buts_aile, ");
        sql.append("  SUM(CASE WHEN resultat = 'But' AND secteur = '7m' THEN 1 ELSE 0 END) AS buts_7m, ");

        // --- Efficacité par Zone (Total Tirs) ---
        sql.append("  SUM(CASE WHEN resultat IN " + LISTE_TIRS + " AND secteur = '6m' THEN 1 ELSE 0 END) AS tirs_6m, ");
        sql.append("  SUM(CASE WHEN resultat IN " + LISTE_TIRS + " AND secteur LIKE '9m%' THEN 1 ELSE 0 END) AS tirs_9m, ");
        sql.append("  SUM(CASE WHEN resultat IN " + LISTE_TIRS + " AND secteur IN ('ALG', 'ALD') THEN 1 ELSE 0 END) AS tirs_aile, ");
        // (Logique 7m basée sur 'secteur')
        sql.append("  SUM(CASE WHEN resultat IN " + LISTE_TIRS + " AND secteur = '7m' THEN 1 ELSE 0 END) AS tirs_7m, ");

        // --- 3. Défense & Impact (Basé sur votre guide et evenements.csv) ---
        // (Toutes ces valeurs sont stockées dans detail_action par l'importateur)
        sql.append("  SUM(CASE WHEN detail_action = 'recup +' THEN 1 ELSE 0 END) AS total_recup, ");
        sql.append("  SUM(CASE WHEN detail_action = 'duel gagné' THEN 1 ELSE 0 END) AS total_duels_gagnes, ");
        sql.append("  SUM(CASE WHEN detail_action = 'Neut contre' THEN 1 ELSE 0 END) AS total_contres, ");
        sql.append("  SUM(CASE WHEN detail_action = 'Duel perdu' THEN 1 ELSE 0 END) AS total_duels_perdus, ");

        // --- CORRECTION: Utilisation des termes de votre liste ---
        sql.append("  SUM(CASE WHEN detail_action IN ('PF provoqué', 'PF Mauvais bloc') THEN 1 ELSE 0 END) AS total_fautes, ");
        sql.append("  SUM(CASE WHEN detail_action = 'Sanction 2 ou R' THEN 1 ELSE 0 END) AS total_sanctions_2min ");
        // --- FIN CORRECTION ---

        sql.append("FROM ").append(EVENT_TABLE);
        sql.append(" WHERE joueur_id = ? "); // Joueur est obligatoire

        if (matchId != null) {
            sql.append("AND match_id = ? "); // Filtre par match si fourni
        }

        // Exécution de la requête (style login)
        return DatabaseExecutor.executeQuery(HikariConnector.get(), connection -> {
            try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {

                statement.setObject(1, joueurId);
                if (matchId != null) {
                    statement.setObject(2, matchId);
                }

                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        // Map pour stocker les résultats bruts et calculés
                        Map<String, Object> stats = new HashMap<>();

                        // --- Récupération des données brutes ---
                        long totalButs = rs.getLong("total_buts");
                        long totalPassesD = rs.getLong("total_passes_d");
                        long totalPdb = rs.getLong("total_pdb");
                        long totalTirs = rs.getLong("total_tirs");

                        long tirs6m = rs.getLong("tirs_6m");
                        long buts6m = rs.getLong("buts_6m");
                        long tirs9m = rs.getLong("tirs_9m");
                        long buts9m = rs.getLong("buts_9m");
                        long tirsAile = rs.getLong("tirs_aile");
                        long butsAile = rs.getLong("buts_aile");
                        long tirs7m = rs.getLong("tirs_7m");
                        long buts7m = rs.getLong("buts_7m");

                        // --- 1. Stats Clés (avec calculs Java) ---
                        stats.put("total_buts", totalButs);
                        stats.put("total_passes_decisives", totalPassesD);
                        stats.put("total_balles_perdues", totalPdb);
                        // Efficacité au tir (%)
                        stats.put("efficacite_tir_pct", (totalTirs > 0) ? (100.0 * totalButs / totalTirs) : 0.0);

                        // --- 2. Détail Attaque ---
                        stats.put("buts_sur_tirs_ratio_brut", String.format("%d / %d", totalButs, totalTirs));
                        // --- CORRECTION ---
                        stats.put("tirs_arretes", rs.getLong("tirs_arretes"));
                        stats.put("tirs_rates", rs.getLong("tirs_rates"));

                        // Efficacité par zone
                        stats.put("efficacite_6m_pct", (tirs6m > 0) ? (100.0 * buts6m / tirs6m) : 0.0);
                        stats.put("efficacite_9m_pct", (tirs9m > 0) ? (100.0 * buts9m / tirs9m) : 0.0);
                        stats.put("efficacite_aile_pct", (tirsAile > 0) ? (100.0 * butsAile / tirsAile) : 0.0);
                        stats.put("efficacite_7m_pct", (tirs7m > 0) ? (100.0 * buts7m / tirs7m) : 0.0);

                        // Ratio "Passeur"
                        stats.put("ratio_passeur", (totalPdb > 0) ? ((double) totalPassesD / totalPdb) : (totalPassesD > 0 ? 999.0 : 0.0)); // 999 pour "infini"

                        // --- 3. Défense & Impact (CORRIGÉ) ---
                        long defPos = rs.getLong("total_recup") + rs.getLong("total_contres") + rs.getLong("total_duels_gagnes");
                        // --- AJOUT de duel perdu ---
                        long defNeg = rs.getLong("total_fautes") + rs.getLong("total_sanctions_2min") + rs.getLong("total_duels_perdus");

                        stats.put("total_actions_def_positives", defPos);
                        stats.put("total_actions_def_negatives", defNeg);

                        // Détail Défense
                        stats.put("def_recuperations", rs.getLong("total_recup"));
                        stats.put("def_contres", rs.getLong("total_contres"));
                        stats.put("def_duels_gagnes", rs.getLong("total_duels_gagnes"));
                        stats.put("def_duels_perdus", rs.getLong("total_duels_perdus")); // AJOUTÉ
                        stats.put("def_fautes", rs.getLong("total_fautes"));
                        stats.put("def_sanctions_2min", rs.getLong("total_sanctions_2min"));

                        return stats;
                    }
                }
            }
            // Si le ResultSet est vide (aucun event pour ce joueur/match), retourne une map vide
            return new HashMap<>();
        });
    }
}

