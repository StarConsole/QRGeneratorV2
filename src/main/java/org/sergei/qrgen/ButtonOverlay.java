package org.sergei.qrgen;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class ButtonOverlay extends Pane {
    private ImageView imageView;

    public ButtonOverlay(String iconName) {
        this.setMouseTransparent(true);

        try {
            var res = getClass().getResource("/icons/" + iconName + ".png");
            if (res != null) {
                imageView = new ImageView(new Image(res.toExternalForm()));
                imageView.setPreserveRatio(true);
                this.getChildren().add(imageView);
            }
        } catch (Exception e) {
            System.err.println("Не удалось загрузить наложение: " + iconName);
        }
    }

    public void setBasePos(double x, double y, double w, double h) {
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.setPrefSize(w, h);

        if (imageView != null) {
            imageView.setFitHeight(h);
            imageView.setLayoutX((w - h) / 2.0);
            imageView.setLayoutY(0);
        }

        Rectangle clip = new Rectangle(w, h);
        clip.setArcWidth(45);
        clip.setArcHeight(45);
        this.setClip(clip);
    }
}
