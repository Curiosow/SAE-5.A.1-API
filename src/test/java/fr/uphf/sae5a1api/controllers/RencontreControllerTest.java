package fr.uphf.sae5a1api.controllers;

import fr.uphf.sae5a1api.data.impl.actions.Rencontre;
import fr.uphf.sae5a1api.data.sql.managers.actions.RencontreManager;
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

class RencontreControllerTest {

    private MockMvc mockMvc;
    private MockedStatic<RencontreManager> mockedRencontreManager;

    @BeforeEach
    void setUp() {
        RencontreController rencontreController = new RencontreController();
        mockMvc = MockMvcBuilders.standaloneSetup(rencontreController).build();

        // Initialisation du mock statique pour le manager de rencontres
        mockedRencontreManager = mockStatic(RencontreManager.class);
    }

    @AfterEach
    void tearDown() {
        // Fermeture du mock pour éviter les interférences avec d'autres tests
        mockedRencontreManager.close();
    }

    @Test
    @DisplayName("GET /rencontre doit retourner la liste des rencontres sous la clé 'docs'")
    void shouldReturnAllRencontres() throws Exception {
        // 1. Préparation d'une rencontre fictive
        Rencontre mockRencontre = mock(Rencontre.class);
        List<Rencontre> rencontreList = Collections.singletonList(mockRencontre);

        // 2. Définition du comportement attendu du manager
        mockedRencontreManager.when(RencontreManager::getAllRencontres).thenReturn(rencontreList);

        // 3. Exécution de la requête GET et vérifications
        mockMvc.perform(get("/rencontre"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                // Vérification de la structure du JSON
                .andExpect(jsonPath("$.docs").isArray())
                .andExpect(jsonPath("$.docs.length()").value(1));

        // Vérification que le manager a bien été appelé une seule fois
        mockedRencontreManager.verify(RencontreManager::getAllRencontres, times(1));
    }

    @Test
    @DisplayName("GET /rencontre doit retourner une liste vide si aucune rencontre n'est trouvée")
    void shouldReturnEmptyListWhenNoRencontres() throws Exception {
        // Simulation d'une liste vide renvoyée par la base de données
        mockedRencontreManager.when(RencontreManager::getAllRencontres).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/rencontre"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.docs").isArray())
                .andExpect(jsonPath("$.docs.length()").value(0));
    }
}