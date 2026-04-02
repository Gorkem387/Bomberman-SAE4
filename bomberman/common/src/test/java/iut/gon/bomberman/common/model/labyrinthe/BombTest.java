package iut.gon.bomberman.common.model.labyrinthe;

import iut.gon.bomberman.common.model.player.Joueur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe {@link Bomb}
 * 
 * Cette classe de test vérifie :
 * - L'initialisation correcte des propriétés d'une bombe (position, portée, timer, joueur).
 * - Le bon comportement de la diminution du timer avec la méthode tick().
 * - Le déclenchement de l'explosion lorsque le timer atteint zéro.
 * - Le changement de solidité de la bombe (traversable ou non-traversable).
 * - Que le temps de création correspond bien au moment de l'instanciation.
 */
@DisplayName("Tests unitaires pour la classe Bomb")
class BombTest {

    private Joueur joueur;
    private Bomb bombe;

    @BeforeEach
    void setUp() {
        // Initialisation pour chaque test
        joueur = new Joueur(1, "Gorke");
        bombe = new Bomb(10, 10, 3, joueur);
    }

    @Test
    @DisplayName("Vérifie l'initialisation de la position")
    void testInitialisationPosition() {
        assertEquals(10, bombe.getX(), "La coordonnée X initiale est incorrecte");
        assertEquals(10, bombe.getY(), "La coordonnée Y initiale est incorrecte");
    }

    @Test
    @DisplayName("Vérifie l'initialisation des propriétés (portée, joueur)")
    void testInitialisationProprietes() {
        assertEquals(3, bombe.getRange(), "La portée initiale est incorrecte");
        assertEquals(joueur, bombe.getJoueur(), "La bombe doit être associée au bon joueur");
    }

    @Test
    @DisplayName("Vérifie l'état initial de la bombe")
    void testInitialisationEtat() {
        assertEquals(3.0, bombe.getTimer(), 0.001, "Le timer initial de la bombe doit être de 3 secondes");
        assertFalse(bombe.isExploded(), "La bombe ne doit pas être considérée comme explosée au départ");
        assertFalse(bombe.isSolid(), "La bombe ne doit pas être solide à la pose");
    }

    @Test
    @DisplayName("Vérifie la diminution de la valeur du timer via la méthode tick()")
    void testValeurDiminutionTimer() {
        bombe.tick(1.5);
        assertEquals(1.5, bombe.getTimer(), 0.001, "La nouvelle valeur du timer après un passage de 1.5s est incorrecte");
    }
    
    @Test
    @DisplayName("Vérifie qu'un tick n'entraîne pas d'explosion prématurée")
    void testTickSansExplosion() {
        boolean result = bombe.tick(1.5);
        assertFalse(result, "La méthode tick doit retourner false car la bombe ne doit pas exploser");
        assertFalse(bombe.isExploded(), "La bombe ne doit pas être marquée comme explosée");
    }

    @Test
    @DisplayName("Vérifie l'état de la bombe juste avant l'explosion")
    void testAvantExplosion() {
        bombe.tick(2.9);
        assertFalse(bombe.isExploded(), "La bombe ne doit pas exploser avant d'atteindre zéro");
    }

    @Test
    @DisplayName("Vérifie le déclenchement exact de l'explosion")
    void testDeclenchementExplosion() {
        bombe.tick(2.9); // Simule le passage du temps pour la rapprocher de l'explosion
        boolean result = bombe.tick(0.15); // Compense les erreurs de précision
        assertTrue(result, "La méthode tick doit retourner true dès que le timer est dépassé");
        assertTrue(bombe.isExploded(), "La bombe doit être marquée comme explosée");
    }

    @Test
    @DisplayName("Vérifie le déclenchement initial d'explosion au dépassement")
    void testPremiereExplosion() {
        bombe.tick(3.1);
        assertTrue(bombe.isExploded(), "La bombe doit avoir explosé en dépassant 3s");
    }

    @Test
    @DisplayName("Vérifie l'absence de redéclenchement post-explosion")
    void testPasDeDoubleExplosion() {
        bombe.tick(3.1); // Provoque l'explosion
        boolean secondTick = bombe.tick(0.1); 
        assertFalse(secondTick, "Une bombe déjà explosée ne doit plus rien déclencher (tick = false)");
    }

    @Test
    @DisplayName("Vérifie le comportement avec un tick de 0 seconde")
    void testTickZero() {
        boolean result = bombe.tick(0.0);
        assertFalse(result, "Un tick de 0 ne doit pas provoquer d'explosion");
        assertEquals(3.0, bombe.getTimer(), 0.001, "Le timer ne doit pas avoir changé");
    }

    @Test
    @DisplayName("Vérifie l'explosion si le temps écoulé est exactement égal au timer")
    void testTickExactementLimite() {
        boolean result = bombe.tick(3.0);
        assertTrue(result, "La bombe doit exploser si le tick est exactement égal au timer restant");
        assertTrue(bombe.isExploded(), "La bombe doit être marquée comme explosée");
        assertEquals(0.0, bombe.getTimer(), 0.001, "Le timer doit être exactement à 0");
    }

    @Test
    @DisplayName("Vérifie le passage à l'état solide")
    void testMettreSolide() {
        bombe.setSolid(true);
        assertTrue(bombe.isSolid(), "La bombe devrait être solide après modification");
    }

    @Test
    @DisplayName("Vérifie le retrait de l'état solide")
    void testMettreNonSolide() {
        bombe.setSolid(true);
        bombe.setSolid(false);
        assertFalse(bombe.isSolid(), "La bombe devrait redevenir non-solide après annulation");
    }

    @Test
    @DisplayName("Vérifie la cohérence de l'horodatage de création")
    void testTempsCreation() {
        long now = System.currentTimeMillis();
        assertTrue(bombe.getCreationTime() <= now);
        assertTrue(bombe.getCreationTime() > now - 1000);
    }
}
