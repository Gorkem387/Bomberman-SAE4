module iut.gon.bomberman {
    requires javafx.controls;
    requires javafx.fxml;


    opens iut.gon.bomberman to javafx.fxml;
    exports iut.gon.bomberman;
}