package iut.gon.bomberman.client.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests unitaires pour la classe {@link HeatMap}.
 * <p>
 * Cette classe de test vérifie :
 * - L'initialisation d'une grille de chaleur vide.
 * - L'ajout de valeurs de risque sur des cases précises.
 * - La relecture de ces valeurs de risque.
 * - La réinitialisation du danger d'une case.
 */
@DisplayName("Tests de la HeatMap (IA)")
public class HeatMapTest {

    private HeatMap heatMap;
    private final int WIDTH = 5;
    private final int HEIGHT = 5;

    @BeforeEach
    void setUp() {
        heatMap = new HeatMap(WIDTH, HEIGHT);
    }

    @Nested
    @DisplayName("Tests d'initialisation")
    class InitializationTests {
        @Test
        @DisplayName("Le HeatMap doit être initialisé avec des risques à zéro de partout")
        void testerRisqueInitialEstZero() {
            for (int i = 0; i < WIDTH; i++) {
                for (int j = 0; j < HEIGHT; j++) {
                    assertEquals(0, heatMap.readRisk(i, j), "La case (" + i + "," + j + ") devrait être à 0");
                }
            }
        }
    }

    @Nested
    @DisplayName("Tests de mise à jour du risque")
    class UpdateRiskTests {
        @Test
        @DisplayName("L'ajout d'une valeur de danger doit correctement s'enregistrer")
        void testerAjoutValeurRisque() {
            heatMap.updateMap(2, 2, 5);
            assertEquals(5, heatMap.readRisk(2, 2));
        }

        @Test
        @DisplayName("Les ajouts de danger doivent se cumuler")
        void testerCumulValeursRisque() {
            heatMap.updateMap(1, 1, 3);
            heatMap.updateMap(1, 1, 4);
            assertEquals(7, heatMap.readRisk(1, 1), "Le risque devrait être cumulé (3 + 4 = 7)");
        }
        
        @Test
        @DisplayName("L'ajout d'une valeur négative doit se soustraire")
        void testerSoustractionValeurNegative() {
            heatMap.updateMap(3, 3, 10);
            heatMap.updateMap(3, 3, -4);
            assertEquals(6, heatMap.readRisk(3, 3), "Le risque devrait être réduit (10 - 4 = 6)");
        }
    }

    @Nested
    @DisplayName("Tests de réinitialisation")
    class ResetRiskTests {
        @Test
        @DisplayName("La réinitialisation doit remettre le risque de la case spécifique à zéro")
        void testerReinitialisationRisque() {
            heatMap.updateMap(4, 4, 15);
            heatMap.resetRisk(4, 4);
            assertEquals(0, heatMap.readRisk(4, 4), "La case devrait être de nouveau à 0 après reset");
        }

        @Test
        @DisplayName("La réinitialisation d'une case ne doit pas affecter les autres (Vérification de la case ciblée)")
        void testerReinitialisationCaseCiblee() {
            heatMap.updateMap(0, 0, 10);
            heatMap.resetRisk(0, 0);
            assertEquals(0, heatMap.readRisk(0, 0), "La case ciblée devrait être remise à 0");
        }
        
        @Test
        @DisplayName("La réinitialisation d'une case ne doit pas affecter les autres (Vérification du voisin)")
        void testerReinitialisationInnocuiteSurVoisins() {
            heatMap.updateMap(0, 0, 10);
            heatMap.updateMap(0, 1, 20);
            heatMap.resetRisk(0, 0);
            assertEquals(20, heatMap.readRisk(0, 1), "La case voisine ne doit pas être affectée");
        }
    }
}
