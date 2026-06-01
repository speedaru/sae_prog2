package fr.uge.but.schtroumpf.view.components;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.uge.but.schtroumpf.model.save.GameSave;
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
 */
public class SaveSummaryWidget extends HBox {

    private final String saveName;
    private final Path filePath;
    private final Runnable onLoadCallback;
    private final Runnable onDeleteCallback;

    public SaveSummaryWidget(Path filePath, Runnable onLoadCallback, Runnable onDeleteCallback) {
        super();
        this.filePath = filePath;
        this.onLoadCallback = onLoadCallback;
        this.onDeleteCallback = onDeleteCallback;

        // 1. Parse File Name manually without regex (remove "save_" prefix and ".json" suffix)
        String rawName = filePath.getFileName().toString();
        if (rawName.startsWith("save_")) {
            rawName = rawName.substring(5); // length of "save_"
        }
        if (rawName.endsWith(".json")) {
            rawName = rawName.substring(0, rawName.length() - 5); // length of ".json"
        }
        this.saveName = rawName;

        // 2. Styling Container
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

        // 3. Information Container (Left)
        VBox infoContainer = new VBox(6.0);
        infoContainer.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(infoContainer, Priority.ALWAYS);

        // Title Label (Save Name)
        Label nameLabel = new Label(saveName.toUpperCase());
        nameLabel.setTextFill(Color.web("#3b82f6")); // Blue signature accent
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 15.0));

        // Metadata details
        Label detailsLabel = new Label(generateMetadataDetailsString());
        detailsLabel.setTextFill(Color.web("#94a3b8"));
        detailsLabel.setFont(Font.font("System", 12.0));
        detailsLabel.setWrapText(true);

        infoContainer.getChildren().addAll(nameLabel, detailsLabel);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.NEVER);

        // 4. Action Buttons Container (Right)
        HBox actionsContainer = new HBox(8.0);
        actionsContainer.setAlignment(Pos.CENTER);

        // Load Game Button
        Button loadBtn = new Button("Charger");
        loadBtn.setPrefHeight(32.0);
        loadBtn.setPrefWidth(85.0);
        loadBtn.setFont(Font.font("System", FontWeight.BOLD, 12.0));
        loadBtn.setStyle(
            "-fx-background-color: #10b981; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 4; " +
            "-fx-cursor: hand;"
        );
        loadBtn.setOnAction(_ -> {
            if (this.onLoadCallback != null) {
                this.onLoadCallback.run();
            }
        });

        // Delete Game Button
        Button deleteBtn = new Button("Supprimer");
        deleteBtn.setPrefHeight(32.0);
        deleteBtn.setPrefWidth(90.0);
        deleteBtn.setFont(Font.font("System", FontWeight.BOLD, 12.0));
        deleteBtn.setStyle(
            "-fx-background-color: #ef4444; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 4; " +
            "-fx-cursor: hand;"
        );
        deleteBtn.setOnAction(_ -> {
            if (this.onDeleteCallback != null) {
                this.onDeleteCallback.run();
            }
        });

        actionsContainer.getChildren().addAll(loadBtn, deleteBtn);

        // Assemble widget structure
        this.getChildren().addAll(infoContainer, spacer, actionsContainer);
    }

    /**
     * Extracts date and parses target JSON attributes safely.
     */
    private String generateMetadataDetailsString() {
        // Read file modification date
        String formattedDate = "Inconnu";
        try {
            FileTime time = Files.getLastModifiedTime(filePath);
            Date date = new Date(time.toMillis());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            formattedDate = sdf.format(date);
        } catch (IOException e) {
            // Silently swallow and use fallback date
        }

        // Try to load basic information directly from the JSON save
        int round = 1;
        int activeCrisesCount = 0;
        try {
            ObjectMapper mapper = new ObjectMapper();
            GameSave save = mapper.readValue(filePath.toFile(), GameSave.class);
            if (save != null && save.engineState() != null) {
                round = save.engineState().currentRound();
            }
            if (save != null && save.villageState() != null && save.villageState().activeCrises() != null) {
                activeCrisesCount = save.villageState().activeCrises().size();
            }
        } catch (Exception e) {
            // Fallback parameters if reading failed
        }

        return "Modifié le : " + formattedDate + "  •  Mois : " + round + "  •  Crises actives : " + activeCrisesCount;
    }

    public String getSaveName() {
        return saveName;
    }
}
