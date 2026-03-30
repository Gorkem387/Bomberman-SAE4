package iut.gon.bomberman.common.model.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DFSGenerator implements LabyrintheFactory {
    private final Random random = new Random();

    public Labyrinthe createLabyrinthe(int width, int height) {
        Labyrinthe labyrinthe = new Labyrinthe(width, height);
        generateRecursive(labyrinthe, 1, 1);
        addDestructibleWalls(labyrinthe);
        return labyrinthe;
    }

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

    private boolean isSpawnArea(int x, int y, Labyrinthe laby) {
        return (x <= 2 && y <= 2) ||
                (x >= laby.getWidth() - 3 && y >= laby.getHeight() - 3);
    }
}
