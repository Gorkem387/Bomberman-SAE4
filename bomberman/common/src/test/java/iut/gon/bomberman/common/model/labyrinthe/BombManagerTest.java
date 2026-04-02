package iut.gon.bomberman.common.model.labyrinthe;

import iut.gon.bomberman.common.model.player.Joueur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe {@link BombManager}
 *
 * Cette classe de test vérifie :
 * - La pose de bombes (succès, échec si manque de munitions, retrait du stock).
 * - Les conséquences d'une explosion (dégâts aux joueurs, destruction des murs).
 * - La restitution des munitions au poseur après l'explosion de sa propre bombe.
 * - La gestion de la solidité dynamique de la bombe (traversable à la pose, solide ensuite).
 */
@DisplayName("Tests unitaires pour la classe BombManager")
class BombManagerTest {
    private BombManager bm;
    private Labyrinthe laby;
    private Joueur joueur;

    @BeforeEach
    void setUp() {
        bm = new BombManager();
        laby = new Labyrinthe(11, 11);
        joueur = new Joueur(1, "Gorke");
        joueur.setX(1);
        joueur.setY(1);
        laby.setCell(1, 1, CellType.EMPTY);
    }

    @Test
    @DisplayName("Vérifie que placer une bombe réussit et l'ajoute à la liste")
    void testPoseBombeAjouteListe() {
        boolean success = bm.placeBomb(joueur, 2, laby);
        assertTrue(success, "La méthode placeBomb doit retourner true");
        assertEquals(1, bm.getBombs().size(), "La liste des bombes doit contenir 1 élément");
    }

    @Test
    @DisplayName("Vérifie que poser une bombe diminue le stock du joueur")
    void testPoseBombeDiminueStock() {
        bm.placeBomb(joueur, 2, laby);
        assertEquals(2, joueur.getNb_bombes(), "Le stock du joueur doit diminuer de 1");
    }

    @Test
    @DisplayName("Vérifie l'impossibilité de poser une bombe sans munitions - Retourne false")
    void testPoseBombeSansMunitionsRetourFalse() {
        joueur.setNb_bombes(0);
        boolean success = bm.placeBomb(joueur, 2, laby);
        assertFalse(success, "Impossible de poser une bombe sans munitions");
    }

    @Test
    @DisplayName("Vérifie l'impossibilité de poser une bombe sans munitions - N'ajoute pas à la liste")
    void testPoseBombeSansMunitionsN_ajoutePas() {
        joueur.setNb_bombes(0);
        bm.placeBomb(joueur, 2, laby);
        assertEquals(0, bm.getBombs().size(), "Aucune bombe ne doit être ajoutée à la liste");
    }

    @Test
    @DisplayName("Vérifie qu'une explosion inflige des dégâts au joueur présent")
    void testExplosionInfligeDegatsJoueur() {
        bm.placeBomb(joueur, 2, laby);
        int pvInitiaux = joueur.getPv();
        bm.update(3.1, laby, List.of(joueur));
        assertTrue(joueur.getPv() < pvInitiaux, "Le joueur doit perdre des PV s'il est dans l'explosion");
    }

    @Test
    @DisplayName("Vérifie la destruction d'un mur destructible par une explosion")
    void testExplosionDetruitMurDestructible() {
        laby.setCell(2, 1, CellType.DESTRUCTIBLE);
        bm.placeBomb(joueur, 2, laby);
        bm.update(3.1, laby, List.of(joueur));
        assertNotEquals(CellType.DESTRUCTIBLE, laby.getCell(2, 1), "Le mur destructible doit disparaître");
    }

    @Test
    @DisplayName("Vérifie qu'une explosion est stoppée par un mur indestructible")
    void testExplosionArreteeParMur() {
        laby.setCell(2, 1, CellType.WALL);
        bm.placeBomb(joueur, 2, laby);
        bm.update(3.1, laby, List.of(joueur));
        // Si l'explosion est stoppée par le mur en (2,1), elle ne devrait pas atteindre (3,1)
        boolean hasExplosionAt3_1 = false;
        for (int[] cell : bm.getExplosionCells()) {
            if (cell[0] == 3 && cell[1] == 1) {
                hasExplosionAt3_1 = true;
                break;
            }
        }
        assertFalse(hasExplosionAt3_1, "L'explosion ne doit pas traverser un mur solide");
    }

    @Test
    @DisplayName("Vérifie la mort d'un joueur suite aux dégâts d'une explosion - Modifie l'état isAlive")
    void testJoueurMeurtDansExplosionChangeEtatAlive() {
        joueur.setPv(1); // Le joueur n'a plus qu'un seul PV
        bm.placeBomb(joueur, 2, laby);
        bm.update(3.1, laby, List.of(joueur));
        assertFalse(joueur.isAlive(), "Le joueur doit mourir s'il perd son dernier PV");
    }

    @Test
    @DisplayName("Vérifie la mort d'un joueur suite aux dégâts d'une explosion - Retire les bombes")
    void testJoueurMeurtDansExplosionReinitialiseBombes() {
        joueur.setPv(1);
        bm.placeBomb(joueur, 2, laby);
        bm.update(3.1, laby, List.of(joueur));
        assertEquals(0, joueur.getNb_bombes(), "Le joueur mort ne doit plus pouvoir poser de bombes");
    }

    @Test
    @DisplayName("Vérifie qu'un joueur déjà mort ne subit plus de dégâts")
    void testJoueurMortIgnoreExplosion() {
        joueur.setPv(0);
        joueur.setAlive(false);
        bm.placeBomb(joueur, 2, laby);
        bm.update(3.1, laby, List.of(joueur));
        assertEquals(0, joueur.getPv(), "Les PV d'un joueur mort ne doivent pas descendre en dessous de zéro");
    }

    @Test
    @DisplayName("Vérifie l'impossibilité de poser une bombe sur une case non-traversable")
    void testPoseBombeSurMurEchoue() {
        laby.setCell(1, 1, CellType.WALL);
        boolean success = bm.placeBomb(joueur, 2, laby);
        assertFalse(success, "Impossible de poser une bombe sur un mur");
        assertEquals(0, bm.getBombs().size(), "Aucune bombe ne doit être ajoutée");
    }

    @Test
    @DisplayName("Vérifie l'impossibilité de poser deux bombes sur la même case - Retourne false au 2eme essai")
    void testPoseDoubleBombeMemeCaseRetourFalse() {
        bm.placeBomb(joueur, 2, laby);
        boolean success2 = bm.placeBomb(joueur, 2, laby);
        assertFalse(success2, "Ne peut pas poser une deuxième bombe sur la même case");
    }

    @Test
    @DisplayName("Vérifie l'impossibilité de poser deux bombes sur la même case - Liste contient une seule bombe")
    void testPoseDoubleBombeMemeCaseTailleListe() {
        bm.placeBomb(joueur, 2, laby);
        bm.placeBomb(joueur, 2, laby);
        assertEquals(1, bm.getBombs().size(), "Il ne doit y avoir qu'une seule bombe");
    }

    @Test
    @DisplayName("Vérifie que hasExplosion() est false sur une case vide")
    void testIsBombAtVide() {
        assertFalse(bm.isBombAt(2, 2), "isBombAt doit renvoyer false sur une case sans bombe");
    }

    @Test
    @DisplayName("Vérifie que la méthode isBombAt renvoie false avant que la bombe soit solide")
    void testIsBombAtNonSolide() {
        bm.placeBomb(joueur, 2, laby);
        assertFalse(bm.isBombAt(1, 1), "isBombAt renvoie false si la bombe n'est pas encore solide");
    }
        
    @Test
    @DisplayName("Vérifie que la méthode isBombAt renvoie true après que la bombe devient solide")
    void testIsBombAtSolide() {
        bm.placeBomb(joueur, 2, laby);
        // Eloigner le joueur pour rendre la bombe solide
        joueur.setX(5);
        joueur.setY(5);
        bm.update(0.1, laby, List.of(joueur));
        
        assertTrue(bm.isBombAt(1, 1), "isBombAt doit renvoyer true pour une bombe solide aux coordonnées exactes");
    }

    @Test
    @DisplayName("Vérifie que hasExplosion() est true juste après la detonation")
    void testNettoyageCellulesPresenceInitiale() {
        bm.placeBomb(joueur, 2, laby);
        bm.update(3.1, laby, List.of(joueur));
        assertTrue(bm.hasExplosion(), "L'explosion doit être active immédiatement après detonation");
    }

    @Test
    @DisplayName("Vérifie le nettoyage des cellules d'explosion après le délai d'affichage")
    void testNettoyageCellulesAbsenceApresDelai() {
        bm.placeBomb(joueur, 2, laby);
        bm.update(3.1, laby, List.of(joueur));
        // Simuler le passage du délai d'explosion (0.8s) + marge
        bm.update(0.9, laby, List.of(joueur));
        
        assertFalse(bm.hasExplosion(), "Les cellules d'explosion doivent être nettoyées après le délai");
        assertTrue(bm.getExplosionCells().isEmpty(), "La liste des cellules d'explosion doit être vide");
    }

    @Test
    @DisplayName("Vérifie qu'une bombe fraîchement posée n'est pas solide pour le poseur")
    void testBombeTraversableInitiale() {
        bm.placeBomb(joueur, 2, laby);
        Bomb b = bm.getBombs().getFirst();
        bm.update(0.1, laby, List.of(joueur));
        assertFalse(b.isSolid(), "La bombe doit laisser le joueur sortir");
    }

    @Test
    @DisplayName("Vérifie qu'une bombe devient solide une fois le joueur éloigné")
    void testBombeSolideApresSortieJoueur() {
        bm.placeBomb(joueur, 2, laby);
        Bomb b = bm.getBombs().getFirst();
        joueur.setX(5); // On téléporte le joueur loin de la bombe
        joueur.setY(5);
        bm.update(0.1, laby, List.of(joueur));
        assertTrue(b.isSolid(), "La bombe doit devenir solide une fois le joueur parti car il n'est plus en collision avec elle");
    }
}

