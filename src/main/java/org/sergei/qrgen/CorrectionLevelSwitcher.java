package org.sergei.qrgen;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CorrectionLevelSwitcher extends StackPane {
    private final List<Button> buttons = new ArrayList<>();
    private final Region selector;
    private final double segmentWidth;
    private Consumer<Integer> onLevelChanged;
    private boolean isLocked = false;
    private int activeIndex = 0;
    private Timeline vibrationTimeline;

    public CorrectionLevelSwitcher(double totalWidth) {
        this.segmentWidth = totalWidth / 4;

        this.setMaxSize(totalWidth, 40);
        this.getStyleClass().add("correction-container");

        selector = new Region();
        selector.getStyleClass().add("correction-selector");
        selector.setMaxSize(segmentWidth - 4, 36);

        this.setAlignment(Pos.CENTER_LEFT);

        HBox buttonsLayer = new HBox(0);
        buttonsLayer.setAlignment(Pos.CENTER);

        String[] levels = {"L", "M", "Q", "H"};
        for (int i = 0; i < levels.length; i++) {
            buttonsLayer.getChildren().add(createBtn(levels[i], i));
        }

        this.getChildren().addAll(selector, buttonsLayer);

        int defaultIdx = AppSettings.DEFAULT_ERROR_CORRECTION;
        this.activeIndex = defaultIdx;

        double startX = defaultIdx * segmentWidth + 2;
        selector.setTranslateX(startX);

        updateButtonColors(defaultIdx);
    }

    private Button createBtn(String text, int index) {
        Button btn = new Button(text);
        btn.getStyleClass().add("correction-btn");
        btn.setPrefSize(segmentWidth, 40);
        btn.setFocusTraversable(false);
        btn.setOnAction(e -> select(index));
        buttons.add(btn);
        return btn;
    }

    public void select(int index) {
        if (isLocked) {
            playVibration();
            return;
        }

        this.activeIndex = index;

        double targetX = index * segmentWidth + 2;

        TranslateTransition tt = new TranslateTransition(Duration.millis(200), selector);
        tt.setToX(targetX);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        tt.play();

        updateButtonColors(index);

        if (onLevelChanged != null) {
            onLevelChanged.accept(index);
        }
    }

    public void setOnLevelChanged(Consumer<Integer> listener) {
        this.onLevelChanged = listener;
    }

    public void setLocked(boolean locked) {
        this.isLocked = locked;
        if (locked) {
            if (vibrationTimeline != null) {
                vibrationTimeline.stop();
            }
            selector.setTranslateX(baseXForIndex(activeIndex));
        }
    }

    private void playVibration() {
        double baseX = baseXForIndex(activeIndex);

        if (vibrationTimeline != null) {
            vibrationTimeline.stop();
        }

        selector.setTranslateX(baseX);

        vibrationTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new javafx.animation.KeyValue(selector.translateXProperty(), baseX)),
                new KeyFrame(Duration.millis(50), new javafx.animation.KeyValue(selector.translateXProperty(), baseX - 7)),
                new KeyFrame(Duration.millis(100), new javafx.animation.KeyValue(selector.translateXProperty(), baseX + 7)),
                new KeyFrame(Duration.millis(150), new javafx.animation.KeyValue(selector.translateXProperty(), baseX - 7)),
                new KeyFrame(Duration.millis(200), new javafx.animation.KeyValue(selector.translateXProperty(), baseX + 7)),
                new KeyFrame(Duration.millis(250), new javafx.animation.KeyValue(selector.translateXProperty(), baseX))
        );
        vibrationTimeline.play();
    }

    private double baseXForIndex(int index) {
        return index * segmentWidth + 2;
    }

    private void updateButtonColors(int activeIndex) {
        for (int i = 0; i < buttons.size(); i++) {
            if (i == activeIndex) {
                buttons.get(i).setStyle("-fx-text-fill: #000000;");
            } else {
                buttons.get(i).setStyle("-fx-text-fill: #ffffff;");
            }
        }
    }
}
