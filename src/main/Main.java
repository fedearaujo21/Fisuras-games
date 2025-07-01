package main;

import lemmings.control.DataManager;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()-> {
            JFrame ventana = new JFrame("Fisuras Games");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setResizable(false);
            ventana.setSize(500,700);

            MenuPrincipal menu = new MenuPrincipal(ventana);
            ventana.setContentPane(menu);
            ventana.setVisible(true);});

        //DataManager.insert("Pong", "Fer", 100); //test escritura db
        /*for (String i: DataManager.getRanking("Lemmings")) {
            System.out.println("hola: " + i);
        }// test lectura bd*/
    }
}