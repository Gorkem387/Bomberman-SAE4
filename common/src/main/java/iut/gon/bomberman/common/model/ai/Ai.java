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
}