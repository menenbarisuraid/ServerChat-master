package com.example.chat;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HelloApplication extends Application{

    @Override
    public void start(Stage stage){
        HelloController hc;
        hc = new HelloController();
        VBox mainV = new VBox();
        mainV.getChildren().addAll(hc.upper,hc.bottom);
        BorderPane mainbp = new BorderPane();
        mainbp.setCenter(mainV);
        mainV.setAlignment(Pos.CENTER_LEFT);
        mainbp.setRight(hc.righttextlist);
        hc.righttextlist.setStyle("background:purple");
        stage.setOnCloseRequest(e -> hc.sendMessage("/close"));
        stage.setScene(new Scene(mainbp,1000,800));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}