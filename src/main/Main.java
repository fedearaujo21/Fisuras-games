package main;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()-> {
            JFrame ventana = new JFrame("Fisuras Games");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setResizable(false);
            ventana.setSize(800,600);

            MenuPrincipal menu = new MenuPrincipal(ventana);
            ventana.setContentPane(menu);
            ventana.setVisible(true);});
    }
}
