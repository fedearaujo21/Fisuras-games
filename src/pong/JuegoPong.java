package pong;

import main.Juego;
import main.LanzadorPong;

import javax.swing.*;
import java.awt.event.*;

public class JuegoPong extends Juego implements KeyListener {
    private Cancha cancha;
    private boolean reiniciarSolicitado = false;
    private boolean volverAlMenu = false;
    private boolean volverAConfiguracion = false;

    public JuegoPong(JFrame ventana) {
        super("Pong");
        setVentana(ventana);

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
        requestFocusInWindow();
        addKeyListener(this);

        iniciar();
    }

    @Override
    public void actualizar() {
        cancha.actualizar();
        cancha.repaint();

        if (reiniciarSolicitado) {
            reiniciarJuego();
            detener();
        } else if (volverAlMenu) {
            volverAlMenu();
            detener();
        } else if (volverAConfiguracion) {
            volverAConfiguracion();
            detener();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_R) {
            reiniciarSolicitado = true;
        } else if (code == KeyEvent.VK_ESCAPE) {
            volverAlMenu = true;
        } else if (code == KeyEvent.VK_C) {
            volverAConfiguracion = true;
        } else {
            cancha.teclaPresionada(e);
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
        new JuegoPong(nuevaVentana);
    }

    @Override
    public void volverAConfiguracion() {
        JFrame ventanaActual = (JFrame) SwingUtilities.getWindowAncestor(this);
        ventanaActual.dispose();

        JFrame ventana = new JFrame("Fisuras Games");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setResizable(false);
        ventana.setSize(800, 600);

        new LanzadorPong(ventana);
    }
}
