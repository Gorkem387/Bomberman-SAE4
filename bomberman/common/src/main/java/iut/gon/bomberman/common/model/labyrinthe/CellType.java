package iut.gon.bomberman.common.model.labyrinthe;

/**
 * Énumération définissant la nature de chaque case de la grille.
 * Utilisé par le moteur de rendu pour l'affichage et par le moteur physique pour les collisions.
 */
public enum CellType {
    WALL,
    EMPTY,
    DESTRUCTIBLE,
    BOMB,
    EXPLOSION,
    SPEED_BONUS,
    FIRE_BONUS,
    BOMB_BONUS,
    HEAL_BONUS
}
