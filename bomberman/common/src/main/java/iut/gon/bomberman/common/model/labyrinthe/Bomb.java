package iut.gon.bomberman.common.model.labyrinthe;

import iut.gon.bomberman.common.model.player.Joueur;

import java.io.Serializable;

public class Bomb implements Serializable {

    private final int x;
    private final int y;
    private final int range;
    private double timer;
    private boolean exploded;
    private boolean solid = false;
    Joueur joueur;

    public Bomb(int x, int y, int range, Joueur joueur) {
        this.x = x;
        this.y = y;
        this.range = range;
        this.timer = 3.0;
        this.exploded = false;
        this.joueur = joueur;
    }

    public boolean tick(double deltaTime) {
        if (exploded) return false;
        timer -= deltaTime;
        if (timer <= 0) {
            exploded = true;
            return true;
        }
        return false;
    }

    public boolean isExploded() { return exploded; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getRange() { return range; }
    public double getTimer() { return timer; }
    public boolean isSolid() { return solid; }
    public void setSolid(boolean solid) { this.solid = solid; }
}