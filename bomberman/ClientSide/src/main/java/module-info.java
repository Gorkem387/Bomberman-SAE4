module iut.gon.bomberman.client {
    requires common;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;

    opens iut.gon.bomberman.client.controllers to javafx.fxml;
    exports iut.gon.bomberman.client;
}