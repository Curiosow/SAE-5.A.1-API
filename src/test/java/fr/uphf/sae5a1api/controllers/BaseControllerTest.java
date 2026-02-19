package fr.uphf.sae5a1api.controllers;

import fr.uphf.sae5a1api.SAE5A1ApiApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.logging.Logger;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BaseControllerTest {

    private MockMvc mockMvc;
    private MockedStatic<SAE5A1ApiApplication> mockedApiApp;

    @BeforeEach
    void setUp() {
        // Initialisation de MockMvc pour le contrôleur
        BaseController baseController = new BaseController();
        mockMvc = MockMvcBuilders.standaloneSetup(baseController).build();

        // Mock de la classe statique SAE5A1ApiApplication pour éviter les erreurs de Logger
        mockedApiApp = mockStatic(SAE5A1ApiApplication.class);
        Logger mockLogger = mock(Logger.class);
        mockedApiApp.when(SAE5A1ApiApplication::getLogger).thenReturn(mockLogger);
    }

    @AfterEach
    void tearDown() {
        // Toujours fermer le mock statique après chaque test
        mockedApiApp.close();
    }

    @Test
    @DisplayName("GET / doit retourner un statut OK et le message de confirmation")
    void shouldReturnPong() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("Server response : OK"));

        // Vérifie que le logger a bien été appelé (facultatif mais recommandé)
        mockedApiApp.verify(SAE5A1ApiApplication::getLogger, times(1));
    }
}