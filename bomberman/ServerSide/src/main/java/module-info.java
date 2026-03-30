module iut.gon.serverside {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens iut.gon.serverside to javafx.fxml;
    exports iut.gon.serverside;
}