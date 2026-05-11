package org.sergei.qrgen;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ColorPalettePane extends VBox {
    private final MainView mainView;
    private boolean isBgMode = true;

    private String selectedBgColor = "FFFFFF";
    private String selectedPatternColor = "000000";

    private final GridPane grid;
    private final StackPane inputContainer;
    private final TextField hexInput;
    private final Label prefixLabel;

    private final String neutralBg = "#2b2b2b";

    public ColorPalettePane(MainView mainView) {
        this.mainView = mainView;
        this.setSpacing(20);
        this.setPadding(new Insets(15, 0, 0, 0));

        grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setAlignment(Pos.TOP_LEFT);

        String[] row1 = {
                "FFFFFF", "FADADD", "FFE5B4", "FFFACD", "C1E1C1",
                "B2FFFF", "AEC6CF", "E6E6FA", "D3D3D3"
        };
        String[] row2 = {
                "000000", "8B0000", "5D4037", "556B2F", "006400",
                "008080", "00008B", "4B0082", "2F4F4F"
        };

        createColorRow(row1, 0);
        createColorRow(row2, 1);

        inputContainer = new StackPane();
        inputContainer.setPrefHeight(45);
        inputContainer.setStyle("-fx-border-color: white; -fx-border-width: 2; -fx-border-radius: 25; -fx-background-radius: 25;");

        HBox layout = new HBox(5);
        layout.setAlignment(Pos.CENTER_LEFT);
        layout.setPadding(new Insets(0, 15, 0, 15));

        prefixLabel = new Label("#");
        prefixLabel.getStyleClass().add("hex-prefix");

        hexInput = new TextField();
        hexInput.getStyleClass().add("hex-field");
        hexInput.setPromptText("FFFFFF");
        HBox.setHgrow(hexInput, Priority.ALWAYS);
        hexInput.setMaxWidth(Double.MAX_VALUE);

        hexInput.textProperty().addListener((obs, old, newValue) -> {
            String cleaned = newValue.toUpperCase().replaceAll("[^0-9A-F]", "");
            if (cleaned.length() > 6) cleaned = cleaned.substring(0, 6);

            if (!newValue.equals(cleaned)) {
                hexInput.setText(cleaned);
                return;
            }

            processManualInput(cleaned);
        });

        layout.getChildren().addAll(prefixLabel, hexInput);
        inputContainer.getChildren().add(layout);

        this.getChildren().addAll(grid, inputContainer);

        updateMode(true);
    }

    private void createColorRow(String[] hexes, int row) {
        for (int i = 0; i < hexes.length; i++) {
            String hex = hexes[i];
            StackPane circle = new StackPane();
            circle.setPrefSize(38, 38);
            circle.setStyle("-fx-background-color: #" + hex + "; -fx-background-radius: 50%; -fx-cursor: hand;");

            circle.setOnMouseClicked(e -> hexInput.setText(hex));

            grid.add(circle, i, row);
        }
    }

    public void updateMode(boolean isBg) {
        this.isBgMode = isBg;
        String current = isBg ? selectedBgColor : selectedPatternColor;
        hexInput.setText(current);
    }

    private void processManualInput(String hex) {
        if (hex.length() == 6) {
            try {
                Color c = Color.web("#" + hex);
                inputContainer.setStyle(inputContainer.getStyle() + "-fx-background-color: #" + hex + ";");

                String textColor = (c.getBrightness() > 0.6) ? "black" : "white";
                prefixLabel.setStyle("-fx-text-fill: " + textColor + ";");
                hexInput.setStyle("-fx-text-fill: " + textColor + ";");

                if (isBgMode) selectedBgColor = hex;
                else selectedPatternColor = hex;

                requestQRUpdate();
            } catch (Exception e) {
                setNeutralState();
            }
        } else {
            setNeutralState();
        }
    }

    private void setNeutralState() {
        inputContainer.setStyle(inputContainer.getStyle() + "-fx-background-color: " + neutralBg + ";");
        prefixLabel.setStyle("-fx-text-fill: #888888;");
        hexInput.setStyle("-fx-text-fill: white;");
    }

    private void requestQRUpdate() {
        QRPreviewPane preview = mainView.getQrPreview();
        if (preview != null) {
            String bg = "#" + selectedBgColor;
            String pattern = "#" + selectedPatternColor;

            preview.updateColors(bg, pattern);
        }
    }
}
