package fr.uge.but.schtroumpf.controller.gui.windows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import fr.uge.but.schtroumpf.controller.AppController;
import fr.uge.but.schtroumpf.controller.Navigation.NavigationAction;
import fr.uge.but.schtroumpf.controller.WindowSubController;
import fr.uge.but.schtroumpf.model.save.GameSaveManager;
import fr.uge.but.schtroumpf.model.types.WindowType;
import fr.uge.but.schtroumpf.model.utils.Logger;
import fr.uge.but.schtroumpf.view.components.SaveSummaryWidget;
import fr.uge.but.schtroumpf.view.themes.ThemeManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class SaveController implements WindowSubController {
    private AppController router;
    
    @FXML private TextField saveNameField;
    @FXML private VBox savesContainer;
    @FXML private Label statusLabel;

    @Override
    public void setRouter(AppController router) {
        this.router = router;
    }

    @FXML
    public void initialize() {
        refreshSavesList();
        statusLabel.setText("");
    }

    @FXML
    void handleBack(ActionEvent event) {
        router.navigate(NavigationAction.POP, null);
    }

    @FXML
    void handleCreateSave(ActionEvent event) {
        String saveName = saveNameField.getText().trim().toLowerCase();

        if (saveName.isEmpty()) {
            setStatus("Veuillez entrer un nom de sauvegarde valide.", ThemeManager.getFailColor());
            return;
        }

        // Clean name to prevent path traversal vulnerabilities
        String cleanName = saveName.replaceAll("[^a-zA-Z0-9_\\-]", "");
        if (cleanName.isEmpty()) {
            setStatus("Le nom de sauvegarde contient des caractères interdits.", ThemeManager.getFailColor());
            return;
        }

        // Check if save already exists to prompt or alert
        for (String existingSave : GameSaveManager.getSaveNames()) {
            if (existingSave.toLowerCase().equals(cleanName)) {
                setStatus(String.format("La sauvegarde '%s' existe déjà !", cleanName), ThemeManager.getFailColor());
                return;
            }
        }

        GameController gameController = router.getWindowController(WindowType.GAME_WINDOW);
        if (gameController == null) {
            Logger.LogError("Failed to get game controller context.");
            return;
        }

        gameController.saveGame(cleanName);
        saveNameField.clear();
        setStatus(String.format("Partie '%s' sauvegardée !", cleanName), ThemeManager.getSuccessColor());
        
        refreshSavesList(); // Dynamic refresh
    }

    /**
     * Loops over active directory directories to build SaveSummaryWidgets.
     */
    private void refreshSavesList() {
        savesContainer.getChildren().clear();

        List<String> saveNames = GameSaveManager.getSaveNames();

        if (saveNames.isEmpty()) {
            Label emptyLabel = new Label("Aucune sauvegarde trouvée.");
            emptyLabel.setTextFill(Color.web("#64748b"));
            emptyLabel.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontPosture.ITALIC, 13.0));
            savesContainer.getChildren().add(emptyLabel);
            return;
        }

        for (String name : saveNames) {
            Path file = Path.of("saves/save_" + name + ".json").toAbsolutePath();

            SaveSummaryWidget widget = new SaveSummaryWidget(
                file,
                () -> handleDeleteSave(name, file)
            );

            savesContainer.getChildren().add(widget);
        }
    }

    private void handleDeleteSave(String saveName, Path file) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Suppression de sauvegarde");
        alert.setHeaderText(null);
        alert.setContentText("Voulez-vous vraiment supprimer la sauvegarde '" + saveName + "' ? Cette action est irréversible.");

        // Customise dialog styling to match our dark theme
        alert.getDialogPane().setStyle("-fx-background-color: #202225;");
        alert.getDialogPane().lookup(".content.label").setStyle("-fx-text-fill: #f8fafc; -fx-font-weight: bold;");

        // Customise buttons to say "Oui" and "Non"
        ButtonType yesButton = new ButtonType("Oui");
        ButtonType noButton = new ButtonType("Non");
        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            executeDelete(saveName, file);
        }
    }
    
    private void executeDelete(String saveName, Path filePath) {
        try {
            Files.deleteIfExists(filePath);
            setStatus("La sauvegarde '" + saveName + "' a été supprimée.", ThemeManager.getSuccessColor());
            refreshSavesList();
        } catch (IOException e) {
            setStatus("Impossible de supprimer le fichier.", ThemeManager.getFailColor());
        }
    }

    private void setStatus(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setTextFill(color);
    }
}
