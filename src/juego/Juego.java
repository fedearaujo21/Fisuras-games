package juego;

import java.awt.*;
import java.awt.event.*;

public class Juego extends Frame implements Runnable, KeyListener {
    private Thread hiloJuego;
    private boolean ejecutando = false;
    private Cancha cancha;

    public Juego() {
        setTitle("Fisuras Pong");
        setSize(800, 600);
        setResizable(false);
        setLayout(null);
        setVisible(true);
        setLocationRelativeTo(null);

        // Cierre
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                ejecutando = false;
                dispose();
            }
        });

        // Cancha
        cancha = new Cancha();
        cancha.setBounds(0, 0, 800, 600);
        add(cancha);

        // Entrada de teclado
        addKeyListener(this);
        setFocusable(true);
    }

    public void iniciar() {
        ejecutando = true;
        hiloJuego = new Thread(this);
        hiloJuego.start();
    }

    @Override
    public void run() {
        while (ejecutando) {
            cancha.actualizar();    // lógica de movimiento
            cancha.repaint();       // redibujar todo

            try {
                Thread.sleep(16);  // ~60 FPS
            } catch (InterruptedException e) {
                System.out.println("Error en el loop: " + e.getMessage());
            }
        }
    }

    // Entrada teclado
    @Override
    public void keyPressed(KeyEvent e) {
        cancha.teclaPresionada(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        cancha.teclaSoltada(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // no usado
    }
}
