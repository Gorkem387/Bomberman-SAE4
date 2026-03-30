package iut.gon.bomberman.client.ai;

import iut.gon.bomberman.common.model.labyrinthe.BombManager;
import iut.gon.bomberman.common.model.labyrinthe.Labyrinthe;
import iut.gon.bomberman.common.model.player.Joueur;
import iut.gon.bomberman.client.MainApp;

public class Ai {
    private Joueur player;
    private Labyrinthe labyrinth;
    private AISTRATEGIES strategy;
    private Joueur trackedPlayer;
    private MainApp app;
    private BombManager bombManager;

    public Ai(Joueur j, Labyrinthe l, BombManager bm, AISTRATEGIES strategy, MainApp a){
        this.player = j;
        this.labyrinth = l;
        this.bombManager = bm;
        this.strategy = strategy;
        this.app = a;
    }

    public int track(){
        return -1;
    }

    public void play(){
        if(this.player.getPv() > 0){
            this.strategy.play(this);
        }
    }

    public void randomMove() {
        int[][] directions = {{1,0}, {-1,0}, {0,1}, {0,-1}};

        for (int[] d : directions) {
            int nx = (int)this.player.getX() + d[0];
            int ny = (int)this.player.getY() + d[1];

            if (this.labyrinth.isWalkable(nx, ny)) {
                if (Math.random() < 0.2) {
                    player.setX(nx);
                    player.setY(ny);
                    break;
                }
            }
        }
    }

    public Joueur getPlayer() { return player; }
    public void setPlayer(Joueur player) { this.player = player; }
    public Labyrinthe getLabyrinthe() { return this.labyrinth; }
    public void setLobby(Labyrinthe labyrinth) { this.labyrinth = labyrinth; }
    public AISTRATEGIES getStrategy() { return strategy; }
    public void setStrategy(AISTRATEGIES strategy) { this.strategy = strategy; }
    public Joueur getTrackedPlayer() { return trackedPlayer; }
    public void setTrackedPlayer(Joueur trackedPlayer) { this.trackedPlayer = trackedPlayer; }
    public BombManager getBombManager() { return bombManager; }
}