package com.olezo.gui;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.net.URL;

@Slf4j
public class BrowserUtil {

    @SneakyThrows
    public static void open(String url) {
        log.debug("Opening url: {}", url);

        Desktop.getDesktop().browse(new URL(url).toURI());
    }
}
