package org.sergei.qrgen;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TitleBar extends StackPane {

    public TitleBar(Stage stage) {
        this.setStyle("-fx-background-color: #1e1e1e; -fx-min-height: " + AppSettings.TITLE_BAR_HEIGHT + "px;");

        Button btnHome = new Button("QRgenerator");
        btnHome.getStyleClass().add("home-button");
        btnHome.setMaxWidth(Double.MAX_VALUE);
        btnHome.setMaxHeight(Double.MAX_VALUE);
        btnHome.setFocusTraversable(false);
        btnHome.setStyle(AppSettings.getFontSize() + " -fx-font-weight: bold;");

        btnHome.setOnAction(e -> {
            if (getScene() != null && getScene().getRoot() instanceof BorderPane root) {
                if (root.getCenter() instanceof MainView mv) {
                    mv.closeSettings();
                }
            }
            if (btnHome.getParent() != null) {
                btnHome.getParent().requestFocus();
            }
        });

        HBox iconsLayer = new HBox();
        iconsLayer.setPickOnBounds(false);
        iconsLayer.setAlignment(Pos.CENTER_LEFT);
        iconsLayer.setPadding(new Insets(0, 15, 0, 15));

        int iconSize = AppSettings.getIconSize();

        Button btnHelp = createHelpButton(iconSize);
        btnHelp.setOnAction(e -> {
            if (getScene() != null && getScene().getRoot() instanceof BorderPane root) {
                if (root.getCenter() instanceof MainView mv) {
                    mv.toggleHelp();
                }
            }
            e.consume();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        spacer.setMouseTransparent(true);

        Button btnMinimize = createIconButton("/icons/minimize.png", iconSize, true);
        Button btnClose = createIconButton("/icons/close.png", iconSize, true);

        btnMinimize.setOnAction(e -> stage.hide());
        btnClose.setOnAction(e -> System.exit(0));

        HBox rightButtons = new HBox(15, btnMinimize, btnClose);
        rightButtons.setAlignment(Pos.CENTER_RIGHT);

        iconsLayer.getChildren().addAll(btnHelp, spacer, rightButtons);

        this.getChildren().addAll(btnHome, iconsLayer);
    }

    private Button createIconButton(String path, int size, boolean isWindowControl) {
        Button btn = new Button();

        String style = "-fx-background-color: transparent; -fx-padding: 0; -fx-cursor: hand;";
        if (isWindowControl) {
            style += String.format(
                    " -fx-border-color: black; -fx-border-width: %.1fpx; -fx-border-radius: 50%%; -fx-background-radius: 50%%;",
                    AppSettings.CONTROL_BORDER_WIDTH
            );
        }
        btn.setStyle(style);

        try {
            var res = getClass().getResource(path);
            if (res != null) {
                ImageView view = new ImageView(new Image(res.toExternalForm()));
                view.setFitHeight(size);
                view.setPreserveRatio(true);

                btn.setOnMouseEntered(e -> view.setOpacity(0.7));
                btn.setOnMouseExited(e -> view.setOpacity(1.0));
                btn.setGraphic(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return btn;
    }

    private Button createHelpButton(int size) {
        var res = getClass().getResource("/icons/help.png");
        if (res != null) {
            Button b = createIconButton("/icons/help.png", size, false);
            b.getStyleClass().add("help-button");
            return b;
        }

        Button b = new Button("?");
        b.getStyleClass().add("help-button");
        b.setMinSize(size, size);
        b.setPrefSize(size, size);
        b.setMaxSize(size, size);
        b.setFocusTraversable(false);
        b.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: "
                + (size * 0.9) + "px; -fx-cursor: hand;");
        b.setOnMouseEntered(e -> b.setOpacity(0.7));
        b.setOnMouseExited(e -> b.setOpacity(1.0));
        return b;
    }
}
