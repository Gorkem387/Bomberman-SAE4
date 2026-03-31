module iut.gon.serverside {
    // Dépendance vers le module commun pour accéder aux Messages
    requires common;
    
    // Dépendances Java natives si nécessaire
    requires java.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    // Exportation des packages du serveur
    exports iut.gon.serverside;
    exports iut.gon.serverside.Threads;
    exports iut.gon.serverside.Threads.PlayerInputHandling;
    exports iut.gon.serverside.Logger;
}
