package iut.gon.serverside;

import iut.gon.bomberman.common.model.labyrinthe.TypeLab;
import iut.gon.bomberman.common.model.player.Joueur;
import iut.gon.serverside.Lob.Lobby;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe {@link LobbyManager}.
 * <p>
 * Cette classe de test vérifie :
 * - L'unicité de l'instance via le design pattern Singleton.
 * - Le fait que la création d'un lobby génère un objet non nul.
 * - La bonne assignation du nom du lobby lors de sa création.
 * - L'enregistrement et la récupération d'un lobby via son ID généré.
 * - La suppression correcte d'un lobby existant par son ID.
 */
@DisplayName("Tests du LobbyManager (Côté Serveur)")
public class LobbyManagerTest {

    private LobbyManager lobbyManager;

    @BeforeEach
    void setUp() {
        lobbyManager = LobbyManager.getInstance();
        // Optionnel : nettoyer l'instance singleton si des tests l'ont modifiée (un peu plus difficile à faire sur un singleton natif sans réinitialisation)
    }

    /**
     * Vérifie que le gestionnaire de salons retourne toujours la même instance en mémoire,
     * respectant ainsi le patron de conception Singleton.
     */
    @Test
    @DisplayName("LobbyManager agit comme un Singleton")
    void testerUniciteSingleton() {
        LobbyManager instance2 = LobbyManager.getInstance();
        assertSame(lobbyManager, instance2, "Les deux instances de LobbyManager doivent pointer vers le même objet en mémoire");
    }

    /**
     * S'assure que la méthode de création d'un lobby retourne bien un objet instancié et non nul.
     */
    @Test
    @DisplayName("La création d'un lobby doit renvoyer une instance valide (non nulle)")
    void testerCreationLobbyRenvoieInstance() {
        Joueur host = new Joueur(1, "Owner");
        Lobby lobby = lobbyManager.createLobby(host, "TestLobby", 4, TypeLab.DEEPSEARCH, 10, 10);
        assertNotNull(lobby, "Le lobby créé ne doit pas être nul");
    }

    /**
     * Contrôle que le nom spécifié lors de la création du lobby est correctement appliqué à l'instance.
     */
    @Test
    @DisplayName("La création d'un lobby doit lui attribuer le nom demandé")
    void testerCreationLobbyAssigneNom() {
        Joueur host = new Joueur(1, "Owner");
        Lobby lobby = lobbyManager.createLobby(host, "TestLobby", 4, TypeLab.DEEPSEARCH, 10, 10);
        assertEquals("TestLobby", lobby.getNom(), "Le nom du lobby doit correspondre");
    }

    /**
     * Valide qu'un lobby fraîchement créé est bien enregistré en interne et peut être
     * récupéré ultérieurement grâce à son identifiant unique.
     */
    @Test
    @DisplayName("La création d'un lobby doit l'enregistrer et permettre sa récupération par ID")
    void testerRecuperationLobbyParId() {
        Joueur host = new Joueur(1, "Owner");
        Lobby lobby = lobbyManager.createLobby(host, "TestLobby", 4, TypeLab.DEEPSEARCH, 10, 10);
        assertSame(lobby, lobbyManager.getLobby(lobby.getId()), "On doit pouvoir récupérer le lobby via son ID généré");
    }

    /**
     * Vérifie que l'opération de suppression retire effectivement le lobby de la liste des salons actifs,
     * rendant sa récupération par identifiant impossible (retourne null).
     */
    @Test
    @DisplayName("Il doit pouvoir retirer un lobby via son ID")
    void testerSuppressionLobby() {
        Joueur host = new Joueur(2, "Owner2");
        Lobby lobby = lobbyManager.createLobby(host, "LobbyToDelete", 4, TypeLab.DEEPSEARCH, 10, 10);
        int id = lobby.getId();

        assertNotNull(lobbyManager.getLobby(id), "Le lobby doit exister avant suppression");

        lobbyManager.removeLobby(id);
        assertNull(lobbyManager.getLobby(id), "Le lobby doit être null après sa suppression");
    }
}
