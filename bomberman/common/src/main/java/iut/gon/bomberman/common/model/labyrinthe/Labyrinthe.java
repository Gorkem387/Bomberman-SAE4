package iut.gon.bomberman.common.model.labyrinthe;

import java.io.Serializable;
import iut.gon.bomberman.common.model.player.Joueur;

/**
 * Représente la structure de données de la carte du jeu.
 * Gère la grille de cellules, les collisions et les limites du terrain.
 * Cette classe est Serializable pour être transmise via le réseau.
 */
public class Labyrinthe implements Serializable {
    private final int width;
    private final int height;
    private final CellType[][] grid;

    /**
     * Initialise un labyrinthe vide rempli de murs par défaut.
     * @param width Nombre de colonnes.
     * @param height Nombre de lignes.
     */
    public Labyrinthe(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new CellType[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j] = CellType.WALL;
            }
        }
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    /**
     * Vérifie si des coordonnées sont situées à l'intérieur des limites de la carte.
     * @param x Coordonnée X à tester.
     * @param y Coordonnée Y à tester.
     * @return true si les coordonnées sont valides.
     */
    public boolean isInside(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /**
     * Récupère le type de contenu d'une case spécifique.
     * @param x Coordonnée X.
     * @param y Coordonnée Y.
     * @return Le CellType de la case, ou WALL si en dehors des limites.
     */
    public CellType getCell(int x, int y) {
        return isInside(x, y) ? grid[x][y] : CellType.WALL;
    }

    /**
     * Modifie le contenu d'une case.
     * @param x Coordonnée X.
     * @param y Coordonnée Y.
     * @param type Le nouveau type de cellule à appliquer.
     */
    public void setCell(int x, int y, CellType type) {
        if (isInside(x, y)) {
            grid[x][y] = type;
        }
    }

    /**
     * Détermine si un joueur peut marcher sur une case donnée.
     * @param x Coordonnée X.
     * @param y Coordonnée Y.
     * @return true si la case n'est pas un obstacle solide (Mur ou Bombe).
     */
    public boolean isWalkable(int x, int y) {
        if (!isInside(x, y)) return false;
        CellType type = getCell(x, y);

        return type == CellType.EMPTY
                || type == CellType.EXPLOSION
                || type == CellType.SPEED_BONUS
                || type == CellType.FIRE_BONUS;
    }
}