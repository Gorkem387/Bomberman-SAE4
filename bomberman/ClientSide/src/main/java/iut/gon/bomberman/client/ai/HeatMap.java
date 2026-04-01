package iut.gon.bomberman.client.ai;

/**
 * Représente une carte thermique (HeatMap) de danger pour le labyrinthe.
 * Cette classe permet de stocker et de consulter des valeurs de "risque"
 * sur une grille à deux dimensions, aidant l'IA à identifier les zones
 * menacées par des explosions imminentes.
 */
public class HeatMap {

    /** Matrice d'entiers représentant le niveau de danger par case */
    int[][] map;
    /** Largeur de la carte (nombre de colonnes) */
    int x;
    /** Hauteur de la carte (nombre de lignes) */
    int y;

    /**
     * Incrémente la valeur de danger d'une case spécifique.
     * Utilisé généralement lorsqu'une bombe est posée pour marquer sa zone d'effet.
     *
     * @param x     Coordonnée X de la case
     * @param y     Coordonnée Y de la case
     * @param value Valeur de danger à ajouter
     */
    public void updateMap(int x, int y, int value){
        this.map[x][y] += value;
    }

    /**
     * Lit le niveau de risque actuel d'une case.
     * Plus la valeur est élevée, plus la case est dangereuse pour l'IA.
     *
     * @param x Coordonnée X de la case
     * @param y Coordonnée Y de la case
     * @return  La valeur de risque cumulée sur cette case
     */
    public int readRisk(int x, int y){
        return this.map[x][y];
    }

    /**
     * Réinitialise le risque d'une case à zéro.
     * Utile après une explosion ou lors d'un cycle de nettoyage de la carte.
     *
     * @param x Coordonnée X de la case
     * @param y Coordonnée Y de la case
     */
    public void resetRisk(int x, int y){
        this.map[x][y] = 0;
    }

    /**
     * Constructeur de la HeatMap.
     * Initialise une grille vide (toutes les valeurs à 0) aux dimensions spécifiées.
     *
     * @param x Largeur de la grille
     * @param y Hauteur de la grille
     */
    public HeatMap(int x, int y){
        this.x = x;
        this.y = y;
        this.map = new int[x][y];

        // Parcours de la matrice pour s'assurer que chaque case est initialisée à zéro
        for (int i = 0; i < this.x; i++) {
            for (int j = 0; j < this.y; j++) {
                this.map[i][j] = 0;
            }
        }
    }
}