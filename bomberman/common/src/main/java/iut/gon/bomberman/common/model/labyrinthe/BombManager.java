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
     * @param joueur Le joueur qui pose la bombe.
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

        // Vérifie qu'il n'y a pas déjà une bombe ici
        for (Bomb b : bombs) {
            if (b.getX() == bx && b.getY() == by) return false;
        }
        bombs.add(new Bomb(bx, by, range, joueur));
        joueur.setNb_bombes(joueur.getNb_bombes() - 1);

        System.out.println(String.format("[POSE] Bombe à (%d,%d) par %s", bx, by, joueur.getNom()));
        return true;
    }

    /**
     * Met à jour l'état des bombes et des explosions à chaque frame.
     * @param deltaTime Temps écoulé depuis la dernière frame (en secondes).
     * @param labyrinthe Référence du labyrinthe pour les collisions.
     * @param joueurs Liste des joueurs présents pour tester les dégâts et la solidité.
     */
    public boolean update(double deltaTime, Labyrinthe labyrinthe, List<Joueur> joueurs) {
        // Gestion du timer de l'effet visuel de l'explosion
        if (!explosionCells.isEmpty()) {
            explosionTimer -= deltaTime;

            // On vérifie les degats à chaque frame
            checkExplosionDamage(joueurs);

            if (explosionTimer <= 0) {
                explosionCells.clear();
            }
        }

        // Gestion du passage "traversable -> solide" des bombes
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

        boolean anExplosionOccurred = false;
        // Mise à jour des timers des bombes et déclenchement des explosions
        Iterator<Bomb> it = bombs.iterator();
        while (it.hasNext()) {
            Bomb bomb = it.next();
            boolean justExploded = bomb.tick(deltaTime);

            if (justExploded) {
                it.remove();
                anExplosionOccurred = true;

                // debug logs
                long tempsActuel = System.currentTimeMillis();
                long dureeReelle = tempsActuel - bomb.getCreationTime();
                System.out.println("------------------------------------");
                System.out.println(String.format("[EXPLOSION] Bombe à (%d,%d)", bomb.getX(), bomb.getY()));
                System.out.println(String.format(" > Temps écoulé : %d ms (Attendu: 3000 ms)", dureeReelle));
                System.out.println("------------------------------------");

                explode(bomb, labyrinthe);

                // On rend la bombe au proprio
                Joueur proprio = bomb.getJoueur();
                if (proprio != null) {
                    proprio.setNb_bombes(proprio.getNb_bombes() + 1);
                }
            }
        }
        return anExplosionOccurred;
    }

    /**
     * Calcule la zone d'impact d'une explosion et modifie le labyrinthe.
     * @param bomb La bombe arrivant à expiration.
     * @param labyrinthe Le labyrinthe subissant les modifications (murs brisés).
     */
    private void explode(Bomb bomb, Labyrinthe labyrinthe) {
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
                if (cell == CellType.WALL) break; // Mur incassable, on arrête les flammes
                explosionCells.add(new int[]{cx, cy});
                if (cell == CellType.DESTRUCTIBLE) {
                    handleBoxDestruction(labyrinthe, cx, cy);
                    break;
                }
            }
        }
    }

    /**
     * Gère le remplacement d'un mur destructible par du vide ou un bonus aléatoire.
     * @param laby Le labyrinthe à modifier.
     * @param x Coordonnée X de la caisse détruite.
     * @param y Coordonnée Y de la caisse détruite.
     */
    private void handleBoxDestruction(Labyrinthe laby, int x, int y) {
        double rand = Math.random();
        if (rand < 0.10) laby.setCell(x, y, CellType.FIRE_BONUS);
        else if (rand < 0.30) laby.setCell(x, y, CellType.SPEED_BONUS);
        else if (rand < 0.35) laby.setCell(x, y, CellType.BOMB_BONUS);
        else if (rand < 0.40) laby.setCell(x, y, CellType.HEAL_BONUS);
        else laby.setCell(x, y, CellType.EMPTY);
    }

    /**
     * Analyse les collisions entre les flammes actives et les hitboxes des joueurs.
     * * @param joueurs Liste des joueurs à tester.
     */
    private void checkExplosionDamage(List<Joueur> joueurs) {
        for (Joueur joueur : joueurs) {
            if (!joueur.isAlive() || !joueur.canTakeDamage()) continue;

            double pSize = 0.8;
            double pOffset = 0.1;
            double pLeft = joueur.getX() + pOffset;
            double pRight = joueur.getX() + pOffset + pSize;
            double pTop = joueur.getY() + pOffset;
            double pBottom = joueur.getY() + pOffset + pSize;

            for (int[] cell : explosionCells) {
                boolean collisionX = pRight > cell[0] && pLeft < cell[0] + 1;
                boolean collisionY = pBottom > cell[1] && pTop < cell[1] + 1;

                if (collisionX && collisionY) {
                    joueur.registerDamage();
                    System.out.println(joueur.getNom() + " touché ! PV restants: " + joueur.getPv());
                    if (joueur.getPv() <= 0) {
                        joueur.setAlive(false);
                        joueur.setNb_bombes(0);
                        System.out.println(joueur.getNom() + " est MORT !");
                    }
                    break; // Le joueur a été touché par cette explosion, on passe au joueur suivant
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
        for (Bomb b : bombs) {
            if (b.getX() == x && b.getY() == y && b.isSolid()) {
                return true;
            }
        }
        return false;
    }

    // Getters
    public List<Bomb> getBombs() { return bombs; }
    public List<int[]> getExplosionCells() { return explosionCells; }
    public boolean hasExplosion() { return !explosionCells.isEmpty(); }
}
