package iut.gon.bomberman.client.ai;

import iut.gon.bomberman.common.model.labyrinthe.CellType;
import iut.gon.bomberman.common.model.player.Joueur;

public enum AISTRATEGIES {
    /*AGGRESSIVE {
        @Override
        public void play(Ai ia) {
            final int  BOMB_COUNTDOWN = 3;

            if (ia.getTrackedPlayer() == null || ia.getTrackedPlayer().getPv() <= 0) {
                ia.track();
            }
            // Calcule la différence de position
            int dx = (int) (ia.getTrackedPlayer().getX() - ia.getPlayer().getX());
            int dy = (int) (ia.getTrackedPlayer().getY() - ia.getPlayer().getY());
            // Priorise le mouvement horizontal si dx != 0
            if (dx > 1 && ia.getLabyrinthe().getCell((int) (ia.getPlayer().getX() + 1), (int) ia.getPlayer().getY()) == CellType.EMPTY) {
                // Déplace vers la droite si possible
                ia.getPlayer().setX(ia.getPlayer().getX() + 1);

            } else if (dx < -1 && ia.getLabyrinthe().getCell((int) (ia.getPlayer().getX() - 1), (int) ia.getPlayer().getY()) == CellType.EMPTY) {
                // Déplace vers la gauche si possible
                ia.getPlayer().setX(ia.getPlayer().getX() - 1);
            } else if (dy > 1 && ia.getLabyrinthe().getCell((int) ia.getPlayer().getX(), (int) (ia.getPlayer().getY() + 1)) == CellType.EMPTY) {
                // Déplace vers le bas si possible
                ia.getPlayer().setY(ia.getPlayer().getY() + 1);
            } else if (dy < -1 && ia.getLabyrinthe().getCell((int) ia.getPlayer().getX(), (int) (ia.getPlayer().getY() - 1)) == CellType.EMPTY) {
                // Déplace vers le haut si possible
                ia.getPlayer().setY(ia.getPlayer().getY() - 1);
            } else if ((Math.abs(dx) + Math.abs(dy) <= 1) && ia.getPlayer().getNb_bombes() > 0) {
                ia.getLabyrinthe().setBomb(ia.getPlayer().getX(), ia.getPlayer().getY(), ia.getPlayer(), BOMB_COUNTDOWN);
            } else {
                ia.randomMove();
            }
        }
    },
    SURVIVOR {
        @Override
        public void play(Ai ia) {
            final int BOMB_COUNTDOWN = 3;

            int count = 0;
            for (Joueur j : ia.lobby.getJoueurs()) {
                if (j.getPv() > 0) {
                    count++;
                }
            }
            if (count > 1) {
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx == 0 && dy == 0) continue; // Ignore la position actuelle
                        int newX = ia.getPlayer().getX() + dx;
                        int newY = ia.getPlayer().getY() + dy;
                        if (ia.getLabyrinthe().getCell(newX, newY) == CellType.BOMB) {
                            // Si une bombe est détectée, essaie de se déplacer dans la direction opposée
                            int oppositeX = ia.getPlayer().getX() - dx;
                            int oppositeY = ia.getPlayer().getY() - dy;
                            if (ia.getLabyrinthe().isWalkable(oppositeX, oppositeY)) {
                                ia.getPlayer().setX(oppositeX);
                                ia.getPlayer().setY(oppositeY);
                                return;
                            }
                        }
                    }
                }
                // Si aucune bombe n'est détectée, se déplace aléatoirement
                ia.randomMove();

                if (Math.random() < 0.3 && ia.getPlayer().getNb_bombes() > 0 && ia.getLabyrinthe().getCell(ia.getPlayer().getX(), ia.getPlayer().getY()) == CellType.EMPTY) {
                    ia.getLabyrinthe().setBomb(ia.getPlayer().getX(), ia.getPlayer().getY(), ia.getPlayer(), BOMB_COUNTDOWN); // Compte à rebours de 3 tours (ajustez si nécessaire)
                }
            } else {
                ia.setStrategy(AISTRATEGIES.AGGRESSIVE);
            }
        }
    },
    CHAOS {
        @Override
        public void play(Ai ia) {
            final int BOMB_COUNTDOWN = 3;

            if (ia.getLabyrinthe().getHeatMap().readRisk(ia.getPlayer().getX(), ia.getPlayer().getY()) > 0) {
                //Si je suis dans une zone à risque, j'essaie de me déplacer aléatoirement
                ia.randomMove();
            } else {
                if (Math.random() < 0.5 && ia.getPlayer().getNb_bombes() > 0) {
                    ia.getLabyrinthe().setBomb(ia.getPlayer().getX(), ia.getPlayer().getY(), ia.getPlayer(), BOMB_COUNTDOWN);
                }
            }
        }
    };

    public void play(Ai ai) {
    }*/
}