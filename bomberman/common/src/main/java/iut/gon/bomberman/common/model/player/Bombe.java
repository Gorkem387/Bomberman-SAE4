package iut.gon.bomberman.common.model.player;

import iut.gon.bomberman.common.model.labyrinthe.Labyrinthe;

public class Bombe {
    private int x, y;
    private long tempsPose;
    private int portee;
    private final int DELAI_EXPLOSION = 3000; // 3 secondes
    private boolean explosee = false;

    public Bombe(int x, int y, int portee) {
        this.x = x;
        this.y = y;
        this.portee = portee;
        this.tempsPose = System.currentTimeMillis();
    }

    public boolean doitExploser() {
        return System.currentTimeMillis() - tempsPose >= DELAI_EXPLOSION;
    }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getPortee() { return portee; }
    public boolean isExplosee() { return explosee; }
    public void setExplosee(boolean explosee) { this.explosee = explosee; }
}