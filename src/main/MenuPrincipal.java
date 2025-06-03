package main;

import pong.LanzadorPong;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MenuPrincipal extends Panel {
    public MenuPrincipal(JFrame ventana){
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Selecciona un juego");
        titulo.setFont(new Font("Arial",Font.BOLD,26));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBorder(new EmptyBorder(30,0,30,0));
        add(titulo,BorderLayout.NORTH);

        JButton btnLemmings = new JButton("Jugar Lemmings");
        JButton btnPong = new JButton("Jugar Pong");

        btnLemmings.addActionListener((ActionEvent e) -> {
            LanzadorLemming.iniciar(ventana); // cambia el panel a Lemmings
        });

        btnPong.addActionListener((ActionEvent e) -> {
            LanzadorPong.iniciar(ventana);
        });

        JPanel botones = new JPanel();
        botones.setLayout(new GridLayout(2, 1, 10, 10));
        botones.add(btnLemmings);
        botones.add(btnPong);
        botones.setBorder(new EmptyBorder(10, 100, 200, 100));
        add(botones, BorderLayout.CENTER);
    }
}
