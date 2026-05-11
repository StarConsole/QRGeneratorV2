package org.sergei.qrgen;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExportPanel extends VBox {
    static final double PANEL_WIDTH = 500;
    private boolean isOpen = false;
    private final List<Button> segments = new ArrayList<>();
    private Region selector;
    private double segmentWidth;
    private int exportTypeIndex = 0;
    private File lastExportDir = null;

    public ExportPanel(MainView mainView) {
        this.getStyleClass().add("side-settings-panel");
        this.setPrefWidth(PANEL_WIDTH);
        this.setMinWidth(PANEL_WIDTH);
        this.setMaxWidth(PANEL_WIDTH);
        this.setPadding(new Insets(15, 20, 15, 20));
        this.setSpacing(10);
        this.setTranslateX(PANEL_WIDTH);
        this.setMouseTransparent(true);

        Label title = new Label("ЭКСПОРТ");
        title.getStyleClass().add("settings-label");

        ImageView previewImage = new ImageView();
        previewImage.setPreserveRatio(true);
        previewImage.setSmooth(true);
        previewImage.setManaged(false);
        if (mainView.getQrPreview() != null && mainView.getQrPreview().getQrCode() != null) {
            previewImage.setImage(mainView.getQrPreview().getQrCode().getImage());
            mainView.getQrPreview().getQrCode().imageProperty().addListener((obs, oldImg, newImg) ->
                    previewImage.setImage(newImg));
        }

        Pane previewCard = new Pane(previewImage) {
            @Override
            protected void layoutChildren() {
                Insets p = getPadding();
                double w = Math.max(0, getWidth() - (p.getLeft() + p.getRight()));
                double h = Math.max(0, getHeight() - (p.getTop() + p.getBottom()));
                double size = Math.min(w, h);
                double x = p.getLeft() + (w - size) / 2.0;
                double y = p.getTop() + (h - size) / 2.0;

                previewImage.setFitWidth(size);
                previewImage.setFitHeight(size);
                previewImage.relocate(x, y);
            }
        };
        previewCard.getStyleClass().add("logo-editor-container");
        previewCard.setPadding(new Insets(8));
        previewCard.setMinHeight(0);

        VBox.setVgrow(previewCard, Priority.ALWAYS);

        StackPane exportTypeControl = buildExportTypeControl();
        Button exportBtn = new Button("ЭКСПОРТ");
        exportBtn.getStyleClass().add("export-action-btn");
        exportBtn.setMaxWidth(Double.MAX_VALUE);
        exportBtn.setMinHeight(AppSettings.SEGMENTED_HEIGHT);
        exportBtn.setPrefHeight(AppSettings.SEGMENTED_HEIGHT);
        exportBtn.setFocusTraversable(false);

        exportBtn.setOnAction(e -> exportQr(mainView));

        this.getChildren().addAll(title, previewCard, exportTypeControl, exportBtn);
    }

    private void exportQr(MainView mainView) {
        if (getScene() == null) return;
        Window window = getScene().getWindow();
        if (window == null) return;
        if (mainView == null || mainView.getQrPreview() == null || mainView.getQrPreview().getQrCode() == null) {
            return;
        }

        QRCodeImage qrNode = mainView.getQrPreview().getQrCode();
        Image img = qrNode.getImage();
        if (img == null) return;

        String ext;
        String desc;
        switch (exportTypeIndex) {
            case 1 -> {
                ext = "svg";
                desc = "SVG";
            }
            case 2 -> {
                ext = "jpg";
                desc = "JPG";
            }
            case 3 -> {
                ext = "jpeg";
                desc = "JPEG";
            }
            default -> {
                ext = "png";
                desc = "PNG";
            }
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Сохранить QR");
        if (lastExportDir != null && lastExportDir.exists() && lastExportDir.isDirectory()) {
            fc.setInitialDirectory(lastExportDir);
        }
        String nextName = nextAvailableQrName(lastExportDir, ext);
        fc.setInitialFileName(nextName);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(desc.toUpperCase() + " (*." + ext + ")", "*." + ext));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));

        File file = fc.showSaveDialog(window);
        if (file == null) return;
        lastExportDir = file.getParentFile();

        file = ensureExtension(file, ext);
        if (file.exists()) {
            File dir = file.getParentFile();
            String base = stripExtension(file.getName());
            File bumped = bumpIfQrNameExists(dir, base, ext);
            if (bumped != null) file = bumped;
        }

        try {
            if ("svg".equalsIgnoreCase(ext)) {
                exportAsEmbeddedPngSvg(img, file);
            } else if ("png".equalsIgnoreCase(ext)) {
                exportRaster(img, file, "png");
            } else {
                exportRaster(img, file, "jpg");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void exportRaster(Image img, File file, String format) throws Exception {
        BufferedImage b = SwingFXUtils.fromFXImage(img, null);
        if ("jpg".equalsIgnoreCase(format) || "jpeg".equalsIgnoreCase(format)) {
            BufferedImage rgb = new BufferedImage(b.getWidth(), b.getHeight(), BufferedImage.TYPE_INT_RGB);
            rgb.getGraphics().drawImage(b, 0, 0, null);
            b = rgb;
        }
        ImageIO.write(b, format, file);
    }

    private void exportAsEmbeddedPngSvg(Image img, File file) throws Exception {
        BufferedImage b = SwingFXUtils.fromFXImage(img, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(b, "png", baos);
        String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());

        int w = b.getWidth();
        int h = b.getHeight();
        String svg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\""
                + w + "\" height=\"" + h + "\" viewBox=\"0 0 " + w + " " + h + "\">\n"
                + "  <image width=\"" + w + "\" height=\"" + h + "\" xlink:href=\"data:image/png;base64," + base64 + "\"/>\n"
                + "</svg>\n";

        try (FileWriter fw = new FileWriter(file, StandardCharsets.UTF_8)) {
            fw.write(svg);
        }
    }

    private File ensureExtension(File file, String ext) {
        String name = file.getName();
        String lower = name.toLowerCase();
        String dotExt = "." + ext.toLowerCase();
        if (lower.endsWith(dotExt)) return file;
        if (name.contains(".") && !lower.endsWith(".")) return file;
        return new File(file.getParentFile(), stripExtension(name) + dotExt);
    }

    private String stripExtension(String name) {
        int idx = name.lastIndexOf('.');
        return idx <= 0 ? name : name.substring(0, idx);
    }

    private String nextAvailableQrName(File dir, String ext) {
        File targetDir = (dir != null && dir.exists() && dir.isDirectory()) ? dir : null;
        int n = 1;
        if (targetDir == null) {
            return "QR1." + ext;
        }
        while (new File(targetDir, "QR" + n + "." + ext).exists()) {
            n++;
        }
        return "QR" + n + "." + ext;
    }

    private File bumpIfQrNameExists(File dir, String base, String ext) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) return null;
        Pattern p = Pattern.compile("(?i)^QR(\\d+)$");
        Matcher m = p.matcher(base.trim());
        if (!m.matches()) return null;

        int start = Integer.parseInt(m.group(1));
        int n = Math.max(1, start);
        while (new File(dir, "QR" + n + "." + ext).exists()) {
            n++;
        }
        return new File(dir, "QR" + n + "." + ext);
    }

    private StackPane buildExportTypeControl() {
        double containerWidth = PANEL_WIDTH - 40;
        segmentWidth = containerWidth / 4.0;

        StackPane segmentedStack = new StackPane();
        segmentedStack.setMaxWidth(containerWidth);
        segmentedStack.setMinHeight(AppSettings.SEGMENTED_HEIGHT);
        segmentedStack.setPrefHeight(AppSettings.SEGMENTED_HEIGHT);
        segmentedStack.setMaxHeight(AppSettings.SEGMENTED_HEIGHT);
        segmentedStack.getStyleClass().add("segmented-control-container");

        selector = new Region();
        selector.getStyleClass().add("segment-selector");
        selector.setMaxSize(segmentWidth - 6, AppSettings.SEGMENTED_HEIGHT - 6);
        selector.setTranslateX(-(containerWidth / 2.0) + (segmentWidth / 2.0));

        HBox buttonsLayer = new HBox(0);
        buttonsLayer.setAlignment(Pos.CENTER);

        String[] titles = {"PNG", "SVG", "JPG", "JPEG"};
        segments.clear();
        for (int i = 0; i < titles.length; i++) {
            buttonsLayer.getChildren().add(createSegment(titles[i], i, containerWidth));
        }

        segments.get(0).getStyleClass().add("segment-button-active");
        segmentedStack.getChildren().addAll(selector, buttonsLayer);
        return segmentedStack;
    }

    private Button createSegment(String text, int index, double totalWidth) {
        Button btn = new Button(text);
        btn.getStyleClass().add("segment-button");
        btn.setPrefSize(segmentWidth, AppSettings.SEGMENTED_HEIGHT);
        btn.setMinHeight(AppSettings.SEGMENTED_HEIGHT);
        btn.setFocusTraversable(false);
        segments.add(btn);

        btn.setOnAction(e -> {
            animateSelector(btn, index, totalWidth);
            exportTypeIndex = index;
        });
        return btn;
    }

    private void animateSelector(Button targetBtn, int index, double totalWidth) {
        segments.forEach(b -> b.getStyleClass().remove("segment-button-active"));
        targetBtn.getStyleClass().add("segment-button-active");
        double targetX = -(totalWidth / 2.0) + (segmentWidth / 2.0) + (index * segmentWidth);
        TranslateTransition tt = new TranslateTransition(Duration.millis(250), selector);
        tt.setToX(targetX);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        tt.play();
    }

    public void toggle() {
        isOpen = !isOpen;
        this.setMouseTransparent(!isOpen);
        TranslateTransition tt = new TranslateTransition(Duration.millis(400), this);
        tt.setToX(isOpen ? 0 : PANEL_WIDTH);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        tt.play();
    }

    public void close() {
        if (isOpen) {
            toggle();
        }
    }

    public boolean isOpen() {
        return isOpen;
    }
}
