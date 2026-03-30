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
        this.track();
        while(this.player.getPv() > 0 && trackedPlayer.getPv() > 0){
            if (trackedPlayer.getPv() < 0){
                this.track()
            }
            if (this.player.getX() > trackedPlayer.getX() && this.lobby.getLabyrinthe.get()){

            }
        }
    }
}