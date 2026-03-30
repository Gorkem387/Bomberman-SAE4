module iut.gon.bomberman {
    requires javafx.controls;
    requires javafx.fxml;
	requires javafx.graphics;


    opens iut.gon.bomberman to javafx.fxml;
    exports iut.gon.bomberman;
}