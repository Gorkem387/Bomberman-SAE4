package iut.gon.bomberman.common.model.labyrinthe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *  Tests unitaires pour la classe {@link Labyrinthe}
 *
 *  Cette classe de test vérifie :
 *  - La création d'un labyrinthe avec des dimensions valides et invalides.
 *  - L'initialisation par défaut des cellules.
 *  - La validation des coordonnées avec isInside().
 *  - La modification et la lecture des cellules avec setCell() et getCell().
 *  - La logique de traversabilité avec isWalkable().
 */
@DisplayName("Tests unitaires pour la classe Labyrinthe")
class LabyrintheTest {
    private Labyrinthe laby;

    @BeforeEach
    void setUp() {
        // Initialise un labyrinthe de test 5x5
        laby = new Labyrinthe(5, 5);
    }

    @Test
    @DisplayName("Vérification des dimensions du labyrinthe")
    void testDimensions() {
        // Vérifie la cohérence des dimensions
        assertEquals(5, laby.getWidth());
        assertEquals(5, laby.getHeight());
    }

    @Test
    @DisplayName("Création d'un labyrinthe avec des dimensions invalides doit lever une exception")
    void testDimensionsInvalides() {
        assertThrows(NegativeArraySizeException.class, () -> new Labyrinthe(-5, 5), "Créer un Labyrinthe avec une largeur négative doit lever une exception");
        assertThrows(NegativeArraySizeException.class, () -> new Labyrinthe(5, -5), "Créer un Labyrinthe avec une hauteur négative doit lever une exception");
    }

    @Test
    @DisplayName("Vérification de l'initialisation par défaut des cellules")
    void testInitialisationParDefaut() {
        // Vérifie que toutes les cellules sont initialisées à WALL
        for (int x = 0; x < laby.getWidth(); x++) {
            for (int y = 0; y < laby.getHeight(); y++) {
                assertEquals(CellType.WALL, laby.getCell(x, y), "La cellule (" + x + "," + y + ") devrait être un WALL par défaut.");
            }
        }
    }

    @Test
    @DisplayName("Vérification de la validité des coordonnées avec isInside()")
    void testIsInsideValide() {
        assertTrue(laby.isInside(0, 0), "Les coordonnées (0,0) doivent être valides");
        assertTrue(laby.isInside(4, 4), "Les coordonnées (4,4) doivent être valides");
    }

    @Test
    @DisplayName("Vérification de l'invalidité des coordonnées avec isInside()")
    void testIsInsideInvalide() {
        assertFalse(laby.isInside(-1, 2), "Une coordonnée x négative doit être invalide");
        assertFalse(laby.isInside(5, 2), "Une coordonnée x au-delà de la largeur doit être invalide");
        assertFalse(laby.isInside(2, -1), "Une coordonnée y négative doit être invalide");
        assertFalse(laby.isInside(2, 5), "Une coordonnée y au-delà de la hauteur doit être invalide");
    }

    @Test
    @DisplayName("Vérification de la lecture et de la modification des cellules avec getCell() et setCell()")
    void testSetCellValide() {
        laby.setCell(1, 1, CellType.EMPTY);
        assertEquals(CellType.EMPTY, laby.getCell(1, 1), "La cellule (1,1) doit être modifiée en EMPTY");
    }

    @Test
    @DisplayName("Vérification de l'accès hors limites avec getCell()")
    void testGetCellHorsLimites() {
        assertEquals(CellType.WALL, laby.getCell(10, 10), "Un accès hors limite doit renvoyer WALL par sécurité");
    }
        
    @Test
    @DisplayName("Vérification de la modification hors limites avec setCell()")
    void testSetCellHorsLimites() {
        assertDoesNotThrow(() -> laby.setCell(-1, -1, CellType.EMPTY), "Une modification hors limite ne doit pas lever d'exception");
    }

    @Test
    @DisplayName("Vérification de la traversabilité avec isWalkable() pour les cases vides et les bonus")
    void testIsWalkableTraversable() {
        laby.setCell(0, 0, CellType.EMPTY);
        laby.setCell(0, 2, CellType.SPEED_BONUS);
        laby.setCell(1, 0, CellType.FIRE_BONUS);
        laby.setCell(1, 1, CellType.BOMB_BONUS);
        laby.setCell(1, 2, CellType.HEAL_BONUS);
        laby.setCell(2, 0, CellType.EXPLOSION);
        
        assertTrue(laby.isWalkable(0, 0), "Une case vide doit être traversable");
        assertTrue(laby.isWalkable(0, 2), "Un bonus de vitesse doit être traversable");
        assertTrue(laby.isWalkable(1, 0), "Un bonus de feu doit être traversable");
        assertTrue(laby.isWalkable(1, 1), "Un bonus de bombe doit être traversable");
        assertTrue(laby.isWalkable(1, 2), "Un bonus de soin doit être traversable");
        assertTrue(laby.isWalkable(2, 0), "Une explosion doit être traversable");
    }

    @Test
    @DisplayName("Vérification de la traversabilité avec isWalkable() pour les obstacles")
    void testIsWalkableObstacles() {
        laby.setCell(0, 1, CellType.WALL);
        laby.setCell(2, 1, CellType.DESTRUCTIBLE);
        laby.setCell(2, 2, CellType.BOMB);
        
        assertFalse(laby.isWalkable(0, 1), "Un mur indestructible ne doit pas être traversable");
        assertFalse(laby.isWalkable(2, 1), "Un mur destructible ne doit pas être traversable");
        assertFalse(laby.isWalkable(2, 2), "Une bombe posée ne doit pas être traversable");
    }

    @Test
    @DisplayName("Vérification de la traversabilité avec isWalkable() pour les cases hors-limites")
    void testIsWalkableHorsLimites() {
        assertFalse(laby.isWalkable(-1, 0), "Une case hors-limites (négative) ne doit pas être traversable");
        assertFalse(laby.isWalkable(5, 5), "Une case hors-limites (trop grande) ne doit pas être traversable");
    }
}
