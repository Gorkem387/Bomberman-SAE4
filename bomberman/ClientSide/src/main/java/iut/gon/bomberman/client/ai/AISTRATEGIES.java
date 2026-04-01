package iut.gon.bomberman.client.ai;

import iut.gon.bomberman.common.model.labyrinthe.BombManager;
import iut.gon.bomberman.common.model.player.Joueur;

/**
 * Définit les différents comportements (stratégies) disponibles pour l'Intelligence Artificielle.
 * Chaque stratégie implémente une logique de décision spécifique pour le mouvement et la pose de bombes.
 */
public enum AISTRATEGIES {

    /**
     * Stratégie AGGRESSIVE :
     * Se focalise sur l'élimination des adversaires.
     * Elle traque activement un joueur cible, s'en approche au maximum,
     * pose une bombe à proximité immédiate, puis s'éloigne pour survivre à l'explosion.
     */
    AGGRESSIVE {
        @Override
        public void play(Ai ia, Joueur[] players, HeatMap hM, BombManager bM) {
            // Si aucune cible n'est définie ou si la cible actuelle est morte, on en cherche une nouvelle
            if (ia.getTrackedPlayer() == null || !ia.getTrackedPlayer().isAlive()) {
                ia.track(players);
            }

            // Si toujours pas de cible (IA seule sur la carte), mouvement aléatoire
            if (ia.getTrackedPlayer() == null) {
                ia.randomMove();
                return;
            }

            // Récupération des coordonnées pour le calcul de distance
            int myX = (int) ia.getPlayer().getX();
            int myY = (int) ia.getPlayer().getY();
            int tx  = (int) ia.getTrackedPlayer().getX();
            int ty  = (int) ia.getTrackedPlayer().getY();

            int dx   = tx - myX;
            int dy   = ty - myY;
            int dist = Math.abs(dx) + Math.abs(dy); // Distance de Manhattan

            // Condition d'attaque : si l'IA est à 3 cases ou moins de la cible
            if (dist <= 3) {
                ia.tryPlaceBomb(3); // Tente de poser une bombe de portée 3
                ia.randomMove();    // Fuit immédiatement après la pose
                return;
            }

            // Navigation vers la cible : tente de réduire l'écart sur l'axe le plus éloigné
            if (Math.abs(dx) >= Math.abs(dy)) {
                // Priorité à l'axe X
                int ndx = dx > 0 ? 1 : -1;
                if (!ia.isBlocked(ndx, 0)) {
                    ia.setCurrentDirection(ndx, 0);
                } else if (dy != 0 && !ia.isBlocked(0, dy > 0 ? 1 : -1)) {
                    // Chemin bloqué en X, tente un détour par l'axe Y
                    ia.setCurrentDirection(0, dy > 0 ? 1 : -1);
                } else {
                    ia.randomMove();
                }
            } else {
                // Priorité à l'axe Y
                int ndy = dy > 0 ? 1 : -1;
                if (!ia.isBlocked(0, ndy)) {
                    ia.setCurrentDirection(0, ndy);
                } else if (dx != 0 && !ia.isBlocked(dx > 0 ? 1 : -1, 0)) {
                    // Chemin bloqué en Y, tente un détour par l'axe X
                    ia.setCurrentDirection(dx > 0 ? 1 : -1, 0);
                } else {
                    ia.randomMove();
                }
            }
        }
    },

    /**
     * Stratégie SURVIVOR :
     * Comportement prudent qui privilégie la sécurité.
     * Elle surveille la HeatMap pour éviter les zones de danger.
     * Si la partie arrive en duel (1v1), elle bascule automatiquement en mode AGGRESSIVE.
     */
    SURVIVOR {
        @Override
        public void play(Ai ia, Joueur[] players, HeatMap hM, BombManager bM) {
            // Comptage des joueurs encore en lice
            long aliveCount = 0;
            for (Joueur j : players) if (j.isAlive()) aliveCount++;

            // Changement de comportement si duel final
            if (aliveCount <= 2) {
                ia.setStrategy(AISTRATEGIES.AGGRESSIVE);
                return;
            }

            int myX = (int) ia.getPlayer().getX();
            int myY = (int) ia.getPlayer().getY();

            // Réaction au danger : si la case actuelle est menacée par une bombe
            if (hM.readRisk(myX, myY) > 0) {
                ia.randomMove(); // Cherche une case libre pour fuir
                return;
            }

            // Pose de bombes défensives/opportunistes (faible probabilité)
            if (Math.random() < 0.15) {
                ia.tryPlaceBomb(3);
            }

            // Continue de patrouiller de manière sécurisée
            ia.randomMove();
        }
    },

    /**
     * Stratégie CHAOS :
     * Comportement imprévisible et destructeur.
     * Pose des bombes très fréquemment pour saturer le terrain,
     * tout en conservant un instinct de survie basique face au danger direct.
     */
    CHAOS {
        @Override
        public void play(Ai ia, Joueur[] players, HeatMap hM, BombManager bM) {
            int myX = (int) ia.getPlayer().getX();
            int myY = (int) ia.getPlayer().getY();

            // Priorité à la survie si une bombe va exploser sur l'IA
            if (hM.readRisk(myX, myY) > 0) {
                ia.randomMove();
                return;
            }

            // Probabilité élevée de poser une bombe à chaque tick de décision
            if (Math.random() < 0.4) {
                ia.tryPlaceBomb(3);
            }

            // Mouvement erratique sur la carte
            ia.randomMove();
        }
    };

    /**
     * Exécute la logique de la stratégie sélectionnée.
     * * @param ia      L'instance de l'IA qui exécute la stratégie.
     * @param players Le tableau de tous les joueurs pour la détection des cibles.
     * @param hM      La HeatMap pour l'analyse des risques au sol.
     * @param bM      Le gestionnaire de bombes pour les interactions physiques.
     */
    public abstract void play(Ai ia, Joueur[] players, HeatMap hM, BombManager bM);
}