package fr.uge.but.schtroumpf.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import fr.uge.but.schtroumpf.model.types.GameModifierType;

public class GameModifierRow extends HBox {

    public GameModifierRow(GameModifierType type, Object value) {
        super();
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new Insets(4, 8, 4, 8));
        this.setPrefHeight(24.0);

        String prettyValue = type.formatDisplayValue(value);
        
        Label label = new Label(String.format("%s : %s", type.getName(), prettyValue));
        label.setTextFill(Color.web("#cbd5e1"));
        label.setFont(Font.font("System", FontWeight.BOLD, 14.0));

        this.getChildren().addAll(label);
    }
}
