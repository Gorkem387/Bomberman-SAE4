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

    private static LogHandler logHandler;

    // Instance unique de Logger
    private static Logger log;

    /**
     *  Constructeur privé pour empêcher l'instanciation de la classe depuis l'extérieur.
     * */
    private Logger() {
    }

    /**
     *  Méthode statique pour obtenir l'instance unique de Logger.
     *  @return Logger l'instance unique de Logger
     * */
    public static synchronized Logger getInstance() {
        if(log == null) {
            logHandler = new LogHandler();
            log = new Logger();
        }
        return log;
    }

    /**
     *  Méthode pour afficher un message de log dans la console avec une couleur spécifique en fonction du type de log.
     * */
    public void log(LogTypes type, String message) {
        logHandler.handle(type, message);
    }



}
