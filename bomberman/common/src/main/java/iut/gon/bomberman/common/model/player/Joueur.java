package iut.gon.bomberman.common.model.player;


import iut.gon.bomberman.common.model.player.Effects.Bonus;
import iut.gon.bomberman.common.model.player.EtatJoueur;
import iut.gon.bomberman.common.model.labyrinthe.Labyrinthe;
import iut.gon.bomberman.common.model.labyrinthe.BombManager;
import iut.gon.bomberman.common.model.labyrinthe.CellType;
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
    private static final int MAX_BOMBES = 6;

    private boolean alive = true;

    private String skinPath = "/iut/gon/bomberman/client/assets/8/S_0.png";

    private int explosionRange = 2;

    private long lastDamageTime = 0;
    private static final long INVINCIBILITY_DURATION = 1000;

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

    public Joueur(int id, String nom, double cooX, double cooY){
        this.id = id;
        this.nom = nom;
        this.cooX = cooX;
        this.cooY = cooY;
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
    public void move(double deltaX, double deltaY, double deltaTime, Labyrinthe laby, BombManager bombManager) {
        double vitesseBase = 4.0;
        double speed = vitesseBase * speed_multiplier * deltaTime;

        double hitboxSize = 0.7;
        double offset = 0.15;
        // Mise à jour de la direction pour l'animation
        if (deltaX > 0) direction = Direction.RIGHT;
        else if (deltaX < 0) direction = Direction.LEFT;
        else if (deltaY > 0) direction = Direction.DOWN;
        else if (deltaY < 0) direction = Direction.UP;

        // On bouge soit en X, soit en Y
        if (deltaX != 0) {
            double nextX = cooX + (deltaX * speed);
            if (canMoveTo(nextX, cooY, hitboxSize, offset, laby, bombManager)) {
                this.cooX = nextX;
            }
        } else if (deltaY != 0) {
            double nextY = cooY + (deltaY * speed);
            if (canMoveTo(cooX, nextY, hitboxSize, offset, laby, bombManager)) {
                this.cooY = nextY;
            }
        }

        if (deltaX == 0 && deltaY == 0) direction = Direction.IDLE;
    }

    public boolean checkBonus(Labyrinthe laby) {
        int centerX = (int) Math.round(cooX);
        int centerY = (int) Math.round(cooY);

        if (laby.isInside(centerX, centerY)) {
            CellType typeCase = laby.getCell(centerX, centerY);

            // On vérifie si la case est un type de bonus
            if (typeCase == CellType.SPEED_BONUS || typeCase == CellType.FIRE_BONUS ||
                    typeCase == CellType.BOMB_BONUS || typeCase == CellType.HEAL_BONUS) {

                if (typeCase == CellType.SPEED_BONUS) {
                    this.speed_multiplier += 0.2f;
                    System.out.println("[BONUS] Vitesse : " + speed_multiplier);
                }
                else if (typeCase == CellType.FIRE_BONUS) {
                    this.addExplosionRange();
                    System.out.println("[BONUS] Portée explosion : " + explosionRange);
                }
                else if (typeCase == CellType.BOMB_BONUS){
                    if (nb_bombes_max < MAX_BOMBES){
                        nb_bombes_max++;
                        nb_bombes = Math.min(nb_bombes + 1, nb_bombes_max);
                        System.out.println("[BONUS] Bombes max : " + nb_bombes_max);
                    }
                }
                else if (typeCase == CellType.HEAL_BONUS){
                    if (pv < 3){
                        pv++;
                        System.out.println("[BONUS] PV récupéré : " + pv);
                    }
                }

                // On vide la case après avoir appliqué l'effet
                laby.setCell(centerX, centerY, CellType.EMPTY);
                return true;// On confirme qu'on a ramassé quelque chose
            }
        }
        return false; // Rien ramassé
    }


    private boolean canMoveTo(double x, double y, double size, double offset, Labyrinthe laby, BombManager bm) {
        if (laby == null) return true; // Sécurité si le labyrinthe n'est pas encore chargé

        double left = x + offset;
        double right = x + offset + size;
        double top = y + offset;
        double bottom = y + offset + size;

        double[][] corners = {
                {left, top}, {right, top},
                {left, bottom}, {right, bottom}
        };

        for (double[] p : corners) {
            if (!laby.isWalkable((int)p[0], (int)p[1])) return false;
            if (bm != null && bm.isBombAt((int)p[0], (int)p[1])) return false;
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
        if (pv < 0) {
            this.pv = 0;
        } else {
            this.pv = pv;
        }
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

    public int getExplosionRange() {
        return explosionRange;
    }

    public void setExplosionRange(int explosionRange) {
        this.explosionRange = explosionRange;
    }

    public void addExplosionRange() {
        this.explosionRange++;
    }

    public boolean canTakeDamage() {
        return System.currentTimeMillis() - lastDamageTime > INVINCIBILITY_DURATION;
    }

    public void registerDamage() {
        this.pv--;
        this.lastDamageTime = System.currentTimeMillis();
    }
}