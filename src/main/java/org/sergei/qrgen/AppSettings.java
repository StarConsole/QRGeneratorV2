package org.sergei.qrgen;

import javafx.scene.image.Image;

public class AppSettings {
    public static double WINDOW_WIDTH = 600;
    public static double WINDOW_HEIGHT = 700;
    public static double TITLE_BAR_HEIGHT = 50;
    public static double ICON_SCALE = 0.6;
    public static double CONTROL_BORDER_WIDTH = 0.0;
    public static double SEGMENTED_HEIGHT = 45.0;
    public static double INPUT_ROW_HEIGHT = 45.0;
    public static double INPUT_GAP = 10.0;
    public static double INPUT_CONTAINER_WIDTH_PERCENT = 0.9;
    public static String DEFAULT_QR_TEXT = "by StarConsole";
    public static int DEFAULT_ERROR_CORRECTION = 0;
    public static int QR_MASK_PATTERN = -1;
    public static Image currentLogo = null;
    public static double logoX = 0;
    public static double logoY = 0;
    public static double logoScale = 0.55;
    public static boolean isLogoActive = false;
    public static final double LOGO_SAFE_ZONE_RATIO_H = 0.30;
    public static final double LOGO_MIN_SCALE = 0.02;
    public static final double LOGO_MAX_SCALE = 1.0;
    public static final double LOGO_EDITOR_MAGNIFICATION = 4.0;
    public static final double GHOST_OFFSET = 30.0;

    public static int getIconSize() {
        return (int) (TITLE_BAR_HEIGHT * ICON_SCALE);
    }

    public static String getFontSize() {
        return String.format("-fx-font-size: %.0fpx;", TITLE_BAR_HEIGHT * 0.35);
    }
}
