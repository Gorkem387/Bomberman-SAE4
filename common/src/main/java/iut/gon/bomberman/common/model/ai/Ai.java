public class Ai {
    private Joueur player;
    private Lobby lobby;
    private AiStrategies strategy;
    private Joueur trackedPlayer;

    public Ai(Joueur j, Lobby l, AiStrategies strategy){
        this.player = j;
        this.lobby = l;
        this.strategy = strategy;
    }

    public int track(){
        for(Joueur p : this.lobby.getPlayers()){
            if(p.getPv() > 0 && !p.equals(this.player)){
                this.trackedPlayer = p;
                return 0;
            }
        }
        return -1;
    }

    public void play(){
        while(this.player.getPv() > 0){
            this.strategy.play(this);
        }
    }

    public Joueur getPlayer() {
        return player;
    }

    public void setPlayer(Joueur player) {
        this.player = player;
    }

    public Lobby getLobby() {
        return lobby;
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    public AiStrategies getStrategy() {
        return strategy;
    }

    public void setStrategy(AiStrategies strategy) {
        this.strategy = strategy;
    }

    public Joueur getTrackedPlayer() {
        return trackedPlayer;
    }

    public void setTrackedPlayer(Joueur trackedPlayer) {
        this.trackedPlayer = trackedPlayer;
    }

    public void randomMove() {
        int[][] directions = {{1,0}, {-1,0}, {0,1}, {0,-1}};
        int bestX = this.player.getX();
        int bestY = this.player.getY();
        int bestRisk = this.lobby.getLabyrinthe().getHeatMap().readRisk(bestX, bestY);
        for (int[] d : directions) {
            int nx = bestX + d[0];
            int ny = bestY + d[1];
            if (this.lobby.getLabyrinthe().isWalkable(nx, ny)) {
                int risk = this.lobby.getLabyrinthe().getHeatMap().readRisk(nx, ny);

                if (risk < bestRisk || Math.random() < 0.2) { // un peu de chaos
                    bestRisk = risk;
                    bestX = nx;
                    bestY = ny;
                }
            }
        }
        player.setX(bestX);
        player.setY(bestY);
    }
}