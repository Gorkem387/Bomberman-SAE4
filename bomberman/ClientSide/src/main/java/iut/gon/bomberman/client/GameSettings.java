package iut.gon.bomberman.client;

public class GameSettings {
    private static String selectedSkinPath = "/iut/gon/bomberman/client/assets/8/S_0.png";
    private static String selectedBombPath = "/iut/gon/bomberman/client/assets/B_0.png";

    // Utilisés par le GameController et le LabRenderer pour savoir quoi dessiner
    public static String getSelectedSkinPath() { return selectedSkinPath; }
    public static String getSelectedBombPath() { return selectedBombPath; }

    // Utilisés par le CustomizeController pour sauvegarder le choix de l'utilisateur
    public static void setSelectedSkinPath(String path) { selectedSkinPath = path; }
    public static void setSelectedBombPath(String path) { selectedBombPath = path; }
}
