package pooGame;

import main.Juego;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PooGame extends Juego {
    public PooGame(JFrame ventana) {
        super("POO Game");
        setVentana(ventana);

        ventana.setTitle("POO Game - Demostración");
        ventana.setSize(800, 600);
        ventana.setResizable(false);
        ventana.setLocationRelativeTo(null);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(null);
        setBackground(Color.BLACK);

        JLabel profesor1 = crearLabel("Fede", 100);
        JLabel profesor2 = crearLabel("Manu", 300);
        JLabel profesor3 = crearLabel("Mauri", 500);

        add(profesor1);
        add(profesor2);
        add(profesor3);

        ventana.setContentPane(this);
        ventana.setVisible(true);

        // Tecla Escape vuelve al menú principal usando el método común
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    volverAlMenu();
                    detener();
                }
            }
        });

        iniciar();
    }

    private JLabel crearLabel(String nombre, int x) {
        JLabel label = new JLabel(nombre, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setBounds(x, 200, 150, 30);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        return label;
    }

    @Override
    public void actualizar() {
        // No hace nada, es solo demostrativo
    }
}
