package iut.gon.bomberman.common.model.player;

import iut.gon.bomberman.common.model.labyrinthe.BombManager;
import iut.gon.bomberman.common.model.labyrinthe.Labyrinthe;
import iut.gon.bomberman.common.model.player.Effects.Bonus;
import iut.gon.bomberman.common.model.player.EtatJoueur;

public class Joueur {

    /////////////
    //ATTRIBUTS//
    /////////////

    private int id;
    private double cooX;
    private double cooY;
    private EtatJoueur etat;
    private int pv;
    private int nb_bombes_max;
    private int nb_bombes;
    private Bonus[] bonus;
    private float speed_multiplier;
    private String nom;
    private int skinId;

    private boolean alive = true;

    private String skinPath = "/iut/gon.bomberman/client/assets/8/S_0.png";

    // Directions
    private Direction direction = Direction.DOWN;

    ////////////////
    //CONSTRUCTEUR//
    ////////////////

    public Joueur(int id, String nom){
        this.id = id;
        this.nom = nom;
        this.cooX = 0;
        this.cooY = 0;
        this.etat = EtatJoueur.NOT_CONNECTED;
        this.pv = 3;
        this.nb_bombes_max = 3;
        this.nb_bombes = 3;
        this.bonus = new Bonus[3];
        this.speed_multiplier = 1.0f;
    }


    public Joueur(int id, String nom, double cooX, double cooY, EtatJoueur etat, int pv, int nb_bombes_max, int nb_bombes, Bonus[] bonus, float speed_multiplier) {
        this.id = id;
        this.nom = nom;
        this.cooX = cooX;
        this.cooY = cooY;
        this.etat = etat;
        this.pv = pv;
        this.nb_bombes_max = nb_bombes_max;
        this.nb_bombes = nb_bombes;
        this.bonus = bonus;
        this.speed_multiplier = speed_multiplier;
    }

    // Méthode move
    // Méthode move
    public void move(double deltaX, double deltaY, Labyrinthe laby, BombManager bombManager) {
        double vitesseBase = 0.05;
        double nextX = cooX + (deltaX * vitesseBase * speed_multiplier);
        double nextY = cooY + (deltaY * vitesseBase * speed_multiplier);
        double size = 0.9;

        // On vérifie les collisions
        if (canMoveTo(nextX, nextY, size, laby, bombManager)) {
            this.cooX = nextX;
            this.cooY = nextY;
        }
    }

    private boolean canMoveTo(double x, double y, double size, Labyrinthe laby, BombManager bm) {
        int[][] points = {
                {(int)x, (int)y},
                {(int)(x + size), (int)y},
                {(int)x, (int)(y + size)},
                {(int)(x + size), (int)(y + size)}
        };

        for (int[] p : points) {
            if (!laby.isWalkable(p[0], p[1])) return false;
            if (bm.isBombAt(p[0], p[1])) return false;
        }
        return true;
    }

    ///////////////////
    //GETTERS/SETTERS//
    ///////////////////

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getX() {
        return cooX;
    }
    public double getY(){
        return cooY;
    }

    public void setX(double newX) {
        this.cooX = newX;
    }
    public void setY(double newY) {
        this.cooY = newY;
    }

    public EtatJoueur getEtat() {
        return etat;
    }

    public void setEtat(EtatJoueur etat) {
        this.etat = etat;
    }

    public int getPv() {
        return pv;
    }

    public void setPv(int pv) {
        this.pv = pv;
    }

    public int getNb_bombes_max() {
        return nb_bombes_max;
    }

    public void setNb_bombes_max(int nb_bombes_max) {
        this.nb_bombes_max = nb_bombes_max;
    }

    public int getNb_bombes() {
        return nb_bombes;
    }

    public void setNb_bombes(int nb_bombes) {
        this.nb_bombes = nb_bombes;
    }

    public Bonus[] getBonus() {
        return bonus;
    }

    public void setBonus(Bonus[] bonus) {
        this.bonus = bonus;
    }

    public float getSpeed_multiplier() {
        return speed_multiplier;
    }

    public void setSpeed_multiplier(float speed_multiplier) {
        this.speed_multiplier = speed_multiplier;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getRadius() {
        return 1;
    }

    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }

    public String getSkinPath() {
        return skinPath;
    }

    public void setSkinPath(String skinPath) {
        this.skinPath = skinPath;
    }
}
