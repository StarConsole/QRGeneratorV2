package org.sergei.qrgen;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.util.HashMap;
import java.util.Map;

public class QRCodeImage extends ImageView {
    private String currentBgColor = "FFFFFF";
    private String currentPatternColor = "000000";

    public QRCodeImage() {
        this.setPreserveRatio(true);
    }

    public void updateColors(String bgHex, String patternHex) {
        this.currentBgColor = bgHex.replace("#", "");
        this.currentPatternColor = patternHex.replace("#", "");
        if (getAccessibleText() != null) {
            updateContent(getAccessibleText());
        }
    }

    public void updateContent(String text) {
        String finalContent = (text == null || text.trim().isEmpty())
                ? AppSettings.DEFAULT_QR_TEXT
                : text;

        this.setAccessibleText(finalContent);

        try {
            QRCodeWriter writer = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();

            ErrorCorrectionLevel[] levels = {
                    ErrorCorrectionLevel.L, ErrorCorrectionLevel.M,
                    ErrorCorrectionLevel.Q, ErrorCorrectionLevel.H
            };
            hints.put(EncodeHintType.ERROR_CORRECTION, levels[AppSettings.DEFAULT_ERROR_CORRECTION]);
            hints.put(EncodeHintType.MARGIN, 1);

            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            if (AppSettings.QR_MASK_PATTERN != -1) {
                hints.put(EncodeHintType.QR_MASK_PATTERN, AppSettings.QR_MASK_PATTERN);
            }

            BitMatrix bitMatrix = writer.encode(finalContent, BarcodeFormat.QR_CODE, 512, 512, hints);
            int w = bitMatrix.getWidth();
            int h = bitMatrix.getHeight();
            WritableImage qrImage = new WritableImage(w, h);
            PixelWriter pixelWriter = qrImage.getPixelWriter();

            int argbBg = (int) Long.parseLong("FF" + currentBgColor, 16);
            int argbPattern = (int) Long.parseLong("FF" + currentPatternColor, 16);

            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    pixelWriter.setArgb(x, y, bitMatrix.get(x, y) ? argbPattern : argbBg);
                }
            }

            if (AppSettings.isLogoActive && AppSettings.currentLogo != null) {
                Canvas canvas = new Canvas(w, h);
                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.drawImage(qrImage, 0, 0);

                double safeZoneSize = w * AppSettings.LOGO_SAFE_ZONE_RATIO_H;
                double normalizedScale = clamp(
                        AppSettings.logoScale,
                        AppSettings.LOGO_MIN_SCALE,
                        AppSettings.LOGO_MAX_SCALE
                );
                double finalSize = safeZoneSize * normalizedScale;

                double ratio = safeZoneSize / LogoEditorPane.EDITOR_SIZE;

                double centerX = (w - finalSize) / 2.0 + (AppSettings.logoX * ratio);
                double centerY = (h - finalSize) / 2.0 + (AppSettings.logoY * ratio);

                gc.setImageSmoothing(true);
                Image logo = AppSettings.currentLogo;
                double iw = logo.getWidth();
                double ih = logo.getHeight();
                if (iw > 0 && ih > 0) {
                    double scale = finalSize / Math.max(iw, ih);
                    double dw = iw * scale;
                    double dh = ih * scale;
                    double dx = centerX + (finalSize - dw) / 2.0;
                    double dy = centerY + (finalSize - dh) / 2.0;
                    gc.drawImage(logo, dx, dy, dw, dh);
                } else {
                    gc.drawImage(logo, centerX, centerY, finalSize, finalSize);
                }

                WritableImage finalResult = new WritableImage(w, h);
                canvas.snapshot(null, finalResult);
                this.setImage(finalResult);
            } else {
                this.setImage(qrImage);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
