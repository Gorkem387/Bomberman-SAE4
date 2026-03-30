public interface Ai {
    private Joueur player;
    this.player = j;
    this.lobby = l;
    this.strategy = AiStrategies.SURVIVOR;
    private Joueur trackedPlayer;

    public Ai(Joueur j, Lobby l, AiStrategies strategy){
        this.player = j;
        this.lobby = l;
        this.strategy = strategy
    }

    public int track(){
        for(Joueur p : this.lobby.getPlayers()){
            if(p.getPv() > 0){
                this.trackedPlayer = p;
                return 0;
            }
        }
        return -1
    }

    public void setStrategy(Aistrategies strategy) {
        this.strategy = strategy;
    }

    public void play(){
        this.strategy.play(this);
    }
}