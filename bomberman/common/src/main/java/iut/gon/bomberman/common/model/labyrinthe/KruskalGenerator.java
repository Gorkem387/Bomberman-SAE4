package iut.gon.bomberman.common.model.labyrinthe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class KruskalGenerator implements LabyrintheFactory {

    private final Random random = new Random();
    private int parent[];
    private int rank[];

    // Remonte l'arbre jusqu'à la racine
    private int find(int x){
        if (parent[x] != x)
            parent[x] = find(parent[x]);
        return parent[x];
    }

    /*
    Essaye de fusionner les ensembles a et b
    true -> Fusion | false -> Déjà même ensemble
     */
    public boolean union(int a, int b){
        // Les racines
        int ra = find(a);
        int rb = find(b);

        // Test si même ensemble
        if (ra == rb) return false;

        // Attache arbre de rang inférieur sous celui de rang supérieur
        if (rank[ra] < rank[rb]) { int tmp = ra; ra = rb; rb = tmp; }
        parent[rb] = ra; // rb devient enfant de ra

        // Si les deux rangs étaient égaux, la hauteur de ra augmente de 1
        if (rank[ra] == rank[rb]) rank[ra]++;

        return true;
    }

    // Créer le labyrinthe
    @Override
    public Labyrinthe createLabyrinthe(int width, int height) {
        Labyrinthe laby = new Labyrinthe(width, height);

        int cellCols = width / 2;
        int cellRows = height / 2;
        int total = cellCols * cellRows;

        // Étape 2 : initialiser l'Union-Find
        parent = new int[total];
        rank = new int[total];
        for (int i = 0; i < total; i++) { parent[i] = i; rank[i] = 0; }

        // Étape 3 : ouvrir les cellules logiques
        for (int cy = 0; cy < cellRows; cy++)
            for (int cx = 0; cx < cellCols; cx++)
                laby.setCell(1 + cx * 2, 1 + cy * 2, CellType.EMPTY);

        // Étapes 4 & 5 : lister et mélanger les murs
        List<int[]> walls = buildWallList(cellCols, cellRows);
        Collections.shuffle(walls, random);

        // Étape 6 : fusion de Kruskal
        applyKruskal(laby, walls, cellCols);

        // Étape 7 : murs destructibles
        addDestructibleWalls(laby);

        return laby;
    }

    // Construit la liste de tous les murs cassables du labyrinthe
    private List<int[]> buildWallList(int cellCols, int cellRows) {
        List<int[]> walls = new ArrayList<>();

        for (int cy = 0; cy < cellRows; cy++) {
            for (int cx = 0; cx < cellCols; cx++) {
                if (cx + 1 < cellCols) walls.add(new int[]{cx, cy, cx + 1, cy}); // droite
                if (cy + 1 < cellRows) walls.add(new int[]{cx, cy, cx, cy + 1}); // bas
            }
        }

        return walls;
    }

    // Parcourt la liste de murs déjà mélangée et applique la règle de Kruskal
    private void applyKruskal(Labyrinthe laby, List<int[]> walls, int cellCols) {
        for (int[] wall : walls) {
            int cx1 = wall[0], cy1 = wall[1];
            int cx2 = wall[2], cy2 = wall[3];

            int id1 = cy1 * cellCols + cx1;
            int id2 = cy2 * cellCols + cx2;

            if (union(id1, id2)) {
                int wallX = 1 + cx1 * 2 + (cx2 - cx1);
                int wallY = 1 + cy1 * 2 + (cy2 - cy1);
                laby.setCell(wallX, wallY, CellType.EMPTY);
            }
        }
    }

    // Parcourt les murs restants (non destructibles) et convertit 40% en destructible
    public void addDestructibleWalls(Labyrinthe laby) {
        for (int x = 1; x < laby.getWidth() - 1; x++) {
            for (int y = 1; y < laby.getHeight() - 1; y++) {
                if (laby.getCell(x, y) == CellType.WALL && !isSpawnArea(x, y, laby)) {
                    if (random.nextDouble() < 0.4)
                        laby.setCell(x, y, CellType.DESTRUCTIBLE);
                }
            }
        }
    }

    private boolean isSpawnArea(int x, int y, Labyrinthe laby) {
        return (x <= 2 && y <= 2) ||
                (x >= laby.getWidth() - 3 && y >= laby.getHeight() - 3);
    }
}