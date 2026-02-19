package fr.uphf.sae5a1api.controllers;

import fr.uphf.sae5a1api.data.impl.users.Coach;
import fr.uphf.sae5a1api.data.impl.users.Player;
import fr.uphf.sae5a1api.data.impl.users.User;
import fr.uphf.sae5a1api.data.sql.managers.users.UserManager;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private MockedStatic<UserManager> mockedUserManager;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockedUserManager = mockStatic(UserManager.class);
        authController = new AuthController();
    }

    @AfterEach
    void tearDown() {
        mockedUserManager.close();
    }

    // --- TESTS : REGISTER COACH ---
    @Nested
    @DisplayName("Tests registerCoach")
    class RegisterCoachTests {

        @Test
        @DisplayName("Succès : Inscription d'un coach")
        void shouldRegisterCoachSuccessfully() {
            String teamId = UUID.randomUUID().toString();
            mockedUserManager.when(() -> UserManager.createUser(any(User.class))).then(invocation -> null);

            ResponseEntity<String> response = authController.registerCoach("coach@test.com", "pass123", "John", "Doe", teamId);

            assertEquals(200, response.getStatusCode().value());
            assertEquals("Création de l'utilisateur réussie !", response.getBody());
            mockedUserManager.verify(() -> UserManager.createUser(any(Coach.class)), times(1));
        }

        @Test
        @DisplayName("Erreur : Format UUID de team_id invalide")
        void shouldFailWithInvalidUuid() {
            assertThrows(IllegalArgumentException.class,
                    () -> authController.registerCoach("c@t.com", "p", "J", "D", "not-a-uuid"));
        }
    }

    // --- TESTS : REGISTER PLAYER ---
    @Nested
    @DisplayName("Tests registerPlayer")
    class RegisterPlayerTests {

        @Test
        @DisplayName("Succès : Inscription d'un joueur avec normalisation du nom")
        void shouldRegisterPlayerSuccessfully() {
            String teamId = UUID.randomUUID().toString();
            mockedUserManager.when(() -> UserManager.createUser(any(Player.class))).then(invocation -> null);

            ResponseEntity<String> response = authController.registerPlayer(
                    "player@test.com", "pass", "Élise", "Duchêne", teamId, 10, "2000-05-15", 185);

            assertEquals(200, response.getStatusCode().value());
            assertTrue(response.getBody().contains("réussie"));
        }

        @Test
        @DisplayName("Erreur : Mauvais format de date de naissance")
        void shouldReturn400ForInvalidDateFormat() {
            ResponseEntity<String> response = authController.registerPlayer(
                    "p@t.com", "p", "P", "N", UUID.randomUUID().toString(), 7, "15/05/2000", 180);

            assertEquals(400, response.getStatusCode().value());
            assertEquals("Format de date invalide (attendu : yyyy-MM-dd)", response.getBody());
        }
    }

    // --- TESTS : LOGIN ---
    @Nested
    @DisplayName("Tests Login")
    class LoginTests {

        @Test
        @DisplayName("Succès : Authentification réussie")
        void shouldLoginSuccessfully() {
            User mockUser = mock(User.class);
            when(mockUser.getFirst_name()).thenReturn("Alice");
            when(mockUser.getLast_name()).thenReturn("Smith");

            mockedUserManager.when(() -> UserManager.login("alice@test.com", "secret"))
                    .thenReturn(mockUser);

            ResponseEntity<String> response = authController.login("alice@test.com", "secret");

            assertEquals(200, response.getStatusCode().value());
            assertTrue(response.getBody().contains("Connexion réussie pour Alice Smith"));
        }

        @Test
        @DisplayName("Erreur 401 : Identifiants incorrects")
        void shouldReturnUnauthorized() {
            mockedUserManager.when(() -> UserManager.login(anyString(), anyString())).thenReturn(null);

            ResponseEntity<String> response = authController.login("wrong@test.com", "wrong");

            assertEquals(401, response.getStatusCode().value());
            assertEquals("L'adresse e-mail ou le mot de passe est incorrect !", response.getBody());
        }
    }

    // --- TESTS : UPDATE INFORMATION ---
    @Nested
    @DisplayName("Tests updateInformation")
    class UpdateInformationTests {

        @Test
        @DisplayName("Succès : Mise à jour d'un booléen (is_active)")
        void shouldUpdateBooleanValue() {
            // "1" doit être converti en true
            authController.updateInformation("true", "is_active", "1", "user@test.com");

            mockedUserManager.verify(() ->
                    UserManager.updateInformation(eq(true), eq("is_active"), eq(true), eq("user@test.com")));
        }

        @Test
        @DisplayName("Succès : Mise à jour d'un entier (jersey_number)")
        void shouldUpdateIntegerValue() {
            authController.updateInformation("false", "jersey_number", "23", "player@test.com");

            mockedUserManager.verify(() ->
                    UserManager.updateInformation(eq(false), eq("jersey_number"), eq(23), eq("player@test.com")));
        }

        @Test
        @DisplayName("Succès : Mise à jour d'un UUID (team_id)")
        void shouldUpdateUuidValue() {
            UUID newTeamId = UUID.randomUUID();
            authController.updateInformation("true", "team_id", newTeamId.toString(), "coach@test.com");

            mockedUserManager.verify(() ->
                    UserManager.updateInformation(eq(true), eq("team_id"), eq(newTeamId), eq("coach@test.com")));
        }

        @Test
        @DisplayName("Erreur : Valeur entière invalide")
        void shouldThrowExceptionForInvalidInt() {
            assertThrows(IllegalArgumentException.class,
                    () -> authController.updateInformation("false", "height_cm", "not-an-int", "test@test.com"));
        }
    }

    // --- TESTS : DELETE PLAYER ---
    @Test
    @DisplayName("Succès : Suppression d'un joueur")
    void shouldDeletePlayerSuccessfully() {
        String email = "to-delete@test.com";

        ResponseEntity<String> response = authController.deletePlayer(email);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().contains(email));
        mockedUserManager.verify(() -> UserManager.deleteUser(email), times(1));
    }
}