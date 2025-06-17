package pong;

import main.MenuPrincipal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Juego extends JPanel implements Runnable, KeyListener {
    private Thread hiloJuego;
    private boolean ejecutando = false;
    private Cancha cancha;
    private boolean reiniciarSolicitado = false;
    private boolean volverAlMenu = false;
    private JFrame ventana;


    public Juego(JFrame ventana) {
        this.ventana = ventana;

        ventana.setTitle("Fisuras Pong");
        ventana.setSize(800, 600);
        ventana.setResizable(false);
        ventana.setLocationRelativeTo(null);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(null);
        cancha = new Cancha();
        cancha.setBounds(0, 0, 800, 600);
        add(cancha);

        ventana.setContentPane(this);
        ventana.setVisible(true);

        setFocusable(true);
        addKeyListener(this); // ← MOVER AQUÍ
    }




    public void iniciar() {
        ejecutando = true;
        hiloJuego = new Thread(this);
        hiloJuego.start();
    }

    @Override
    public void run() {
        requestFocusInWindow(); // ← AQUÍ sí garantiza que tenga el foco
        while (ejecutando) {
            cancha.actualizar();
            cancha.repaint();

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                System.out.println("Error en el loop: " + e.getMessage());
            }

            if (reiniciarSolicitado) {
                reiniciarJuego();
                return;
            }

            if (volverAlMenu) {
                volverAlMenuPrincipal();
                return;
            }
        }
    }


    // Entrada teclado
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_R) {
            reiniciarSolicitado = true;
        } else if (code == KeyEvent.VK_ESCAPE) {
            volverAlMenu = true;
        } else {
            cancha.teclaPresionada(e); // solo si no fue una tecla especial
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {
        cancha.teclaSoltada(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // no usado
    }

    private void reiniciarJuego() {
        JFrame ventanaActual = (JFrame) SwingUtilities.getWindowAncestor(this);
        ventanaActual.dispose();
        JFrame nuevaVentana = new JFrame("Fisuras Pong");
        nuevaVentana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Juego nuevoJuego = new Juego(nuevaVentana);
        nuevoJuego.iniciar();
    }

    private void volverAlMenuPrincipal() {
        JFrame ventanaActual = (JFrame) SwingUtilities.getWindowAncestor(this);
        ventanaActual.dispose();
        JFrame ventana = new JFrame("Fisuras");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setContentPane(new MenuPrincipal(ventana));
        ventana.setSize(800, 600);
        ventana.setResizable(false);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }


}
