package pong;

import main.Juego;
import main.LanzadorPong;
import main.MenuPrincipal;

import javax.swing.*;
import java.awt.event.*;

public class JuegoPong extends Juego implements KeyListener{
    private Cancha cancha;
    private boolean reiniciarSolicitado = false;
    private boolean volverAlMenu = false;
    private boolean volverAConfiguracion = false;
    private JFrame ventana;

    public JuegoPong(JFrame ventana) {
        super("Pong");
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
            volverAlMenuPrincipal();
            detener();
        } else if (volverAConfiguracion) {
            volverAConfig();
            detener();
        }
    }


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


    public void keyReleased(KeyEvent e) {
        cancha.teclaSoltada(e);
    }


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

    private void volverAlMenuPrincipal() {
        JFrame ventanaActual = (JFrame) SwingUtilities.getWindowAncestor(this);
        ventanaActual.dispose();
        JFrame ventana = new JFrame("Fisuras");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setContentPane(new MenuPrincipal(ventana));
        ventana.setSize(500, 700);
        ventana.setResizable(false);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    private void volverAConfig() {
        JFrame ventanaActual = (JFrame) SwingUtilities.getWindowAncestor(this);
        ventanaActual.dispose();

        JFrame ventana = new JFrame("Fisuras Games");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setResizable(false);
        ventana.setSize(800,600);

        new LanzadorPong(ventana);
    }
}
