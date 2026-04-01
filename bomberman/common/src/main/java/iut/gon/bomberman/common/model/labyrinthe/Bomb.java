package iut.gon.bomberman.common.model.labyrinthe;

import iut.gon.bomberman.common.model.player.Joueur;

import java.io.Serializable;

/**
 * Représente une bombe posée sur la carte par un joueur.
 * Cette classe gère le décompte avant l'explosion et l'état physique de l'objet.
 * Elle est Serializable pour permettre le transfert de données en multijoueur.
 */
public class Bomb implements Serializable {

    private final int x;
    private final int y;
    private final int range;
    private double timer;
    private boolean exploded;
    private boolean solid = false;
    Joueur joueur;

    /**
     * Constructeur de la bombe.
     * @param x Coordonnée X sur la grille.
     * @param y Coordonnée Y sur la grille.
     * @param range Portée de l'explosion.
     * @param joueur Le joueur propriétaire de la bombe.
     */
    public Bomb(int x, int y, int range, Joueur joueur) {
        this.x = x;
        this.y = y;
        this.range = range;
        this.timer = 3.0;
        this.exploded = false;
        this.joueur = joueur;
    }

    /**
     * Met à jour le décompte de la bombe à chaque frame.
     * @param deltaTime Temps écoulé depuis la dernière mise à jour (en secondes).
     * @return true si le timer est écoulé et que la bombe doit exploser, false sinon.
     */
    public boolean tick(double deltaTime) {
        if (exploded) return false;
        timer -= deltaTime;
        if (timer <= 0) {
            exploded = true;
            return true;
        }
        return false;
    }

    // Getters / Setters
    public boolean isExploded() { return exploded; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getRange() { return range; }
    public double getTimer() { return timer; }

    /** * Une bombe n'est pas solide immédiatement après la pose pour
     * permettre au joueur de sortir de la case sans être bloqué.
     * @return true si la bombe agit comme un mur.
     */
    public boolean isSolid() { return solid; }

    public void setSolid(boolean solid) { this.solid = solid; }
}