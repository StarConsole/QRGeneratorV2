package org.sergei.qrgen;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class GhostButton extends StackPane {

    private double stdWidth;
    private double initialX;
    private final boolean isLeft;
    private final Timeline timeline = new Timeline();

    public GhostButton(String iconText, boolean isLeft) {
        this.isLeft = isLeft;

        this.setMouseTransparent(true);

        this.getStyleClass().add("side-button");
        this.getStyleClass().add("ghost-button");

        Text text = new Text(iconText);
        text.getStyleClass().add("side-button-text");

        StackPane.setAlignment(text, isLeft ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
        StackPane.setMargin(text, isLeft ? new Insets(0, 0, 0, 8) : new Insets(0, 8, 0, 0));

        this.getChildren().add(text);
    }

    public void setBasePos(double x, double y, double w, double h) {
        this.initialX = x;
        this.stdWidth = w;

        this.setLayoutX(x);
        this.setLayoutY(y);

        this.setMinWidth(w);
        this.setPrefWidth(w);
        this.setMaxWidth(Double.MAX_VALUE);

        this.setMinHeight(h);
        this.setPrefHeight(h);
        this.setMaxHeight(h);
    }

    public void animateExpansion() {
        timeline.stop();
        timeline.getKeyFrames().clear();

        double targetWidth = stdWidth + AppSettings.GHOST_OFFSET;
        double targetX = isLeft ? (initialX - AppSettings.GHOST_OFFSET) : initialX;

        KeyValue kvW = new KeyValue(this.prefWidthProperty(), targetWidth, Interpolator.EASE_BOTH);
        KeyValue kvX = new KeyValue(this.layoutXProperty(), targetX, Interpolator.EASE_BOTH);

        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(200), kvW, kvX));
        timeline.play();
    }

    public void animateBack() {
        timeline.stop();
        timeline.getKeyFrames().clear();

        KeyValue kvW = new KeyValue(this.prefWidthProperty(), stdWidth, Interpolator.EASE_BOTH);
        KeyValue kvX = new KeyValue(this.layoutXProperty(), initialX, Interpolator.EASE_BOTH);

        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(200), kvW, kvX));
        timeline.play();
    }
}
