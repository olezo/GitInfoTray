package com.olezo;

import com.olezo.gui.Gui;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;

@Slf4j
public class Starter {
    private static final String ERROR_TITLE = "ERROR";

    private final Gui gui = Gui.getInstance();

    public void run() {
        log.info("Starting the app..");

        try {
            new Application().run();
        } catch (Exception e) {
            log.error("Application Failed", e);

            gui.displayMessage(ERROR_TITLE, e.getMessage(), TrayIcon.MessageType.ERROR);

            System.exit(0);
        }
    }
}
