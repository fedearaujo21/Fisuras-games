package pong;
import juego.ConfigPantalla;

import javax.swing.*;

public class LanzadorPong {
    public static void iniciar(JFrame ventanaDelMenu) {
        // Cerramos solo el menú visualmente (opcional)
        ventanaDelMenu.setVisible(false);

        new ConfigPantalla(() -> {
            Juego juego = new Juego(); // Tu clase original que extiende Frame
            juego.setVisible(true);
            juego.iniciar();});
    }
}

