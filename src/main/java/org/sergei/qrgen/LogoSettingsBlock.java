package org.sergei.qrgen;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LogoSettingsBlock extends VBox {
    private final LogoEditorPane editor;
    private final Label fileTypeValue = new Label("—");
    private final Label fileSizeValue = new Label("—");
    private final Label fileDimValue = new Label("—");

    public LogoSettingsBlock(MainView mainView, CorrectionLevelSwitcher switcher) {
        this.editor = new LogoEditorPane();
        editor.setPrefSize(140, 140);
        editor.setMinSize(140, 140);
        editor.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        this.setSpacing(5);
        this.setPadding(new Insets(0, 0, 5, 0));
        this.setFillWidth(true);

        Label lblLogo = new Label("ЛОГОТИП");
        lblLogo.getStyleClass().add("settings-label");

        HBox content = new HBox();
        content.setAlignment(Pos.CENTER_LEFT);
        content.setSpacing(10);
        content.setPrefWidth(SideSettingsPanel.PANEL_WIDTH - 40);
        content.setMaxWidth(Double.MAX_VALUE);

        VBox btnBox = new VBox(7);
        btnBox.setAlignment(Pos.CENTER_LEFT);

        Button btnL = createBtn("Загрузить");
        Button btnA = createBtn("Применить");
        Button btnC = createBtn("Очистить");
        Button btnPlus = createZoomBtn("+");
        Button btnMinus = createZoomBtn("-");
        btnPlus.getStyleClass().add("logo-zoom-btn-top");
        btnMinus.getStyleClass().add("logo-zoom-btn-bottom");
        btnPlus.setOnAction(e -> editor.zoomIn());
        btnMinus.setOnAction(e -> editor.zoomOut());

        btnL.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif", "*.webp", "*.svg"),
                    new FileChooser.ExtensionFilter("SVG", "*.svg"),
                    new FileChooser.ExtensionFilter("PNG", "*.png"),
                    new FileChooser.ExtensionFilter("JPEG", "*.jpg", "*.jpeg"),
                    new FileChooser.ExtensionFilter("All files", "*.*")
            );
            File f = fc.showOpenDialog(mainView.getScene().getWindow());
            if (f != null) {
                try {
                    Image loaded = loadImageByExtension(f);
                    if (loaded == null || loaded.isError()) {
                        throw new IllegalArgumentException("Формат не поддерживается или файл поврежден.");
                    }

                    AppSettings.logoX = 0;
                    AppSettings.logoY = 0;
                    AppSettings.logoScale = AppSettings.LOGO_MAX_SCALE;
                    AppSettings.currentLogo = loaded;
                    AppSettings.isLogoActive = true;

                    editor.setLogo(loaded);
                    editor.syncSettings();
                    updateFileInfo(f, loaded);
                    switcher.select(3);
                    switcher.setLocked(true);
                    mainView.getQrPreview().requestRefresh();
                } catch (Exception ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка загрузки");
                    alert.setHeaderText("Не удалось загрузить изображение");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                }
            }
        });
        btnA.setOnAction(e -> {
            editor.syncSettings();
            mainView.getQrPreview().requestRefresh();
        });
        btnC.setOnAction(e -> {
            editor.clear();
            updateFileInfo(null, null);
            AppSettings.isLogoActive = false;
            AppSettings.currentLogo = null;
            AppSettings.logoX = 0;
            AppSettings.logoY = 0;
            AppSettings.logoScale = 0.55;
            switcher.setLocked(false);
            mainView.getQrPreview().requestRefresh();
        });

        btnBox.getChildren().addAll(btnL, btnA, btnC);

        HBox.setHgrow(editor, Priority.NEVER);
        VBox.setVgrow(content, Priority.ALWAYS);
        editor.prefHeightProperty().bind(content.heightProperty());
        editor.prefWidthProperty().bind(editor.prefHeightProperty());
        editor.minWidthProperty().bind(editor.prefWidthProperty());
        editor.maxWidthProperty().bind(editor.prefWidthProperty());

        VBox zoomBox = new VBox(0);
        zoomBox.getStyleClass().add("logo-zoom-pill");
        zoomBox.setAlignment(Pos.CENTER);
        zoomBox.getChildren().addAll(btnPlus, btnMinus);

        VBox infoBox = createInfoBox();
        updateFileInfo(null, null);

        content.getChildren().addAll(btnBox, editor, zoomBox, infoBox);
        this.getChildren().addAll(lblLogo, content);
    }

    private Button createBtn(String t) {
        Button b = new Button(t);
        b.setPrefWidth(130);
        b.setPrefHeight(38);
        b.getStyleClass().add("logo-action-btn");
        b.setFocusTraversable(false);
        return b;
    }

    private Button createZoomBtn(String text) {
        Button b = new Button(text);
        b.setPrefSize(34, 30);
        b.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(b, Priority.ALWAYS);
        b.getStyleClass().add("logo-zoom-btn");
        b.setFocusTraversable(false);
        return b;
    }

    private VBox createInfoBox() {
        VBox box = new VBox(3);
        box.getStyleClass().add("logo-file-meta-box");
        box.setAlignment(Pos.TOP_LEFT);
        box.setMinWidth(92);
        box.setPrefWidth(92);
        box.setMaxWidth(110);

        Label title = new Label("ФАЙЛ");
        title.getStyleClass().add("logo-file-meta-title");

        Label typeLine = new Label("Тип:");
        typeLine.getStyleClass().add("logo-file-meta-label");
        fileTypeValue.getStyleClass().add("logo-file-meta-value");

        Label sizeLine = new Label("Размер:");
        sizeLine.getStyleClass().add("logo-file-meta-label");
        fileSizeValue.getStyleClass().add("logo-file-meta-value");

        Label dimLine = new Label("Разреш:");
        dimLine.getStyleClass().add("logo-file-meta-label");
        fileDimValue.getStyleClass().add("logo-file-meta-value");

        Label hint = new Label("ЛКМ: двигать\n+/-: масштаб");
        hint.getStyleClass().add("logo-file-meta-hint");

        box.getChildren().addAll(
                title,
                typeLine, fileTypeValue,
                sizeLine, fileSizeValue,
                dimLine, fileDimValue,
                hint
        );

        return box;
    }

    private Image loadImageByExtension(File file) throws Exception {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".svg")) {
            return renderSvg(file);
        }
        return new Image(file.toURI().toString());
    }

    private Image renderSvg(File svgFile) throws IOException, TranscoderException {
        try (InputStream inputStream = new FileInputStream(svgFile)) {
            TranscoderInput input = new TranscoderInput(inputStream);
            FxBufferedImageTranscoder transcoder = new FxBufferedImageTranscoder(1024, 1024);
            transcoder.transcode(input, null);
            BufferedImage bufferedImage = transcoder.getBufferedImage();
            if (bufferedImage == null) {
                throw new IllegalArgumentException("SVG не удалось преобразовать в изображение.");
            }
            return SwingFXUtils.toFXImage(bufferedImage, null);
        }
    }

    private void updateFileInfo(File file, Image image) {
        if (file == null || image == null) {
            fileTypeValue.setText("—");
            fileSizeValue.setText("—");
            fileDimValue.setText("—");
            return;
        }

        String extension = extractExtension(file.getName());
        fileTypeValue.setText(extension.isEmpty() ? "UNKNOWN" : extension.toUpperCase());
        fileSizeValue.setText(formatBytes(file.length()));
        int width = (int) Math.round(image.getWidth());
        int height = (int) Math.round(image.getHeight());
        fileDimValue.setText(width + "x" + height);
    }

    private String extractExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx >= filename.length() - 1) return "";
        return filename.substring(idx + 1);
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        double kb = bytes / 1024.0;
        if (kb < 1024) return String.format(java.util.Locale.US, "%.1f KB", kb);
        double mb = kb / 1024.0 / 1024.0;
        return String.format(java.util.Locale.US, "%.2f MB", mb);
    }

    private static class FxBufferedImageTranscoder extends ImageTranscoder {
        private BufferedImage bufferedImage;

        FxBufferedImageTranscoder(float width, float height) {
            addTranscodingHint(KEY_WIDTH, width);
            addTranscodingHint(KEY_HEIGHT, height);
        }

        @Override
        public BufferedImage createImage(int width, int height) {
            return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }

        @Override
        public void writeImage(BufferedImage image, TranscoderOutput out) {
            this.bufferedImage = image;
        }

        BufferedImage getBufferedImage() {
            return bufferedImage;
        }
    }
}
