package fr.uge.but.schtroumpf.view.components;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import fr.uge.but.schtroumpf.model.save.GameSave;
import fr.uge.but.schtroumpf.model.save.GameSaveManager;

public class LoadSummaryWidget extends HBox {

    private final String saveName;
    private final Path filePath;
    private final Label nameLabel;
    private final Label detailsLabel;

    public LoadSummaryWidget(String saveName) {
        super();
        this.saveName = Objects.requireNonNull(saveName, "Le nom de sauvegarde ne peut pas être nul.");
        
        // Expose path resolution cleanly to the save manager to avoid any parsing inside this widget
        this.filePath = GameSaveManager.getSaveFilePath(saveName);

        // 1. Styling Container
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new Insets(12.0, 15.0, 12.0, 15.0));
        this.setSpacing(15.0);
        this.setStyle(
            "-fx-background-color: #202225; " +
            "-fx-border-color: #3f444c; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 6; " +
            "-fx-background-radius: 6; " +
            "-fx-cursor: hand;"
        );

        // 2. Information Container (Left)
        VBox infoContainer = new VBox(6.0);
        infoContainer.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(infoContainer, Priority.ALWAYS);

        nameLabel = new Label(saveName.toUpperCase());
        nameLabel.setTextFill(Color.web("#3b82f6")); // Blue signature accent
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 15.0));

        detailsLabel = new Label(generateMetadataDetailsString());
        detailsLabel.setTextFill(Color.web("#94a3b8"));
        detailsLabel.setFont(Font.font("System", 12.0));

        infoContainer.getChildren().addAll(nameLabel, detailsLabel);
        this.getChildren().add(infoContainer);
    }

    /**
     * Toggles the background highlights when selected in the menu list.
     */
    public void setSelectedState(boolean selected) {
        if (selected) {
            this.setStyle(
                "-fx-background-color: #3b82f6; " +
                "-fx-border-color: #60a5fa; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 6; " +
                "-fx-background-radius: 6; " +
                "-fx-cursor: hand;"
            );
            nameLabel.setTextFill(Color.WHITE);
            detailsLabel.setTextFill(Color.WHITE);
        } else {
            this.setStyle(
                "-fx-background-color: #202225; " +
                "-fx-border-color: #3f444c; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 6; " +
                "-fx-background-radius: 6; " +
                "-fx-cursor: hand;"
            );
            nameLabel.setTextFill(Color.web("#3b82f6"));
            detailsLabel.setTextFill(Color.web("#94a3b8"));
        }
    }

    private String generateMetadataDetailsString() {
        String formattedDate = "Inconnu";
        try {
            FileTime time = Files.getLastModifiedTime(filePath);
            Date date = new Date(time.toMillis());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            formattedDate = sdf.format(date);
        } catch (IOException e) {
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
