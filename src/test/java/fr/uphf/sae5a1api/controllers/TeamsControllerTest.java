package fr.uphf.sae5a1api.controllers;

import fr.uphf.sae5a1api.data.impl.teams.Team;
import fr.uphf.sae5a1api.data.sql.managers.teams.TeamsManager;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TeamsControllerTest {

    private MockMvc mockMvc;
    private MockedStatic<TeamsManager> mockedTeamsManager;

    @BeforeEach
    void setUp() {
        TeamsController teamsController = new TeamsController();
        mockMvc = MockMvcBuilders.standaloneSetup(teamsController).build();

        // Initialisation du mock statique pour TeamsManager
        mockedTeamsManager = mockStatic(TeamsManager.class);
    }

    @AfterEach
    void tearDown() {
        // Libération du mock statique après chaque test
        mockedTeamsManager.close();
    }

    @Test
    @DisplayName("GET /teams doit retourner la liste de toutes les équipes sous la clé 'docs'")
    void shouldReturnAllTeams() throws Exception {
        // 1. Préparation des données simulées
        Team mockTeam = mock(Team.class);
        List<Team> teamList = Collections.singletonList(mockTeam);

        // 2. Définition du comportement attendu du manager
        mockedTeamsManager.when(TeamsManager::getAllTeams).thenReturn(teamList);

        // 3. Exécution de la requête et vérifications
        mockMvc.perform(get("/teams"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                // Vérifie que la réponse contient "docs" et un tableau de taille 1
                .andExpect(jsonPath("$.docs").isArray())
                .andExpect(jsonPath("$.docs.length()").value(1));

        // Vérification que le manager a bien été sollicité
        mockedTeamsManager.verify(TeamsManager::getAllTeams, times(1));
    }

    @Test
    @DisplayName("GET /teams doit retourner une liste vide si aucune équipe n'est présente")
    void shouldReturnEmptyListWhenNoTeams() throws Exception {
        // Simulation d'une liste vide
        mockedTeamsManager.when(TeamsManager::getAllTeams).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.docs").isArray())
                .andExpect(jsonPath("$.docs.length()").value(0));
    }
}