package iut.gon.bomberman.client.ai;

import static org.junit.jupiter.api.Assertions.*;

import iut.gon.bomberman.common.model.player.Effects.Bonus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import iut.gon.bomberman.common.model.labyrinthe.BombManager;
import iut.gon.bomberman.common.model.labyrinthe.Labyrinthe;
import iut.gon.bomberman.common.model.labyrinthe.CellType;
import iut.gon.bomberman.common.model.player.EtatJoueur;
import iut.gon.bomberman.common.model.player.Joueur;

/**
 * Tests unitaires pour la classe {@link Ai} et ses stratégies {@link AISTRATEGIES}.
 * <p>
 * Cette classe de test vérifie :
 * - L'initialisation de l'IA.
 * - Le repérage de la cible (track).
 * - La détection de blocages.
 * - Le comportement des différentes stratégies.
 */
@DisplayName("Tests de l'Intelligence Artificielle (IA)")
public class AIsTest {

    private Ai ia;
    private Joueur joueurIA;
    private Joueur joueurCible;
    private Labyrinthe labyrinthe;
    private BombManager bombManager;
    private HeatMap heatMap;

    @BeforeEach
    void setUp() {
        joueurIA = new Joueur(1, "Bot1", 1.0, 1.0, EtatJoueur.PRET, 3, 2, 2, new Bonus[0], 1.0f);
        joueurCible = new Joueur(2, "Player1", 5.0, 5.0, EtatJoueur.PRET, 3, 1, 1, new Bonus[0], 1.0f);
        
        labyrinthe = new Labyrinthe(10, 10); 
        // Le labyrinthe initialise toutes ses cases en CellType.WALL par défaut !
        // Pour les tests de mouvement de l'IA, on nettoie en mettant tout à EMPTY.
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                labyrinthe.setCell(i, j, CellType.EMPTY);
            }
        }
        
        bombManager = new BombManager();
        heatMap = new HeatMap(10, 10);
        
        ia = new Ai(joueurIA, labyrinthe, AISTRATEGIES.SURVIVOR, null, heatMap, bombManager);
    }

    @Nested
    @DisplayName("Tests d'initialisation et de suivi (Tracking)")
    class InitAndTrackTests {
        @Test
        @DisplayName("L'IA doit s'initialiser avec les coordonnées du joueur")
        void testerInitialisationPositions() {
            assertEquals(1, ia.getGridX());
            assertEquals(1, ia.getGridY());
        }

        @Test
        @DisplayName("L'IA doit correctement identifier et traquer un joueur en vie")
        void testerSuiviJoueurVivant() {
            Joueur[] joueurs = {joueurIA, joueurCible};
            ia.track(joueurs);
            assertEquals(joueurCible, ia.getTrackedPlayer(), "L'IA devrait cibler le joueur 2");
        }

        @Test
        @DisplayName("L'IA ne doit pas cibler un joueur mort")
        void testerNePasSuivreJoueurMort() {
            joueurCible.setAlive(false);
            Joueur[] joueurs = {joueurIA, joueurCible};
            ia.track(joueurs);
            assertNull(ia.getTrackedPlayer(), "L'IA ne devrait pas avoir de cible si tout le monde est mort");
        }
    }

    @Nested
    @DisplayName("Tests de la Stratégie AGGRESSIVE")
    class AggressiveStrategyTests {
        @Test
        @DisplayName("L'IA agressive doit chercher une cible si elle n'en a pas")
        void testerRechercheCibleAggressive() {
            ia.setStrategy(AISTRATEGIES.AGGRESSIVE);
            Joueur[] joueurs = {joueurIA, joueurCible};
            ia.getStrategy().play(ia, joueurs, heatMap, bombManager);
            
            assertNotNull(ia.getTrackedPlayer(), "La stratégie AGGRESSIVE doit forcer la recherche de cible");
            assertEquals(joueurCible, ia.getTrackedPlayer());
        }

        @Test
        @DisplayName("L'IA agressive doit s'approcher de sa cible (axe X ou Y)")
        void testerApprocheCibleAggressive() {
            ia.setStrategy(AISTRATEGIES.AGGRESSIVE);
            Joueur[] joueurs = {joueurIA, joueurCible};
            
            // On s'assure que le chemin est libre
            ia.getStrategy().play(ia, joueurs, heatMap, bombManager);
            
            // On vérifie que ia a voulu bouger vers la cible (pos:IA=1,1 Cible=5,5)
            // L'IA privilégie l'axe avec le plus de différence, ici la diff est égale donc il prend X ou Y 
            boolean aBouge = (ia.getCurrentDx() != 0 || ia.getCurrentDy() != 0);
            assertTrue(aBouge, "L'IA devrait modifier sa direction pour s'approcher de la cible");
        }
    }

    @Nested
    @DisplayName("Tests de la stratégie SURVIVOR")
    class SurvivorStrategyTests {
        @Test
        @DisplayName("SURVIVOR bascule en AGGRESSIVE s'il ne reste que 2 joueurs")
        void testerBasculeAggressiveDuel() {
            ia.setStrategy(AISTRATEGIES.SURVIVOR);
            Joueur[] joueurs = {joueurIA, joueurCible}; // 2 joueurs en vie
            
            ia.getStrategy().play(ia, joueurs, heatMap, bombManager);
            assertEquals(AISTRATEGIES.AGGRESSIVE, ia.getStrategy(), "L'IA doit devenir agressive en duel");
        }

        @Test
        @DisplayName("SURVIVOR fuit le danger de la HeatMap")
        void testerFuiteDangerSurvivor() {
            ia.setStrategy(AISTRATEGIES.SURVIVOR);
            Joueur joueur3 = new Joueur(3, "P3", 8.0, 8.0, EtatJoueur.PRET, 3, 1, 1, new Bonus[0], 1.0f);
            Joueur[] joueurs = {joueurIA, joueurCible, joueur3}; 
            
            // On met du danger de base (en supposant qu'elle ne bougeait pas sans danger)
            // On simule une bombe sur la case de l'IA
            heatMap.updateMap(1, 1, 10);
            
            ia.getStrategy().play(ia, joueurs, heatMap, bombManager);
            
            assertTrue(ia.getCurrentDx() != 0 || ia.getCurrentDy() != 0, "L'IA doit tenter de fuir la case dangereuse");
        }
    }

    @Nested
    @DisplayName("Tests des mouvements (randomMove & Détection de blocages)")
    class MovementsTests {

        @Test
        @DisplayName("L'IA poursuit tout droit si le chemin est libre")
        void testerMouvementToutDroitLibre() {
            ia.setCurrentDirection(1, 0); // On force déplacement à droite
            ia.randomMove();
            
            assertEquals(1, ia.getCurrentDx(), "L'IA doit garder sa direction X libre");
            assertEquals(0, ia.getCurrentDy(), "L'IA doit garder sa direction Y libre");
        }

        @Test
        @DisplayName("L'IA tourne sur un côté si le passage direct est bloqué")
        void testerMouvementTourneSiBloque() {
            ia.setCurrentDirection(1, 0); // On force déplacement à droite (vers x=2)
            
            // Simuler la présence d'un mur qui bloque le chemin de face (2,1)
            labyrinthe.setCell(2, 1, CellType.WALL);
            
            ia.randomMove();
            
            assertNotEquals(1, ia.getCurrentDx(), "L'IA a dû changer de direction sur X car bloquée");
            assertTrue(ia.getCurrentDy() == 1 || ia.getCurrentDy() == -1, "L'IA doit avoir pris une direction latérale Y (1 ou -1)");
        }

        @Test
        @DisplayName("L'IA fait demi-tour si l'avant et les côtés sont bloqués")
        void testerMouvementDemiTourSiImpasse() {
            ia.setCurrentDirection(1, 0); // Déplacement à droite
            
            // Bloquer devant (2,1)
            labyrinthe.setCell(2, 1, CellType.WALL);
            // Bloquer en haut (1,0)
            labyrinthe.setCell(1, 0, CellType.WALL);
            // Bloquer en bas (1,2)
            labyrinthe.setCell(1, 2, CellType.WALL);
            
            ia.randomMove();
            
            assertEquals(-1, ia.getCurrentDx(), "L'IA doit faire demi-tour et aller à gauche (-1)");
            assertEquals(0, ia.getCurrentDy(), "L'IA ne doit pas aller en Y car c'est bloqué");
        }
    }
}
