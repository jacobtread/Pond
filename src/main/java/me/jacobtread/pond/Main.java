package me.jacobtread.pond;

import com.formdev.flatlaf.FlatDarkLaf;
import me.jacobtread.pond.ui.PondEditor;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatDarkLaf.setup();
            new PondEditor().setVisible(true);
        });
    }

}
