package fr.uphf.sae5a1api.data.sql.managers.data; // (Adaptez le package)

import fr.uphf.sae5a1api.data.HikariConnector;
import fr.uphf.sae5a1api.data.sql.executor.DatabaseExecutor;
import fr.uphf.sae5a1api.data.actions.Match; // (Importez le POJO Match)

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate; // AJOUT: Pour la conversion retour
import java.util.UUID;

/**
 * Gère les opérations BDD pour la table 'matches' (style UserManager).
 */
public class MatchManager {

    // --- TABLE ---
    public static final String MATCH_TABLE = "matches";

    // --- REQUESTS ---
    public static final String SAVE_MATCH = "INSERT INTO " + MATCH_TABLE +
            " (id, team_id, adversaire, date_match, lieu, score_team, score_adversaire) " +
            " VALUES (?, ?, ?, ?, ?, ?, ?)";

    public static final String CHECK_MATCH_EXISTS = "SELECT COUNT(*) FROM " + MATCH_TABLE + " WHERE id = ?";

    // NOUVELLE REQUÊTE AJOUTÉE
    public static final String FIND_MATCH_BY_ID = "SELECT * FROM " + MATCH_TABLE + " WHERE id = ?";

    // Vous ajouterez ici des requêtes pour GET, UPDATE, DELETE si nécessaire.

    /**
     * Crée une nouvelle entrée de match dans la BDD (style createUser).
     */
    public static void createMatch(Match match) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            try (PreparedStatement statement = connection.prepareStatement(SAVE_MATCH)) {
                statement.setObject(1, match.getId()); // L'UUID généré avant l'appel
                statement.setObject(2, match.getTeamId());
                statement.setString(3, match.getAdversaire());
                statement.setDate(4, Date.valueOf(match.getDateMatch())); // Conversion LocalDate -> sql.Date
                statement.setString(5, match.getLieu());
                statement.setInt(6, match.getScoreTeam());
                statement.setInt(7, match.getScoreAdversaire());

                statement.executeUpdate();
            }
        });
    }

    /**
     * Vérifie si un match existe par son UUID (style login, mais retourne boolean).
     */
    public static boolean matchExists(UUID matchId) {
        return DatabaseExecutor.executeQuery(HikariConnector.get(), connection -> {
            try (PreparedStatement statement = connection.prepareStatement(CHECK_MATCH_EXISTS)) {
                statement.setObject(1, matchId);
                try (ResultSet rs = statement.executeQuery()) {
                    // Si on a un résultat et que le compte est > 0, le match existe
                    return rs.next() && rs.getInt(1) > 0;
                }
            }
        });
    }

    /**
     * NOUVELLE MÉTHODE: Récupère un objet Match complet par son ID.
     * (style buildCoach ou login)
     */
    public static Match findMatchById(UUID matchId) {
        return DatabaseExecutor.executeQuery(HikariConnector.get(), connection -> {
            try (PreparedStatement statement = connection.prepareStatement(FIND_MATCH_BY_ID)) {
                statement.setObject(1, matchId);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        // "Build" l'objet Match
                        return new Match(
                                (UUID) rs.getObject("id"),
                                (UUID) rs.getObject("team_id"),
                                rs.getString("adversaire"),
                                // Conversion sql.Date -> LocalDate
                                rs.getDate("date_match").toLocalDate(),
                                rs.getString("lieu"),
                                rs.getInt("score_team"),
                                rs.getInt("score_adversaire")
                        );
                    } else {
                        return null; // Match non trouvé
                    }
                }
            }
        });
    }
}
