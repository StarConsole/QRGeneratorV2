package org.sergei.qrgen;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class QRPreviewPane extends Pane {
    private Rectangle background;
    private QRCodeImage qrCode;
    private SideButton leftBtn;
    private SideButton rightBtn;
    private GhostButton leftGhost;
    private GhostButton rightGhost;
    private ButtonOverlay leftOverlay;
    private ButtonOverlay rightOverlay;

    public QRPreviewPane() {
        background = new Rectangle();
        background.setFill(Color.WHITE);
        background.setArcWidth(45);
        background.setArcHeight(45);

        qrCode = new QRCodeImage();
        qrCode.updateContent("");

        leftBtn = new SideButton("<");
        rightBtn = new SideButton(">");

        leftGhost = new GhostButton("<", true);
        rightGhost = new GhostButton(">", false);

        leftOverlay = new ButtonOverlay("bLeft");
        rightOverlay = new ButtonOverlay("bRight");

        this.getChildren().addAll(
                background, qrCode,
                leftGhost, rightGhost,
                leftBtn, rightBtn,
                leftOverlay, rightOverlay
        );

        leftBtn.setOnMouseEntered(e -> leftGhost.animateExpansion());
        leftBtn.setOnMouseExited(e -> leftGhost.animateBack());
        rightBtn.setOnMouseEntered(e -> rightGhost.animateExpansion());
        rightBtn.setOnMouseExited(e -> rightGhost.animateBack());

        this.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            double side = Math.min(newVal.getWidth(), newVal.getHeight()) * 0.95;
            double centerX = (newVal.getWidth() - side) / 2;
            double btnW = 80;
            double commonY = 5;

            background.setWidth(side);
            background.setHeight(side);
            background.setLayoutX(centerX);
            background.setLayoutY(commonY);

            double qrSize = side * 0.96;
            qrCode.setFitWidth(qrSize);
            qrCode.setFitHeight(qrSize);
            qrCode.setLayoutX(centerX + (side - qrSize) / 2);
            qrCode.setLayoutY(commonY + (side - qrSize) / 2);

            double gap = 5;
            double leftX = centerX - gap - btnW;
            double rightX = centerX + side + gap;

            leftGhost.setBasePos(leftX, commonY, btnW, side);
            rightGhost.setBasePos(rightX, commonY, btnW, side);
            leftBtn.setBasePos(leftX, commonY, btnW, side);
            rightBtn.setBasePos(rightX, commonY, btnW, side);
            leftOverlay.setBasePos(leftX, commonY, btnW, side);
            rightOverlay.setBasePos(rightX, commonY, btnW, side);

            boolean hasSpace = (leftX > 0);
            this.getChildren().forEach(node -> node.setVisible(hasSpace));
        });
    }

    public SideButton getLeftBtn() {
        return leftBtn;
    }

    public SideButton getRightBtn() {
        return rightBtn;
    }

    public void updateColors(String bg, String pattern) {
        if (background != null) {
            try {
                background.setFill(Color.web(bg));
            } catch (Exception e) {
                background.setFill(Color.WHITE);
            }
        }

        if (qrCode != null) {
            qrCode.updateColors(bg, pattern);
        }
    }

    public void updateQR(String text) {
        qrCode.updateContent(text);
    }

    public void requestRefresh() {
        updateQR(qrCode.getAccessibleText());
    }

    public QRCodeImage getQrCode() {
        return qrCode;
    }
}
