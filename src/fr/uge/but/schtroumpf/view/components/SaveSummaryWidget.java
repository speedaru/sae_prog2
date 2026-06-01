package fr.uge.but.schtroumpf.view.components;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.uge.but.schtroumpf.model.save.GameSave;
import fr.uge.but.schtroumpf.model.save.GameSaveManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * A reusable card widget that represents a single saved game entry.
 * Completely immune to un-styled hover or click text shrinkage issues.
 */
public class SaveSummaryWidget extends HBox {

    private final String saveName;
    private final Path filePath;
    private final Runnable onDeleteCallback;

    public SaveSummaryWidget(Path filePath, Runnable onDeleteCallback) {
        super();
        this.filePath = Objects.requireNonNull(filePath);
        this.onDeleteCallback = Objects.requireNonNull(onDeleteCallback);

        // Parse File Name manually (remove "save_" prefix and ".json" suffix)
        String rawName = filePath.getFileName().toString();
        if (rawName.startsWith("save_")) {
            rawName = rawName.substring(5);
        }
        if (rawName.endsWith(".json")) {
            rawName = rawName.substring(0, rawName.length() - 5);
        }
        this.saveName = rawName;

        // Container Styling
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new Insets(12.0, 15.0, 12.0, 15.0));
        this.setSpacing(15.0);
        this.setStyle(
            "-fx-background-color: #2d3139; " +
            "-fx-border-color: #3f444c; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 6; " +
            "-fx-background-radius: 6;"
        );

        // Information Container (Left)
        VBox infoContainer = new VBox(6.0);
        infoContainer.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(infoContainer, Priority.ALWAYS);

        Label nameLabel = new Label(saveName.toUpperCase());
        nameLabel.setTextFill(Color.web("#3b82f6")); // Blue signature accent
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 15.0));

        Label detailsLabel = new Label(generateMetadataDetailsString());
        detailsLabel.setTextFill(Color.web("#94a3b8"));
        detailsLabel.setFont(Font.font("System", 12.0));
        detailsLabel.setWrapText(true);

        infoContainer.getChildren().addAll(nameLabel, detailsLabel);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.NEVER);

        // Action Buttons Container (Right)
        HBox actionsContainer = new HBox(8.0);
        actionsContainer.setAlignment(Pos.CENTER);

        // Delete Game Button (Fixed size CSS hover protection)
        Button deleteBtn = new Button("Supprimer");
        deleteBtn.setPrefHeight(32.0);
        deleteBtn.setPrefWidth(90.0);
        deleteBtn.setStyle(
            "-fx-background-color: #ef4444; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 4; " +
            "-fx-cursor: hand; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 12px;"
        );
        deleteBtn.setOnAction(_ -> onDeleteCallback.run());

        actionsContainer.getChildren().add(deleteBtn);

        this.getChildren().addAll(infoContainer, spacer, actionsContainer);
    }

    private String generateMetadataDetailsString() {
        String formattedDate = "Inconnu";
        try {
            FileTime time = Files.getLastModifiedTime(filePath);
            Date date = new Date(time.toMillis());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            formattedDate = sdf.format(date);
        } catch (IOException e) {
            // Fallback gracefully
        }

        int round = 1;
        int activeCrisesCount = 0;
        try {
            GameSave save = GameSaveManager.getGameSave(this.saveName);
            if (save != null) {
				round = save.engineState().currentRound();
				activeCrisesCount = save.villageState().activeCrises().size();
            }
        } catch (Exception e) {
        }

        return String.format("Modifié le : %s • Mois : %d • Crises actives : %d",
                formattedDate, round, activeCrisesCount);
    }

    public String getSaveName() {
        return saveName;
    }
}
