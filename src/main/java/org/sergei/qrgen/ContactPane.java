package org.sergei.qrgen;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ContactPane extends VBox {
    private final TextField tfName;
    private final TextField tfPhone;
    private final TextField tfEmail;
    private final TextField tfOrg;
    private final MainView mainView;

    public ContactPane(MainView mainView) {
        this.mainView = mainView;
        this.setSpacing(AppSettings.INPUT_GAP);
        this.setAlignment(Pos.TOP_CENTER);

        tfName = new TextField();
        tfPhone = new TextField();
        tfEmail = new TextField();
        tfOrg = new TextField();

        this.getChildren().addAll(
                createRow("Имя:", "Иван Иванов", tfName),
                createRow("Моб:", "+7 900 ...", tfPhone),
                createRow("Email:", "example@mail.com", tfEmail),
                createRow("Орг:", "Название компании", tfOrg)
        );
    }

    private String getFullData() {
        String name = tfName.getText().trim();
        if (name.isEmpty() && tfPhone.getText().isEmpty() && tfEmail.getText().isEmpty()) return "";

        return String.format("BEGIN:VCARD\nVERSION:3.0\nFN:%s\nTEL:%s\nEMAIL:%s\nORG:%s\nEND:VCARD",
                name, tfPhone.getText(), tfEmail.getText(), tfOrg.getText());
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
