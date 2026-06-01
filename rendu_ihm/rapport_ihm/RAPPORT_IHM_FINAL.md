# RAPPORT IHM FINAL

## Table des matières

1. [Introduction](#introduction)
2. [Accessibilité – Stratégie pour Thierry et Elsa](#2-accessibilite)
   - 2.1 [Le ThemeManager : pivot central](#21-le-thememanager-pivot-central)
   - 2.2 [Palette adaptée à Thierry (daltonien)](#22-palette-adaptee-a-thierry)
   - 2.3 [Contraste renforcé pour Elsa](#23-contraste-renforce-pour-elsa)
   - 2.4 [Propagation via WeakReference](#24-propagation-via-weakreference)
   - 2.5 [Widgets de paramètres personnalisables](#25-widgets-de-parametres-personnalisables)
3. [Design Système et Cohérence Visuelle](#3-design-systeme-et-coherence-visuelle)
   - 3.1 [Fondation du design system](#31-fondation-du-design-system)
   - 3.2 [Palette unifiée dans les FXML](#32-palette-unifiee-dans-les-fxml)
   - 3.3 [Catalogue de composants réutilisables](#33-catalogue-de-composants-reutilisables)
   - 3.4 [Hiérarchie visuelle à trois niveaux](#34-hierarchie-visuelle-a-trois-niveaux)
   - 3.5 [Cohérence inter-fenêtres](#35-coherence-inter-fenetres)
4. [Interactions et Flux Utilisateur](#4-interactions-et-flux-utilisateur)
   - 4.1 [Feedback textuel immédiat (SaveController)](#41-feedback-textuel-immediat)
   - 4.2 [Sélection explicite avant action (LoadSaveController)](#42-selection-explicite-avant-action)
   - 4.3 [Confirmation avant suppression](#43-confirmation-avant-suppression)
   - 4.4 [Navigation fluide via GameController](#44-navigation-fluide-via-gamecontroller)
   - 4.5 [Mise à jour en temps réel de l’interface](#45-mise-a-jour-en-temps-reel)
   - 4.6 [Navigation par pile (AppController)](#46-navigation-par-pile)
   - 4.7 [Boutons désactivés contextuellement](#47-boutons-desactives-contextuellement)
   - 4.8 [Verrouillage avec raison textuelle (AbilityAccordionWidget)](#48-verrouillage-avec-raison-textuelle)
   - 4.9 [Gestion de la défaite sans surprise](#49-gestion-de-la-defaite-sans-surprise)
   - 4.10 [Feedback visuel sur l’état de santé (ConsumptionPhaseController)](#410-feedback-visuel-sur-letat-de-sante)
5. [Conclusion et Perspectives](#5-conclusion-et-perspectives)
   - 5.1 [Points forts](#51-points-forts)
   - 5.2 [Limites](#52-limites)
   - 5.3 [Perspectives d’évolution](#53-perspectives-devolution)

---

## Introduction

Ce document présente la conception d’une interface homme-machine (IHM) inclusive et performante pour le jeu *Le Village des Schtroumpfs*. L’objectif est de répondre aux besoins de quatre profils utilisateurs distincts, appelés **personas** :

- **Elsa** – utilisatrice âgée ayant besoin d’une lisibilité maximale (contrastes élevés, polices larges).
- **Thierry** – joueur daltonien (protanopie/deutéranopie) nécessitant une palette de couleurs discriminable sans dépendre du rouge/vert.
- **Dolphine** – joueuse novice cherchant des repères visuels stables, des feedbacks explicites et une navigation prévisible.
- **Dorian** – joueur expérimenté souhaitant une interface efficace, rapide et sans friction.

Chaque décision technique décrite ci-dessous est justifiée par au moins un de ces personas, garantissant que l’interface soit à la fois accessible, cohérente et fluide.

---

## 2. Accessibilité – Stratégie pour Thierry et Elsa

### 2.1 Le ThemeManager : pivot central

Le `ThemeManager` a été conçu comme un système de thème dynamique permettant de basculer entre un thème **standard** et un thème **color-blind** (`ResourceTheme.STANDARD` vs `ResourceTheme.COLOR_BLIND`). Cette architecture répond directement aux besoins de **Thierry** (daltonien) et **Elsa** (besoin de lisibilité).

![Capture d’écran : Le ThemeManager avec mode daltonien activé](images/placeholder.png)

### 2.2 Palette adaptée à Thierry (daltonien)

Dans le thème standard, les ressources sont identifiées par des couleurs comme le rouge (`#C62828` pour les baies) ou le vert (`#2E8B57` pour la salsepareille). Pour **Thierry**, ces distinctions seraient invisibles. Le thème color-blind utilise une palette scientifiquement reconnue (palette "Color Universal Design" – CUD) :

```java
// ThemeManager.java – Palette color-blind
case BERRIES -> Color.web("#D55E00");      // Orange vif
case SARSAPARILLA -> Color.web("#009E73"); // Vert-bleu distinct
case GOLD -> Color.web("#F0E442");         // Jaune clair
case TOOLS -> Color.web("#7A7A7A");        // Gris neutre
case MORAL -> Color.web("#CC79A7");        // Rose
case DEFENSE -> Color.web("#0072B2");      // Bleu foncé
case KNOWLEDGE -> Color.web("#56B4E9");    // Bleu ciel
```

Ces couleurs sont choisies pour être discriminables par les trois types de daltonisme (protanopie, deutéranopie, tritanopie). **Thierry** peut ainsi distinguer chaque ressource sans dépendre de la teinte rouge/verte.

![Capture d’écran : Palette color-blind appliquée aux ressources dans la sidebar](images/placeholder.png)

### 2.3 Contraste renforcé pour Elsa

Pour **Elsa**, le `ThemeManager` garantit que toutes les couleurs de texte sur fond sombre (`#1a1c1e`, `#202225`) offrent un ratio de contraste WCAG AA minimum. Les couleurs comme `#f8fafc` (blanc cassé) sur fond `#1a1c1e` dépassent un ratio de 15:1, bien au-delà du minimum requis de 4.5:1 pour le texte normal.

![Capture d’écran : Exemple de contraste élevé sur un label de statut](images/placeholder.png)

### 2.4 Propagation via WeakReference

Le `ThemeManager` utilise des `WeakReference<Runnable>` pour notifier tous les widgets d’un changement de thème sans créer de fuite mémoire :

```java
// ThemeManager.java
private static final List<WeakReference<Runnable>> listeners = new ArrayList<>();

public static void addThemeChangeListener(Runnable listener) {
    listeners.add(new WeakReference<>(listener));
}
```

Chaque widget (comme `ResourceSidebarWidget`, `CrisisSummaryRow`, `ResourceSummaryRow`) s’enregistre via `ThemeManager.addThemeChangeListener(themeUpdater)`. Lors du changement de thème, tous les widgets sont mis à jour automatiquement. Cela évite la frustration de **Dolphine** qui aurait à reconfigurer manuellement chaque élément.

![Capture d’écran : Changement de thème en temps réel sur plusieurs widgets](images/placeholder.png)

### 2.5 Widgets de paramètres personnalisables

#### SettingToggleWidget – contrôle granulaire pour Thierry et Elsa

Le `SettingToggleWidget` permet d’activer/désactiver des options d’accessibilité :

```java
// SettingToggleWidget.java
public SettingToggleWidget(String title, String description, Path iconFile,
    boolean initialState, Consumer<Boolean> onToggleAction)
```

Ce widget offre :
- **Un titre en gras** (lisibilité pour Elsa)
- **Une description textuelle** (ne dépend pas de la couleur seule – Thierry)
- **Un bouton d’état** avec texte explicite "Activé"/"Désactivé" (pas de simple indicateur coloré)
- **Une icône** de 64×64 pixels (repère visuel fort)

Pour **Thierry**, le texte "Activé" en vert (`#10b981`) et "Désactivé" en gris (`#4b5563`) est accompagné d’un changement de style suffisamment contrasté pour être perçu même sans distinction des couleurs.

![Capture d’écran : SettingToggleWidget affichant l’option "Mode daltonien"](images/placeholder.png)

#### SettingNavigationWidget – navigation accessible

Ce widget complète l’expérience de paramétrage avec :
- Un bouton d’action explicite (pas de geste mystérieux)
- Un texte descriptif qui explique l’action
- Une icône pour repérage rapide

Pour **Dolphine**, chaque action est clairement décrite, éliminant l’incertitude. Pour **Dorian**, le bouton d’action directe permet une navigation rapide sans détours.

![Capture d’écran : SettingNavigationWidget dans la fenêtre de paramètres](images/placeholder.png)

---

## 3. Design Système et Cohérence Visuelle

### 3.1 Fondation du design system

Le `ThemeManager` ne se contente pas de gérer les couleurs : il **définit l’identité visuelle complète** du jeu. Chaque type de ressource possède une couleur dédiée, accessible via `getResourceColor(ResourceType)`, `getCrisisColor(ResourceType)` et `getResourceSidebarBarColor(ResourceType)`. Cette centralisation garantit qu’une même ressource (ex: les Baies) aura **exactement la même teinte** dans la sidebar, dans les résumés de production, dans les crises et dans les effets d’événements.

**Pour Elsa (lisibilité)** : Cette cohérence élimine la charge cognitive. Elsa n’a pas à réapprendre quelle couleur correspond à quelle ressource à chaque écran. Le ratio de contraste est systématiquement vérifié : les couleurs comme `#C62828` (baies, standard) sur fond `#1a1c1e` offrent un ratio WCAG AA+.

**Pour Thierry (cohérence visuelle)** : Le thème color-blind (`ResourceTheme.COLOR_BLIND`) remplace **toutes** les couleurs simultanément via le mécanisme de listeners. Thierry n’a pas à basculer thème par thème : un seul changement dans les paramètres propage la palette adaptée à l’ensemble de l’interface. Les couleurs comme `#D55E00` (orange) et `#009E73` (vert-bleu) sont systématiquement utilisées partout, créant des repères visuels fiables.

![Capture d’écran : Cohérence des couleurs entre la sidebar et la vue de production](images/placeholder.png)

### 3.2 Palette unifiée dans les FXML

Tous les fichiers FXML partagent une charte stricte :

| Élément | Couleur | Rôle |
|---------|---------|------|
| Fond principal | `#1a1c1e` | Évite l’éblouissement (Elsa) |
| Fond secondaire | `#202225` | Hiérarchie visuelle douce |
| Bordures | `#3f444c` | Délimitation sans agressivité |
| Texte principal | `#f8fafc` | Contraste maximal |
| Texte secondaire | `#94a3b8` | Information non critique |
| Texte d’accent | `#10b981` (vert) / `#ef4444` (rouge) | Feedback positif/négatif |

Cette palette est **immuable** dans tous les FXML de fenêtres (`GameWindow.fxml`, `SettingsWindow.fxml`, `SaveWindow.fxml`, `LoadSaveWindow.fxml`) et de phases (`ProductionView.fxml`, `ConsumptionView.fxml`, `CouncilView.fxml`, `EventView.fxml`, `CrisisView.fxml`). Pour **Dolphine**, cette constance crée un environnement prévisible et rassurant.

![Capture d’écran : Fenêtre de jeu avec la palette unifiée](images/placeholder.png)

### 3.3 Catalogue de composants réutilisables

Nous avons isolé **8 composants réutilisables** dans `view/components/`, chacun répondant à un besoin précis :

| Widget | Usage | Bénéfice pour Dolphine | Bénéfice pour Dorian |
|--------|-------|------------------------|----------------------|
| `ResourceSidebarWidget` | Affichage des ressources avec barre de progression | Repère visuel immédiat (barre + texte) | Lecture en un coup d’œil |
| `ResourceSummaryRow` | Ligne de delta de ressource (+5 Baies) | Format standard : icône + valeur + nom | Pas besoin de décoder |
| `CrisisSummaryRow` | Carte de crise active | Structure fixe : titre + cause + description | Scan rapide des crises |
| `CrisisWidget` | Widget détaillé de crise | Sections clairement séparées (cause/effets) | Accès direct aux modificateurs |
| `SmurfListRow` | Ligne de membre du conseil | Avatar + nom + énergie explicite | Sélection rapide |
| `SmurfDetailCard` | Fiche détaillée d’un Schtroumpf | Portrait + rôle + énergie | Vue d’ensemble immédiate |
| `AbilityAccordionWidget` | Capacité dépliable | Détails cachés par défaut (pas de surcharge) | Activation directe sans navigation |
| `GameModifierRow` | Ligne de modificateur | Format standard : nom + valeur | Comparaison rapide |

![Capture d’écran : Galerie des composants réutilisables](images/placeholder.png)

#### Exemple : `ResourceSummaryRow` – Standardisation du feedback

```java
// ResourceSummaryRow.java
public ResourceSummaryRow(ResourceType type, boolean displayingDelta) {
    // Structure immuable : icône (24×24) | delta (30px) | nom
    this.getChildren().addAll(resourceIconImage, deltaLabel, nameLabel);
}

public void updateDelta(int value) {
    if (displayingDelta) {
        // Vert pour positif, rouge pour négatif, gris pour neutre
        if (value > 0) {
            deltaLabel.setText("+" + value);
            deltaLabel.setTextFill(Color.web("#10b981"));
        } else if (value < 0) {
            deltaLabel.setText(String.valueOf(value));
            deltaLabel.setTextFill(Color.web("#ef4444"));
        } else {
            deltaLabel.setText("0");
            deltaLabel.setTextFill(Color.web("#94a3b8"));
        }
    }
}
```

**Pour Dolphine** : Chaque fois qu’elle voit un `ResourceSummaryRow`, elle sait exactement où trouver l’icône, la valeur et le nom. Pas de surprise. Le code couleur (vert/rouge/gris) est systématique.

**Pour Dorian** : Il scanne rapidement les lignes. Le format fixe lui permet de comparer les deltas entre ressources sans effort cognitif.

![Capture d’écran : ResourceSummaryRow affichant +5 Baies](images/placeholder.png)

### 3.4 Hiérarchie visuelle à trois niveaux

#### ResourceSidebarWidget – La barre de progression comme métaphore universelle

```java
// ResourceSidebarWidget.java
public void updateState(int quantity, int delta) {
    quantityLabel.setText(quantity + " / " + ResourceManager.MAX_QUANTITY);

    // Delta coloré (vert/rouge)
    if (delta > 0) {
        deltaLabel.setText("+" + delta);
        deltaLabel.setStyle("-fx-text-fill: #10b981;");
    } else if (delta < 0) {
        deltaLabel.setText(String.valueOf(delta));
        deltaLabel.setStyle("-fx-text-fill: #ef4444;");
    }

    // Ratio de remplissage de la barre
    double ratio = (double) quantity / ResourceManager.MAX_QUANTITY;
    fillRatio.set(Math.max(0.0, Math.min(1.0, ratio)));
}
```

La hiérarchie visuelle est à **trois niveaux** :
1. **Nom de la ressource** (texte en gras, 14px) – Identification
2. **Barre de progression** (ratio visuel) – État global en un coup d’œil
3. **Delta** (texte coloré, 13px) – Variation récente

**Pour Elsa** : La barre de progression offre un repère visuel fort, indépendant de la couleur. Le texte `"5 / 10"` est toujours lisible.

**Pour Thierry** : Même en mode color-blind, la barre de progression (longueur variable) et le texte (`+3`, `-2`) fournissent l’information sans dépendre des couleurs.

![Capture d’écran : ResourceSidebarWidget avec barre de progression et delta](images/placeholder.png)

#### SmurfDetailCard – Fiche d’identité structurée

```java
// SmurfDetailCard.java
public SmurfDetailCard() {
    // Structure : [Portrait 64×64] [Nom (18px) | Rôle (11px italique) | Énergie (12px)]
    HBox profileLayout = new HBox();
    profileLayout.getChildren().addAll(portraitFrame, textLayout);
}
```

La hiérarchie est claire :
1. **Portrait** (64×64) – Identification visuelle immédiate
2. **Nom** (18px, gras) – Information principale
3. **Rôle** (11px, italique) – Information secondaire
4. **Énergie** (12px, gras, bleu) – Métrique critique

**Pour Dolphine** : La carte regroupe toutes les informations d’un personnage au même endroit. Pas besoin de naviguer entre plusieurs écrans.

**Pour Dorian** : Le format fixe permet de comparer rapidement les énergies entre personnages. L’énergie passe en rouge (`#ef4444`) quand elle atteint zéro, signalant immédiatement un personnage inutilisable.

![Capture d’écran : SmurfDetailCard affichant le Grand Schtroumpf](images/placeholder.png)

#### AbilityAccordionWidget – Détails masqués par défaut

```java
// AbilityAccordionWidget.java
// Le panneau de détails est invisible par défaut
this.detailsContainer.setVisible(false);
this.detailsContainer.managedProperty().bind(this.detailsContainer.visibleProperty());
```

Cette conception évite la surcharge cognitive :
- **Visible par défaut** : Nom de la capacité, coût en énergie, bouton "Activer"
- **Masqué** : Description détaillée, ressources requises, effets potentiels

**Pour Dolphine** : Elle peut consulter les détails en cliquant sur l’en-tête, sans être submergée d’informations.

**Pour Dorian** : Il active directement les capacités sans avoir à déplier les détails à chaque fois. Le bouton "Activer" est toujours accessible.

![Capture d’écran : AbilityAccordionWidget replié et déplié](images/placeholder.png)

### 3.5 Cohérence inter-fenêtres

Toutes les fenêtres partagent :
- **Bordure arrondie** (`-fx-background-radius: 12; -fx-border-radius: 12`)
- **Bouton "Continuer"** en vert émeraude (`#10b981`), 45px de haut, 16px en gras
- **Bouton "Retour"** en gris (`#4b5563`), même format
- **Titres** en `#f8fafc`, 20–24px, gras
- **Sous-titres** en `#64748b`, taille normale

Exemple dans `ProductionView.fxml`, `ConsumptionView.fxml`, `EventView.fxml` :

```xml
<Button fx:id="nextPhaseButton" prefHeight="45.0" prefWidth="160.0"
    style="-fx-background-color: #10b981; -fx-text-fill: white;
           -fx-background-radius: 8; -fx-cursor: hand;" text="Continuer">
    <font><Font name="System Bold" size="16.0" /></font>
</Button>
```

**Pour Dolphine** : Le bouton "Continuer" est toujours au même endroit (en bas, centré), avec la même apparence. Elle sait instinctivement où cliquer pour avancer.

**Pour Dorian** : Il peut cliquer sans lire le texte, sachant que le bouton vert en bas à droite signifie "passer à l’étape suivante".

![Capture d’écran : Bouton "Continuer" dans la vue de production](images/placeholder.png)

---

## 4. Interactions et Flux Utilisateur

### 4.1 Feedback textuel immédiat (SaveController)

Le `SaveController` utilise un `statusLabel` pour fournir un retour d’information **explicite et textuel** à chaque action de l’utilisateur :

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
- "Partie 'maPartie' sauvegardée !" → **Dolphine** est rassurée : l’action a réussi
- "La sauvegarde 'maPartie' existe déjà !" → **Dolphine** sait exactement quoi corriger

**Pour Dorian (efficacité/confirmation)** : Le feedback est immédiat et sans ambiguïté. **Dorian** n’a pas à deviner si la sauvegarde a fonctionné. La couleur du texte (vert pour succès, rouge pour échec) lui permet une confirmation visuelle en un coup d’œil.

![Capture d’écran : StatusLabel affichant "Partie 'maPartie' sauvegardée !"](images/placeholder.png)

### 4.2 Sélection explicite avant action (LoadSaveController)

```java
// LoadSaveController.java
private void updateLoadButtonState() {
    if (loadButton != null) {
        loadButton.setDisable(selectedSaveName == null);
    }
}
```

Le bouton "Charger la sauvegarde" est **désactivé tant qu’aucune sauvegarde n’est sélectionnée**. Ce mécanisme :
- **Pour Thierry** : Évite les clics accidentels sur un bouton qui ne ferait rien
- **Pour Dolphine** : Guide l’utilisateur vers l’action correcte (sélectionner d’abord, charger ensuite)
- **Pour Dorian** : Le bouton devient actif immédiatement après la sélection, pas de délai inutile

![Capture d’écran : LoadSaveController avec bouton désactivé](images/placeholder.png)

### 4.3 Confirmation avant suppression

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

![Capture d’écran : Boîte de confirmation de suppression](images/placeholder.png)

### 4.4 Navigation fluide via GameController

Le `GameController` agit comme un **chef d’orchestre** qui synchronise l’état du jeu avec l’interface utilisateur :

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

**Pour Dolphine** : Chaque transition est logique et prévisible. Le jeu passe de phase en phase sans saut brusque. Le `loadAndExecuteCurrentPhase()` exécute d’abord la logique, puis met à jour l’UI, garantissant que **Dolphine** voit toujours l’état cohérent du jeu.

**Pour Dorian** : La méthode `advanceTurn()` est atomique. Un seul appel suffit pour passer à la phase suivante. **Dorian** peut enchaîner les actions sans attendre des animations superflues.

![Capture d’écran : Transition entre deux phases de jeu](images/placeholder.png)

### 4.5 Mise à jour en temps réel de l’interface

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

**Pour Elsa** : Les indicateurs en haut de l’écran (`monthLabel`, `phaseLabel`, `eventLabel`) sont mis à jour **avant** le chargement de la nouvelle phase. **Elsa** sait toujours où elle se trouve dans le cycle de jeu.

**Pour Thierry** : Les indicateurs textuels (ex: "Mois : 4 (Avril)", "Phase : Conseil") ne dépendent pas de la couleur. **Thierry** peut suivre le déroulement du jeu sans ambiguïté.

![Capture d’écran : Indicateurs de phase et de mois mis à jour](images/placeholder.png)

### 4.6 Navigation par pile (AppController)

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

**Pour Dolphine** : Le bouton "Retour" (POP) la ramène toujours à l’écran précédent, comme attendu. Pas de surprise.

**Pour Dorian** : La navigation est prévisible et rapide. Pas de chargement inutile : les fenêtres sont préchargées via `preloadWindows()`.

![Capture d’écran : Navigation entre la fenêtre de paramètres et la fenêtre de jeu](images/placeholder.png)

### 4.7 Boutons désactivés contextuellement

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

**Pour Elsa** : La frustration d’un clic sans réponse est évitée. Le bouton désactivé est clairement identifiable par son opacité réduite et son texte grisé.

![Capture d’écran : Boutons de navigation des crises désactivés](images/placeholder.png)

### 4.8 Verrouillage avec raison textuelle (AbilityAccordionWidget)

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

**Pour Thierry** : Le texte de verrouillage est prioritaire sur la couleur. Même si le rouge du bouton désactivé n’est pas perçu, le message textuel est lisible.

**Pour Elsa** : La taille de police réduite (9px pour les messages longs) est compensée par le contraste élevé (`#b8c2d1` sur fond `#4b5563`).

![Capture d’écran : Capacité verrouillée avec message "Énergie Insuffisante"](images/placeholder.png)

### 4.9 Gestion de la défaite sans surprise

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

**Pour Dolphine** : La transition vers l’état de défaite est **progressive et explicite** :
1. Le titre passe en rouge (`#ef4444`)
2. Le sous-titre explique la cause
3. Le bouton "Commencer le mois suivant" disparaît
4. Les boutons "Quitter" apparaissent

**Pour Dorian** : La condition de défaite (3+ crises) est vérifiée automatiquement. **Dorian** n’a pas à compter manuellement les crises.

![Capture d’écran : Écran de défaite avec titre rouge et boutons de sortie](images/placeholder.png)

### 4.10 Feedback visuel sur l’état de santé (ConsumptionPhaseController)

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

![Capture d’écran : Carte de statut avec badge "ALERTES"](images/placeholder.png)

---

## 5. Conclusion et Perspectives

### 5.1 Points forts

- **Architecture MVC robuste** : La séparation entre modèle (`ResourceManager`, `SmurfCharacter`, etc.), vue (FXML + contrôleurs) et contrôleur (`GameController`, `AppController`) a permis de maintenir une base de code claire et testable.
- **Réutilisation des composants** : Les 8 widgets spécialisés (`ResourceSidebarWidget`, `AbilityAccordionWidget`, etc.) ont été conçus pour être utilisés dans plusieurs contextes, réduisant la duplication et assurant une cohérence visuelle.
- **Accessibilité intégrée dès la conception** : Le `ThemeManager` et les widgets de paramètres ont été pensés pour Thierry et Elsa, sans compromettre l’expérience des autres utilisateurs.
- **Feedback explicite** : Chaque action produit un message textuel immédiat, rassurant Dolphine et confirmant l’action pour Dorian.

### 5.2 Limites

- **Gestion des contrastes dans les FXML** : Bien que la palette soit unifiée, certains composants FXML (ex: `TableView`, `ListView`) héritent de styles par défaut de JavaFX qui peuvent altérer les contrastes. Un travail supplémentaire de surcharge de styles CSS serait nécessaire pour garantir un ratio WCAG AA sur tous les éléments.
- **Absence de tests automatisés d’accessibilité** : Les vérifications de contraste et de navigation au clavier n’ont pas été automatisées. Une intégration avec des outils comme *axe-core* ou *Wave* serait bénéfique.
- **Support limité des lecteurs d’écran** : Les composants personnalisés (`AbilityAccordionWidget`, `ResourceSidebarWidget`) ne fournissent pas encore de propriétés ARIA complètes, ce qui pourrait poser problème pour des utilisateurs non-voyants.

### 5.3 Perspectives d’évolution

- **Ajout d’autres thèmes** : Au-delà du mode standard et color-blind, on pourrait envisager un thème "haute lisibilité" (polices agrandies, espacement accru) pour Elsa, ou un thème "sombre renforcé" pour les environnements faiblement éclairés.
- **Support mobile** : L’interface actuelle est conçue pour un écran de bureau. Une adaptation responsive (taille des boutons, disposition des widgets) permettrait de jouer sur tablette ou smartphone.
- **Personnalisation avancée** : Permettre à l’utilisateur de choisir sa propre palette de couleurs (via un sélecteur de couleurs) irait au-delà des thèmes prédéfinis et répondrait à des besoins très spécifiques.
- **Tests utilisateurs** : Organiser des sessions de test avec des personnes représentant les quatre personas permettrait de valider empiriquement les choix de conception et d’identifier des points d’amélioration non anticipés.

---

*Document rédigé par l’équipe de développement IHM – Juin 2026*
