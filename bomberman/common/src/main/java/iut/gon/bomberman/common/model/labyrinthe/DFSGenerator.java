package iut.gon.bomberman.common.model.labyrinthe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Implémentation du générateur de labyrinthe utilisant l'algorithme
 * de recherche en profondeur (Depth-First Search - DFS).
 */
public class DFSGenerator implements LabyrintheFactory {
    private final Random random = new Random();

    /**
     * Méthode principale de la fabrique pour générer un labyrinthe complet.
     * @param width Largeur souhaitée.
     * @param height Hauteur souhaitée.
     * @return Un labyrinthe avec des couloirs et des murs destructibles.
     */
    @Override
    public Labyrinthe createLabyrinthe(int width, int height) {
        Labyrinthe labyrinthe = new Labyrinthe(width, height);
        generateRecursive(labyrinthe, 1, 1);
        addDestructibleWalls(labyrinthe);
        return labyrinthe;
    }

    /**
     * Algorithme récursif de creusement du labyrinthe.
     * Il avance de deux cases en deux cases pour laisser un mur entre chaque couloir.
     * * @param labyrinthe Le labyrinthe en cours de modification.
     * @param x Coordonnée X de départ.
     * @param y Coordonnée Y de départ.
     */
    public void generateRecursive(Labyrinthe labyrinthe, int x, int y) {
        labyrinthe.setCell(x, y, CellType.EMPTY);

        int[][] directions = {{0, 2}, {0, -2}, {2, 0}, {-2, 0}};
        List<int[]> dirList = new ArrayList<>(List.of(directions));
        Collections.shuffle(dirList, random);

        for (int[] dir : dirList) {
            int nx = x + dir[0];
            int ny = y + dir[1];

            if (labyrinthe.isInside(nx, ny) && labyrinthe.getCell(nx, ny) == CellType.WALL) {
                labyrinthe.setCell(x + dir[0] / 2, y + dir[1] / 2, CellType.EMPTY);
                generateRecursive(labyrinthe, nx, ny);
            }
        }
    }

    /**
     * Ajoute aléatoirement des murs destructibles sur les murs restants.
     * * @param labyrinthe Le labyrinthe à remplir.
     */
    public void addDestructibleWalls(Labyrinthe labyrinthe) {
        for (int x = 1; x < labyrinthe.getWidth() - 1; x++) {
            for (int y = 1; y < labyrinthe.getHeight() - 1; y++) {
                // Si c'est un mur et qu'on est pas dans un coin de spawn
                if (labyrinthe.getCell(x, y) == CellType.WALL && !isSpawnArea(x, y, labyrinthe)) {
                    if (random.nextDouble() < 0.4) {
                        labyrinthe.setCell(x, y, CellType.DESTRUCTIBLE);
                    }
                }
            }
        }
    }

    /**
     * Vérifie si une coordonnée se situe dans une zone de départ des joueurs.
     * On protège ces zones pour éviter que les joueurs soient bloqués dès le début.
     * * @param x Coordonnée X.
     * @param y Coordonnée Y.
     * @param laby Référence du labyrinthe.
     * @return true si la case est proche d'un coin de spawn.
     */
    private boolean isSpawnArea(int x, int y, Labyrinthe laby) {
        return (x <= 2 && y <= 2) ||
                (x >= laby.getWidth() - 3 && y >= laby.getHeight() - 3);
    }
}
