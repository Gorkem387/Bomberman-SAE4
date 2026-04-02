module iut.gon.bomberman.client {
    requires common;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
    requires javafx.media;

    opens iut.gon.bomberman.client.controllers;
    exports iut.gon.bomberman.client;
    exports iut.gon.bomberman.client.ai;
    exports iut.gon.bomberman.client.controllers;
    exports iut.gon.bomberman.client.network;
    exports iut.gon.bomberman.client.sound;
}