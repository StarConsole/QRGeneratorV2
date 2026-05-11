package org.sergei.qrgen;

import javafx.application.Platform;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class TrayManager {
    private final Stage stage;

    public TrayManager(Stage stage) {
        this.stage = stage;
    }

    public void setupTray() {
        if (!SystemTray.isSupported()) {
            System.out.println("System tray is not supported");
            return;
        }

        try {
            SystemTray tray = SystemTray.getSystemTray();

            URL imageLoc = getClass().getResource("/icons/help.png");
            if (imageLoc == null) {
                imageLoc = getClass().getResource("/icons/settings.png");
            }
            Image image = Toolkit.getDefaultToolkit().getImage(imageLoc);

            TrayIcon trayIcon = new TrayIcon(image, "QRGeneratorV2");
            trayIcon.setImageAutoSize(true);

            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        Platform.runLater(() -> {
                            stage.show();
                            stage.setIconified(false);
                        });
                    }
                }
            });

            PopupMenu popup = new PopupMenu();
            MenuItem exitItem = new MenuItem("Exit");
            exitItem.addActionListener(e -> System.exit(0));
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);

            tray.add(trayIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
