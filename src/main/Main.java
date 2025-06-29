package main;

import lemmings.control.DataManager;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.sql.*;

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

        //DataManager.insert("Lemmings", "Fer", 100); //test escritura db
        /*for (String i: DataManager.getRanking("Lemmings")) {
            System.out.println("hola: " + i);
        }// test lectura bd*/
    }
}