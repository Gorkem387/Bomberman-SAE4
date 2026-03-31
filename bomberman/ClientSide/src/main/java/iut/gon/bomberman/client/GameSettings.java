package iut.gon.bomberman.client;

public class GameSettings {
    private static String selectedSkinPath = "/iut/gon/bomberman/client/assets/8/S_0.png";
    private static String selectedBombPath = "/iut/gon/bomberman/client/assets/B_0.png";

    public static String getSelectedSkinPath() { return selectedSkinPath; }
    public static void setSelectedSkinPath(String path) { selectedSkinPath = path; }

    public static String getSelectedBombPath() { return selectedBombPath; }
    public static void setSelectedBombPath(String path) { selectedBombPath = path; }
}
