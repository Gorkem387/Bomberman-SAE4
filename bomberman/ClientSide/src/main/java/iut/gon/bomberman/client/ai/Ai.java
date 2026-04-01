package iut.gon.bomberman.client.ai;

import iut.gon.bomberman.client.controllers.GameController;
import iut.gon.bomberman.common.model.labyrinthe.BombManager;
import iut.gon.bomberman.common.model.labyrinthe.Labyrinthe;
import iut.gon.bomberman.common.model.player.Direction;
import iut.gon.bomberman.common.model.player.Joueur;

public class Ai {

    private Joueur player;
    private Labyrinthe labyrinth;
    private AISTRATEGIES strategy;
    private Joueur trackedPlayer;
    private final HeatMap heatMap;
    private final BombManager bombManager;

    // Position logique sur la grille (entiers)
    private int gridX;
    private int gridY;

    // Position visuelle (doubles, interpolée entre deux cases)
    private double visualX;
    private double visualY;

    // Case cible vers laquelle on se déplace
    private int targetX;
    private int targetY;

    // Vitesse de déplacement en cases/seconde
    private static final double SPEED = 5.0;

    // verifie si sa bouge vers la case cible
    private boolean moving = false;

    // Direction courante
    private int currentDx = 0;
    private int currentDy = 1;

    // Cooldown bombe
    private static final double BOMB_COOLDOWN = 2.0;
    private double bombTimer = 0;

    // Cooldown décision stratégie
    private static final double DECISION_INTERVAL = 0.4;
    private double decisionTimer = 0;

    public Ai(Joueur player, Labyrinthe labyrinth, AISTRATEGIES strategy,
              GameController gc, HeatMap heatMap, BombManager bombManager) {
        this.player      = player;
        this.labyrinth   = labyrinth;
        this.strategy    = strategy;
        this.heatMap     = heatMap;
        this.bombManager = bombManager;

        // Initialise les positions depuis le joueur
        this.gridX   = (int) Math.round(player.getX());
        this.gridY   = (int) Math.round(player.getY());
        this.visualX = gridX;
        this.visualY = gridY;
        this.targetX = gridX;
        this.targetY = gridY;
    }

    /**
     * À appeler à chaque frame dans GameController.update(deltaTime).
     * Système à deux couches :
     * Logique  : déplacement case par case sur la grille (gridX, gridY)
     * Visuelle : interpolation fluide entre les cases (visualX, visualY)
     * Le joueur.setX/setY reçoit la position visuelle.
     * isBlocked() travaille sur la grille.
     */
    public void update(double deltaTime, Joueur[] players) {
        bombTimer    -= deltaTime;
        decisionTimer -= deltaTime;

        if (moving) {
            // Interpolation vers la case cible
            double dx = targetX - visualX;
            double dy = targetY - visualY;
            double dist = Math.sqrt(dx * dx + dy * dy);
            double step = SPEED * deltaTime;

            if (dist <= step) {
                // Arrivée sur la case cible
                visualX = targetX;
                visualY = targetY;
                gridX   = targetX;
                gridY   = targetY;
                moving  = false;

                // Synchronise la position logique du joueur
                player.setX(gridX);
                player.setY(gridY);

                player.checkBonus(labyrinth);
            } else {
                // Avance vers la cible
                visualX += (dx / dist) * step;
                visualY += (dy / dist) * step;

                // Met à jour la position visuelle du joueur
                player.setX(visualX);
                player.setY(visualY);
            }
        } else {
            // Pas en mouvement, choisit la prochaine case
            if (decisionTimer <= 0) {
                decisionTimer = DECISION_INTERVAL;
                strategy.play(this, players, heatMap, bombManager);
            } else if (isBlocked(currentDx, currentDy)) {
                // Bloqué, cherche une direction immédiatement
                randomMove();
            }

            // Lance le mouvement vers la case suivante si elle est libre
            int nx = gridX + currentDx;
            int ny = gridY + currentDy;
            if (!isBlocked(currentDx, currentDy)) {
                targetX = nx;
                targetY = ny;
                moving  = true;
            }

            updateDirection();
        }
    }

    private void updateDirection() {
        if (currentDx > 0)       player.setDirection(Direction.RIGHT);
        else if (currentDx < 0)  player.setDirection(Direction.LEFT);
        else if (currentDy > 0)  player.setDirection(Direction.DOWN);
        else if (currentDy < 0)  player.setDirection(Direction.UP);
        else                     player.setDirection(Direction.IDLE);
    }

    /**
     * Vérifie si la case (gridX+dx, gridY+dy) est accessible.
     * Travaille sur la position logique entière.
     */
    public boolean isBlocked(int dx, int dy) {
        int nx = gridX + dx;
        int ny = gridY + dy;
        return !labyrinth.isWalkable(nx, ny) || bombManager.isBombAt(nx, ny);
    }

    public void track(Joueur[] players) {
        for (Joueur p : players) {
            if (p.isAlive() && !p.equals(this.player)) {
                this.trackedPlayer = p;
                return;
            }
        }
    }

    public void setCurrentDirection(int dx, int dy) {
        this.currentDx = dx;
        this.currentDy = dy;
    }

    /**
     * Choisit la meilleure direction accessible sans aléatoire sur le choix principal.
     * Priorité : tout droit > latéral (choix aléatoire entre les deux) > demi-tour.
     */
    public void randomMove() {
        if (currentDx == 0 && currentDy == 0) {
            int[][] all = {{1,0},{-1,0},{0,1},{0,-1}};
            for (int[] d : all) {
                if (!isBlocked(d[0], d[1])) {
                    currentDx = d[0];
                    currentDy = d[1];
                    return;
                }
            }
            return;
        }

        // Tout droit (priorité absolue si libre)
        if (!isBlocked(currentDx, currentDy)) {
            return; // garde la direction actuelle
        }

        // Directions latérales, on les met dans un tableau et on les mélange
        // pour ne pas toujours tourner dans le même sens
        int[][] laterals = {
                {currentDy, -currentDx},   // tourner "gauche"
                {-currentDy, currentDx}    // tourner "droite"
        };
        // Mélange aléatoire simple entre les deux latérales
        if (Math.random() < 0.5) {
            int[] tmp = laterals[0]; laterals[0] = laterals[1]; laterals[1] = tmp;
        }
        for (int[] d : laterals) {
            if (!isBlocked(d[0], d[1])) {
                currentDx = d[0];
                currentDy = d[1];
                return;
            }
        }

        // Demi-tour en dernier recours uniquement
        int bdx = -currentDx;
        int bdy = -currentDy;
        if (!isBlocked(bdx, bdy)) {
            currentDx = bdx;
            currentDy = bdy;
        }
        // Si tout est bloqué → reste IDLE, ne change rien
    }

    public boolean tryPlaceBomb(int range) {
        if (bombTimer <= 0 && player.getNb_bombes() > 0) {
            boolean placed = bombManager.placeBomb(player, range, labyrinth);
            if (placed) bombTimer = BOMB_COOLDOWN;
            return placed;
        }
        return false;
    }

    // Getters / Setters
    public Joueur       getPlayer()                    { return player; }
    public void         setPlayer(Joueur p)            { this.player = p; }
    public Labyrinthe   getLabyrinthe()                { return labyrinth; }
    public AISTRATEGIES getStrategy()                  { return strategy; }
    public void         setStrategy(AISTRATEGIES s)    { this.strategy = s; }
    public Joueur       getTrackedPlayer()             { return trackedPlayer; }
    public BombManager  getBombManager()               { return bombManager; }
    public HeatMap      getHeatMap()                   { return heatMap; }
    public int          getCurrentDx()                 { return currentDx; }
    public int          getCurrentDy()                 { return currentDy; }
    public int          getGridX()                     { return gridX; }
    public int          getGridY()                     { return gridY; }
}