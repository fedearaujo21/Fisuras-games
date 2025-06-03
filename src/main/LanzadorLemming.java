package main;

import lemmings.vista.PanelLemmings;

import javax.swing.*;

public class LanzadorLemming {
    public static void iniciar(JFrame ventana){
        PanelLemmings juego = new PanelLemmings(ventana);
        ventana.setContentPane(juego);
        ventana.revalidate();
        juego.requestFocusInWindow();
    }
}
