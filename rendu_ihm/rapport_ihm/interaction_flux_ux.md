# Rapport IHM 3 : Interaction et Flux Utilisateur

## 1. Gestion des Erreurs et Feedback : Rassurance et Confirmation

### `SaveController` : Feedback textuel immédiat

Le `SaveController` utilise un `statusLabel` pour fournir un retour d'information **explicite et textuel** à chaque action de l'utilisateur :

```java
// SaveController.java
@FXML private Label statusLabel;

@FXML
void handleCreateSave(ActionEvent event) {
    String saveName = saveNameField.getText().trim().toLowerCase();

    if (saveName.isEmpty()) {
        setStatus("Veuillez entrer un nom de sauvegarde valide.", ThemeManager.getFailColor());
        return;
    }

    // Nettoyage du nom pour éviter les injections de chemin
    String cleanName = saveName.replaceAll("[^a-zA-Z0-9_\\-]", "");
    if (cleanName.isEmpty()) {
        setStatus("Le nom de sauvegarde contient des caractères interdits.", ThemeManager.getFailColor());
        return;
    }

    // Vérification de doublon
    for (String existingSave : GameSaveManager.getSaveNames()) {
        if (existingSave.toLowerCase().equals(cleanName)) {
            setStatus(String.format("La sauvegarde '%s' existe déjà !", cleanName), ThemeManager.getFailColor());
            return;
        }
    }

    // Succès
    gameController.saveGame(cleanName);
    setStatus(String.format("Partie '%s' sauvegardée !", cleanName), ThemeManager.getSuccessColor());
    refreshSavesList();
}
```

**Pour Dolphine (rassurance)** : Chaque action produit un message textuel clair :
- "Veuillez entrer un nom de sauvegarde valide." → **Dolphine** comprend pourquoi son action a échoué
- "Partie 'maPartie' sauvegardée !" → **Dolphine** est rassurée : l'action a réussi
- "La sauvegarde 'maPartie' existe déjà !" → **Dolphine** sait exactement quoi corriger

**Pour Dorian (efficacité/confirmation)** : Le feedback est immédiat et sans ambiguïté. **Dorian** n'a pas à deviner si la sauvegarde a fonctionné. La couleur du texte (vert pour succès, rouge pour échec) lui permet une confirmation visuelle en un coup d'œil.

### `LoadSaveController` : Sélection explicite avant action

```java
// LoadSaveController.java
private void updateLoadButtonState() {
    if (loadButton != null) {
        loadButton.setDisable(selectedSaveName == null);
    }
}
```

Le bouton "Charger la sauvegarde" est **désactivé tant qu'aucune sauvegarde n'est sélectionnée**. Ce mécanisme :
- **Pour Thierry** : Évite les clics accidentels sur un bouton qui ne ferait rien
- **Pour Dolphine** : Guide l'utilisateur vers l'action correcte (sélectionner d'abord, charger ensuite)
- **Pour Dorian** : Le bouton devient actif immédiatement après la sélection, pas de délai inutile

### `SaveController` : Confirmation avant suppression

```java
// SaveController.java
private void handleDeleteSave(String saveName, Path file) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Suppression de sauvegarde");
    alert.setContentText("Voulez-vous vraiment supprimer la sauvegarde '" + saveName + "' ? Cette action est irréversible.");

    // Boutons personnalisés "Oui" / "Non"
    ButtonType yesButton = new ButtonType("Oui");
    ButtonType noButton = new ButtonType("Non");
    alert.getButtonTypes().setAll(yesButton, noButton);
}
```

La boîte de confirmation utilise des boutons **textuels** ("Oui"/"Non") plutôt que des icônes ou des couleurs seules. **Thierry** peut lire le texte sans dépendre de la couleur du bouton. **Elsa** bénéficie du contraste élevé du dialogue stylisé en thème sombre (`-fx-background-color: #202225`).

## 2. Fluidité du Flux : Navigation intuitive

### `GameController` : Orchestrateur central des transitions

Le `GameController` agit comme un **chef d'orchestre** qui synchronise l'état du jeu avec l'interface utilisateur :

```java
// GameController.java
public void advanceTurn() {
    if (game.getGameState() != GameState.RUNNING) {
        Logger.LogWarn("can't advance turn because game is not running");
        return;
    }

    game.advance();

    if (game.getGameState() == GameState.VICTORY) {
        loadCenterView(GamePhaseType.VICTORY.getFxmlFile());
    } else if (game.getGameState() == GameState.DEFEAT) {
        loadCenterView(GamePhaseType.DEFEAT.getFxmlFile());
    } else if (game.getGameState() == GameState.RUNNING) {
        loadAndExecuteCurrentPhase();
    }
}
```

**Pour Dolphine** : Chaque transition est logique et prévisible. Le jeu passe de phase en phase sans saut brusque. Le `loadAndExecuteCurrentPhase()` exécute d'abord la logique, puis met à jour l'UI, garantissant que **Dolphine** voit toujours l'état cohérent du jeu.

**Pour Dorian** : La méthode `advanceTurn()` est atomique. Un seul appel suffit pour passer à la phase suivante. **Dorian** peut enchaîner les actions sans attendre des animations superflues.

### Mise à jour en temps réel de l'interface

```java
// GameController.java
private void loadCurrentPhase() {
    syncPhaseView();  // Met à jour les indicateurs (mois, phase, événement)
    updateHudResources();  // Met à jour les ressources dans la sidebar
}

private void syncPhaseView() {
    GamePhase currentPhase = game.getCurrentPhase();
    updateHudPhaseIndicator(currentPhase);
    updateHudRoundIndicator(game.getCurrentRound());
    updateHudEventIndicator();

    Path phaseFxmlFile = currentPhase.getType().getFxmlFile();
    loadCenterView(phaseFxmlFile);
}
```

**Pour Elsa** : Les indicateurs en haut de l'écran (`monthLabel`, `phaseLabel`, `eventLabel`) sont mis à jour **avant** le chargement de la nouvelle phase. **Elsa** sait toujours où elle se trouve dans le cycle de jeu.

**Pour Thierry** : Les indicateurs textuels (ex: "Mois : 4 (Avril)", "Phase : Conseil") ne dépendent pas de la couleur. **Thierry** peut suivre le déroulement du jeu sans ambiguïté.

### Navigation entre fenêtres via `AppController`

```java
// AppController.java
public void navigate(NavigationAction action, WindowType target) {
    switch (action) {
        case PUSH -> {
            windowStack.push(new AppWindow(target, compileLayout(target)));
            updateWindow();
        }
        case POP -> {
            if (!windowStack.isEmpty()) windowStack.pop();
            if (!windowStack.isEmpty()) updateWindow();
            else Platform.exit();
        }
        case REPLACE -> {
            if (!windowStack.isEmpty()) windowStack.pop();
            navigate(NavigationAction.PUSH, target);
        }
        case EXIT -> Platform.exit();
    }
}
```

Le système de navigation utilise une **pile** (`Deque<AppWindow>`), ce qui signifie :
- **PUSH** : Ouvre une nouvelle fenêtre par-dessus la précédente
- **POP** : Revient à la fenêtre précédente
- **REPLACE** : Remplace la fenêtre courante

**Pour Dolphine** : Le bouton "Retour" (POP) la ramène toujours à l'écran précédent, comme attendu. Pas de surprise.

**Pour Dorian** : La navigation est prévisible et rapide. Pas de chargement inutile : les fenêtres sont préchargées via `preloadWindows()`.

## 3. Prévention de l'Erreur : Sécuriser l'expérience

### Boutons désactivés : Guide plutôt que punition

```java
// GameController.java
private void updateHudCrisisNavBar(int totalPages) {
    prevCrisisBtn.setDisable(currentCrisisPage == 1);
    nextCrisisBtn.setDisable(currentCrisisPage == totalPages);
}
```

Les boutons de navigation des crises sont désactivés quand on atteint les limites :
- `prevCrisisBtn` désactivé à la page 1
- `nextCrisisBtn` désactivé à la dernière page

**Pour Thierry** : Évite les clics accidentels qui ne produiraient aucun effet. Le bouton désactivé est visuellement distinct (style grisé), mais **Thierry** peut le reconnaître même sans distinguer les couleurs grâce au changement de curseur (`-fx-cursor: not-allowed`).

**Pour Elsa** : La frustration d'un clic sans réponse est évitée. Le bouton désactivé est clairement identifiable par son opacité réduite et son texte grisé.

### `AbilityAccordionWidget` : Verrouillage contextuel des capacités

```java
// CouncilPhaseController.java
private void setAbilityButtonConstraints(SmurfCharacter smurf, CharacterAbility ability, 
    AbilityAccordionWidget widget) {
    
    if (village.isActionLimitReached()) {
        widget.setActivationAllowed(false, 
            String.format("Limite de %d actions", village.getDynamicMaxAbilitiesPerTurn()));
    } else if (!smurf.hasEnoughEnergy(ability)) {
        widget.setActivationAllowed(false, "Énergie Insuffisante");
    } else if (!smurf.hasRequiredResources(village, ability)) {
        widget.setActivationAllowed(false, "Ressources Manquantes");
    } else {
        widget.setActivationAllowed(true, "Activer");
    }
}
```

Le bouton "Activer" est désactivé avec une **raison textuelle explicite** :
- "Énergie Insuffisante" → **Dolphine** comprend pourquoi elle ne peut pas agir
- "Ressources Manquantes" → **Dolphine** sait quoi collecter
- "Limite de 3 actions" → **Dolphine** comprend la règle du jeu

**Pour Thierry** : Le texte de verrouillage est prioritaire sur la couleur. Même si le rouge du bouton désactivé n'est pas perçu, le message textuel est lisible.

**Pour Elsa** : La taille de police réduite (9px pour les messages longs) est compensée par le contraste élevé (`#b8c2d1` sur fond `#4b5563`).

### `CrisisPhaseController` : Gestion de la défaite sans surprise

```java
// CrisisPhaseController.java
private void renderCrises() {
    List<Crisis> activeCrises = game.getVillage().getActiveCrises();
    if (activeCrises.size() > 3) {
        displayGameOverState();
        return;
    }
    // ... affichage normal des crises
}

private void displayGameOverState() {
    safeStateContainer.setVisible(false);
    crisisListWrapper.setVisible(false);
    gameOverContainer.setVisible(true);
    
    phaseTitleLabel.setText("FIN DE LA PARTIE");
    phaseTitleLabel.setTextFill(Color.web("#ef4444"));
    phaseSubtitleLabel.setText("Le village a succombé aux crises.");
    
    nextMonthButton.setVisible(false);
    nextMonthButton.setManaged(false);
    gameOverButtonsBox.setVisible(true);
    gameOverButtonsBox.setManaged(true);
}
```

**Pour Dolphine** : La transition vers l'état de défaite est **progressive et explicite** :
1. Le titre passe en rouge (`#ef4444`)
2. Le sous-titre explique la cause
3. Le bouton "Commencer le mois suivant" disparaît
4. Les boutons "Quitter" apparaissent

**Pour Dorian** : La condition de défaite (3+ crises) est vérifiée automatiquement. **Dorian** n'a pas à compter manuellement les crises.

### `ConsumptionPhaseController` : Feedback visuel sur l'état de santé

```java
// ConsumptionPhaseController.java
if (report.hasAnyCrisis()) {
    statusCardFrame.setStyle("-fx-background-color: #202225; -fx-border-color: #ef4444; ...");
    statusBadgeHeader.setStyle("-fx-background-color: #ef4444; ...");
    statusTitleLabel.setText(String.format("⚠️ ALERTES : %s", report.seasonName().toUpperCase()));
} else {
    statusCardFrame.setStyle("-fx-background-color: #202225; -fx-border-color: #3f444c; ...");
    statusBadgeHeader.setStyle("-fx-background-color: #10b981; ...");
    statusTitleLabel.setText(String.format("✅ RAS : %s", report.seasonName().toUpperCase()));
}
```

**Pour Thierry** : Le statut est indiqué par :
1. **Texte** : "ALERTES" vs "RAS" (indépendant de la couleur)
2. **Emoji** : ⚠️ vs ✅ (reconnaissable même sans couleur)
3. **Bordure** : Rouge vs vert (mais aussi épaisses, 1.5px)

**Pour Elsa** : Le contraste entre le badge rouge (`#ef4444`) et le fond sombre (`#1a1c1e`) est suffisant pour une identification rapide.

## 4. Conclusion : Une expérience sans friction

Le système d'interaction du Village des Schtroumpfs a été conçu pour **prévenir les erreurs avant qu'elles ne surviennent** plutôt que de les corriger après coup :

| Mécanisme | Persona cible | Bénéfice |
|-----------|---------------|----------|
| Feedback textuel immédiat | Dolphine, Dorian | Rassurance et confirmation |
| Boutons désactivés contextuellement | Thierry, Elsa | Prévention des clics accidentels |
| Confirmation avant suppression | Thierry, Dolphine | Sécurité des données |
| Navigation par pile (PUSH/POP) | Dolphine, Dorian | Prévisibilité et efficacité |
| Verrouillage avec raison textuelle | Dolphine, Thierry | Compréhension des contraintes |
| Transition progressive vers défaite | Dolphine, Elsa | Acceptation de l'échec |

Chaque interaction a été pensée pour qu'aucun utilisateur, quel que soit son profil (Elsa, Thierry, Dolphine ou Dorian), ne se retrouve bloqué, frustré ou perdu dans le flux du jeu.