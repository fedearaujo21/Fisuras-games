package main;

import pong.ConfigPantalla;
import pong.Juego;

import javax.swing.*;

public class LanzadorPong {
    public static void iniciar(JFrame ventanaDelMenu) {
        // Cerramos solo el menú visualmente (opcional)
        ventanaDelMenu.setVisible(false);

        new ConfigPantalla(() -> {
            Juego juego = new Juego(ventanaDelMenu);
            juego.iniciar();
        });
    }
}

