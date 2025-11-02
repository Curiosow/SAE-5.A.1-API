package fr.uphf.sae5a1api.data.sql.managers.users;

import at.favre.lib.crypto.bcrypt.BCrypt;
import fr.uphf.sae5a1api.data.sql.executor.DatabaseExecutor;
import fr.uphf.sae5a1api.data.users.Coach;
import fr.uphf.sae5a1api.data.HikariConnector;
import fr.uphf.sae5a1api.data.users.Player;
import fr.uphf.sae5a1api.data.users.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class UserManager {

    // TABLES
    // Ici je définis les tables pour les avoir en raccourci et que si je modif le nom dans la db, j'ai juste a modifier ici
    public static final String COACH_TABLE = "coaches";
    public static final String PLAYER_TABLE = "players";

    // REQUESTS
    // Ca c'est mes requêtess, c'est un peu comme des templates, en gros c'est les requêtes de bases et les "?" sont remplacés par des valeurs
    public static final String GET_COACH_BY_MAIL = "SELECT * FROM " + COACH_TABLE + " where email = ?";
    public static final String SAVE_COACH = "INSERT INTO " + COACH_TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String SAVE_PLAYER = "INSERT INTO " + PLAYER_TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?,?,?)";
    public static final String GET_MEMBER = "SELECT first_name, last_name, email, 'player' AS account_type FROM " + PLAYER_TABLE + " UNION ALL SELECT first_name, last_name, email, 'coach' AS account_type FROM " + COACH_TABLE;
    public static final String GET_PLAYER = "SELECT p.*, t.name AS team_name FROM " + PLAYER_TABLE + " p JOIN teams t ON p.team_id = t.id ";
    public static final String GET_COACH = "SELECT * FROM " + COACH_TABLE;
    public static final String UPDATE_PLAYER_FIRSTNAME = "UPDATE " + PLAYER_TABLE + " SET first_name = ? WHERE email = ?";
    public static final String UPDATE_PLAYER_LASTNAME = "UPDATE " + PLAYER_TABLE + " SET last_name = ? WHERE email = ?";
    public static final String UPDATE_PLAYER_NUMBER = "UPDATE " + PLAYER_TABLE + " SET jersey_number = ? WHERE email = ?";
    public static final String UPDATE_PLAYER_HEIGHT = "UPDATE " + PLAYER_TABLE + " SET height_cm = ? WHERE email = ?";
    public static final String UPDATE_PLAYER_ACTIVE = "UPDATE " + PLAYER_TABLE + " SET is_active = ? WHERE email = ?";
    public static final String UPDATE_PLAYER_EMAIL = "UPDATE " + PLAYER_TABLE + " SET email = ? WHERE email = ?";
    public static final String UPDATE_PLAYER_BIRTHDATE = "UPDATE " + PLAYER_TABLE + " SET birth_date = ? WHERE email = ?";
    public static final String UPDATE_COACH_FIRSTNAME = "UPDATE " + COACH_TABLE + " SET first_name = ? WHERE email = ?";
    public static final String UPDATE_COACH_LASTNAME = "UPDATE " + COACH_TABLE + " SET last_name = ? WHERE email = ?";
    public static final String UPDATE_COACH_EMAIL = "UPDATE " + COACH_TABLE + " SET email = ? WHERE email = ?";
    public static final String UPDATE_COACH_ACTIVE = "UPDATE " + COACH_TABLE + " SET is_active = ? WHERE email = ?";


    // ça c'est pour créer un user, donc c'est une fonction qui est void, elle ne retourne rien, elle fait juste des actions
    public static void createUser(User user) {
        // on le remarque ici, quand on fait un executeVoidQuery c'est que ça retourne strictement rien, c'est du void
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            // là je met ma stateement donc en gros, la requête, ici je verif juste si c'est pour un coach ou un user mais on s'en fou
            PreparedStatement statement = connection.prepareStatement(user instanceof Coach ? SAVE_COACH : PLAYER_TABLE);

            // Ca c'est définir les arguments, en gros le "?" numéro 1 sera remplacé par un objet UUID, sur celle d'en dessous, on défini directement le type (donc String, Int, Boolean.....) si on ne sait pas, on met "Object"
            statement.setObject(1, user.getUuid());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getFirst_name());
            statement.setString(5, user.getLast_name());
            statement.setBoolean(6, user.isActive());
            statement.setTimestamp(7, new Timestamp(user.getCreated_at().getTime()));
            statement.setTimestamp(8, new Timestamp(user.getUpdated_at().getTime()));
            // Et donc executeUpdate() c'est pour faire une update, c'est pas pour récupérer une valeur
            statement.executeUpdate();
        });
    }

    public static void createPlayer(Player player) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(SAVE_PLAYER);

            statement.setObject(1, player.getUuid());
            statement.setObject(2, player.getTeam_id());
            statement.setString(3, player.getEmail());
            statement.setString(4, player.getPassword());
            statement.setString(5, player.getFirst_name());
            statement.setString(6, player.getLast_name());
            statement.setInt(7, player.getJersey_number());
            statement.setDate(8, new java.sql.Date(player.getBirth_date().getTime()));
            statement.setInt(9, player.getHeight_cm());
            statement.setInt(10, player.getWeight_kg());
            statement.setBoolean(11, player.isActive());
            statement.setTimestamp(12, new Timestamp(player.getCreated_at().getTime()));
            statement.setTimestamp(13, new Timestamp(player.getUpdated_at().getTime()));
            statement.setString(14, player.getPicture_url());
            statement.setString(15, player.getNom_csv());


            statement.executeUpdate();
        });
    }


    // Récupère tous les membres (players et coaches) avec leur type de compte
    public static void getMembers(java.util.function.Consumer<ResultSet> consumer) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(GET_MEMBER);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                consumer.accept(resultSet);
            }
        });
    }

    // Récupère tous les joueurs
    public static void getPlayer(java.util.function.Consumer<ResultSet> consumer) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(GET_PLAYER);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                consumer.accept(resultSet);
            }
        });
    }

    public static void getCoach(java.util.function.Consumer<ResultSet> consumer) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(GET_COACH);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                consumer.accept(resultSet);
            }
        });
    }

    public static void updatePlayerFirstName(String email, String newFirstName) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(UPDATE_PLAYER_FIRSTNAME);
            statement.setString(1, newFirstName);
            statement.setString(2, email);
            statement.executeUpdate();
        });
    }

    public static void updatePlayerLastName(String email, String newLastName) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(UPDATE_PLAYER_LASTNAME);
            statement.setString(1, newLastName);
            statement.setString(2, email);
            statement.executeUpdate();
        });
    }

    public static void updatePlayerNumber(String email, int jerseyNumber) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(UPDATE_PLAYER_NUMBER);
            statement.setInt(1, jerseyNumber);
            statement.setString(2, email);
            statement.executeUpdate();
        });
    }

    public static void updatePlayerHeight(String email, int heightCm) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(UPDATE_PLAYER_HEIGHT);
            statement.setInt(1, heightCm);
            statement.setString(2, email);
            statement.executeUpdate();
        });
    }

    public static void updatePlayerActive(String email, boolean isActive) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(UPDATE_PLAYER_ACTIVE);
            statement.setBoolean(1, isActive);
            statement.setString(2, email);
            statement.executeUpdate();
        });
    }

    public static void updatePlayerEmail(String oldEmail, String newEmail) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(UPDATE_PLAYER_EMAIL);
            statement.setString(1, newEmail);
            statement.setString(2, oldEmail);
            statement.executeUpdate();
        });
    }

    public static void updatePlayerBirthDate(String email, java.util.Date birthDate) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(UPDATE_PLAYER_BIRTHDATE);
            statement.setDate(1, new java.sql.Date(birthDate.getTime()));
            statement.setString(2, email);
            statement.executeUpdate();
        });
    }

    public static void updateCoachFirstName(String email, String newFirstName) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(UPDATE_COACH_FIRSTNAME);
            statement.setString(1, newFirstName);
            statement.setString(2, email);
            statement.executeUpdate();
        });
    }

    public static void updateCoachLastName(String email, String newLastName) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(UPDATE_COACH_LASTNAME);
            statement.setString(1, newLastName);
            statement.setString(2, email);
            statement.executeUpdate();
        });
    }

    public static void updateCoachEmail(String oldEmail, String newEmail) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(UPDATE_COACH_EMAIL);
            statement.setString(1, newEmail);
            statement.setString(2, oldEmail);
            statement.executeUpdate();
        });
    }

    public static void updateCoachActive(String email, boolean isActive) {
        DatabaseExecutor.executeVoidQuery(HikariConnector.get(), connection -> {
            PreparedStatement statement = connection.prepareStatement(UPDATE_COACH_ACTIVE);
            statement.setBoolean(1, isActive);
            statement.setString(2, email);
            statement.executeUpdate();
        });
    }






        // La je veux me login donc récupérer un User, c'est pour ça que je met des paramètres, en gros le mail et le rawPassword, c'est à dire le champ que le mec à mis qui n'est pas encore hashé
    public static User login(String email, String rawPassword) {
        // La on fait un executeQuery sans le "void" puisqu'oon retourne quelque chose, le User
        return DatabaseExecutor.executeQuery(HikariConnector.get(), connection -> {
            // Ici on fait pareil, on récup l'utilisateur par son mail grace a la template créer en haut
            PreparedStatement statement = connection.prepareStatement(GET_COACH_BY_MAIL);
            statement.setString(1, email);

            // Alors le ResultSet, c'est en gros le resultat de notre requete, en général on l'utilise soit avec un if() pour vérifier qu'il y a UN résultat sinon, on met un while() et tant qu'on a des prochaines lignes, on fait quelque chose
            ResultSet resultSet = statement.executeQuery();

            // Si on a un reésultat
            if(resultSet.next()) {
                // La j'encode le rawPassword et je vérif si c'est le même que le password dejà encodé dans la db
                if(BCrypt.verifyer().verify(rawPassword.toCharArray(), resultSet.getString("password")).verified)
                    // et la je retourne un buildCoach()
                    return buildCoach(resultSet);
                else
                    return null;
            } else
                return null;

        });
    }



    // Le buildCoach() il me permet de build un objet Java juste en récupérant le ResultSet, c'est bcp plus simple, je récupère chaque data et ça me permet de créer mon objet
    private static Coach buildCoach(ResultSet rs) throws SQLException {
        // C'est comme pour le setObject() quand on créer l'utilisateur, là on récup l'objet UUID
        UUID uuid = (UUID) rs.getObject("id");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        boolean isActive = rs.getBoolean("is_active");
        Date createdAt = rs.getTimestamp("created_at");
        Date updatedAt = rs.getTimestamp("updated_at");

        // Une fois que j'ai toute mes datas, je build l'objet Java et je le retourne
        return new Coach(uuid, email, password, firstName, lastName, isActive, createdAt, updatedAt);
    }

}
