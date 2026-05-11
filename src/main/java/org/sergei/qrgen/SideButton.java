package org.sergei.qrgen;

import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class SideButton extends StackPane {

    public SideButton(String icon) {
        this.getStyleClass().add("side-button");

        Text text = new Text(icon);
        text.getStyleClass().add("side-button-text");

        this.getChildren().add(text);
    }

    public void setBasePos(double x, double y, double w, double h) {
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.setPrefSize(w, h);
    }
}
