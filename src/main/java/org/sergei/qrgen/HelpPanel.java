package org.sergei.qrgen;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class HelpPanel extends StackPane {
    private boolean isOpen = false;

    public HelpPanel() {
        getStyleClass().add("help-panel");
        setPickOnBounds(false);
        setMouseTransparent(true);
        setVisible(false);
        setManaged(false);

        VBox card = new VBox(10);
        card.getStyleClass().add("help-card");
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setMaxWidth(AppSettings.WINDOW_WIDTH * 0.92);

        Label title = new Label("КАК ПОЛЬЗОВАТЬСЯ QRGENERATOR");
        title.getStyleClass().add("help-title");

        Label text = new Label(buildHelpText());
        text.getStyleClass().add("help-text");
        text.setWrapText(true);

        card.setMinHeight(AppSettings.WINDOW_HEIGHT * 0.72);
        card.setPrefHeight(AppSettings.WINDOW_HEIGHT * 0.72);
        card.setMaxHeight(AppSettings.WINDOW_HEIGHT * 0.72);

        card.getChildren().addAll(title, text);
        getChildren().add(card);
        StackPane.setAlignment(card, Pos.TOP_CENTER);

        setTranslateY(0);
    }

    public void toggle() {
        if (isOpen) {
            close();
        } else {
            open();
        }
    }

    public void open() {
        if (isOpen) return;
        isOpen = true;
        setVisible(true);
        setManaged(true);
        setMouseTransparent(false);

        TranslateTransition tt = new TranslateTransition(Duration.millis(380), this);
        tt.setToY(0);
        tt.setInterpolator(Interpolator.EASE_OUT);
        tt.play();
    }

    public void close() {
        if (!isOpen) return;
        isOpen = false;
        setMouseTransparent(true);

        TranslateTransition tt = new TranslateTransition(Duration.millis(320), this);
        tt.setToY(-getHeight() - 20);
        tt.setInterpolator(Interpolator.EASE_IN);
        tt.setOnFinished(e -> {
            setVisible(false);
            setManaged(false);
            setTranslateY(0);
        });
        tt.play();
    }

    public boolean isOpen() {
        return isOpen;
    }

    private String buildHelpText() {
        return ""
                + "1. Ввод данных\n"
                + "В верхней части выбери тип данных (URL, Текст, WiFi или Контакт) и введи данные в поле.\n"
                + "QR‑код обновляется автоматически при каждом вводе.\n"
                + "\n"
                + "2. Настройка внешнего вида\n"
                + "Нажми на стрелку слева, чтобы открыть панель настроек:\n"
                + "\n"
                + "  • Коррекция ошибок: уровни L, M, Q, H. Если планируешь логотип — выбирай H,\n"
                + "    чтобы QR оставался читаемым даже если часть кода перекрыта.\n"
                + "\n"
                + "  • Цвета: переключайся между «Подложкой» (фон) и «Узором» (сам код).\n"
                + "    Можно выбрать цвет из палитры или ввести HEX.\n"
                + "\n"
                + "  • Маска перемешивания: выбери один из 8 вариантов, чтобы изменить рисунок кода.\n"
                + "\n"
                + "3. Работа с логотипом\n"
                + "В блоке «ЛОГОТИП» нажми «Загрузить» и выбери картинку — она встанет по центру.\n"
                + "\n"
                + "Внимание: рекомендуется использовать .png.\n"
                + "Другие форматы могут отображаться некорректно или приводить к ошибкам.\n"
                + "\n"
                + "4. Сохранение\n"
                + "Открой панель экспорта (стрелка справа) и выбери формат:\n"
                + "  • SVG — для печати и максимального качества.\n"
                + "  • PNG / JPG / JPEG — для обычного использования.\n";
    }
}
