package fr.uge.but.schtroumpf.view.components;

import java.net.URL;
import java.util.function.Consumer;

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

public class SettingToggleWidget extends HBox {

    private boolean isActivated;
    private final Button toggleButton;
    private final Consumer<Boolean> onToggleAction;

    public SettingToggleWidget(String title, String description, String iconFileName, boolean initialState, Consumer<Boolean> onToggleAction) {
        this.isActivated = initialState;
        this.onToggleAction = onToggleAction;

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

        ImageView iconView = new ImageView();
        iconView.setFitHeight(64.0);
        iconView.setFitWidth(64.0);
        iconView.setPreserveRatio(true);
        loadIconResource(iconView, iconFileName);
        Logger.LogDebug("icon file: %s", iconFileName);

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

        this.toggleButton = new Button();
        this.toggleButton.setPrefHeight(45.0);
        this.toggleButton.setPrefWidth(120.0);
        this.toggleButton.setFont(Font.font("System", FontWeight.BOLD, 14.0));
        this.toggleButton.setStyle("-fx-cursor: hand; -fx-background-radius: 6;");
        updateButtonVisuals();

        this.toggleButton.setOnAction(_ -> {
            this.isActivated = !this.isActivated;
            updateButtonVisuals();
            if (this.onToggleAction != null) {
                this.onToggleAction.accept(this.isActivated);
            }
        });

        this.getChildren().addAll(iconView, textContainer, toggleButton);
    }

    private void updateButtonVisuals() {
        if (isActivated) {
            toggleButton.setText("Activé");
            toggleButton.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");
        } else {
            toggleButton.setText("Désactivé");
            toggleButton.setStyle("-fx-background-color: #4b5563; -fx-text-fill: #94a3b8; -fx-background-radius: 6; -fx-cursor: hand;");
        }
    }

    private void loadIconResource(ImageView iconView, String fileName) {
        try {
			URL resource = getClass().getResource("/settings/" + fileName);
        	iconView.setImage(new Image(resource.toExternalForm()));
        } catch (Exception e) {
            iconView.setImage(null);
        }
    }
}
