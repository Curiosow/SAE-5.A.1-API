package fr.uphf.sae5a1api.controllers;

import fr.uphf.sae5a1api.data.impl.actions.Match;
import fr.uphf.sae5a1api.data.sql.managers.actions.MatchManager;
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

class MatchControllerTest {

    private MockMvc mockMvc;
    private MockedStatic<MatchManager> mockedMatchManager;

    @BeforeEach
    void setUp() {
        MatchController matchController = new MatchController();
        mockMvc = MockMvcBuilders.standaloneSetup(matchController).build();

        // Mock de la classe statique MatchManager
        mockedMatchManager = mockStatic(MatchManager.class);
    }

    @AfterEach
    void tearDown() {
        // Libération du mock statique
        mockedMatchManager.close();
    }

    @Test
    @DisplayName("GET /match doit retourner la liste des matchs dans un objet JSON 'docs'")
    void shouldReturnAllMatchs() throws Exception {
        // 1. Préparation des données simulées
        Match mockMatch = mock(Match.class);
        List<Match> matchList = Collections.singletonList(mockMatch);

        // 2. Définition du comportement du manager
        mockedMatchManager.when(MatchManager::getAllMatchs).thenReturn(matchList);

        // 3. Exécution de la requête et vérifications
        mockMvc.perform(get("/match"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                // Vérifie que la clé "docs" existe et contient un tableau d'un élément
                .andExpect(jsonPath("$.docs").isArray())
                .andExpect(jsonPath("$.docs.length()").value(1));

        // Vérifie que le manager a bien été sollicité
        mockedMatchManager.verify(MatchManager::getAllMatchs, times(1));
    }

    @Test
    @DisplayName("GET /match doit retourner une liste vide si aucun match n'existe")
    void shouldReturnEmptyListWhenNoMatchs() throws Exception {
        mockedMatchManager.when(MatchManager::getAllMatchs).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/match"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.docs").isArray())
                .andExpect(jsonPath("$.docs.length()").value(0));
    }
}