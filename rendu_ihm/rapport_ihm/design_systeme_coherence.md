# Rapport IHM 2 : Design Système et Cohérence Visuelle

## 1. Système de Design : Une identité visuelle forte et inclusive

### Le `ThemeManager` comme fondation du design system

Le `ThemeManager` ne se contente pas de gérer les couleurs : il **définit l'identité visuelle complète** du jeu. Chaque type de ressource possède une couleur dédiée, accessible via `getResourceColor(ResourceType)`, `getCrisisColor(ResourceType)` et `getResourceSidebarBarColor(ResourceType)`. Cette centralisation garantit qu'une même ressource (ex: les Baies) aura **exactement la même teinte** dans la sidebar, dans les résumés de production, dans les crises et dans les effets d'événements.

**Pour Elsa (lisibilité)** : Cette cohérence élimine la charge cognitive. Elsa n'a pas à réapprendre quelle couleur correspond à quelle ressource à chaque écran. Le ratio de contraste est systématiquement vérifié : les couleurs comme `#C62828` (baies, standard) sur fond `#1a1c1e` offrent un ratio WCAG AA+.

**Pour Thierry (cohérence visuelle)** : Le thème color-blind (`ResourceTheme.COLOR_BLIND`) remplace **toutes** les couleurs simultanément via le mécanisme de listeners. Thierry n'a pas à basculer thème par thème : un seul changement dans les paramètres propage la palette adaptée à l'ensemble de l'interface. Les couleurs comme `#D55E00` (orange) et `#009E73` (vert-bleu) sont systématiquement utilisées partout, créant des repères visuels fiables.

### Palette unifiée dans les FXML

Tous les fichiers FXML partagent une charte stricte :

| Élément | Couleur | Rôle |
|---------|---------|------|
| Fond principal | `#1a1c1e` | Évite l'éblouissement (Elsa) |
| Fond secondaire | `#202225` | Hiérarchie visuelle douce |
| Bordures | `#3f444c` | Délimitation sans agressivité |
| Texte principal | `#f8fafc` | Contraste maximal |
| Texte secondaire | `#94a3b8` | Information non critique |
| Texte d'accent | `#10b981` (vert) / `#ef4444` (rouge) | Feedback positif/négatif |

Cette palette est **immuable** dans tous les FXML de fenêtres (`GameWindow.fxml`, `SettingsWindow.fxml`, `SaveWindow.fxml`, `LoadSaveWindow.fxml`) et de phases (`ProductionView.fxml`, `ConsumptionView.fxml`, `CouncilView.fxml`, `EventView.fxml`, `CrisisView.fxml`). Pour **Dolphine**, cette constance crée un environnement prévisible et rassurant.

## 2. Composants Réutilisables : Standardisation et apprentissage

### Catalogue de widgets spécialisés

Nous avons isolé **8 composants réutilisables** dans `view/components/`, chacun répondant à un besoin précis :

| Widget | Usage | Bénéfice pour Dolphine | Bénéfice pour Dorian |
|--------|-------|------------------------|----------------------|
| `ResourceSidebarWidget` | Affichage des ressources avec barre de progression | Repère visuel immédiat (barre + texte) | Lecture en un coup d'œil |
| `ResourceSummaryRow` | Ligne de delta de ressource (+5 Baies) | Format standard : icône + valeur + nom | Pas besoin de décoder |
| `CrisisSummaryRow` | Carte de crise active | Structure fixe : titre + cause + description | Scan rapide des crises |
| `CrisisWidget` | Widget détaillé de crise | Sections clairement séparées (cause/effets) | Accès direct aux modificateurs |
| `SmurfListRow` | Ligne de membre du conseil | Avatar + nom + énergie explicite | Sélection rapide |
| `SmurfDetailCard` | Fiche détaillée d'un Schtroumpf | Portrait + rôle + énergie | Vue d'ensemble immédiate |
| `AbilityAccordionWidget` | Capacité dépliable | Détails cachés par défaut (pas de surcharge) | Activation directe sans navigation |
| `GameModifierRow` | Ligne de modificateur | Format standard : nom + valeur | Comparaison rapide |

### Exemple : `ResourceSummaryRow` - Standardisation du feedback

```java
// ResourceSummaryRow.java
public ResourceSummaryRow(ResourceType type, boolean displayingDelta) {
    // Structure immuable : icône (24x24) | delta (30px) | nom
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

**Pour Dolphine** : Chaque fois qu'elle voit un `ResourceSummaryRow`, elle sait exactement où trouver l'icône, la valeur et le nom. Pas de surprise. Le code couleur (vert/rouge/gris) est systématique.

**Pour Dorian** : Il scanne rapidement les lignes. Le format fixe lui permet de comparer les deltas entre ressources sans effort cognitif.

### `SettingToggleWidget` et `SettingNavigationWidget` : Paramètres standardisés

Ces widgets suivent un patron identique :
```
[Icône 64x64] [Titre + Description] [Bouton d'action]
```

Pour **Thierry**, l'icône et le texte sont des repères redondants qui ne dépendent pas de la couleur. Pour **Elsa**, la taille de police (18px pour le titre, 13px pour la description) garantit une lisibilité optimale.

## 3. Hiérarchie Visuelle : Lecture rapide sans surcharge

### `ResourceSidebarWidget` : La barre de progression comme métaphore universelle

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
1. **Nom de la ressource** (texte en gras, 14px) - Identification
2. **Barre de progression** (ratio visuel) - État global en un coup d'œil
3. **Delta** (texte coloré, 13px) - Variation récente

**Pour Elsa** : La barre de progression offre un repère visuel fort, indépendant de la couleur. Le texte `"5 / 10"` est toujours lisible.

**Pour Thierry** : Même en mode color-blind, la barre de progression (longueur variable) et le texte (`+3`, `-2`) fournissent l'information sans dépendre des couleurs.

### `SmurfDetailCard` : Fiche d'identité structurée

```java
// SmurfDetailCard.java
public SmurfDetailCard() {
    // Structure : [Portrait 64x64] [Nom (18px) | Rôle (11px italique) | Énergie (12px)]
    HBox profileLayout = new HBox();
    profileLayout.getChildren().addAll(portraitFrame, textLayout);
}
```

La hiérarchie est claire :
1. **Portrait** (64x64) - Identification visuelle immédiate
2. **Nom** (18px, gras) - Information principale
3. **Rôle** (11px, italique) - Information secondaire
4. **Énergie** (12px, gras, bleu) - Métrique critique

**Pour Dolphine** : La carte regroupe toutes les informations d'un personnage au même endroit. Pas besoin de naviguer entre plusieurs écrans.

**Pour Dorian** : Le format fixe permet de comparer rapidement les énergies entre personnages. L'énergie passe en rouge (`#ef4444`) quand elle atteint zéro, signalant immédiatement un personnage inutilisable.

### `AbilityAccordionWidget` : Détails masqués par défaut

```java
// AbilityAccordionWidget.java
// Le panneau de détails est invisible par défaut
this.detailsContainer.setVisible(false);
this.detailsContainer.managedProperty().bind(this.detailsContainer.visibleProperty());
```

Cette conception évite la surcharge cognitive :
- **Visible par défaut** : Nom de la capacité, coût en énergie, bouton "Activer"
- **Masqué** : Description détaillée, ressources requises, effets potentiels

**Pour Dolphine** : Elle peut consulter les détails en cliquant sur l'en-tête, sans être submergée d'informations.

**Pour Dorian** : Il active directement les capacités sans avoir à déplier les détails à chaque fois. Le bouton "Activer" est toujours accessible.

## 4. Cohérence inter-fenêtres : Un langage visuel unifié

Toutes les fenêtres partagent :
- **Bordure arrondie** (`-fx-background-radius: 12; -fx-border-radius: 12`)
- **Bouton "Continuer"** en vert émeraude (`#10b981`), 45px de haut, 16px en gras
- **Bouton "Retour"** en gris (`#4b5563`), même format
- **Titres** en `#f8fafc`, 20-24px, gras
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

**Pour Dorian** : Il peut cliquer sans lire le texte, sachant que le bouton vert en bas à droite signifie "passer à l'étape suivante".

## 5. Conclusion

Le design system du Village des Schtroumpfs repose sur trois piliers :
1. **Centralisation** via `ThemeManager` pour une identité visuelle cohérente et adaptable
2. **Composants réutilisables** pour une expérience d'apprentissage progressive (Dolphine) et une efficacité maximale (Dorian)
3. **Hiérarchie visuelle à trois niveaux** (identification → état → variation) pour une lecture rapide sans surcharge cognitive

Chaque choix de design est justifié par au moins un persona, garantissant que l'interface est à la fois accessible (Elsa, Thierry), claire (Dolphine) et efficace (Dorian).