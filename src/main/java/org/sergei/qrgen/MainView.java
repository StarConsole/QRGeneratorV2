package org.sergei.qrgen;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class MainView extends StackPane {

    private final List<Button> segments = new ArrayList<>();
    private Region selector;
    private double segmentWidth;
    private StackPane contentArea;
    private QRPreviewPane qrPreview;
    private SideSettingsPanel settingsPanel;
    private ExportPanel exportPanel;
    private HelpPanel helpPanel;

    public MainView() {
        this.getStyleClass().add("main-view");

        settingsPanel = new SideSettingsPanel(this);
        helpPanel = new HelpPanel();

        VBox mainContainer = new VBox(5);
        mainContainer.setPadding(new Insets(5, 0, 0, 0));
        mainContainer.setAlignment(Pos.TOP_CENTER);

        setupSegmentedControl(mainContainer);

        contentArea = new StackPane();
        contentArea.setPadding(new Insets(0, 20, 0, 20));
        switchContent(0);

        qrPreview = new QRPreviewPane();
        exportPanel = new ExportPanel(this);
        VBox.setVgrow(qrPreview, Priority.ALWAYS);

        mainContainer.getChildren().addAll(contentArea, qrPreview);

        this.getChildren().addAll(mainContainer, settingsPanel, exportPanel, helpPanel);
        StackPane.setAlignment(settingsPanel, Pos.TOP_LEFT);
        StackPane.setAlignment(exportPanel, Pos.TOP_RIGHT);
        StackPane.setAlignment(helpPanel, Pos.TOP_CENTER);

        qrPreview.getLeftBtn().setOnMouseClicked(e -> {
            if (exportPanel.isOpen()) exportPanel.close();
            if (helpPanel.isOpen()) helpPanel.close();
            settingsPanel.toggle();
            e.consume();
        });
        qrPreview.getRightBtn().setOnMouseClicked(e -> {
            if (settingsPanel.isOpen()) settingsPanel.close();
            if (helpPanel.isOpen()) helpPanel.close();
            exportPanel.toggle();
            e.consume();
        });

        this.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            boolean consumed = false;

            if (settingsPanel.isOpen() && !settingsPanel.getBoundsInParent().contains(e.getX(), e.getY())) {
                settingsPanel.close();
                consumed = true;
            }

            if (exportPanel.isOpen() && !exportPanel.getBoundsInParent().contains(e.getX(), e.getY())) {
                exportPanel.close();
                consumed = true;
            }

            if (helpPanel.isOpen() && !helpPanel.getBoundsInParent().contains(e.getX(), e.getY())) {
                helpPanel.close();
                consumed = true;
            }

            if (consumed) {
                e.consume();
            }
        });
    }

    private void setupSegmentedControl(VBox container) {
        double containerWidth = AppSettings.WINDOW_WIDTH * AppSettings.INPUT_CONTAINER_WIDTH_PERCENT;
        segmentWidth = containerWidth / 4;

        StackPane segmentedStack = new StackPane();
        segmentedStack.setMaxSize(containerWidth, AppSettings.SEGMENTED_HEIGHT);
        segmentedStack.getStyleClass().add("segmented-control-container");

        selector = new Region();
        selector.getStyleClass().add("segment-selector");
        selector.setMaxSize(segmentWidth - 6, AppSettings.SEGMENTED_HEIGHT - 6);
        selector.setTranslateX(-(containerWidth / 2) + (segmentWidth / 2));

        HBox buttonsLayer = new HBox(0);
        buttonsLayer.setAlignment(Pos.CENTER);

        String[] titles = {"URL", "ТЕКСТ", "WIFI", "КОНТАКТ"};
        for (int i = 0; i < titles.length; i++) {
            buttonsLayer.getChildren().add(createSegment(titles[i], i, containerWidth));
        }

        segments.get(0).getStyleClass().add("segment-button-active");
        segmentedStack.getChildren().addAll(selector, buttonsLayer);
        container.getChildren().add(segmentedStack);
    }

    private Button createSegment(String text, int index, double totalWidth) {
        Button btn = new Button(text);
        btn.getStyleClass().add("segment-button");
        btn.setPrefSize(segmentWidth, AppSettings.SEGMENTED_HEIGHT);
        btn.setFocusTraversable(false);
        segments.add(btn);

        btn.setOnAction(e -> {
            animateSelector(btn, index, totalWidth);
            switchContent(index);
        });
        return btn;
    }

    private void animateSelector(Button targetBtn, int index, double totalWidth) {
        segments.forEach(b -> b.getStyleClass().remove("segment-button-active"));
        targetBtn.getStyleClass().add("segment-button-active");
        double targetX = -(totalWidth / 2) + (segmentWidth / 2) + (index * segmentWidth);
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), selector);
        tt.setToX(targetX);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        tt.play();
    }

    private void switchContent(int index) {
        contentArea.getChildren().clear();
        Node newPane;
        switch (index) {
            case 0 -> newPane = new UrlTextPane("Введите URL ссылку...", this);
            case 1 -> newPane = new UrlTextPane("Введите любой текст...", this);
            case 2 -> newPane = new WifiPane(this);
            case 3 -> newPane = new ContactPane(this);
            default -> newPane = new UrlTextPane("...", this);
        }
        contentArea.getChildren().add(newPane);
    }

    public QRPreviewPane getQrPreview() {
        return qrPreview;
    }

    public void closeSettings() {
        if (settingsPanel != null && settingsPanel.isOpen()) {
            settingsPanel.close();
        }
        if (exportPanel != null && exportPanel.isOpen()) {
            exportPanel.close();
        }
        if (helpPanel != null && helpPanel.isOpen()) {
            helpPanel.close();
        }
    }

    public void toggleHelp() {
        if (helpPanel == null) return;
        if (settingsPanel != null && settingsPanel.isOpen()) settingsPanel.close();
        if (exportPanel != null && exportPanel.isOpen()) exportPanel.close();
        helpPanel.toggle();
    }
}
