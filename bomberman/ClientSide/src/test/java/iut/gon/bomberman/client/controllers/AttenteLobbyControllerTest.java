package iut.gon.bomberman.client.controllers;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.Pane;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour le contrôleur {@link AttenteLobbyController}.
 * <p>
 * Cette classe de test vérifie de manière isolée (sans serveur ni interface active complète) :
 * - Le bon fonctionnement du redimensionnement ("bindings" de taille) entre la fenêtre, le menu et la liste.
 * - Le déclenchement sans erreur des actions utilisateur (clic sur Créer, clic sur Actualiser).
 */
@DisplayName("Tests du Contrôleur de la salle d'attente (sans Mockito)")
public class AttenteLobbyControllerTest {

    private AttenteLobbyController controller;
    private ListView<String> mockListView;
    private MenuBar mockMenu;
    private Pane mockPane;

    @BeforeAll
    static void initJFX() {
        // Initialisation de l'environnement JavaFX pour autoriser la création de ListView/Pane
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Toolkit déjà initialisé (ex: si on lance la suite de test en entier)
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        controller = new AttenteLobbyController();

        // 1. Initialiser les composants JavaFX réels (au lieu de mocks Mockito)
        mockListView = new ListView<>();
        mockMenu = new MenuBar();
        mockPane = new Pane();

        // 2. Assigner manuellement ces composants aux champs @FXML privés via la réflexion Java
        Field listeLobbyField = AttenteLobbyController.class.getDeclaredField("listeLobby");
        listeLobbyField.setAccessible(true);
        listeLobbyField.set(controller, mockListView);

        Field menuField = AttenteLobbyController.class.getDeclaredField("menu");
        menuField.setAccessible(true);
        menuField.set(controller, mockMenu);

        Field paneField = AttenteLobbyController.class.getDeclaredField("pane");
        paneField.setAccessible(true);
        paneField.set(controller, mockPane);
    }

    @Test
    @DisplayName("L'initialisation doit lier la largeur du menu à celle du Pane principal")
    void testerInitializeBindingMenuWidth() {
        controller.initialize(null, null);
        mockPane.resize(800, 600);
        
        assertEquals(800, mockMenu.getPrefWidth(), "Le menu doit s'adapter à la largeur du pane");
    }

    @Test
    @DisplayName("L'initialisation doit lier la largeur de la liste à celle du Pane principal")
    void testerInitializeBindingListWidth() {
        controller.initialize(null, null);
        mockPane.resize(800, 600);
        
        assertEquals(800, mockListView.getPrefWidth(), "La liste doit s'adapter à la largeur du pane");
    }

    @Test
    @DisplayName("L'initialisation doit lier la hauteur de la liste à celle du Pane principal")
    void testerInitializeBindingListHeight() {
        controller.initialize(null, null);
        mockPane.resize(800, 600);
        
        assertEquals(600, mockListView.getPrefHeight(), "La liste doit s'adapter à la hauteur du pane");
    }

    @Test
    @DisplayName("Le bouton Créer un lobby doit s'exécuter sans erreur")
    void testerBoutonCreateLobbyExecution() {
        assertDoesNotThrow(() -> controller.handleCreateLobby(), "Le clic sur 'Créer un lobby' ne doit pas lancer d'exception");
    }

    @Test
    @DisplayName("Le bouton Actualiser doit s'exécuter sans erreur")
    void testerBoutonRefreshExecution() {
        assertDoesNotThrow(() -> controller.handleRefresh(), "Le clic sur 'Actualiser' ne doit pas lancer d'exception");
    }
}
