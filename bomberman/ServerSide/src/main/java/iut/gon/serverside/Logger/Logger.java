package iut.gon.serverside.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe Singleton qui gère les logs du serveur.
 * Les logs sont affichés dans la console avec un format spécifique : [TYPE :: DATE] MESSAGE
 * Le type de log peut être INFO, ERROR, etc. et la date est formatée pour être lisible.
 * La couleur des logs peut changé en fonction de son type.
 * Les logs les plus importants seront écrits dans un fichier de log.
 */
public class Logger {

    // Codes de couleurs ANSI pour la console
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String ORANGE = "\u001B[38;5;208m";
    private static final String CYAN = "\u001B[36m";
    private static final String RESET = "\u001B[37m";

    // Instance unique de Logger
    static Logger log = new Logger();

    /**
     *  Constructeur privé pour empêcher l'instanciation de la classe depuis l'extérieur.
     * */
    private Logger() {
    }

    /**
     *  Méthode statique pour obtenir l'instance unique de Logger.
     *  @return Logger l'instance unique de Logger
     * */
    public static Logger getInstance() {
        if(log == null) {
            log = new Logger();
        }
        return log;
    }

    /**
     *  Méthode pour afficher un message de log dans la console avec une couleur spécifique en fonction du type de log.
     * */
    public void log(LogTypes type, String message) {
        switch (type) {
            case INFO -> System.out.println(CYAN + createLogMessage(type, message) + RESET);
            case ERROR -> System.out.println(RED + createLogMessage(type, message) + RESET);
            case WARNING -> System.out.println(ORANGE + createLogMessage(type, message) + RESET);
            case SUCCESS -> System.out.println(GREEN + createLogMessage(type, message) + RESET);
            default ->  System.out.println(RESET + createLogMessage(type, message) + RESET);
        }
    }

    /**
     *  Méthode pour créer un message de log formaté avec la date et le type de log.
     *  @param type le type de log (INFO, ERROR, etc.)
     *  @param message le message de log à afficher
     *  @return String le message de log formaté
     * */
    public String createLogMessage(LogTypes type, String message) {
        return getCurrentTimeFormated() + " [" + type + "] " + message;
    }

    /**
     *  Méthode pour obtenir la date et l'heure actuelles formatées de manière lisible.
     *  @return String la date et l'heure actuelles formatées
     * */
    public String getCurrentTimeFormated(){
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy | HH:mm:ss");
        LocalDateTime myDateObj = LocalDateTime.now();

        return myDateObj.format(myFormatObj);
    }

    public static void main(String[] args) {
        Logger logger = Logger.getInstance();
        logger.log(LogTypes.INFO, "This is an info log.");
        logger.log(LogTypes.ERROR, "This is an error log.");
        logger.log(LogTypes.WARNING, "This is a warning log.");
        logger.log(LogTypes.SUCCESS, "This is a success log.");
        logger.log(LogTypes.LOG, "This is a classic log.");
    }
}
