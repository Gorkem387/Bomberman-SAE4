package iut.gon.bomberman.common.model.player;

/**
 * Représente les directions possibles pour le mouvement d'un joueur.
 * Utilisé par le moteur de jeu pour calculer les déplacements et par
 * le moteur de rendu pour sélectionner les animations (N, S, E, W).
 */
public enum Direction {
    UP, DOWN, LEFT, RIGHT, IDLE
}
