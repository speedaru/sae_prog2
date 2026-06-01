package fr.uge.but.schtroumpf.view.components;

import java.nio.file.Path;
import fr.uge.but.schtroumpf.model.utils.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SettingNavigationWidget extends HBox {

    public SettingNavigationWidget(String title, String description, Path iconFile, String buttonText, Runnable onNavigateAction) {
        super();

        // 1. Container Styling
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(20.0);
        this.setPadding(new Insets(15.0, 20.0, 15.0, 20.0));
        this.setStyle(
            "-fx-background-color: #202225; " +
            "-fx-border-color: #3f444c; " +
            "-fx-border-width: 1.5; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8;"
        );

        // 2. Icon Graphic (Left)
        ImageView iconView = new ImageView();
        iconView.setFitHeight(64.0);
        iconView.setFitWidth(64.0);
        iconView.setPreserveRatio(true);
        loadIconResource(iconView, iconFile);

        // 3. Text Container (Middle)
        VBox textContainer = new VBox(5.0);
        textContainer.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textContainer, Priority.ALWAYS);

        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.web("#f8fafc"));
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18.0));

        Label descLabel = new Label(description);
        descLabel.setTextFill(Color.web("#94a3b8"));
        descLabel.setFont(Font.font("System", 13.0));
        descLabel.setWrapText(true);

        textContainer.getChildren().addAll(titleLabel, descLabel);

        // 4. Action Navigation Button (Right)
        Button navigationButton = new Button(buttonText);
        navigationButton.setPrefHeight(45.0);
        navigationButton.setPrefWidth(120.0);
        navigationButton.setFont(Font.font("System", FontWeight.BOLD, 14.0));
        navigationButton.setStyle(
            "-fx-background-color: #3b82f6; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 6; " +
            "-fx-cursor: hand;"
        );

        // 5. Click Event Logic
        navigationButton.setOnAction(_ -> {
            if (onNavigateAction != null) {
                onNavigateAction.run();
            }
        });

        // Assemble the widget
        this.getChildren().addAll(iconView, textContainer, navigationButton);
    }

    private void loadIconResource(ImageView iconView, Path file) {
        try {
            iconView.setImage(new Image(file.toUri().toString()));
        } catch (Exception e) {
            Logger.LogDebug("Navigation widget icon not loaded: %s", file.toString());
            iconView.setImage(null);
        }
    }
}
