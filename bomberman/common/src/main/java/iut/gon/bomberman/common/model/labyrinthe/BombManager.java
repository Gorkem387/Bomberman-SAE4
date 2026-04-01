package iut.gon.bomberman.common.model.labyrinthe;

import iut.gon.bomberman.common.model.player.Joueur;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Gestionnaire central des bombes et des explosions.
 * Cette classe gère la pose, le décompte de temps, la propagation des flammes
 * et la détection précise des dégâts sur les joueurs.
 */
public class BombManager {
    private final List<int[]> explosionCells = new ArrayList<>();
    private final List<Bomb> bombs = new ArrayList<>();
    private static final double EXPLOSION_DURATION = 0.8;
    private double explosionTimer = 0;

    /**
     * Tente de placer une bombe sur la carte à la position du joueur.
     * * @param joueur Le joueur qui pose la bombe.
     * @param range La portée de l'explosion.
     * @param labyrinthe Le labyrinthe actuel pour vérifier si la case est libre.
     * @return true si la bombe a été posée, false sinon.
     */
    public boolean placeBomb(Joueur joueur, int range, Labyrinthe labyrinthe) {
        if (joueur.getNb_bombes() <= 0) return false;

        // Arondit la position pour centrer la bombe sur la case
        int bx = (int) Math.round(joueur.getX());
        int by = (int) Math.round(joueur.getY());
        if (!labyrinthe.isWalkable(bx, by)) return false;
        synchronized (bombs) {
            for (Bomb b : bombs) {
                if (b.getX() == bx && b.getY() == by) return false;
            }
            bombs.add(new Bomb(bx, by, range, joueur));
        }
        joueur.setNb_bombes(joueur.getNb_bombes() - 1);

        System.out.println(String.format("[POSE] Bombe à (%d,%d) par %s", bx, by, joueur.getNom()));
        return true;
    }

    /**
     * Met à jour l'état des bombes et des explosions à chaque frame.
     * * @param deltaTime Temps écoulé depuis la dernière frame (en secondes).
     * @param labyrinthe Référence du labyrinthe pour les collisions.
     * @param joueurs Liste des joueurs présents pour tester les dégâts et la solidité.
     */
    public void update(double deltaTime, Labyrinthe labyrinthe, List<Joueur> joueurs) {
        synchronized (explosionCells) {
            if (!explosionCells.isEmpty()) {
                explosionTimer -= deltaTime;
                if (explosionTimer <= 0) {
                    explosionCells.clear();
                }
            }
        }
        synchronized (bombs) {
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
                // On rend la bombe au proprio
                Joueur proprio = bomb.getJoueur();
                if (proprio != null) {
                    proprio.setNb_bombes(proprio.getNb_bombes() + 1);
                }
            }
        }
    }

    /**
     * Gère la détonation d'une bombe et propage les flammes.
     * * @param bomb La bombe qui explose.
     * @param labyrinthe Le labyrinthe à modifier (murs destructibles).
     * @param joueurs La liste des joueurs à tester pour les dégâts.
     */
    private void explode(Bomb bomb, Labyrinthe labyrinthe, List<Joueur> joueurs) {
        synchronized (explosionCells) {
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
                            // 10% fire
                            labyrinthe.setCell(cx, cy, CellType.FIRE_BONUS);
                        }
                        else if (rand < 0.30) {
                            // 20% speed
                            labyrinthe.setCell(cx, cy, CellType.SPEED_BONUS);
                        }
                        else {
                            // 70% rien
                            labyrinthe.setCell(cx, cy, CellType.EMPTY);
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
    }

    /**
     * Vérifie la présence d'une bombe solide sur une case donnée.
     * * @param x Coordonnée X.
     * @param y Coordonnée Y.
     * @return true si une bombe solide bloque la case.
     */
    public boolean isBombAt(int x, int y) {
        synchronized (bombs) {
            for (Bomb b : bombs) {
                if (b.getX() == x && b.getY() == y && b.isSolid()) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Bomb> getBombs() {
        synchronized (bombs) {
            return new java.util.ArrayList<>(bombs);
        }
    }

    public void setBombs(List<Bomb> newBombs) {
        synchronized (bombs) {
            this.bombs.clear();
            this.bombs.addAll(newBombs);
        }
    }
    public List<int[]> getExplosionCells() {
        synchronized (explosionCells) {
            return new java.util.ArrayList<>(explosionCells);
        }
    }

    public void setExplosionCells(List<int[]> newCells) {
        synchronized (explosionCells) {
            this.explosionCells.clear();
            this.explosionCells.addAll(newCells);
        }
    }

    public boolean hasExplosion() {
        synchronized (explosionCells) {
            return !explosionCells.isEmpty();
        }
    }
    // Getters
    public List<Bomb> getBombs() { return bombs; }
    public List<int[]> getExplosionCells() { return explosionCells; }
    public boolean hasExplosion() { return !explosionCells.isEmpty(); }
}
