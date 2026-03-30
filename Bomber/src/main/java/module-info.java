module iut.gon.bomber {
    requires javafx.controls;
    requires javafx.fxml;


    opens iut.gon.bomber to javafx.fxml;
    exports iut.gon.bomber;
}