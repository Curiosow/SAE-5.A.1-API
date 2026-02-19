package fr.uphf.sae5a1api.controllers;

import fr.uphf.sae5a1api.data.impl.actions.Objective;
import fr.uphf.sae5a1api.data.sql.managers.actions.ObjectiveManager;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ObjectiveControllerTest {

    private MockMvc mockMvc;
    private MockedStatic<ObjectiveManager> mockedObjectiveManager;

    @BeforeEach
    void setUp() {
        ObjectiveController objectiveController = new ObjectiveController();
        mockMvc = MockMvcBuilders.standaloneSetup(objectiveController).build();

        // Mock de la classe statique ObjectiveManager
        mockedObjectiveManager = mockStatic(ObjectiveManager.class);
    }

    @AfterEach
    void tearDown() {
        mockedObjectiveManager.close();
    }

    @Nested
    @DisplayName("Tests GET /objective")
    class GetObjectivesTests {
        @Test
        @DisplayName("Succès : Récupération de tous les objectifs")
        void shouldReturnAllObjectives() throws Exception {
            Objective mockObjective = mock(Objective.class);
            List<Objective> objectiveList = Collections.singletonList(mockObjective);

            mockedObjectiveManager.when(ObjectiveManager::getAllObjectives).thenReturn(objectiveList);

            mockMvc.perform(get("/objective"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.docs").isArray())
                    .andExpect(jsonPath("$.docs.length()").value(1));
        }
    }

    @Nested
    @DisplayName("Tests POST /objective")
    class CreateObjectiveTests {
        @Test
        @DisplayName("Succès : Création d'un objectif")
        void shouldCreateObjectiveSuccessfully() throws Exception {
            String jsonContent = "{\"title\": \"Gagner le match\", \"description\": \"Marquer 3 buts\"}";

            mockMvc.perform(post("/objective")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonContent))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Objectif créé avec succès"));

            mockedObjectiveManager.verify(() -> ObjectiveManager.createObjective(any(Objective.class)), times(1));
        }

        @Test
        @DisplayName("Erreur 500 : Échec de création en base")
        void shouldReturnErrorWhenCreateFails() throws Exception {
            mockedObjectiveManager.when(() -> ObjectiveManager.createObjective(any(Objective.class)))
                    .thenThrow(new RuntimeException("Erreur SQL"));

            mockMvc.perform(post("/objective")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Erreur lors de la création")));
        }
    }

    @Nested
    @DisplayName("Tests PATCH /objective/{id}/status")
    class UpdateStatusTests {
        @Test
        @DisplayName("Succès : Mise à jour du statut")
        void shouldUpdateStatusSuccessfully() throws Exception {
            String jsonContent = "{\"status\": \"Terminé\"}";

            mockMvc.perform(patch("/objective/1/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonContent))
                    .andExpect(status().isOk());

            mockedObjectiveManager.verify(() -> ObjectiveManager.updateStatus(eq(1), eq("Terminé")), times(1));
        }

        @Test
        @DisplayName("Erreur 400 : Statut manquant dans le JSON")
        void shouldReturnBadRequestWhenStatusMissing() throws Exception {
            mockMvc.perform(patch("/objective/1/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Erreur 500 : Échec lors de la mise à jour")
        void shouldReturnInternalServerErrorWhenUpdateFails() throws Exception {
            mockedObjectiveManager.when(() -> ObjectiveManager.updateStatus(anyInt(), anyString()))
                    .thenThrow(new RuntimeException("Crash"));

            mockMvc.perform(patch("/objective/1/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"status\": \"En cours\"}"))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("Tests DELETE /objective/{id}")
    class DeleteObjectiveTests {
        @Test
        @DisplayName("Succès : Suppression d'un objectif")
        void shouldDeleteObjectiveSuccessfully() throws Exception {
            mockMvc.perform(delete("/objective/10"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Objectif supprimé"));

            mockedObjectiveManager.verify(() -> ObjectiveManager.deleteObjective(10), times(1));
        }

        @Test
        @DisplayName("Erreur 500 : Échec de suppression")
        void shouldReturnErrorWhenDeleteFails() throws Exception {
            mockedObjectiveManager.when(() -> ObjectiveManager.deleteObjective(anyInt()))
                    .thenThrow(new RuntimeException("DB Error"));

            mockMvc.perform(delete("/objective/10"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("Erreur suppression"));
        }
    }
}