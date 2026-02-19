package fr.uphf.sae5a1api.controllers;

import fr.uphf.sae5a1api.data.impl.teams.RankedTeam;
import fr.uphf.sae5a1api.data.sql.managers.teams.RankingManager;
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

class RankingControllerTest {

    private MockMvc mockMvc;
    private MockedStatic<RankingManager> mockedRankingManager;

    @BeforeEach
    void setUp() {
        RankingController rankingController = new RankingController();
        mockMvc = MockMvcBuilders.standaloneSetup(rankingController).build();

        // Mock de la classe statique RankingManager
        mockedRankingManager = mockStatic(RankingManager.class);
    }

    @AfterEach
    void tearDown() {
        // Libération du mock statique
        mockedRankingManager.close();
    }

    @Nested
    @DisplayName("Tests GET /ranking")
    class GetAllRankingTests {
        @Test
        @DisplayName("Succès : Récupération du classement complet")
        void shouldReturnAllTeamsRanking() throws Exception {
            RankedTeam mockTeam = mock(RankedTeam.class);
            List<RankedTeam> teamList = Collections.singletonList(mockTeam);

            mockedRankingManager.when(RankingManager::getAllTeams).thenReturn(teamList);

            mockMvc.perform(get("/ranking"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.docs").isArray())
                    .andExpect(jsonPath("$.docs.length()").value(1));

            mockedRankingManager.verify(RankingManager::getAllTeams, times(1));
        }
    }

    @Nested
    @DisplayName("Tests GET /rankingAvesnois")
    class GetRankingAvesnoisTests {
        @Test
        @DisplayName("Succès : Récupération des statistiques de l'équipe Avesnois")
        void shouldReturnAvesnoisStats() throws Exception {
            // Création et configuration d'une équipe fictive
            RankedTeam mockTeam = mock(RankedTeam.class);
            when(mockTeam.getClassement_nbr_match_gagne()).thenReturn(10);
            when(mockTeam.getClassement_nbr_match_nul()).thenReturn(2);
            when(mockTeam.getClassement_nbr_match_perdu()).thenReturn(3);
            when(mockTeam.getClassement_point_total()).thenReturn(32);
            when(mockTeam.getClassement_place()).thenReturn(1);

            mockedRankingManager.when(RankingManager::getTeamAvesnois).thenReturn(mockTeam);

            mockMvc.perform(get("/rankingAvesnois"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.victoires").value(10))
                    .andExpect(jsonPath("$.nuls").value(2))
                    .andExpect(jsonPath("$.defaites").value(3))
                    .andExpect(jsonPath("$.points").value(32))
                    .andExpect(jsonPath("$.position").value(1));

            mockedRankingManager.verify(RankingManager::getTeamAvesnois, times(1));
        }
    }
}