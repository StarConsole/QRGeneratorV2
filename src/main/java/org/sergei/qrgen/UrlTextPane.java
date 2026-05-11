package org.sergei.qrgen;

import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class UrlTextPane extends VBox {
    private final TextArea area;
    private final MainView mainView;

    public UrlTextPane(String prompt, MainView mainView) {
        this.mainView = mainView;
        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(AppSettings.INPUT_GAP);

        area = new TextArea();
        area.setPromptText(prompt);
        area.getStyleClass().add("input-area");
        area.setWrapText(true);

        double totalHeight = (AppSettings.INPUT_ROW_HEIGHT * 4) + (AppSettings.INPUT_GAP * 3);
        double totalWidth = AppSettings.WINDOW_WIDTH * AppSettings.INPUT_CONTAINER_WIDTH_PERCENT;

        area.setPrefHeight(totalHeight);
        area.setMinHeight(totalHeight);
        area.setMaxHeight(totalHeight);

        area.setPrefWidth(totalWidth);
        area.setMinWidth(totalWidth);
        area.setMaxWidth(totalWidth);

        area.textProperty().addListener((obs, oldText, newText) -> {
            if (mainView.getQrPreview() != null) {
                mainView.getQrPreview().updateQR(newText);
            }
        });

        this.getChildren().add(area);
    }

    public String getText() {
        return area.getText();
    }
}
