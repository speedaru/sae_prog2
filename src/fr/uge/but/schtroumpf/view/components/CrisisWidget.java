package fr.uge.but.schtroumpf.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

import fr.uge.but.schtroumpf.model.crises.Crisis;
import fr.uge.but.schtroumpf.model.types.ModifierEffect;
import fr.uge.but.schtroumpf.model.utils.ColorUtils;
import fr.uge.but.schtroumpf.view.themes.ThemeManager;

public class CrisisWidget extends VBox {
    private final Crisis crisis;
    
    private final HBox headerBadge;
    private final Label titleLabel;
    private final VBox causesCardBox, causesContentContainer;
    private final VBox effectsCardBox, effectsContentContainer;

    private final Runnable themeUpdater = this::applyCurrentThemeColors;

    public CrisisWidget(Crisis crisis) {
        super();
        this.crisis = crisis;

        this.setSpacing(15.0);
        VBox.setVgrow(this, Priority.ALWAYS);

        this.headerBadge = new HBox();
        this.headerBadge.setAlignment(Pos.CENTER);
        this.headerBadge.setMinHeight(40.0);
        this.headerBadge.setPrefHeight(40.0);
        
        this.titleLabel = new Label( crisis.getType().getName());
        this.titleLabel.setTextFill(Color.WHITE);
        this.titleLabel.setFont(Font.font("System", FontWeight.BOLD, 14.0));
        this.headerBadge.getChildren().add(this.titleLabel);

        causesCardBox = new VBox(8.0);
        causesCardBox.setBackground(new Background(new BackgroundFill(
			Color.web("#2d3139"), new CornerRadii(8), Insets.EMPTY
        )));
        causesCardBox.setPadding(new Insets(12.0));
        causesCardBox.setPrefHeight(85.0);

        Label causesHeaderLabel = new Label("Cause de la crise :");
        causesHeaderLabel.setTextFill(Color.WHITE);
        causesHeaderLabel.setFont(Font.font("System", FontWeight.BOLD, 13.0));
        
        this.causesContentContainer = new VBox();
        VBox.setVgrow(this.causesContentContainer, Priority.ALWAYS);
        loadCauseWidget();
        
        causesCardBox.getChildren().addAll(causesHeaderLabel, this.causesContentContainer);

        effectsCardBox = new VBox(8.0);
        effectsCardBox.setBackground(new Background(new BackgroundFill(
			Color.web("#2d3139"), new CornerRadii(8), Insets.EMPTY
        )));
        effectsCardBox.setPadding(new Insets(12.0));
        VBox.setVgrow(effectsCardBox, Priority.ALWAYS);

        Label effectsHeaderLabel = new Label("Effets produits :");
        effectsHeaderLabel.setTextFill(Color.WHITE);
        effectsHeaderLabel.setFont(Font.font("System", FontWeight.BOLD, 13.0));

        this.effectsContentContainer = new VBox(4.0);
        this.effectsContentContainer.setPadding(new Insets(0));
        VBox.setVgrow(this.effectsContentContainer, Priority.ALWAYS);
        loadEffectWidgets();
        
        effectsCardBox.getChildren().addAll(effectsHeaderLabel, this.effectsContentContainer);

        this.getChildren().addAll(this.headerBadge, causesCardBox, effectsCardBox);

        applyCurrentThemeColors();
        ThemeManager.addThemeChangeListener(themeUpdater);
    }

    private void applyCurrentThemeColors() {
        Color borderColor = ThemeManager.getResourceColor(crisis.getType().getCause());

        String backgroundColorHex = ColorUtils.colorToHex(ColorUtils.darker(borderColor, 40));
        String borderColorHex = ColorUtils.colorToHex(borderColor);
        
        String backgroundStyleFmt = "-fx-background-color: %s; -fx-background-radius: 6; ";
        String borderStyleFmt = "-fx-border-color: %s; -fx-border-radius: 6; -fx-border-width: 1;";
        
        this.headerBadge.setStyle(String.format(
            backgroundStyleFmt + borderStyleFmt,
            backgroundColorHex, borderColorHex
        ));
        
        this.causesCardBox.setStyle(String.format(borderStyleFmt, borderColorHex));
        this.effectsCardBox.setStyle(String.format(borderStyleFmt, borderColorHex));
    }

    private void loadCauseWidget() {
    	ResourceSummaryRow row = new ResourceSummaryRow(crisis.getType().getCause());
    	row.updateDelta(0);
    	this.causesContentContainer.getChildren().add(row);
    }
    
    private void loadEffectWidgets() {
    	List<ModifierEffect> effects = crisis.getModifierEffects();
    	if (effects.isEmpty()) {
    		Label noneLabel = new Label("Aucun effet passif");
    		noneLabel.setTextFill(Color.web("#64748b"));
            noneLabel.setFont(Font.font("System", 12.0));
            this.effectsContentContainer.getChildren().add(noneLabel);
            return;
    	}
    	
    	for (ModifierEffect effect : effects) {
    		GameModifierRow row = new GameModifierRow(effect.getType(), effect.getValue());
    		this.effectsContentContainer.getChildren().add(row);
    	}
    }
}
