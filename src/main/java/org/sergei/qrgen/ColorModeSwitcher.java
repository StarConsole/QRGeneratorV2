package org.sergei.qrgen;

import javafx.animation.TranslateTransition;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.function.Consumer;

public class ColorModeSwitcher extends StackPane {
    private final Region circle;
    private boolean isBgMode = true;
    private Consumer<Boolean> onModeChanged;

    public ColorModeSwitcher() {
        this.setPrefSize(40, 22);
        this.setMaxSize(40, 22);
        this.getStyleClass().add("color-toggle-bg");

        circle = new Region();
        circle.getStyleClass().add("color-toggle-circle");
        circle.setMaxSize(16, 16);

        circle.setTranslateX(-9);

        this.getChildren().add(circle);

        this.setOnMouseClicked(e -> {
            isBgMode = !isBgMode;
            animate();
            if (onModeChanged != null) {
                onModeChanged.accept(isBgMode);
            }
        });
    }

    private void animate() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(200), circle);
        tt.setToX(isBgMode ? -9 : 9);
        tt.play();
    }

    public void setOnModeChanged(Consumer<Boolean> listener) {
        this.onModeChanged = listener;
    }

    public boolean isBgMode() {
        return isBgMode;
    }
}
