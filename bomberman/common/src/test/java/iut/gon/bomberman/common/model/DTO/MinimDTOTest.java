package iut.gon.bomberman.common.model.DTO;

import iut.gon.bomberman.common.model.Mess.MessageType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests unitaires pour l'objet de liste d'attente (MinimDTO).
 * <p>
 * Cette classe de test vérifie la structure des petits conteneurs DTO
 * envoyés en bloc via les requêtes réseau (utilisés pour alléger la bande passante).
 */
@DisplayName("Tests des DTO Minimalistes (MinimDTO)")
public class MinimDTOTest {

    /**
     * S'assure que le constructeur assigne correctement sa valeur.
     */
    @Test
    @DisplayName("Le constructeur doit paramétrer l'ID")
    void testerAssignationConstructeurId() {
        MinimDTO dto = new MinimDTO(5, 12, 14);
        assertEquals(5, dto.getId(), "L'id lu doit être 5");
    }

    /**
     * S'assure que le constructeur assigne correctement sa valeur.
     */
    @Test
    @DisplayName("Le constructeur doit paramétrer l'axe X")
    void testerAssignationConstructeurX() {
        MinimDTO dto = new MinimDTO(5, 12, 14);
        assertEquals(12, dto.getX(), "Le X lu doit être 12");
    }

    /**
     * S'assure que le constructeur assigne correctement sa valeur.
     */
    @Test
    @DisplayName("Le constructeur doit paramétrer l'axe Y")
    void testerAssignationConstructeurY() {
        MinimDTO dto = new MinimDTO(5, 12, 14);
        assertEquals(14, dto.getY(), "Le Y lu doit être 14");
    }

    /**
     * Vérifie que le modificateur (setter) pour l'ID est bien répercuté.
     */
    @Test
    @DisplayName("Le setter de l'ID doit altérer correctement le contenu interne")
    void testerModificateurId() {
        MinimDTO dto = new MinimDTO(0, 0, 0);
        dto.setId(1);
        assertEquals(1, dto.getId(), "L'id mis à jour doit être 1");
    }

    /**
     * Vérifie que le modificateur (setter) pour l'axe X est bien répercuté.
     */
    @Test
    @DisplayName("Le setter de l'axe X doit altérer correctement le contenu interne")
    void testerModificateurX() {
        MinimDTO dto = new MinimDTO(0, 0, 0);
        dto.setX(50);
        assertEquals(50, dto.getX(), "Le X mis à jour doit être 50");
    }

    /**
     * Vérifie que le modificateur (setter) pour l'axe Y est bien répercuté.
     */
    @Test
    @DisplayName("Le setter de l'axe Y doit altérer correctement le contenu interne")
    void testerModificateurY() {
        MinimDTO dto = new MinimDTO(0, 0, 0);
        dto.setY(51);
        assertEquals(51, dto.getY(), "Le Y mis à jour doit être 51");
    }

    /**
     * Valide l'appartenance du DTO au spectre des GameUpdate au niveau Enum.
     */
    @Test
    @DisplayName("Ce DTO représente formellement une entité sous GAME_UPDATE")
    void testerIntegrationConstanteDeType() {
        MinimDTO dto = new MinimDTO(1, 1, 1);
        assertEquals(MessageType.GAME_UPDATE, dto.getType(), "L'interface IDTO oblige de qualifier ce DTO comme un GAME_UPDATE");
    }
}
