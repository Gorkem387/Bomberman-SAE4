package iut.gon.bomberman.common.model.DTO;

import iut.gon.bomberman.common.model.Mess.MessageType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests unitaires pour l'objet de transfert {@link JoueurMisAJourDTO}.
 * <p>
 * Ce DTO est utilisé pour envoyer les coordonnées compressées sur le réseau.
 * Cette classe de test vérifie :
 * - La récupération des attributs simples et du type global lié.
 * - Le bon encodage des données (séquençage et type primitif) avec DataOutputStream.
 */
@DisplayName("Tests d'encodage DTO - Position des joueurs")
public class JoueurMisAJourDTOTest {

    /**
     * S'assure que le constructeur assigne correctement sa valeur.
     */
    @Test
    @DisplayName("Le constructeur doit enregistrer l'ID du joueur")
    void testerAssignationConstructeurId() {
        JoueurMisAJourDTO dto = new JoueurMisAJourDTO(10, 5, 8);
        assertEquals(10, dto.id, "L'identifiant attribué doit correspondre (10)");
    }

    /**
     * S'assure que le constructeur assigne correctement sa valeur.
     */
    @Test
    @DisplayName("Le constructeur doit enregistrer la valeur X")
    void testerAssignationConstructeurX() {
        JoueurMisAJourDTO dto = new JoueurMisAJourDTO(10, 5, 8);
        assertEquals(5, dto.x, "L'abscisse X doit correspondre (5)");
    }

    /**
     * S'assure que le constructeur assigne correctement sa valeur.
     */
    @Test
    @DisplayName("Le constructeur doit enregistrer la valeur Y")
    void testerAssignationConstructeurY() {
        JoueurMisAJourDTO dto = new JoueurMisAJourDTO(10, 5, 8);
        assertEquals(8, dto.y, "L'ordonnée Y doit correspondre (8)");
    }

    /**
     * Valide l'appartenance du DTO au spectre des GameUpdate au niveau Enum.
     */
    @Test
    @DisplayName("Le type renvoyé par ce DTO doit être un GAME_UPDATE")
    void testerTypeDeMessageReseau() {
        JoueurMisAJourDTO dto = new JoueurMisAJourDTO(10, 5, 8);
        
        assertEquals(MessageType.GAME_UPDATE, dto.getType(), "L'enveloppe renvoyée doit posséder l'en-tête GAME_UPDATE");
    }

    /**
     * Injecte le DTO dans un flux réseau mémoire court, lit instantanément sa sortie formatée,
     * et s'assure que la toute première donnée est bien le Flag unique "TYPE".
     */
    @Test
    @DisplayName("L'en-tête de la sérialisation doit correspondre au TYPE statique défini")
    void testerEncodageFluxReseauType() throws IOException {
        JoueurMisAJourDTO dto = new JoueurMisAJourDTO(42, 100, 200);
        dto.positionsAll = new ArrayList<>();
        dto.positionsAll.add(new MinimDTO(42, 100, 200));

        ByteArrayOutputStream byteOs = new ByteArrayOutputStream();
        DataOutputStream dataOs = new DataOutputStream(byteOs);
        dto.write(dataOs);

        DataInputStream dataIs = new DataInputStream(new ByteArrayInputStream(byteOs.toByteArray()));
        int typeLu = dataIs.readInt();
        
        assertEquals(JoueurMisAJourDTO.TYPE, typeLu, "L'en-tête doit correspondre à l'entier du TYPE défini par la classe (2)");
    }

    /**
     * Écrit le DTO et vérifie qu'une fois le type évacué, on récupère bien l'ID.
     */
    @Test
    @DisplayName("La seconde donnée encodée doit être la taille, puis l'ID")
    void testerEncodageFluxReseauId() throws IOException {
        JoueurMisAJourDTO dto = new JoueurMisAJourDTO(42, 100, 200);
        dto.positionsAll = new ArrayList<>();
        dto.positionsAll.add(new MinimDTO(42, 100, 200));

        ByteArrayOutputStream byteOs = new ByteArrayOutputStream();
        dto.write(new DataOutputStream(byteOs));

        DataInputStream dataIs = new DataInputStream(new ByteArrayInputStream(byteOs.toByteArray()));
        dataIs.readInt(); // saute type
        dataIs.readInt(); // saute size
        
        assertEquals(42, dataIs.readInt(), "L'id lu doit correspondre à celui écrit");
    }

    /**
     * Écrit le DTO et vérifie la position de l'axe X dans la chronologie réseau.
     */
    @Test
    @DisplayName("La troisième donnée encodée doit être l'abscisse (X)")
    void testerEncodageFluxReseauX() throws IOException {
        JoueurMisAJourDTO dto = new JoueurMisAJourDTO(42, 100, 200);
        dto.positionsAll = new ArrayList<>();
        dto.positionsAll.add(new MinimDTO(42, 100, 200));

        ByteArrayOutputStream byteOs = new ByteArrayOutputStream();
        dto.write(new DataOutputStream(byteOs));

        DataInputStream dataIs = new DataInputStream(new ByteArrayInputStream(byteOs.toByteArray()));
        dataIs.readInt(); // saute type
        dataIs.readInt(); // saute size
        dataIs.readInt(); // saute id
        
        assertEquals(100, dataIs.readInt(), "La valeur x lue doit correspondre");
    }

    /**
     * Écrit le DTO et vérifie que la toute dernière écriture concerne bien l'axe Y et que le flux se termine.
     */
    @Test
    @DisplayName("La quatrième et dernière donnée encode doit être l'ordonnée (Y)")
    void testerEncodageFluxReseauYEtFin() throws IOException {
        JoueurMisAJourDTO dto = new JoueurMisAJourDTO(42, 100, 200);
        dto.positionsAll = new ArrayList<>();
        dto.positionsAll.add(new MinimDTO(42, 100, 200));

        ByteArrayOutputStream byteOs = new ByteArrayOutputStream();
        dto.write(new DataOutputStream(byteOs));

        DataInputStream dataIs = new DataInputStream(new ByteArrayInputStream(byteOs.toByteArray()));
        dataIs.readInt(); // saute type
        dataIs.readInt(); // saute size
        dataIs.readInt(); // saute id
        dataIs.readInt(); // saute x
        
        assertEquals(200, dataIs.readInt(), "La valeur y lue doit correspondre");
    }
}
