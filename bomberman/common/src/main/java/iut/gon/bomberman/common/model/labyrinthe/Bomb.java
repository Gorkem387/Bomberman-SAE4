package iut.gon.bomberman.common.model.labyrinthe;

import java.io.Serializable;

public class Bomb implements Serializable {

    private final int x;
    private final int y;
    private final int range;
    private double timer;
    private boolean exploded;

    public Bomb(int x, int y, int range) {
        this.x = x;
        this.y = y;
        this.range = range;
        this.timer = 3.0;
        this.exploded = false;
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
}