package iut.gon.bomberman.common.model.labyrinthe;

/**
 * Interface définissant le contrat pour la création de labyrinthes.
 * Ce patron de conception (Factory) permet d'isoler la logique de génération
 * (DFS, Kruskal) du reste de l'application.
 */
public interface LabyrintheFactory {
    /**
     * Crée et initialise un objet Labyrinthe.
     * * @param width  Largeur du labyrinthe.
     * @param height Hauteur du labyrinthe.
     * @return Un objet Labyrinthe entièrement généré.
     */
    Labyrinthe createLabyrinthe(int width, int height);
}
