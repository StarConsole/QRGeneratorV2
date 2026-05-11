package org.sergei.qrgen;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SideSettingsPanel extends VBox {
    static final double PANEL_WIDTH = 500;
    boolean isOpen = false;
    private final Label lblColorTarget;
    private final ColorPalettePane palette;
    private CorrectionLevelSwitcher correctionSwitcher;

    public SideSettingsPanel(MainView mainView) {
        this.getStyleClass().add("side-settings-panel");

        this.setPrefWidth(PANEL_WIDTH);
        this.setMinWidth(PANEL_WIDTH);
        this.setMaxWidth(PANEL_WIDTH);
        this.setStyle("-fx-background-color: #1e1e1e;");
        this.setPadding(new Insets(15, 20, 15, 20));
        this.setSpacing(10);
        this.setTranslateX(-PANEL_WIDTH);

        Label lblCorr = new Label("КОРРЕКЦИЯ ОШИБОК");
        lblCorr.getStyleClass().add("settings-label");
        correctionSwitcher = new CorrectionLevelSwitcher(PANEL_WIDTH - 40);
        correctionSwitcher.setOnLevelChanged(levelIndex -> {
            AppSettings.DEFAULT_ERROR_CORRECTION = levelIndex;
            if (mainView.getQrPreview() != null) {
                mainView.getQrPreview().requestRefresh();
            }
        });

        HBox colorHeader = new HBox(10);
        colorHeader.setAlignment(Pos.CENTER_LEFT);
        lblColorTarget = new Label("ЦВЕТ: ПОДЛОЖКА");
        lblColorTarget.getStyleClass().add("settings-label");
        palette = new ColorPalettePane(mainView);
        ColorModeSwitcher colorSwitcher = new ColorModeSwitcher();
        colorSwitcher.setOnModeChanged(isBg -> {
            lblColorTarget.setText(isBg ? "ЦВЕТ: ПОДЛОЖКА" : "ЦВЕТ: УЗОР");
            palette.updateMode(isBg);
        });
        colorHeader.getChildren().addAll(colorSwitcher, lblColorTarget);

        Label lblMask = new Label("МАСКА ПЕРЕМЕШИВАНИЯ");
        lblMask.getStyleClass().add("settings-label");

        MaskSelectorPane maskSelector = new MaskSelectorPane(mainView, PANEL_WIDTH - 40);
        maskSelector.setMaxWidth(Double.MAX_VALUE);

        LogoSettingsBlock logoBlock = new LogoSettingsBlock(mainView, correctionSwitcher);
        VBox.setVgrow(logoBlock, Priority.ALWAYS);

        this.getChildren().addAll(
                lblCorr, correctionSwitcher,
                colorHeader, palette,
                lblMask, maskSelector,
                logoBlock
        );
    }

    public void toggle() {
        isOpen = !isOpen;
        TranslateTransition tt = new TranslateTransition(Duration.millis(400), this);
        tt.setToX(isOpen ? 0 : -PANEL_WIDTH);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        tt.play();
    }

    public void close() {
        if (isOpen) toggle();
    }

    public boolean isOpen() {
        return isOpen;
    }
}
