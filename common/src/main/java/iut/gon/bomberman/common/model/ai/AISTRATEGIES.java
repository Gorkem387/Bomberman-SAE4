public enum AISTRATEGIES {
    AGGRESSIVE {
        public void play(Ai ia) {
            public final int  BOMB_COUNTDOWN = 3;

            if (ia.trackedPlayer == null || ia.trackedPlayer.getPv() <= 0) {
                ia.track();
            }
            // Calcule la différence de position
            int dx = ia.trackedPlayer.getX() - ia.player.getX();
            int dy = ia.trackedPlayer.getY() - ia.player.getY();
            // Priorise le mouvement horizontal si dx != 0
            if (dx > 1 && ia.lobby.getLabyrinthe().getCell(ia.player.getX() + 1, ia.player.getY()) == CellType.EMPTY) {
                // Déplace vers la droite si possible
                ia.player.setX(ia.player.getX() + 1);

            } else if (dx < -1 && ia.lobby.getLabyrinthe().getCell(ia.player.getX() - 1, ia.player.getY()) == CellType.EMPTY) {
                // Déplace vers la gauche si possible
                ia.player.setX(ia.player.getX() - 1);
            } else if (dy > 1 && ia.lobby.getLabyrinthe().getCell(ia.player.getX(), ia.player.getY() + 1) == CellType.EMPTY) {
                // Déplace vers le bas si possible
                ia.player.setY(ia.player.getY() + 1);
            } else if (dy < -1 && ia.lobby.getLabyrinthe().getCell(ia.player.getX(), ia.player.getY() - 1) == CellType.EMPTY) {
                // Déplace vers le haut si possible
                ia.player.setY(ia.player.getY() - 1);
            } else if ((Math.abs(dx) + Math.abs(dy) <= 1) && ia.player.getNb_bombes() > 0) {
                ia.lobby.getLabyrinthe().setBomb(ia.player.getX(), ia.player.getY(), ia.player, BOMB_COUNTDOWN);
            } else {
                ia.randomMove();
            }
        }
    },
    SURVIVOR {
        public void play(Ai ia) {
            public final int BOMB_COUNTDOWN = 3;

            int count = 0;
            for (Joueur j : lobby.getJoueurs()) {
                if (j.getPv() > 0) {
                    count++;
                }
            }
            if (count > 1) {
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx == 0 && dy == 0) continue; // Ignore la position actuelle
                        int newX = ia.player.getX() + dx;
                        int newY = ia.player.getY() + dy;
                        if (ia.lobby.getLabyrinthe().getCell(newX, newY) == CellType.BOMB) {
                            // Si une bombe est détectée, essaie de se déplacer dans la direction opposée
                            int oppositeX = ia.player.getX() - dx;
                            int oppositeY = ia.player.getY() - dy;
                            if (ia.lobby.getLabyrinthe().isWalkable(oppositeX, oppositeY)) {
                                ia.player.setX(oppositeX);
                                ia.player.setY(oppositeY);
                                return;
                            }
                        }
                    }
                }
                // Si aucune bombe n'est détectée, se déplace aléatoirement
                ia.randomMove();

                if (Math.random() < 0.3 && ia.player.getNb_bombes() > 0 && ia.lobby.getLabyrinthe().getCell(ia.player.getX(), ia.player.getY()) == CellType.EMPTY) {
                    ia.lobby.getLabyrinthe().setBomb(ia.player.getX(), ia.player.getY(), ia.player, BOMB_COUNTDOWN); // Compte à rebours de 3 tours (ajustez si nécessaire)
                }
            } else {
                ia.setStrategy(AISTRATEGIES.AGGRESSIVE);
            }
        }
    },
    CHAOS {
        public void play(Ai ia) {
            public final int BOMB_COUNTDOWN = 3;

            if (ia.lobby.getLabyrinthe().getHeatMap().readRisk(ia.player.getX(), ia.player.getY()) > 0) {
                //Si je suis dans une zone à risque, j'essaie de me déplacer aléatoirement
                ia.randomMove();
            } else {
                if (Math.random() < 0.5 && ia.player.getNb_bombes() > 0) {
                    ia.lobby.getLabyrinthe().setBomb(ia.player.getX(), ia.player.getY(), ia.player, BOMB_COUNTDOWN);
                }
            }
        }
    }
}