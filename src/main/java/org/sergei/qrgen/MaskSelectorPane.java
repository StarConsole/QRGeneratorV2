package org.sergei.qrgen;

import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class MaskSelectorPane extends StackPane {
    private final MainView mainView;
    private int currentMask = -1;

    private final Rectangle selectionBg;
    private final HBox labelsContainer;
    private final double itemWidth;

    public MaskSelectorPane(MainView mainView, double panelWidth) {
        this.mainView = mainView;
        this.itemWidth = panelWidth / 9.0;

        this.setAlignment(Pos.CENTER_LEFT);
        this.setMinWidth(panelWidth);
        this.setPrefWidth(panelWidth);
        this.setMaxWidth(panelWidth);
        this.setMinHeight(40);
        this.setPrefHeight(40);
        this.setMaxHeight(40);

        Rectangle baseBg = new Rectangle(panelWidth, 40);
        baseBg.setArcWidth(40);
        baseBg.setArcHeight(40);
        baseBg.setFill(Color.web("#2b2b2b"));

        selectionBg = new Rectangle(itemWidth - 6, 34);
        selectionBg.setArcWidth(34);
        selectionBg.setArcHeight(34);
        selectionBg.setFill(Color.WHITE);

        labelsContainer = new HBox();
        labelsContainer.setAlignment(Pos.CENTER_LEFT);
        labelsContainer.setPrefWidth(panelWidth);

        String[] labels = {"A", "0", "1", "2", "3", "4", "5", "6", "7"};
        for (int i = 0; i < labels.length; i++) {
            labelsContainer.getChildren().add(createLabel(labels[i], i));
        }

        this.getChildren().addAll(baseBg, selectionBg, labelsContainer);

        double startX = (itemWidth - selectionBg.getWidth()) / 2.0;
        selectionBg.setTranslateX(startX);
        updateLabelsStyle(0);
    }

    private StackPane createLabel(String text, int index) {
        StackPane pane = new StackPane();
        pane.setPrefWidth(itemWidth);
        pane.setMinWidth(itemWidth);
        pane.setMaxWidth(itemWidth);

        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        pane.getChildren().add(lbl);
        pane.setPickOnBounds(true);
        pane.setCursor(Cursor.HAND);

        pane.setOnMouseClicked(e -> {
            int maskValue = (index == 0) ? -1 : index - 1;
            selectMask(maskValue, index);
        });

        return pane;
    }

    private void selectMask(int maskValue, int index) {
        this.currentMask = maskValue;

        double targetX = (index * itemWidth) + (itemWidth - selectionBg.getWidth()) / 2.0;

        TranslateTransition tt = new TranslateTransition(Duration.millis(200), selectionBg);
        tt.setToX(targetX);
        tt.play();

        updateLabelsStyle(index);

        AppSettings.QR_MASK_PATTERN = currentMask;
        if (mainView.getQrPreview() != null) {
            mainView.getQrPreview().requestRefresh();
        }
    }

    private void updateLabelsStyle(int activeIndex) {
        for (int i = 0; i < labelsContainer.getChildren().size(); i++) {
            StackPane pane = (StackPane) labelsContainer.getChildren().get(i);
            Label l = (Label) pane.getChildren().get(0);
            if (i == activeIndex) {
                l.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 14px;");
            } else {
                l.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
            }
        }
    }
}
