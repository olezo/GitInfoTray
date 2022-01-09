package com.olezo.gui;

import com.olezo.dto.RepositoryDto;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.List;

@Slf4j
public class Gui {
    private static final Gui GUI = new Gui();

    private final TrayIcon trayIcon;
    private final TrayPopupMenu trayPopupMenu = new TrayPopupMenu();

    public static Gui getInstance() {
        return GUI;
    }

    @SneakyThrows
    public void displayLoadingTrayMenu() {
        var loadingItem = new MenuItem("App is loading...");
        loadingItem.setEnabled(false);

        var closeItem = new MenuItem("Close");
        closeItem.addActionListener(event -> System.exit(0));

        var popup = new PopupMenu();
        popup.add(loadingItem);
        popup.add(closeItem);

        trayIcon.setToolTip("App is loading");
        trayIcon.setPopupMenu(popup);
    }

    public void displayMessage(String title, String text) {
        displayMessage(title, text, TrayIcon.MessageType.INFO);
    }

    public void displayMessage(String title, String text, TrayIcon.MessageType type) {
        log.debug("Publishing new message: {}. Description: {}", title, text);

        trayIcon.displayMessage(title, text, type);
    }

    public void updateTrayMenu(String login, List<RepositoryDto> repositories) {
        var popup = trayPopupMenu.getMenu(login, repositories);

        trayIcon.setPopupMenu(popup);
    }

    @SneakyThrows
    private Gui() {
        var trayImage = Toolkit.getDefaultToolkit()
                .createImage(getClass().getResource("/tray-icon.png"));

        trayIcon = getTrayIcon(trayImage);

        SystemTray.getSystemTray().add(trayIcon);
        Taskbar.getTaskbar().setIconImage(trayImage);
    }

    private TrayIcon getTrayIcon(Image trayImage) {
        TrayIcon trayIcon = new TrayIcon(trayImage, "Git Info");
        trayIcon.setImageAutoSize(true);

        return trayIcon;
    }
}
