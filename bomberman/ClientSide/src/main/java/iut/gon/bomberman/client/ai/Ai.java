package iut.gon.bomberman.client.ai;

import iut.gon.bomberman.client.controllers.GameController;
import iut.gon.bomberman.common.model.labyrinthe.BombManager;
import iut.gon.bomberman.common.model.labyrinthe.Labyrinthe;
import iut.gon.bomberman.common.model.player.Direction;
import iut.gon.bomberman.common.model.player.Joueur;

/**
 * Représente l'intelligence artificielle pilotant un joueur dans le jeu Bomberman.
 * Cette classe gère la logique de décision, le déplacement fluide (interpolation)
 * entre les cases et les interactions avec l'environnement (bombes, bonus).
 */
public class Ai {

    /** Le joueur contrôlé par cette IA */
    private Joueur player;
    /** Référence au labyrinthe pour la détection de collisions */
    private Labyrinthe labyrinth;
    /** Stratégie actuelle adoptée par l'IA */
    private AISTRATEGIES strategy;
    /** Joueur ciblé/suivi par l'IA */
    private Joueur trackedPlayer;
    /** Carte thermique pour évaluer la dangerosité des cases */
    private final HeatMap heatMap;
    /** Gestionnaire des bombes pour la pose et la détection */
    private final BombManager bombManager;

    /** Position logique sur la grille (abscisse entière) */
    private int gridX;
    /** Position logique sur la grille (ordonnée entière) */
    private int gridY;

    /** Position visuelle interpolée pour l'affichage (abscisse) */
    private double visualX;
    /** Position visuelle interpolée pour l'affichage (ordonnée) */
    private double visualY;

    /** Abscisse de la case cible vers laquelle l'IA se dirige */
    private int targetX;
    /** Ordonnée de la case cible vers laquelle l'IA se dirige */
    private int targetY;

    /** Vitesse de déplacement constante en cases par seconde */
    private static final double SPEED = 5.0;

    /** Indique si l'IA est actuellement en train de se déplacer entre deux cases */
    private boolean moving = false;

    /** Direction actuelle sur l'axe X (-1, 0, 1) */
    private int currentDx = 0;
    /** Direction actuelle sur l'axe Y (-1, 0, 1) */
    private int currentDy = 1;

    /** Temps d'attente requis entre la pose de deux bombes */
    private static final double BOMB_COOLDOWN = 2.0;
    /** Chronomètre pour le cooldown des bombes */
    private double bombTimer = 0;

    /** Intervalle de temps entre deux prises de décision stratégique */
    private static final double DECISION_INTERVAL = 0.4;
    /** Chronomètre pour le cooldown des décisions */
    private double decisionTimer = 0;

    /**
     * Constructeur de l'IA.
     * Initialise les positions logiques et visuelles basées sur les coordonnées du joueur.
     *
     * @param player      Le joueur à contrôler
     * @param labyrinth   Le labyrinthe de la partie
     * @param strategy    La stratégie de départ
     * @param gc          Le contrôleur de jeu
     * @param heatMap     La carte de danger
     * @param bombManager Le gestionnaire de bombes
     */
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
     * Met à jour l'état de l'IA à chaque frame.
     * Gère le refroidissement des timers, l'interpolation du mouvement visuel
     * et l'exécution de la stratégie de jeu.
     *
     * @param deltaTime Temps écoulé depuis la dernière mise à jour
     * @param players   Liste des joueurs présents dans la partie
     */
    public void update(double deltaTime, Joueur[] players) {
        // Si l'IA est morte, on ne fait rien
        if (!player.isAlive()) {
            player.setDirection(Direction.IDLE);
            return;
        }

        // Mise à jour des timers de cooldown
        bombTimer    -= deltaTime;
        decisionTimer -= deltaTime;

        if (moving) {
            // Calcul du vecteur vers la cible et de la distance
            double dx = targetX - visualX;
            double dy = targetY - visualY;
            double dist = Math.sqrt(dx * dx + dy * dy);
            double step = SPEED * deltaTime;

            if (dist <= step) {
                // Arrivée sur la case cible : on stabilise les positions
                visualX = targetX;
                visualY = targetY;
                gridX   = targetX;
                gridY   = targetY;
                moving  = false;

                // Synchronise la position logique du joueur pour le moteur de jeu
                player.setX(gridX);
                player.setY(gridY);

                // Vérification du ramassage d'un éventuel bonus
                player.checkBonus(labyrinth);
            } else {
                // Progression fluide vers la cible (normalisation du vecteur)
                visualX += (dx / dist) * step;
                visualY += (dy / dist) * step;

                // Met à jour la position visuelle pour le rendu
                player.setX(visualX);
                player.setY(visualY);
            }
        } else {
            // Si l'IA est à l'arrêt, elle planifie son prochain mouvement
            if (decisionTimer <= 0) {
                // Temps de réflexion écoulé : exécution de la stratégie
                decisionTimer = DECISION_INTERVAL;
                strategy.play(this, players, heatMap, bombManager);
            } else if (isBlocked(currentDx, currentDy)) {
                // Si bloqué avant le prochain tick de décision, on cherche une issue
                randomMove();
            }

            // Déclenche le mouvement vers la case adjacente si le passage est libre
            int nx = gridX + currentDx;
            int ny = gridY + currentDy;
            if (!isBlocked(currentDx, currentDy)) {
                targetX = nx;
                targetY = ny;
                moving  = true;
            }

            // Met à jour l'animation graphique du joueur selon l'orientation
            updateDirection();
        }
    }

    /**
     * Met à jour l'état de l'énumération Direction du joueur en fonction des vecteurs de mouvement.
     */
    private void updateDirection() {
        if (currentDx > 0)       player.setDirection(Direction.RIGHT);
        else if (currentDx < 0)  player.setDirection(Direction.LEFT);
        else if (currentDy > 0)  player.setDirection(Direction.DOWN);
        else if (currentDy < 0)  player.setDirection(Direction.UP);
        else                     player.setDirection(Direction.IDLE);
    }

    /**
     * Vérifie si une collision (mur ou bombe) bloque le passage vers une direction donnée.
     *
     * @param dx Décalage en X
     * @param dy Décalage en Y
     * @return true si la case est bloquée, false si elle est franchissable
     */
    public boolean isBlocked(int dx, int dy) {
        int nx = gridX + dx;
        int ny = gridY + dy;
        return !labyrinth.isWalkable(nx, ny) || bombManager.isBombAt(nx, ny);
    }

    /**
     * Identifie et mémorise le premier joueur adverse encore en vie pour le suivre.
     *
     * @param players Liste de tous les joueurs de la partie
     */
    public void track(Joueur[] players) {
        for (Joueur p : players) {
            if (p.isAlive() && !p.equals(this.player)) {
                this.trackedPlayer = p;
                return;
            }
        }
    }

    /**
     * Modifie manuellement la direction de déplacement souhaitée.
     *
     * @param dx Vecteur X (-1, 0, 1)
     * @param dy Vecteur Y (-1, 0, 1)
     */
    public void setCurrentDirection(int dx, int dy) {
        this.currentDx = dx;
        this.currentDy = dy;
    }

    /**
     * Algorithme de déplacement par défaut pour éviter les blocages.
     * Priorise le maintien de la trajectoire actuelle, puis les virages,
     * et enfin le demi-tour si aucune autre option n'est possible.
     */
    public void randomMove() {
        // Cas particulier : l'IA est immobile, on cherche n'importe quelle direction libre
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

        // 1. Priorité : Continuer tout droit si possible
        if (!isBlocked(currentDx, currentDy)) {
            return;
        }

        // 2. Priorité : Chercher un virage latéral (gauche ou droite)
        int[][] laterals = {
                {currentDy, -currentDx},   // virage à 90°
                {-currentDy, currentDx}    // virage à -90°
        };

        // Mélange aléatoire pour ne pas avoir un comportement déterministe (tourner toujours à gauche)
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

        // 3. Priorité : Faire demi-tour si le reste est bouché
        int bdx = -currentDx;
        int bdy = -currentDy;
        if (!isBlocked(bdx, bdy)) {
            currentDx = bdx;
            currentDy = bdy;
        }
        // Si tout est bloqué, la direction reste inchangée (l'IA attend)
    }

    /**
     * Tente de poser une bombe à l'emplacement actuel si le cooldown est expiré
     * et que le joueur possède encore des bombes en stock.
     *
     * @param range La portée de l'explosion de la bombe
     * @return true si la bombe a été posée avec succès, false sinon
     */
    public boolean tryPlaceBomb(int range) {
        if (bombTimer <= 0 && player.getNb_bombes() > 0) {
            boolean placed = bombManager.placeBomb(player, range, labyrinth);
            if (placed) bombTimer = BOMB_COOLDOWN;
            return placed;
        }
        return false;
    }

    // --- Getters et Setters ---

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