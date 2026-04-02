package iut.gon.bomberman.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests unitaires pour la configuration globale {@link GameSettings}.
 * <p>
 * Cette classe de test vérifie :
 * - Que les chemins par défaut (skin et bombe) ne sont pas nuls.
 * - Le bon fonctionnement de la modification/récupération du chemin du skin.
 * - Le bon fonctionnement de la modification/récupération du chemin de la bombe.
 */
@DisplayName("Tests des paramètres globaux du jeu (GameSettings)")
public class GameSettingsTest {

    @Test
    @DisplayName("Le skin de départ ne doit pas être nul")
    void testerMaintienOBLIGATOIRECheminSkinDefaut() {
        String currentSkin = GameSettings.getSelectedSkinPath();
        org.junit.jupiter.api.Assertions.assertNotNull(currentSkin, "Le skin par défaut ne doit pas être null pour ne pas crasher le rendu");
    }

    @Test
    @DisplayName("La bombe de départ ne doit pas être nulle")
    void testerMaintienOBLIGATOIRECheminBombeDefaut() {
        String currentBomb = GameSettings.getSelectedBombPath();
        org.junit.jupiter.api.Assertions.assertNotNull(currentBomb, "La bombe par défaut ne doit pas être null pour ne pas crasher le rendu");
    }

    @Test
    @DisplayName("Il doit être possible de modifier et récupérer le chemin du skin")
    void testerModificationCheminSkin() {
        String testPath = "/test/path/skin.png";
        GameSettings.setSelectedSkinPath(testPath);
        assertEquals(testPath, GameSettings.getSelectedSkinPath(), "Le chemin du skin enregistré doit correspondre à celui défini");
    }

    @Test
    @DisplayName("Il doit être possible de modifier et récupérer le chemin de la bombe")
    void testerModificationCheminBombe() {
        String testPath = "/test/path/bomb.png";
        GameSettings.setSelectedBombPath(testPath);
        assertEquals(testPath, GameSettings.getSelectedBombPath(), "Le chemin de la bombe enregistré doit correspondre à celui défini");
    }
}
