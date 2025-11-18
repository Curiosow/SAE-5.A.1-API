package fr.uphf.sae5a1api.data.sql.managers.actions;

import fr.uphf.sae5a1api.data.HikariConnector;
import fr.uphf.sae5a1api.data.impl.actions.Match;
import fr.uphf.sae5a1api.data.sql.executor.DatabaseExecutor;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MatchManager {

    public static final String MATCHS_TABLE = "matchs";
    public static final String GET_ALL_MATCHS = "SELECT * FROM " + MATCHS_TABLE;

    // --- Existing ---
    public static List<Match> getAllMatchs() {
        return DatabaseExecutor.executeQuery(HikariConnector.get(), data -> {
            PreparedStatement statement = data.prepareStatement(GET_ALL_MATCHS);
            List<Match> matchs = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) matchs.add(buildMatch(resultSet));
            return matchs;
        });
    }

    // --- NEW: Find Match by External ID ---
    public static Match findByRencontreId(String rencontreId) {
        return DatabaseExecutor.executeQuery(HikariConnector.get(), conn -> {
            // Vérifie bien que la colonne s'appelle rencontre_id en base
            String sql = "SELECT * FROM " + MATCHS_TABLE + " WHERE rencontre_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, rencontreId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return buildMatch(rs);
            return null;
        });
    }

    // --- NEW: Create Match with Full Info ---
    public static int createMatch(String rencontreId, String adversaire, String lieu, LocalDate dateMatch) {
        return DatabaseExecutor.executeQuery(HikariConnector.get(), conn -> {
            // 1. Récupérer l'ID de notre équipe (Sambre)
            // On suppose que la table s'appelle "teams" et la colonne "name"
            String teamUuidStr = null;
            try (PreparedStatement psTeam = conn.prepareStatement("SELECT id FROM teams WHERE name LIKE ? LIMIT 1")) {
                psTeam.setString(1, "%SAMBRE%"); // Recherche large pour être sûr de trouver
                ResultSet rsTeam = psTeam.executeQuery();
                if (rsTeam.next()) {
                    teamUuidStr = rsTeam.getString("id");
                }
            }

            // 2. Insérer le match
            String sql = "INSERT INTO " + MATCHS_TABLE +
                    " (rencontre_id, team_id, adversaire, lieu, date_match) " +
                    " VALUES (?, ?::uuid, ?, ?, ?) RETURNING id";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, rencontreId);

            if (teamUuidStr != null) {
                ps.setString(2, teamUuidStr);
            } else {
                ps.setNull(2, Types.OTHER); // Ou gérer une erreur si l'équipe n'est pas trouvée
            }

            ps.setString(3, adversaire);
            ps.setString(4, lieu);
            ps.setDate(5, Date.valueOf(dateMatch));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("Echec création match");
        });
    }

    private static Match buildMatch(ResultSet rs) throws SQLException {
        Match match = new Match();
        match.setId(rs.getInt("id"));
        // match.setRencontreId(rs.getString("rencontre_id")); // Ajoute ce setter dans Match.java si tu veux le lire
        match.setTeamId((UUID) rs.getObject("team_id"));
        match.setAdversaire(rs.getString("adversaire"));
        Date sqlDate = rs.getDate("date_match");
        if (sqlDate != null) match.setDateMatch(sqlDate.toLocalDate());
        match.setLieu(rs.getString("lieu"));
        return match;
    }
}