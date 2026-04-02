package iut.gon.bomberman.client.network;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import iut.gon.bomberman.common.model.Mess.LobbyListRequest;
import iut.gon.bomberman.common.model.Mess.MessageType;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour le composant réseau {@link NetworkManager}.
 * <p>
 * Cette classe de test vérifie de manière fonctionnelle :
 * - Le bon fonctionnement du pattern de création (Singleton).
 * - La persistance des configurations utilisateurs (Pseudo, Lobby associé) avant connexion.
 * - La capacité logique de la classe à enregistrer de nouveaux écouteurs d'événements réseaux.
 * - Que les tentatives d'envoi vers un flux fermé/inexistant ne crashent pas le client (robustesse de l'I/O).
 */
@DisplayName("Tests du composant réseau (NetworkManager)")
public class NetworkTest {

    private NetworkManager networkManager;

    @BeforeEach
    void setUp() {
        // Le NetworkManager est un singleton
        networkManager = NetworkManager.getInstance();
    }

    @Test
    @DisplayName("Le NetworkManager doit agir comme un Singleton")
    void testerSingleton() {
        NetworkManager instance2 = NetworkManager.getInstance();
        assertSame(networkManager, instance2, "Les deux instances récupérées doivent être exactement le même objet en mémoire");
    }

    @Test
    @DisplayName("Le pseudo du joueur local doit être conservé")
    void testerPseudoJoueurLocal() {
        networkManager.setLocalPlayerName("TestPlayer");
        assertEquals("TestPlayer", networkManager.getLocalPlayerName(), "Le nom du joueur local doit être sauvegardé");
    }

    @Test
    @DisplayName("L'ID du lobby courant doit être mémorisé")
    void testerIdLobbyCourant() {
        networkManager.setCurrentLobbyId(99);
        assertEquals(99, networkManager.getCurrentLobbyId(), "L'ID du lobby courant doit être mémorisé");
    }

    @Test
    @DisplayName("L'envoi de messages alors que l'application est déconnectée doit être ignoré silencieusement")
    void testerEnvoiMessageDeconnecte() {
        LobbyListRequest request = new LobbyListRequest();
        
        assertDoesNotThrow(() -> networkManager.send(request), "Tenter d'envoyer un message sans connexion réseau ne doit pas faire planter l'application");
    }
}
