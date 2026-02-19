package fr.uphf.sae5a1api.controllers;

import fr.uphf.sae5a1api.data.impl.actions.Position;
import fr.uphf.sae5a1api.data.sql.managers.actions.PositionManager;
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

class PositionControllerTest {

    private MockMvc mockMvc;
    private MockedStatic<PositionManager> mockedPositionManager;

    @BeforeEach
    void setUp() {
        PositionController positionController = new PositionController();
        mockMvc = MockMvcBuilders.standaloneSetup(positionController).build();

        // Mock de la classe statique PositionManager
        mockedPositionManager = mockStatic(PositionManager.class);
    }

    @AfterEach
    void tearDown() {
        // Libération du mock statique pour éviter les conflits entre tests
        mockedPositionManager.close();
    }

    @Test
    @DisplayName("GET /positions doit retourner la liste des positions dans un objet JSON 'docs'")
    void shouldReturnAllPositions() throws Exception {
        // 1. Préparation d'une fausse donnée
        Position mockPosition = mock(Position.class);
        List<Position> positionList = Collections.singletonList(mockPosition);

        // 2. Définition du comportement du manager
        mockedPositionManager.when(PositionManager::getAllPositions).thenReturn(positionList);

        // 3. Exécution et vérification
        mockMvc.perform(get("/positions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.docs").isArray())
                .andExpect(jsonPath("$.docs.length()").value(1));

        // Vérification de l'appel au manager
        mockedPositionManager.verify(PositionManager::getAllPositions, times(1));
    }

    @Test
    @DisplayName("GET /positions doit retourner une liste vide si aucune position n'est enregistrée")
    void shouldReturnEmptyListWhenNoPositions() throws Exception {
        mockedPositionManager.when(PositionManager::getAllPositions).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/positions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.docs").isArray())
                .andExpect(jsonPath("$.docs.length()").value(0));
    }
}