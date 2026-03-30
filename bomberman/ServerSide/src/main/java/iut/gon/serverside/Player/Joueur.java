package iut.gon.serverside.Player;

import iut.gon.serverside.Effects.Bonus;

import java.util.HashMap;

public class Joueur {

    /////////////
    //ATTRIBUTS//
    /////////////

    private int id;
    private int cooX;
    private int cooY;
    private etat_Joueur etat;
    private int pv;
    private int nb_bombes_max;
    private int nb_bombes;
    private Bonus[] bonus;
    private float speed_multiplier;
    private String nom;
    private int radius;

    ////////////////
    //CONSTRUCTEUR//
    ////////////////

    public Joueur(int id, String nom){
        this.id = id;
        this.nom = nom;
        this.cooX = 0;
        this.cooY = 0;
        this.etat = etat_Joueur.NOT_CONNECTED;
        this.pv = 3;
        this.nb_bombes_max = 3;
        this.nb_bombes = 3;
        this.bonus = new Bonus[3];
        this.speed_multiplier = 1.0f;
        this.radius = 2;
    }

    public Joueur(int id, String nom, int cooX, int cooY, etat_Joueur etat, int pv, int nb_bombes_max, int nb_bombes, Bonus[] bonus, float speed_multiplier) {
        this.id = id;
        this.nom = nom;
        this.cooX = 0;
        this.cooY = 0;
        this.etat = etat;
        this.pv = pv;
        this.nb_bombes_max = nb_bombes_max;
        this.nb_bombes = nb_bombes;
        this.bonus = bonus;
        this.speed_multiplier = speed_multiplier;
        this.radius = 2;
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

    public int getX() {
        return cooX;
    }
    public int getY(){
        return cooY;
    }

    public void setX(int newX) {
        this.cooX = newX;
    }

    public void setY(int newY) {
        this.cooY = newY;
    }

    public etat_Joueur getEtat() {
        return etat;
    }

    public void setEtat(etat_Joueur etat) {
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

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
