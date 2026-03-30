module ClientSide {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;

    opens iut.gon.bomberman.client.controllers to javafx.fxml;

    opens iut.gon.bomberman.client to javafx.fxml;

    exports iut.gon.bomberman.client;
}