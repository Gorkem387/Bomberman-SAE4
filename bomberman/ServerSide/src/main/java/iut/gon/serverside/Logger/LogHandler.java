package iut.gon.serverside.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogHandler {

    // Codes de couleurs ANSI pour la console
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String ORANGE = "\u001B[38;5;208m";
    private static final String CYAN = "\u001B[36m";
    private static final String RESET = "\u001B[37m";

    private ErrorHandler errorHandler;

    public LogHandler(){
        this.errorHandler = new ErrorHandler();
    }

    /**
     *  Méthode pour créer un message de log formaté avec la date et le type de log.
     *  @param type le type de log (INFO, ERROR, etc.)
     *  @param message le message de log à afficher
     *  @return String le message de log formaté
     * */
    private String createLogMessage(LogTypes type, String message) {
        return getCurrentTimeFormated() + " [" + type + "] " + message;
    }

    /**
     *  Méthode pour obtenir la date et l'heure actuelles formatées de manière lisible.
     *  @return String la date et l'heure actuelles formatées
     * */
    private String getCurrentTimeFormated(){
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy | HH:mm:ss");
        LocalDateTime myDateObj = LocalDateTime.now();

        return myDateObj.format(myFormatObj);
    }

    public void handle(LogTypes type, String message){
        switch (type) {
            case INFO -> System.out.println(CYAN + createLogMessage(type, message) + RESET);
            case ERROR -> {
                String msg = createLogMessage(type, message);
                System.out.println(RED + msg + RESET);
                errorHandler.handle(msg);
            }
            case WARNING -> System.out.println(ORANGE + createLogMessage(type, message) + RESET);
            case SUCCESS -> System.out.println(GREEN + createLogMessage(type, message) + RESET);
            default ->  System.out.println(RESET + createLogMessage(type, message) + RESET);
        }
    }
}
