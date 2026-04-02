package iut.gon.serverside.Lob;

import iut.gon.bomberman.common.model.labyrinthe.TypeLab;
import iut.gon.bomberman.common.model.player.Joueur;
import iut.gon.serverside.Threads.ClientHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la gestion d'un Salon ({link Lobby}).
 * <p>
 * Vérifie :
 * - L'ajout de joueurs et l'assignation de leurs Handlers.
 * - Le retrait correct d'un joueur du salon.
 * - La gestion des erreurs potentielles lors de l'ajout.
 */
@DisplayName("Tests d'un Lobby (Salon de jeu serveur)")
public class LobbyTest {

    private Lobby lobby;

    /**
     * Initialise un environnement de test frais avant chaque exécution de méthode.
     * Crée un propriétaire et un nouveau lobby.
     */
    @BeforeEach
    void setUp() {
        Joueur owner = new Joueur(1, "Owner");
        lobby = new Lobby(10, "TestLobby", owner, 4, TypeLab.DEEPSEARCH, 10, 10);
    }

    /**
     * Vérifie que lorsqu'on ajoute un joueur, celui-ci est bien présent dans la liste des joueurs du lobby.
     */
    @Test
    @DisplayName("L'ajout d'un joueur doit l'inclure dans la liste des invités du lobby")
    void testerAjoutJoueurPresence() {
        Joueur joueur2 = new Joueur(2, "Player2");
        ClientHandler dummyHandler = null; // Sans Mockito, on utilise null si le comportement le permet
        
        lobby.addJoueur(joueur2, dummyHandler);
        
        assertTrue(lobby.getJoueurs().contains(joueur2), "Le joueur 2 doit être dans la liste des joueurs du lobby");
    }

    /**
     * Vérifie que la taille de la liste des joueurs correspond au nombre de joueurs ajoutés.
     */
    @Test
    @DisplayName("L'ajout d'un joueur doit mettre à jour la taille de la liste des joueurs")
    void testerAjoutJoueurTaille() {
        Joueur joueur2 = new Joueur(2, "Player2");
        ClientHandler dummyHandler = null;
        
        lobby.addJoueur(joueur2, dummyHandler);
        
        assertEquals(1, lobby.getJoueurs().size(), "Il doit y avoir un seul joueur invité");
    }

    /**
     * Vérifie que lorsqu'on retire un joueur, celui-ci n'est effectivement plus présent dans la liste.
     */
    @Test
    @DisplayName("Le retrait d'un joueur doit le supprimer de la liste")
    void testerRetraitJoueurAbsence() {
        Joueur joueur2 = new Joueur(2, "Player2");
        ClientHandler dummyHandler = null; 
        
        lobby.addJoueur(joueur2, dummyHandler);
        lobby.removeJoueur(joueur2);
        
        assertFalse(lobby.getJoueurs().contains(joueur2), "Le joueur 2 ne doit plus faire partie du lobby");
    }

    /**
     * Vérifie que la taille de la liste des joueurs redevient correcte (vide) après retrait.
     */
    @Test
    @DisplayName("Le retrait d'un joueur doit mettre à jour la taille de la liste")
    void testerRetraitJoueurTaille() {
        Joueur joueur2 = new Joueur(2, "Player2");
        ClientHandler dummyHandler = null; 
        
        lobby.addJoueur(joueur2, dummyHandler);
        lobby.removeJoueur(joueur2);
        
        assertEquals(0, lobby.getJoueurs().size(), "La liste doit être vide");
    }

    /**
     * S'assure que la méthode de mise à jour réseau (broadcastUpdate) ne provoque pas d'exception
     * même si les connexions fournies aux joueurs (handlers) ne sont pas initialisées.
     */
    @Test
    @DisplayName("L'ajout de joueur ne doit pas planter même sans connexion réseau réelle")
    void testerBroadcastUpdate() {
        Joueur joueur2 = new Joueur(2, "Player2");
        ClientHandler dummyHandler = null; 
        
        assertDoesNotThrow(() -> {
            // L'ajout appelle en interne broadcastUpdate()
            lobby.addJoueur(joueur2, dummyHandler);
        }, "L'ajout d'un joueur ne doit pas planter quand la connexion est nulle");
    }
}
