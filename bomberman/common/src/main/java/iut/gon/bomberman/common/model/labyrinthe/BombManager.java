package iut.gon.bomberman.common.model.labyrinthe;

import iut.gon.bomberman.common.model.player.Joueur;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BombManager {
    private final List<int[]> explosionCells = new ArrayList<>();
    private final List<Bomb> bombs = new ArrayList<>();
    private static final double EXPLOSION_DURATION = 0.8;
    private double explosionTimer = 0;

    public boolean placeBomb(Joueur joueur, int range, Labyrinthe labyrinthe) {
        if (joueur.getNb_bombes() <= 0) return false;
        int bx = (int) Math.round(joueur.getX());
        int by = (int) Math.round(joueur.getY());
        if (!labyrinthe.isWalkable(bx, by)) return false;
        for (Bomb b : bombs) {
            if (b.getX() == bx && b.getY() == by) return false;
        }
        bombs.add(new Bomb(bx, by, range, joueur));
        joueur.setNb_bombes(joueur.getNb_bombes() - 1);
        return true;
    }

    public void update(double deltaTime, Labyrinthe labyrinthe, List<Joueur> joueurs) {
        if (!explosionCells.isEmpty()) {
            explosionTimer -= deltaTime;
            if (explosionTimer <= 0) {
                explosionCells.clear();
            }
        }
        for (Bomb bomb : bombs) {
            if (!bomb.isSolid()) {
                boolean playerOverlap = false;
                for (Joueur j : joueurs) {
                    double pSize = 0.9;
                    if (j.getX() < bomb.getX() + 1 &&
                            j.getX() + pSize > bomb.getX() &&
                            j.getY() < bomb.getY() + 1 &&
                            j.getY() + pSize > bomb.getY()) {
                        playerOverlap = true;
                        break;
                    }
                }
                // La bombe devient solide quand le joueur de la touche plus du tout
                if (!playerOverlap) {
                    bomb.setSolid(true);
                }
            }
        }

        Iterator<Bomb> it = bombs.iterator();
        while (it.hasNext()) {
            Bomb bomb = it.next();
            boolean justExploded = bomb.tick(deltaTime);

            if (justExploded) {
                it.remove();
                explode(bomb, labyrinthe, joueurs);

                if (!joueurs.isEmpty()) {
                    Joueur j = joueurs.get(0);
                    j.setNb_bombes(j.getNb_bombes() + 1);
                }
            }
        }
    }

    private void explode(Bomb bomb, Labyrinthe labyrinthe, List<Joueur> joueurs) {
        explosionCells.clear();
        explosionTimer = EXPLOSION_DURATION;
        explosionCells.add(new int[]{bomb.getX(), bomb.getY()});

        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for (int[] dir : directions) {
            for (int i = 1; i <= bomb.getRange(); i++) {
                int cx = bomb.getX() + dir[0] * i;
                int cy = bomb.getY() + dir[1] * i;

                if (!labyrinthe.isInside(cx, cy)) break;

                CellType cell = labyrinthe.getCell(cx, cy);

                if (cell == CellType.WALL) {
                    break;
                }

                explosionCells.add(new int[]{cx, cy});

                if (cell == CellType.DESTRUCTIBLE) {
                    double rand = Math.random();
                    if (rand < 0.10) {
                        // 10% speed bonus
                        labyrinthe.setCell(cx, cy, CellType.SPEED_BONUS);
                    } else {
                        // 90% bomb range bonus
                        labyrinthe.setCell(cx, cy, CellType.FIRE_BONUS);
                    }
                    break;
                }
            }
        }

        for (Joueur joueur : joueurs) {
            int jx = (int) joueur.getX();
            int jy = (int) joueur.getY();
            for (int[] cell : explosionCells) {
                if (cell[0] == jx && cell[1] == jy) {
                    joueur.setPv(joueur.getPv() - 1);
                    break;
                }
            }
        }
    }

    public boolean isBombAt(int x, int y) {
        for (Bomb b : bombs) {
            if (b.getX() == x && b.getY() == y && b.isSolid()) {
                return true;
            }
        }
        return false;
    }

    public List<Bomb> getBombs() { return bombs; }
    public List<int[]> getExplosionCells() { return explosionCells; }
    public boolean hasExplosion() { return !explosionCells.isEmpty(); }
}
