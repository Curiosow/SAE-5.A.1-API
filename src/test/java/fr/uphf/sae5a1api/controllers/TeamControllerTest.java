package fr.uphf.sae5a1api.controllers;

import fr.uphf.sae5a1api.data.impl.actions.Team;
import fr.uphf.sae5a1api.data.sql.managers.actions.TeamManager;
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

class TeamControllerTest {

    private MockMvc mockMvc;
    private MockedStatic<TeamManager> mockedTeamManager;

    @BeforeEach
    void setUp() {
        TeamController teamController = new TeamController();
        mockMvc = MockMvcBuilders.standaloneSetup(teamController).build();

        // Initialisation du mock statique pour le manager d'équipes
        mockedTeamManager = mockStatic(TeamManager.class);
    }

    @AfterEach
    void tearDown() {
        // Libération obligatoire du mock statique
        mockedTeamManager.close();
    }

    @Test
    @DisplayName("GET /teamlogo doit retourner la liste des équipes sous la clé 'docs'")
    void shouldReturnAllTeams() throws Exception {
        // 1. Préparation d'une équipe fictive
        Team mockTeam = mock(Team.class);
        List<Team> teamList = Collections.singletonList(mockTeam);

        // 2. Définition du comportement attendu du manager
        mockedTeamManager.when(TeamManager::getAllTeams).thenReturn(teamList);

        // 3. Exécution de la requête GET et vérifications
        mockMvc.perform(get("/teamlogo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                // Vérifie que le JSON contient la clé "docs" avec un tableau de taille 1
                .andExpect(jsonPath("$.docs").isArray())
                .andExpect(jsonPath("$.docs.length()").value(1));

        // Vérification de l'appel au manager
        mockedTeamManager.verify(TeamManager::getAllTeams, times(1));
    }

    @Test
    @DisplayName("GET /teamlogo doit retourner une liste vide si aucune équipe n'existe")
    void shouldReturnEmptyListWhenNoTeams() throws Exception {
        // Simulation d'une liste vide
        mockedTeamManager.when(TeamManager::getAllTeams).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/teamlogo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.docs").isArray())
                .andExpect(jsonPath("$.docs.length()").value(0));
    }
}
