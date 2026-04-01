package iut.gon.bomberman.client.ai;

import iut.gon.bomberman.common.model.labyrinthe.BombManager;
import iut.gon.bomberman.common.model.player.Joueur;

public enum AISTRATEGIES {

    /**
     * Fonce sur la cible. Pose une bombe quand elle est proche, puis fuit.
     */
    AGGRESSIVE {
        @Override
        public void play(Ai ia, Joueur[] players, HeatMap hM, BombManager bM) {
            if (ia.getTrackedPlayer() == null || !ia.getTrackedPlayer().isAlive()) {
                ia.track(players);
            }
            if (ia.getTrackedPlayer() == null) { ia.randomMove(); return; }

            int myX = (int) ia.getPlayer().getX();
            int myY = (int) ia.getPlayer().getY();
            int tx  = (int) ia.getTrackedPlayer().getX();
            int ty  = (int) ia.getTrackedPlayer().getY();

            int dx   = tx - myX;
            int dy   = ty - myY;
            int dist = Math.abs(dx) + Math.abs(dy);

            // Proche, pose bombe et fuit
            if (dist <= 3) {
                ia.tryPlaceBomb(3);
                ia.randomMove();
                return;
            }

            // Se dirige vers la cible, essaie l'axe dominant en premier
            if (Math.abs(dx) >= Math.abs(dy)) {
                int ndx = dx > 0 ? 1 : -1;
                if (!ia.isBlocked(ndx, 0)) {
                    ia.setCurrentDirection(ndx, 0);
                } else if (dy != 0 && !ia.isBlocked(0, dy > 0 ? 1 : -1)) {
                    ia.setCurrentDirection(0, dy > 0 ? 1 : -1);
                } else {
                    ia.randomMove();
                }
            } else {
                int ndy = dy > 0 ? 1 : -1;
                if (!ia.isBlocked(0, ndy)) {
                    ia.setCurrentDirection(0, ndy);
                } else if (dx != 0 && !ia.isBlocked(dx > 0 ? 1 : -1, 0)) {
                    ia.setCurrentDirection(dx > 0 ? 1 : -1, 0);
                } else {
                    ia.randomMove();
                }
            }
        }
    },

    /**
     * Évite les bombes et survit. Passe en AGGRESSIVE en 1v1.
     */
    SURVIVOR {
        @Override
        public void play(Ai ia, Joueur[] players, HeatMap hM, BombManager bM) {
            long aliveCount = 0;
            for (Joueur j : players) if (j.isAlive()) aliveCount++;

            // 1v1, devient agressif
            if (aliveCount <= 2) {
                ia.setStrategy(AISTRATEGIES.AGGRESSIVE);
                return;
            }

            int myX = (int) ia.getPlayer().getX();
            int myY = (int) ia.getPlayer().getY();

            // Fuit si danger immédiat
            if (hM.readRisk(myX, myY) > 0) {
                ia.randomMove();
                return;
            }

            // Pose une bombe occasionnellement
            if (Math.random() < 0.15) {
                ia.tryPlaceBomb(3);
            }

            ia.randomMove();
        }
    },

    /**
     * Mouvement chaotique, pose des bombes fréquemment, fuit si danger.
     */
    CHAOS {
        @Override
        public void play(Ai ia, Joueur[] players, HeatMap hM, BombManager bM) {
            int myX = (int) ia.getPlayer().getX();
            int myY = (int) ia.getPlayer().getY();

            if (hM.readRisk(myX, myY) > 0) {
                ia.randomMove();
                return;
            }

            if (Math.random() < 0.4) {
                ia.tryPlaceBomb(3);
            }

            ia.randomMove();
        }
    };

    // Méthode abstraite, chaque stratégie doit l'implémenter
    public abstract void play(Ai ia, Joueur[] players, HeatMap hM, BombManager bM);
}