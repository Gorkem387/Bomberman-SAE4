package iut.gon.bomberman.client.ai;

import iut.gon.bomberman.common.model.labyrinthe.Labyrinthe;
import main.java.iut.gon.bomberman.common.model.ai.AISTRATEGIES;
import iut.gon.bomberman.common.model.player.Joueur;
import iut.gon.bomberman.client.MainApp;

public class Ai {
    private Joueur player;
    private Labyrinthe labyrinth;
    private AISTRATEGIES strategy;
    private Joueur trackedPlayer;
    private MainApp app;

    public Ai(Joueur j, Labyrinthe l, AISTRATEGIES strategy, MainApp a){
        this.player = j;
        this.labyrinth = l;
        this.strategy = strategy;
        this.app = a;
    }


    public int track(){
        for(Joueur p : this.a.getPlayers())  //Mettre la classe responsable de la gestion du jeu en local {
            if(p.getPv() > 0 && !p.equals(this.player)){
                this.trackedPlayer = p;
                return 0;
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

    public Labyrinthe getLabyrinthe() {
        return this.labyrinth;
    }

    public void setLobby(Labyrinthe labyrinth) {
        this.labyrinth = labyrinth;
    }

    public AISTRATEGIES getStrategy() {
        return strategy;
    }

    public void setStrategy(AISTRATEGIES strategy) {
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
        int bestRisk = this.getLabyrinthe().getHeatMap().readRisk(bestX, bestY);
        for (int[] d : directions) {
            int nx = bestX + d[0];
            int ny = bestY + d[1];
            if (this.getLabyrinthe().isWalkable(nx, ny)) {
                int risk = this.getLabyrinthe().getHeatMap().readRisk(nx, ny);

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