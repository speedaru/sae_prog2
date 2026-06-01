# Rapport IHM 1 : Stratégie d'Accessibilité et Personas

## 1. Gestion des thèmes pour Thierry et Elsa

### Le `ThemeManager` : un pivot central pour l'accessibilité

Le `ThemeManager` a été conçu comme un système de thème dynamique permettant de basculer entre un thème **standard** et un thème **color-blind** (`ResourceTheme.STANDARD` vs `ResourceTheme.COLOR_BLIND`). Cette architecture répond directement aux besoins de **Thierry** (daltonien) et **Elsa** (besoin de lisibilité).

#### Palette de couleurs adaptée à Thierry

Dans le thème standard, les ressources sont identifiées par des couleurs comme le rouge (`#C62828` pour les baies) ou le vert (`#2E8B57` pour la salsepareille). Pour **Thierry**, ces distinctions seraient invisibles. Le thème color-blind utilise une palette scientifiquement reconnue (palette "Color Universal Design" - CUD) :

```java
// ThemeManager.java - Palette color-blind
case BERRIES -> Color.web("#D55E00");      // Orange vif
case SARSAPARILLA -> Color.web("#009E73"); // Vert-bleu distinct
case GOLD -> Color.web("#F0E442");         // Jaune clair
case TOOLS -> Color.web("#7A7A7A");        // Gris neutre
case MORAL -> Color.web("#CC79A7");        // Rose
case DEFENSE -> Color.web("#0072B2");      // Bleu foncé
case KNOWLEDGE -> Color.web("#56B4E9");    // Bleu ciel
```

Ces couleurs sont choisies pour être discriminables par les trois types de daltonisme (protanopie, deutéranopie, tritanopie). **Thierry** peut ainsi distinguer chaque ressource sans dépendre de la teinte rouge/verte.

#### Contraste renforcé pour Elsa

Pour **Elsa**, le `ThemeManager` garantit que toutes les couleurs de texte sur fond sombre (`#1a1c1e`, `#202225`) offrent un ratio de contraste WCAG AA minimum. Les couleurs comme `#f8fafc` (blanc cassé) sur fond `#1a1c1e` dépassent un ratio de 15:1, bien au-delà du minimum requis de 4.5:1 pour le texte normal.

### Propagation des changements via WeakReference

Le `ThemeManager` utilise des `WeakReference<Runnable>` pour notifier tous les widgets d'un changement de thème sans créer de fuite mémoire :

```java
// ThemeManager.java
private static final List<WeakReference<Runnable>> listeners = new ArrayList<>();

public static void addThemeChangeListener(Runnable listener) {
    listeners.add(new WeakReference<>(listener));
}
```

Chaque widget (comme `ResourceSidebarWidget`, `CrisisSummaryRow`, `ResourceSummaryRow`) s'enregistre via `ThemeManager.addThemeChangeListener(themeUpdater)`. Lors du changement de thème, tous les widgets sont mis à jour automatiquement. Cela évite la frustration de **Dolphine** qui aurait à reconfigurer manuellement chaque élément.

## 2. Personnalisation via les widgets de paramètres

### `SettingToggleWidget` : contrôle granulaire pour Thierry et Elsa

Le `SettingToggleWidget` permet d'activer/désactiver des options d'accessibilité :

```java
// SettingToggleWidget.java
public SettingToggleWidget(String title, String description, Path iconFile, 
    boolean initialState, Consumer<Boolean> onToggleAction)
```

Ce widget offre :
- **Un titre en gras** (lisibilité pour Elsa)
- **Une description textuelle** (ne dépend pas de la couleur seule - Thierry)
- **Un bouton d'état** avec texte explicite "Activé"/"Désactivé" (pas de simple indicateur coloré)
- **Une icône** de 64x64 pixels (repère visuel fort)

Pour **Thierry**, le texte "Activé" en vert (`#10b981`) et "Désactivé" en gris (`#4b5563`) est accompagné d'un changement de style suffisamment contrasté pour être perçu même sans distinction des couleurs.

### `SettingNavigationWidget` : navigation accessible

Ce widget complète l'expérience de paramétrage avec :
- Un bouton d'action explicite (pas de geste mystérieux)
- Un texte descriptif qui explique l'action
- Une icône pour repérage rapide

Pour **Dolphine**, chaque action est clairement décrite, éliminant l'incertitude. Pour **Dorian**, le bouton d'action directe permet une navigation rapide sans détours.

## 3. Design global anti-frustration

### Cohérence visuelle dans tous les FXML

Toutes les fenêtres partagent une charte graphique unifiée :
- Fond sombre : `#1a1c1e`
- Bordures : `#3f444c`
- Texte principal : `#f8fafc`
- Texte secondaire : `#94a3b8` ou `#cbd5e1`

Cette cohérence évite la désorientation d'**Elsa** (pas de changements brusques de luminosité) et de **Dolphine** (repères visuels stables).

### Boutons d'action explicites

Tous les boutons utilisent :
- **Texte en gras** (`FontWeight.BOLD`)
- **Taille minimale** (45px de hauteur)
- **Curseur "hand"** (`-fx-cursor: hand`)
- **Texte descriptif** (pas d'icônes seules)

Exemple dans `StartWindow.fxml` :
```xml
<Button fx:id="newGameButton" text="Nouvelle Partie" 
    prefHeight="55.0" prefWidth="340.0"
    style="-fx-background-color: #3b82f6; -fx-text-fill: white; 
           -fx-background-radius: 8; -fx-cursor: hand;">
```

Pour **Thierry**, le texte "Nouvelle Partie" est lisible indépendamment de la couleur bleue. Pour **Elsa**, la taille et le contraste sont optimaux. Pour **Dolphine**, l'action est clairement annoncée.

### Gestion des états désactivés

Les boutons désactivés (comme `loadButton` dans `LoadSaveWindow.fxml`) utilisent un style distinct :
```xml
<Button fx:id="loadButton" disable="true" ... 
    style="-fx-background-color: #10b981; ...">
```

Le `disable="true"` désactive visuellement et fonctionnellement le bouton, évitant la frustration de **Dolphine** qui cliquerait sur un bouton inactif sans feedback.

### Feedback visuel immédiat

Le `statusLabel` dans `SaveWindow.fxml` offre un feedback textuel :
```xml
<Label fx:id="statusLabel" textFill="#10b981" wrapText="true">
```

Pour **Dorian**, ce feedback immédiat confirme que l'action a été prise en compte. Pour **Dolphine**, le message textuel explicite ("Sauvegarde créée") rassure sur le succès de l'opération.

## 4. Conclusion

L'architecture du `ThemeManager` couplée aux widgets de paramètres (`SettingToggleWidget`, `SettingNavigationWidget`) permet une personnalisation fine qui répond aux besoins contradictoires de **Thierry** (palette color-blind) et **Elsa** (contrastes élevés) sans compromettre l'expérience des autres utilisateurs. La cohérence visuelle et les feedbacks explicites réduisent la frustration de **Dolphine** et **Dorian**, créant une interface véritablement inclusive.