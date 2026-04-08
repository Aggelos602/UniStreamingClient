package com.aggelos;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Main extends Application {
    private Api api;
    @Override
    public void start(Stage primaryStage) throws IOException {
        api = new Api();

        Api.ApiResponse formats = api.Get("getFormats");
        Api.ApiResponse resolutions = api.Get("getResolutions");

        System.out.println("Form " + formats.getData());
        System.out.println("Res " + resolutions.getData());


        Label label = new Label("Hello, JavaFX!");

        ObservableList<String> formatOptions = FXCollections.observableArrayList(formats.getData());
        ComboBox<String> formatPicker = new ComboBox<>(formatOptions);

        ObservableList<String> resolutionOptions = FXCollections.observableArrayList(resolutions.getData());
        ComboBox<String> resolutionPicker = new ComboBox<>(resolutionOptions);

        HBox pickersRow = new HBox(10);
        pickersRow.setAlignment(Pos.CENTER);
        pickersRow.getChildren().addAll(formatPicker, resolutionPicker);

        Button postButton = new Button("Start Stream");
        Map<String, Object> data = new HashMap<>();
        postButton.setOnAction(e -> {
            data.put("format", formatPicker.getValue());
            data.put("resolution", resolutionPicker.getValue());
            data.put("videoName", "video1.mp4");

            try {
                api.Post("startStreaming", data);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        HBox buttonRow = new HBox(10);
        buttonRow.setAlignment(Pos.CENTER);
        buttonRow.getChildren().addAll(postButton);

        VBox root = new VBox(10);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(label, pickersRow, buttonRow);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Streaming Client");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception{
        if(api != null){
            api.closeConnection();
        }
    }
}