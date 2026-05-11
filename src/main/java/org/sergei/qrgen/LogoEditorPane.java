package org.sergei.qrgen;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class LogoEditorPane extends StackPane {
    public static final double EDITOR_SIZE = 140.0;
    private final ImageView logoView = new ImageView();
    private final Label noneLabel = new Label("none");
    private final StackPane viewport = new StackPane();
    private final Rectangle viewportClip = new Rectangle(EDITOR_SIZE, EDITOR_SIZE);
    private double mouseX;
    private double mouseY;

    public LogoEditorPane() {
        this.setAlignment(Pos.CENTER);
        this.getStyleClass().add("logo-editor-container");

        this.setPrefSize(EDITOR_SIZE, EDITOR_SIZE);
        this.setMinSize(EDITOR_SIZE, EDITOR_SIZE);
        this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        noneLabel.setStyle("-fx-text-fill: #444444; -fx-font-weight: bold; -fx-font-size: 14;");
        logoView.setPreserveRatio(true);
        logoView.setManaged(true);
        StackPane.setAlignment(logoView, Pos.CENTER);
        StackPane.setAlignment(noneLabel, Pos.CENTER);

        viewport.setAlignment(Pos.CENTER);
        viewport.prefWidthProperty().bind(this.widthProperty());
        viewport.prefHeightProperty().bind(this.heightProperty());
        viewport.minWidthProperty().bind(this.widthProperty());
        viewport.minHeightProperty().bind(this.heightProperty());
        viewport.maxWidthProperty().bind(this.widthProperty());
        viewport.maxHeightProperty().bind(this.heightProperty());

        viewportClip.setArcWidth(24);
        viewportClip.setArcHeight(24);
        viewportClip.widthProperty().bind(viewport.widthProperty());
        viewportClip.heightProperty().bind(viewport.heightProperty());
        viewport.setClip(viewportClip);

        viewport.getChildren().addAll(noneLabel, logoView);
        this.getChildren().add(viewport);

        setupControls();
    }

    private void setupControls() {
        this.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (logoView.getImage() == null || e.getButton() != MouseButton.PRIMARY) return;
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
            e.consume();
        });
        this.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
            if (logoView.getImage() == null || !e.isPrimaryButtonDown()) return;
            logoView.setTranslateX(logoView.getTranslateX() + (e.getSceneX() - mouseX));
            logoView.setTranslateY(logoView.getTranslateY() + (e.getSceneY() - mouseY));
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
            e.consume();
        });
    }

    public void setLogo(Image img) {
        logoView.setImage(img);
        logoView.setFitWidth(EDITOR_SIZE * AppSettings.LOGO_SAFE_ZONE_RATIO_H);
        logoView.setTranslateX(0);
        logoView.setTranslateY(0);
        double editorScale = clamp(
                AppSettings.logoScale * AppSettings.LOGO_EDITOR_MAGNIFICATION,
                AppSettings.LOGO_MIN_SCALE * AppSettings.LOGO_EDITOR_MAGNIFICATION,
                AppSettings.LOGO_MAX_SCALE * AppSettings.LOGO_EDITOR_MAGNIFICATION
        );
        logoView.setScaleX(editorScale);
        logoView.setScaleY(editorScale);
        noneLabel.setVisible(false);
        logoView.setCursor(Cursor.MOVE);
    }

    public void zoomIn() {
        applyZoom(1.25);
    }

    public void zoomOut() {
        applyZoom(0.80);
    }

    public void clear() {
        logoView.setImage(null);
        noneLabel.setVisible(true);
    }

    public void syncSettings() {
        if (logoView.getImage() != null) {
            AppSettings.currentLogo = logoView.getImage();
            AppSettings.logoX = logoView.getTranslateX() / AppSettings.LOGO_EDITOR_MAGNIFICATION;
            AppSettings.logoY = logoView.getTranslateY() / AppSettings.LOGO_EDITOR_MAGNIFICATION;
            AppSettings.logoScale = clamp(
                    logoView.getScaleX() / AppSettings.LOGO_EDITOR_MAGNIFICATION,
                    AppSettings.LOGO_MIN_SCALE,
                    AppSettings.LOGO_MAX_SCALE
            );
        }
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private void applyZoom(double factor) {
        if (logoView.getImage() == null) return;
        double minEditorScale = AppSettings.LOGO_MIN_SCALE * AppSettings.LOGO_EDITOR_MAGNIFICATION;
        double maxEditorScale = AppSettings.LOGO_MAX_SCALE * AppSettings.LOGO_EDITOR_MAGNIFICATION;
        double nextScale = clamp(logoView.getScaleX() * factor, minEditorScale, maxEditorScale);
        logoView.setScaleX(nextScale);
        logoView.setScaleY(nextScale);
    }
}
