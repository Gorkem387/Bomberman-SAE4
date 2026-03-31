package iut.gon.bomberman.client.ai;

import iut.gon.bomberman.client.controllers.GameController;
import iut.gon.bomberman.common.model.labyrinthe.Labyrinthe;
import iut.gon.bomberman.common.model.player.Joueur;
import iut.gon.bomberman.client.MainApp;

public class Ai {
    private Joueur player;
    private Labyrinthe labyrinth;
    private AISTRATEGIES strategy;
    private Joueur trackedPlayer;
    private HeatMap hM;

    public Ai(Joueur j, Labyrinthe l, AISTRATEGIES strategy, GameController gC, HeatMap hM){
        this.player = j;
        this.labyrinth = l;
        this.strategy = strategy;
        this.hM = hM;
    }


    public int track(Joueur[] players){
        for(Joueur p : players)  //Mettre la classe responsable de la gestion du jeu en local {
            if(p.getPv() > 0 && !p.equals(this.player)){
                this.trackedPlayer = p;
                return 0;
            }
        return -1;
    }


    public void play(Joueur[] players){
        while(this.player.getPv() > 0){
            this.strategy.play(this, players, this.hM);
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

    public void setLabyrinth(Labyrinthe labyrinth) {
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

    public void randomMove(HeatMap hM) {
        int[][] directions = {{1,0}, {-1,0}, {0,1}, {0,-1}};
        int bestX = (int) this.player.getX();
        int bestY = (int) this.player.getY();
        int bestRisk = hM.readRisk(bestX, bestY);
        for (int[] d : directions) {
            int nx = bestX + d[0];
            int ny = bestY + d[1];
            if (this.getLabyrinthe().isWalkable(nx, ny)) {
                int risk = hM.readRisk(nx, ny);

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