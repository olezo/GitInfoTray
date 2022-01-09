package com.olezo.gui;

import lombok.SneakyThrows;

import java.awt.*;
import java.net.URL;

public class BrowserUtil {

    @SneakyThrows
    public static void open(String url) {
        Desktop.getDesktop().browse(new URL(url).toURI());
    }
}
