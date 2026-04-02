package iut.gon.bomberman.common.model.labyrinthe;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour les classes {@link DFSGenerator} et {@link KruskalGenerator}
 * 
 * Cette classe de test vérifie :
 * - Les dimensions générées par les algorithmes.
 * - La liberté des zones de spawn pour les joueurs.
 * - La présence aléatoire de murs destructibles.
 * - La fermeture parfaite des bordures horizontales et verticales du labyrinthe.
 */
public class LabyrintheGeneratorsTest {
    @Test
    @DisplayName("DFS - Zone de spawn en haut à gauche libre")
    void testerDFSZoneSpawnHautGauche() {
        DFSGenerator gen = new DFSGenerator();
        Labyrinthe laby = gen.createLabyrinthe(21, 21);
        assertNotEquals(CellType.WALL, laby.getCell(1, 1), "Le spawn (1,1) doit être libre pour le joueur 1");
    }

    @Test
    @DisplayName("DFS - Zone de spawn en bas à droite libre")
    void testerDFSZoneSpawnBasDroite() {
        DFSGenerator gen = new DFSGenerator();
        Labyrinthe laby = gen.createLabyrinthe(21, 21);
        assertNotEquals(CellType.WALL, laby.getCell(19, 19), "Le spawn (19,19) doit être libre pour le joueur 2");
    }

    @Test
    @DisplayName("Kruskal - Bonne largeur générée")
    void testerKruskalLargeur() {
        KruskalGenerator gen = new KruskalGenerator();
        Labyrinthe laby = gen.createLabyrinthe(15, 15);
        assertEquals(15, laby.getWidth(), "La largeur générée doit être respectée");
    }

    @Test
    @DisplayName("Kruskal - Bonne hauteur générée")
    void testerKruskalHauteur() {
        KruskalGenerator gen = new KruskalGenerator();
        Labyrinthe laby = gen.createLabyrinthe(15, 15);
        assertEquals(15, laby.getHeight(), "La hauteur générée doit être respectée");
    }

    @Test
    @DisplayName("DFS - Présence de murs destructibles")
    void testerDFSPresenceMursDestructibles() {
        DFSGenerator gen = new DFSGenerator();
        Labyrinthe laby = gen.createLabyrinthe(21, 21);
        // Parcourt le labyrinthe pour s'assurer que des murs destructibles existent
        boolean hasDestructible = false;
        for (int x = 0; x < 21; x++) {
            for (int y = 0; y < 21; y++) {
                if (laby.getCell(x, y) == CellType.DESTRUCTIBLE) {
                    hasDestructible = true;
                    break;
                }
            }
        }
        assertTrue(hasDestructible, "Le générateur doit placer des murs destructibles");
    }

    @Test
    @DisplayName("Kruskal - Zone de spawn en haut à gauche libre")
    void testerKruskalZoneSpawnHautGauche() {
        KruskalGenerator gen = new KruskalGenerator();
        Labyrinthe laby = gen.createLabyrinthe(21, 21);
        assertNotEquals(CellType.WALL, laby.getCell(1, 1), "Le spawn (1,1) doit être libre");
    }

    @Test
    @DisplayName("Kruskal - Zone de spawn en bas à droite libre")
    void testerKruskalZoneSpawnBasDroite() {
        KruskalGenerator gen = new KruskalGenerator();
        Labyrinthe laby = gen.createLabyrinthe(21, 21);
        assertNotEquals(CellType.WALL, laby.getCell(19, 19), "Le spawn (19,19) doit être libre");
    }

    @Test
    @DisplayName("Kruskal - Présence de murs destructibles")
    void testerKruskalPresenceMursDestructibles() {
        KruskalGenerator gen = new KruskalGenerator();
        Labyrinthe laby = gen.createLabyrinthe(21, 21);
        boolean hasDestructible = false;
        for (int x = 0; x < 21; x++) {
            for (int y = 0; y < 21; y++) {
                if (laby.getCell(x, y) == CellType.DESTRUCTIBLE) {
                    hasDestructible = true;
                    break;
                }
            }
        }
        assertTrue(hasDestructible, "KruskalGenerator doit placer des murs destructibles");
    }

    @Test
    @DisplayName("DFS - Bordures horizontales fermées")
    void testerDFSBorduresHorizontales() {
        DFSGenerator gen = new DFSGenerator();
        Labyrinthe laby = gen.createLabyrinthe(15, 15);
        int w = laby.getWidth();
        int h = laby.getHeight();
        for (int x = 0; x < w; x++) {
            assertEquals(CellType.WALL, laby.getCell(x, 0), "Le bord supérieur doit être un mur");
            assertEquals(CellType.WALL, laby.getCell(x, h - 1), "Le bord inférieur doit être un mur");
        }
    }

    @Test
    @DisplayName("DFS - Bordures verticales fermées")
    void testerDFSBorduresVerticales() {
        DFSGenerator gen = new DFSGenerator();
        Labyrinthe laby = gen.createLabyrinthe(15, 15);
        int w = laby.getWidth();
        int h = laby.getHeight();
        for (int y = 0; y < h; y++) {
            assertEquals(CellType.WALL, laby.getCell(0, y), "Le bord gauche doit être un mur");
            assertEquals(CellType.WALL, laby.getCell(w - 1, y), "Le bord droit doit être un mur");
        }
    }

    @Test
    @DisplayName("Kruskal - Bordures horizontales fermées")
    void testerKruskalBorduresHorizontales() {
        KruskalGenerator gen = new KruskalGenerator();
        Labyrinthe laby = gen.createLabyrinthe(15, 15);
        int w = laby.getWidth();
        int h = laby.getHeight();
        for (int x = 0; x < w; x++) {
            assertEquals(CellType.WALL, laby.getCell(x, 0), "Le bord supérieur doit être un mur");
            assertEquals(CellType.WALL, laby.getCell(x, h - 1), "Le bord inférieur doit être un mur");
        }
    }

    @Test
    @DisplayName("Kruskal - Bordures verticales fermées")
    void testerKruskalBorduresVerticales() {
        KruskalGenerator gen = new KruskalGenerator();
        Labyrinthe laby = gen.createLabyrinthe(15, 15);
        int w = laby.getWidth();
        int h = laby.getHeight();
        for (int y = 0; y < h; y++) {
            assertEquals(CellType.WALL, laby.getCell(0, y), "Le bord gauche doit être un mur");
            assertEquals(CellType.WALL, laby.getCell(w - 1, y), "Le bord droit doit être un mur");
        }
    }
}
