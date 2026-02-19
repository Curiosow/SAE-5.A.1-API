package fr.uphf.sae5a1api.controllers;

import fr.uphf.sae5a1api.data.impl.actions.Evenement;
import fr.uphf.sae5a1api.data.impl.actions.Match;
import fr.uphf.sae5a1api.data.sql.managers.actions.EvenementManager;
import fr.uphf.sae5a1api.data.sql.managers.actions.MatchManager;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ImportControllerTest {

    private MockMvc mockMvc;
    private MockedStatic<MatchManager> mockedMatchManager;
    private MockedStatic<EvenementManager> mockedEvenementManager;

    // On définit des UUID valides pour les tests
    private final String VALID_TEAM_ID = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        ImportController importController = new ImportController();
        mockMvc = MockMvcBuilders.standaloneSetup(importController).build();

        mockedMatchManager = mockStatic(MatchManager.class);
        mockedEvenementManager = mockStatic(EvenementManager.class);
    }

    @AfterEach
    void tearDown() {
        mockedMatchManager.close();
        mockedEvenementManager.close();
    }

    @Test
    @DisplayName("Erreur 400 : Liste d'événements vide")
    void shouldReturnBadRequestWhenEventsEmpty() throws Exception {
        // Utilisation d'un UUID valide pour teamId
        String jsonRequest = String.format("""
                {
                    "rencontreId": "1",
                    "teamId": "%s",
                    "events": []
                }
                """, VALID_TEAM_ID);

        mockMvc.perform(post("/api/import/match-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erreur : La liste d'événements est vide."));
    }

    @Test
    @DisplayName("Succès : Match existant, importation des événements")
    void shouldImportWhenMatchExists() throws Exception {
        Match mockMatch = mock(Match.class);
        when(mockMatch.getId()).thenReturn(50);

        mockedMatchManager.when(() -> MatchManager.findByRencontreId(anyString())).thenReturn(mockMatch);

        String jsonRequest = String.format("""
                {
                    "rencontreId": "1",
                    "teamId": "%s",
                    "events": [{"tempsFormat": "10:00", "phaseJeu": "Attaque"}]
                }
                """, VALID_TEAM_ID);

        mockMvc.perform(post("/api/import/match-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("Succès : 1 événements importés."));

        mockedEvenementManager.verify(() -> EvenementManager.save(any(Evenement.class)), times(1));
    }

    @Test
    @DisplayName("Succès : Création d'un nouveau match puis importation")
    void shouldCreateMatchAndImport() throws Exception {
        mockedMatchManager.when(() -> MatchManager.findByRencontreId(anyString())).thenReturn(null);

        mockedMatchManager.when(() -> MatchManager.createMatch(anyString(), anyString(), anyString(), any(LocalDate.class)))
                .thenReturn(99);

        String jsonRequest = String.format("""
                {
                    "rencontreId": "2",
                    "teamId": "%s",
                    "adversaire": "Team B",
                    "lieu": "Stade X",
                    "dateMatch": "2023-10-25",
                    "events": [{}, {}]
                }
                """, VALID_TEAM_ID);

        mockMvc.perform(post("/api/import/match-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("Succès : 2 événements importés."));

        mockedMatchManager.verify(() -> MatchManager.createMatch(anyString(), eq("Team B"), eq("Stade X"), any()), times(1));
        mockedEvenementManager.verify(() -> EvenementManager.save(any(Evenement.class)), times(2));
    }

    @Test
    @DisplayName("Erreur 500 : Exception lors de la sauvegarde")
    void shouldReturnInternalServerErrorOnException() throws Exception {
        mockedMatchManager.when(() -> MatchManager.findByRencontreId(anyString()))
                .thenThrow(new RuntimeException("Crash BDD"));

        String jsonRequest = String.format("""
                {
                    "rencontreId": "1",
                    "teamId": "%s",
                    "events": [{}]
                }
                """, VALID_TEAM_ID);

        mockMvc.perform(post("/api/import/match-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Erreur serveur : Crash BDD")));
    }
}