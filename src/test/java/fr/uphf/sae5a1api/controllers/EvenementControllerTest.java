package fr.uphf.sae5a1api.controllers;

import fr.uphf.sae5a1api.data.impl.actions.Evenement;
import fr.uphf.sae5a1api.data.sql.managers.actions.EvenementManager;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EvenementControllerTest {

    private MockMvc mockMvc;
    private MockedStatic<EvenementManager> mockedEvenementManager;

    @BeforeEach
    void setUp() {
        EvenementController evenementController = new EvenementController();
        mockMvc = MockMvcBuilders.standaloneSetup(evenementController).build();
        // Initialisation du mock statique pour le manager
        mockedEvenementManager = mockStatic(EvenementManager.class);
    }

    @AfterEach
    void tearDown() {
        mockedEvenementManager.close();
    }

    @Nested
    @DisplayName("Tests GET /evenement")
    class GetEvenementsTests {

        @Test
        @DisplayName("Succès : Récupération d'une liste d'événements")
        void shouldReturnAllEvenements() throws Exception {
            // Préparation des données simulées
            Evenement ev1 = new Evenement(); // Assurez-vous que le constructeur existe
            List<Evenement> evenements = Collections.singletonList(ev1);

            mockedEvenementManager.when(EvenementManager::getAllEvenements).thenReturn(evenements);

            mockMvc.perform(get("/evenement"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.docs").isArray())
                    .andExpect(jsonPath("$.docs.length()").value(1));
        }
    }

    @Nested
    @DisplayName("Tests POST /evenement/import")
    class ImportEvenementsTests {

        @Test
        @DisplayName("Succès : Importation de plusieurs événements")
        void shouldImportEvenementsSuccessfully() throws Exception {
            // Simuler une liste d'événements en JSON
            String jsonContent = "[{}, {}]"; // Deux objets vides pour le test

            mockMvc.perform(post("/evenement/import")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(jsonContent))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Importation réussie : 2 événements ajoutés.")));

            // Vérifier que la méthode save a bien été appelée deux fois
            mockedEvenementManager.verify(() -> EvenementManager.save(any(Evenement.class)), times(2));
        }

        @Test
        @DisplayName("Erreur : Échec lors de l'importation (500 Internal Server Error)")
        void shouldReturnErrorWhenSaveFails() throws Exception {
            String jsonContent = "[{}]";

            // Simuler une exception lors de la sauvegarde
            mockedEvenementManager.when(() -> EvenementManager.save(any(Evenement.class)))
                    .thenThrow(new RuntimeException("Erreur SQL"));

            mockMvc.perform(post("/evenement/import")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(jsonContent))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(containsString("Erreur lors de l'importation : Erreur SQL")));
        }
    }
}