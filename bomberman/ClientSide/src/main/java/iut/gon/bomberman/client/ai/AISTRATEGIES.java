package iut.gon.bomberman.client.ai;

import iut.gon.bomberman.common.model.player.Joueur;

public enum AISTRATEGIES {
    AGGRESSIVE {
        @Override
        public void play(Ai ia, Joueur[] players, HeatMap hM) {
            final int BOMB_COUNTDOWN = 3;
            if (ia.getTrackedPlayer() == null || !ia.getTrackedPlayer().isAlive()) {
                ia.track(players);
            }

            if (ia.getTrackedPlayer() != null) {
                double dx = ia.getTrackedPlayer().getX() - ia.getPlayer().getX();
                double dy = ia.getTrackedPlayer().getY() - ia.getPlayer().getY();
                if (Math.abs(dx) > Math.abs(dy)) {
                    ia.getPlayer().move(dx > 0 ? 1 : -1, 0, ia.getLabyrinthe(), ia.getBombManager());
                } else {
                    ia.getPlayer().move(0, dy > 0 ? 1 : -1, ia.getLabyrinthe(), ia.getBombManager());
                }
                if ((Math.abs(dx) + Math.abs(dy) <= 1.5) && ia.getPlayer().getNb_bombes() > 0) {
                    ia.getBombManager().placeBomb(ia.getPlayer(), BOMB_COUNTDOWN, ia.getLabyrinthe());
                }
            } else {
                ia.randomMove(hM);
            }
        }
    },

    SURVIVOR {
        @Override
        public void play(Ai ia, Joueur[] players, HeatMap hM) {
            final int BOMB_COUNTDOWN = 3;
            int myX = (int) Math.round(ia.getPlayer().getX());
            int myY = (int) Math.round(ia.getPlayer().getY());

            if (hM.readRisk(myX, myY) > 0) {
                ia.randomMove(hM);
            } else {
                if (Math.random() < 0.05) {
                    ia.getBombManager().placeBomb(ia.getPlayer(), BOMB_COUNTDOWN, ia.getLabyrinthe());
                }
                ia.randomMove(hM);
            }
        }
    },

    CHAOS {
        @Override
        public void play(Ai ia, Joueur[] players, HeatMap hM) {
            final int BOMB_COUNTDOWN = 3;
            if (Math.random() < 0.1) {
                ia.getBombManager().placeBomb(ia.getPlayer(), BOMB_COUNTDOWN, ia.getLabyrinthe());
            }
            ia.randomMove(hM);
        }
    };
    public abstract void play(Ai ia, Joueur[] players, HeatMap hM);
}