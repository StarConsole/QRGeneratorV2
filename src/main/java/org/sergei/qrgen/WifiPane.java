package org.sergei.qrgen;

import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class WifiPane extends VBox {
    private List<Button> cryptoButtons = new ArrayList<>();
    private Region miniSelector;
    private double miniSegmentWidth;
    private TextField tfSsid, tfPass;
    private String selectedCrypto = "WPA";
    private MainView mainView;

    public WifiPane(MainView mainView) {
        this.mainView = mainView;
        this.setSpacing(AppSettings.INPUT_GAP);
        this.setAlignment(Pos.TOP_CENTER);

        this.getChildren().add(createRow("Сеть:", "Название...", tfSsid = new TextField()));
        this.getChildren().add(createRow("Пароль:", "********", tfPass = new TextField()));

        HBox infoRow = createRow("Шифр:", "", new TextField());
        Label typeLabel = new Label("WPA2");
        typeLabel.getStyleClass().add("input-prefix");
        infoRow.getChildren().remove(1);
        infoRow.getChildren().add(typeLabel);
        this.getChildren().add(infoRow);

        setupCryptoSwitcher(typeLabel);
    }

    private String getFullData() {
        String ssid = tfSsid.getText().trim();
        if (ssid.isEmpty()) return "";
        return String.format("WIFI:T:%s;S:%s;P:%s;;", selectedCrypto, ssid, tfPass.getText());
    }

    private void setupCryptoSwitcher(Label typeLabel) {
        String[] types = {"WPA2", "WPA3", "WEP", "Open"};
        double totalWidth = AppSettings.WINDOW_WIDTH * AppSettings.INPUT_CONTAINER_WIDTH_PERCENT;
        miniSegmentWidth = totalWidth / types.length;

        StackPane container = new StackPane();
        container.getStyleClass().add("crypto-row");
        container.setPrefHeight(AppSettings.INPUT_ROW_HEIGHT);
        container.setMaxWidth(totalWidth);

        miniSelector = new Region();
        miniSelector.getStyleClass().add("inverted-selector");
        miniSelector.setMaxSize(miniSegmentWidth - 4, AppSettings.INPUT_ROW_HEIGHT - 6);
        miniSelector.setTranslateX(-(totalWidth / 2) + (miniSegmentWidth / 2));

        HBox rowSwitcher = new HBox(0);
        rowSwitcher.setAlignment(Pos.CENTER);

        for (int i = 0; i < types.length; i++) {
            final int index = i;
            Button btn = new Button(types[i]);
            btn.getStyleClass().add("inverted-segment-button");
            btn.setPrefWidth(miniSegmentWidth);
            HBox.setHgrow(btn, Priority.ALWAYS);

            btn.setOnAction(e -> {
                animateMiniSelector(index, totalWidth);
                typeLabel.setText(types[index]);
                selectedCrypto = types[index].equals("Open") ? "nopass" : types[index];
                mainView.getQrPreview().updateQR(getFullData());
            });
            cryptoButtons.add(btn);
            rowSwitcher.getChildren().add(btn);
        }
        cryptoButtons.get(0).getStyleClass().add("inverted-segment-active");
        container.getChildren().addAll(miniSelector, rowSwitcher);
        this.getChildren().add(container);
    }

    private void animateMiniSelector(int index, double totalWidth) {
        cryptoButtons.forEach(b -> b.getStyleClass().remove("inverted-segment-active"));
        cryptoButtons.get(index).getStyleClass().add("inverted-segment-active");
        double targetX = -(totalWidth / 2) + (miniSegmentWidth / 2) + (index * miniSegmentWidth);
        TranslateTransition tt = new TranslateTransition(Duration.millis(250), miniSelector);
        tt.setToX(targetX);
        tt.play();
    }

    private HBox createRow(String prefix, String prompt, TextField tf) {
        HBox row = new HBox(10);
        row.getStyleClass().add("input-row");
        row.setPrefHeight(AppSettings.INPUT_ROW_HEIGHT);
        double totalWidth = AppSettings.WINDOW_WIDTH * AppSettings.INPUT_CONTAINER_WIDTH_PERCENT;
        row.setMaxWidth(totalWidth);

        Label lbl = new Label(prefix);
        lbl.getStyleClass().add("input-prefix");
        lbl.setMinWidth(60);

        tf.setPromptText(prompt);
        tf.getStyleClass().add("input-field");
        HBox.setHgrow(tf, Priority.ALWAYS);
        tf.textProperty().addListener((obs, oldV, newV) -> mainView.getQrPreview().updateQR(getFullData()));

        row.getChildren().addAll(lbl, tf);
        return row;
    }
}