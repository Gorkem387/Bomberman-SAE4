public enum Strategies {
    AGGRESSIVE {
        public void play(Ai ia) {
            if (trackedPlayer.getPv() < 0 || trackedPlayer == null) {
                ia.track();
            }
            // Calcule la différence de position
            int dx = ia.trackedPlayer.getX() - ia.player.getX();
            int dy = ia.trackedPlayer.getY() - ia.player.getY();
            // Priorise le mouvement horizontal si dx != 0
            if (dx > 1) {
                // Déplace vers la droite si possible
                if (ia.lobby.getLabyrinthe().getCell(ia.player.getX() + 1, ia.player.getY()) == CellType.EMPTY) {
                    ia.player.setX(ia.player.getX() + 1);
                }
            } else if (dx < -1) {
                // Déplace vers la gauche si possible
                if (ia.lobby.getLabyrinthe().getCell(ia.player.getX() - 1, ia.player.getY()) == CellType.EMPTY) {
                    ia.player.setX(ia.player.getX() - 1);
                }
            } else if (dy > 1) {
                // Déplace vers le bas si possible
                if (ia.lobby.getLabyrinthe().getCell(ia.player.getX(), ia.player.getY() + 1) == CellType.EMPTY) {
                    ia.player.setY(ia.player.getY() + 1);
                }
            } else if (dy < -1) {
                // Déplace vers le haut si possible
                if (ia.lobby.getLabyrinthe().getCell(ia.player.getX(), ia.player.getY() - 1) == CellType.EMPTY) {
                    ia.player.setY(ia.player.getY() - 1);
                }
            } else if (dy == 0 && dx == 0) {
                ia.player.setNb_bombes(ia.player.getNb_bombes() + 1);
                ia.lobby.getLabyrinthe().setBomb(ia.player.getX(), ia.player.getY(), ia.player, BOMB_COUNTDOWN);
            }
        }
    },
    SURVIVOR {
        public void play(Ai ia){
            int coiunt = 0;
            // Vérifie les cases adjacentes pour éviter les bombes
            for(Joueur j : lobby.getJoueurs()){
                if(j.getPv() > 0){
                    count++
                }
            }
            if(count > 1){
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
                int[] directions = {-1, 0, 1};
                int randomDx = directions[(int) (Math.random() * directions.length)];
                int randomDy = directions[(int) (Math.random() * directions.length)];
                int newX = ia.player.getX() + randomDx;
                int newY = ia.player.getY() + randomDy;
                if (ia.lobby.getLabyrinthe().isWalkable(newX, newY)) {
                    ia.player.setX(newX);
                    ia.player.setY(newY);
                }

                if (Math.random() < 0.3 && ia.player.getNb_bombes() > 0 && ia.lobby.getLabyrinthe().getCell(ia.player.getX(), ia.player.getY()) == CellType.EMPTY) {
                    ia.lobby.getLabyrinthe().setBomb(ia.player.getX(), ia.player.getY(), ia.player, 3); // Compte à rebours de 3 tours (ajustez si nécessaire)
                }
            } else {
                ia.setStrategy(AiStrategies.AGRESSIVE);
            }
        }
    }
}