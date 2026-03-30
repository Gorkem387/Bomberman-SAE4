package iut.gon.bomberman.common.model.labyrinthe;

public class Bomb {
    private final int x;
    private final int y;
    private double timer; // Temps avant l'explosion
    private final int range; // La portée

    public Bomb(int x, int y, int range) {
        this.x = x;
        this.y = y;
        this.range = range;
        this.timer = 3.0; // 3 secondes avant explosion
    }

    public boolean tick(double deltaTime) {
        this.timer -= deltaTime;
        return this.timer <= 0;
    }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getRange() { return range; }
}