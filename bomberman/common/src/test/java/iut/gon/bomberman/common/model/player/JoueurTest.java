package iut.gon.bomberman.common.model.player;

import iut.gon.bomberman.common.model.labyrinthe.BombManager;
import iut.gon.bomberman.common.model.labyrinthe.Labyrinthe;

import iut.gon.bomberman.common.model.player.Effects.Bonus;
import iut.gon.bomberman.common.model.labyrinthe.CellType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe {@link Joueur}.
 * <p>
 * Cette classe de test vérifie :
 * - L'instanciation correcte d'un joueur et ses valeurs par défaut.
 * - Le fonctionnement des getters et setters.
 * - La logique de déplacement (collisions avec les murs et les bombes).
 * - La logique de récupération des différents bonus sur le labyrinthe.
 */
@DisplayName("Tests de l'entité Joueur")
public class JoueurTest {

    private Joueur joueur;
    private Labyrinthe labyrinthe;
    private BombManager bombManager;

    @BeforeEach
    public void setup() {
        joueur = new Joueur(1, "TestPlayer");
        joueur.setX(1.0);
        joueur.setY(1.0); // Placer le joueur sur une case normalement valide en jeu pour éviter de tester en (0,0) (potentiellement un mur dans le labyrinthe par défaut)

        // Instanciation réelle (ou "spy" manuel) plutôt que Mockito 
        // Labyrinthe(3, 3) créé rempli de murs
        labyrinthe = new Labyrinthe(3, 3);
        // On vide la case sur laquelle est le joueur
        labyrinthe.setCell(1, 1, CellType.EMPTY);
        
        bombManager = new BombManager();
    }

    @Test
    @DisplayName("Vérifie les valeurs par défaut lors de la création d'un joueur")
    public void testJoueurCreation() {
        Joueur freshJoueur = new Joueur(1, "TestPlayer");
        assertEquals(1, freshJoueur.getId());
        assertEquals("TestPlayer", freshJoueur.getNom());
        assertEquals(0.0, freshJoueur.getX());
        assertEquals(0.0, freshJoueur.getY());
        assertEquals(EtatJoueur.NOT_CONNECTED, freshJoueur.getEtat());
        assertEquals(3, freshJoueur.getPv());
        assertEquals(3, freshJoueur.getNb_bombes());
        assertEquals(3, freshJoueur.getNb_bombes_max());
        assertEquals(1.0f, freshJoueur.getSpeed_multiplier());
        assertTrue(freshJoueur.isAlive());
    }

    @Test
    @DisplayName("Modification des coordonnées X")
    public void testXSetter() {
        joueur.setX(5.0);
        assertEquals(5.0, joueur.getX());
    }

    @Test
    @DisplayName("Modification des coordonnées Y")
    public void testYSetter() {
        joueur.setY(10.0);
        assertEquals(10.0, joueur.getY());
    }

    @Test
    @DisplayName("Modification des points de vie (PV)")
    public void testPvSetter() {
        joueur.setPv(1);
        assertEquals(1, joueur.getPv());
    }

    @Test
    @DisplayName("Modification de l'état 'vivant'")
    public void testAliveState() {
        joueur.setAlive(false);
        assertFalse(joueur.isAlive());
    }

    @Test
    @DisplayName("Augmentation de la portée d'explosion")
    public void testExplosionRange() {
        joueur.addExplosionRange();
        assertEquals(3, joueur.getExplosionRange());
    }

    @Test
    @DisplayName("Constructeur complet avec tous les paramètres")
    public void testConstructeurComplet() {
        Bonus[] bonuses = new Bonus[3];
        Joueur j2 = new Joueur(2, "Player2", 10.0, 15.0, EtatJoueur.PRET, 5, 4, 2, bonuses, 1.5f);

        assertEquals(2, j2.getId());
        assertEquals("Player2", j2.getNom());
        assertEquals(10.0, j2.getX());
        assertEquals(15.0, j2.getY());
        assertEquals(EtatJoueur.PRET, j2.getEtat());
        assertEquals(5, j2.getPv());
        assertEquals(4, j2.getNb_bombes_max());
        assertEquals(2, j2.getNb_bombes());
        assertEquals(bonuses, j2.getBonus());
        assertEquals(1.5f, j2.getSpeed_multiplier());
    }

    @Test
    @DisplayName("Changement de direction du joueur")
    public void testDirections() {
        assertEquals(Direction.DOWN, joueur.getDirection());
        joueur.setDirection(Direction.UP);
        assertEquals(Direction.UP, joueur.getDirection());
    }

    @Test
    @DisplayName("Modification du chemin de la skin du joueur")
    public void testSkinPath(){
        assertNotNull(joueur.getSkinPath());
        joueur.setSkinPath("/new/path.png");
        assertEquals("/new/path.png", joueur.getSkinPath());
    }

    @Test
    @DisplayName("Modification du nombre maximum de bombes")
    public void testMaxBombsSetter() {
        joueur.setNb_bombes_max(5);
        assertEquals(5, joueur.getNb_bombes_max());
    }

    @Test
    @DisplayName("Modification du nombre de bombes actuelles")
    public void testCurrentBombsSetter() {
        joueur.setNb_bombes(4);
        assertEquals(4, joueur.getNb_bombes());
    }

    @Test
    @DisplayName("Changement de l'état du joueur")
    public void testEtatSetter() {
        joueur.setEtat(EtatJoueur.PRET);
        assertEquals(EtatJoueur.PRET, joueur.getEtat());
    }

    @Test
    @DisplayName("Modification du multiplicateur de vitesse")
    public void testSpeedSetter() {
        joueur.setSpeed_multiplier(2.0f);
        assertEquals(2.0f, joueur.getSpeed_multiplier());
    }

    @Test
    @DisplayName("Changement de l'ID du joueur")
    public void testIdSetter() {
        joueur.setId(99);
        assertEquals(99, joueur.getId());
    }

    @Test
    @DisplayName("Changement du nom du joueur")
    public void testNomSetter() {
        joueur.setNom("NewName");
        assertEquals("NewName", joueur.getNom());
    }

    @Test
    @DisplayName("Modification des bonus du joueur")
    public void testBonusSetter() {
        Bonus[] newBonuses = new Bonus[2];
        joueur.setBonus(newBonuses);
        assertArrayEquals(newBonuses, joueur.getBonus());
    }

    @Test
    public void testRadius() {
        assertEquals(1, joueur.getRadius());
    }

    @Test
    @DisplayName("Le joueur se déplace correctement sur une case libre")
    public void testMoveLibre() {
        // Arrange : On libère la case à droite (2, 1)
        labyrinthe.setCell(2, 1, CellType.EMPTY);

        double deltaX = 1.0;
        double deltaY = 0.0;
        double deltaTime = 0.5;
        
        // Act : Le joueur en (1,1) essaie de bouger vers la droite
        joueur.move(deltaX, deltaY, deltaTime, labyrinthe, bombManager);
        
        // Assert : La direction a bien été mise à jour
        assertEquals(Direction.RIGHT, joueur.getDirection());
    }

    @Test
    @DisplayName("Le déplacement est bloqué par un mur")
    public void testMoveBloqueParMur() {
        // Arrange : La case au dessus (1,0) est un mur par défaut
        double startX = joueur.getX();
        double startY = joueur.getY();
        
        double deltaX = 0.0;
        double deltaY = -1.0; // Y négatif = direction UP
        double deltaTime = 0.5;

        // Act
        joueur.move(deltaX, deltaY, deltaTime, labyrinthe, bombManager);
        
        // Assert : Les coordonnées ne doivent pas changer
        assertEquals(startX, joueur.getX(), 0.001);
        assertEquals(startY, joueur.getY(), 0.001);
        assertEquals(Direction.UP, joueur.getDirection());
    }

    @Test
    @DisplayName("Le déplacement est bloqué par une bombe")
    public void testMoveBloqueParBombe() {
        // Configuration : Case gauche (0,1) est libre mais contient une bombe posée par un autre joueur virtuel
        labyrinthe.setCell(0, 1, CellType.EMPTY);
        Joueur poseur = new Joueur(2, "Poseur");
        poseur.setX(0.0);
        poseur.setY(1.0);
        bombManager.placeBomb(poseur, 1, labyrinthe);

        // Simulation : Il faut rendre la bombe solide en appelant update
        // (les bombes sont non solides tant que les joueurs sont dessus, si on update avec aucun joueur dessus, elle deviendra solide)
        bombManager.update(0.1, labyrinthe, new java.util.ArrayList<>());
        
        double startX = joueur.getX();
        double startY = joueur.getY();
        
        // Act
        joueur.move(-1.0, 0.0, 0.5, labyrinthe, bombManager); // direction left vers la bombe
        
        // Assert : Impossible d'avancer
        assertEquals(startX, joueur.getX(), 0.001);
        assertEquals(startY, joueur.getY(), 0.001);
        assertEquals(Direction.LEFT, joueur.getDirection());
    }

    @Test
    @DisplayName("Le joueur reste immobile sans entrée de mouvement")
    public void testMoveIdle() {
        joueur.move(0.0, 0.0, 0.5, labyrinthe, bombManager);
        assertEquals(Direction.IDLE, joueur.getDirection());
    }

    @Test
    @DisplayName("Récupération d'un bonus de Vitesse")
    public void testCheckBonusSpeed() {
        // Arrange
        labyrinthe.setCell(1, 1, CellType.SPEED_BONUS);
        float oldSpeed = joueur.getSpeed_multiplier();

        // Act
        boolean res = joueur.checkBonus(labyrinthe);

        // Assert
        assertTrue(res);
        assertEquals(oldSpeed + 0.2f, joueur.getSpeed_multiplier(), 0.001);
        assertEquals(CellType.EMPTY, labyrinthe.getCell(1, 1));
    }
    
    @Test
    @DisplayName("Récupération d'un bonus de Soin (Heal)")
    public void testCheckBonusPv() {
        joueur.setPv(1); // On descend les PV de 3 à 1
        labyrinthe.setCell(1, 1, CellType.HEAL_BONUS);

        boolean res = joueur.checkBonus(labyrinthe);

        assertTrue(res);
        assertEquals(2, joueur.getPv());
        assertEquals(CellType.EMPTY, labyrinthe.getCell(1, 1));
    }

    @Test
    @DisplayName("Impossible de dépasser 3 PV avec un bonus Heal")
    public void testCheckBonusPvMaxCap() {
        joueur.setPv(3); // Déjà au maximum
        labyrinthe.setCell(1, 1, CellType.HEAL_BONUS);

        boolean res = joueur.checkBonus(labyrinthe);

        assertTrue(res); // Le bonus est consommé
        assertEquals(3, joueur.getPv()); // Mais la vie reste à 3
        assertEquals(CellType.EMPTY, labyrinthe.getCell(1, 1));
    }

    @Test
    @DisplayName("Récupération d'un bonus de Portée d'Explosion (Fire)")
    public void testCheckBonusFire() {
        int oldRange = joueur.getExplosionRange();
        labyrinthe.setCell(1, 1, CellType.FIRE_BONUS);

        boolean res = joueur.checkBonus(labyrinthe);

        assertTrue(res);
        assertEquals(oldRange + 1, joueur.getExplosionRange());
        assertEquals(CellType.EMPTY, labyrinthe.getCell(1, 1));
    }

    @Test
    @DisplayName("Récupération d'un bonus de Bombe")
    public void testCheckBonusBomb() {
        int oldMax = joueur.getNb_bombes_max();
        int oldActuelles = joueur.getNb_bombes();
        
        labyrinthe.setCell(1, 1, CellType.BOMB_BONUS);

        boolean res = joueur.checkBonus(labyrinthe);

        assertTrue(res);
        assertEquals(oldMax + 1, joueur.getNb_bombes_max());
        assertEquals(oldActuelles + 1, joueur.getNb_bombes());
        assertEquals(CellType.EMPTY, labyrinthe.getCell(1, 1));
    }

    @Test
    @DisplayName("Impossible de dépasser la limite de 6 bombes avec un bonus")
    public void testCheckBonusBombMaxCap() {
        joueur.setNb_bombes_max(6); // Au maximum autorisé
        joueur.setNb_bombes(6);
        labyrinthe.setCell(1, 1, CellType.BOMB_BONUS);

        boolean res = joueur.checkBonus(labyrinthe);

        assertTrue(res); // Le bonus est consommé
        assertEquals(6, joueur.getNb_bombes_max()); // Reste à 6
        assertEquals(6, joueur.getNb_bombes());
        assertEquals(CellType.EMPTY, labyrinthe.getCell(1, 1));
    }

    @Test
    @DisplayName("Aucune action lors de la récupération d'une case vide")
    public void testCheckBonusCaseVide() {
        labyrinthe.setCell(1, 1, CellType.EMPTY); 
        boolean res = joueur.checkBonus(labyrinthe);
        assertFalse(res); 
    }

    @Test
    @DisplayName("Aucune action si le bonus est hors limites du labyrinthe")
    public void testCheckBonusHorsLimite() {
        joueur.setX(100.0);
        joueur.setY(100.0);
        boolean res = joueur.checkBonus(labyrinthe);
        assertFalse(res);
    }

}
