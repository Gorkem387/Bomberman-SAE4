public class AggressiveAi extends Ai {
    private Joueur player;
    private Lobby lobby;
    private Joueur trackedPlayer;

    public AggressiveAi(Joueur j, Lobby l){
        this.player = j;
        this.lobby = l;
    }

    public int track(){
        for(Joueur p : this.lobby.getPlayers()){
            if(p.getPv() > 0){
                this.trackedPlayer = p;
                return 0;
            }
        }
    }

    public void play(){
        if (trackedPlayer.getPv() < 0 || trackedPlayer == null){
            this.track();
        }
        // Calcule la différence de position
        int dx = this.trackedPlayer.getX() - this.player.getX();
        int dy = this.trackedPlayer.getY() - this.player.getY();
            // Priorise le mouvement horizontal si dx != 0
        if (dx > 0) {
            // Déplace vers la droite si possible
            if (this.lobby.getLabyrinthe().getCell(this.player.getX() + 1, this.player.getY()) == CellType.EMPTY) {
                this.player.setX(this.player.getX() + 1);
            }
        } else if (dx < 0) {
            // Déplace vers la gauche si possible
            if (this.lobby.getLabyrinthe().getCell(this.player.getX() - 1, this.player.getY()) == CellType.EMPTY) {
                this.player.setX(this.player.getX() - 1);
            }
        } else if (dy > 0) {
            // Déplace vers le bas si possible
            if (this.lobby.getLabyrinthe().getCell(this.player.getX(), this.player.getY() + 1) == CellType.EMPTY) {
                this.player.setY(this.player.getY() + 1);
            }
        } else if (dy < 0) {
            // Déplace vers le haut si possible
            if (this.lobby.getLabyrinthe().getCell(this.player.getX(), this.player.getY() - 1) == CellType.EMPTY) {
                this.player.setY(this.player.getY() - 1);
            }
        } else if (dy == 0 && dx == 0){
            this.player.setNb_bombes(this.player.getNb_bombes() + 1);
            this.lobby.getLabyrinthe().setcell(this.player.getX(), this.player.getY(), CellType.BOMB);
        }
        // Si dx == 0 et dy == 0, l'IA est déjà sur le joueur (ne bouge pas)
    }
}