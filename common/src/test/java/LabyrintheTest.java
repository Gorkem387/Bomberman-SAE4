import iut.gon.bomberman.common.model.labyrinthe.CellType;
import iut.gon.bomberman.common.model.labyrinthe.DFSGenerator;
import iut.gon.bomberman.common.model.labyrinthe.Labyrinthe;
import iut.gon.bomberman.common.model.labyrinthe.LabyrintheFactory;

public class LabyrintheTest {
    public static void main(String[] args) {
        LabyrintheFactory factory = new DFSGenerator();
        Labyrinthe laby = factory.createLabyrinthe(21, 21);

        // Affichage dans la console
        System.out.println("Génération labyrinthe :");
        for (int y = 0; y < laby.getHeight(); y++) {
            for (int x = 0; x < laby.getWidth(); x++) {
                CellType type = laby.getCell(x, y);

                switch (type) {
                    case WALL -> System.out.print("██");         // Mur incassable
                    case DESTRUCTIBLE -> System.out.print("░░"); // Mur cassable
                    case EMPTY -> System.out.print("  ");        // Vide
                }
            }
            System.out.println();
        }
    }
}
