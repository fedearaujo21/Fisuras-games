package main;

import lemmings.modelo.Config;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Juego extends JPanel implements Runnable {
    protected boolean enEjecucion = false;
    protected Thread hiloJuego;
    //protected List<Jugador> jugadores;
    //protected Ranking ranking;
    protected String nombre;
    protected JFrame ventana; // NUEVO

    public Juego(String nombre) {
        this.nombre = nombre;
        //this.jugadores = new ArrayList<>();
        //this.ranking = new Ranking(); // o cargar desde archivo
        setFocusable(true);
    }

    public void iniciar() {
        if (enEjecucion) return;
        enEjecucion = true;
        hiloJuego = new Thread(this);
        hiloJuego.start();
    }
    public void setVentana(JFrame ventana) {
        this.ventana = ventana;
    }
    public void detener() {
        enEjecucion = false;
        try {
            hiloJuego.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        while (enEjecucion) {
            actualizar();
            repaint();

            try {
                Thread.sleep(Config.interframe); // Usa la variable dinámica
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // cada juego tiene que sobreescribir este metodo
    public void actualizar() {}

//    public Ranking getRanking() {
//        return ranking;
//    }
//
//    public void agregarJugador(Jugador jugador) {
//        jugadores.add(jugador);
//    }
//
//    public List<Jugador> getJugadores() {
//        return jugadores;
//    }

    public String getNombre() {
        return nombre;
    }
    public void volverAlMenu() {
        if (ventana != null) {
            ventana.dispose();
        }
        SwingUtilities.invokeLater(() -> {
            JFrame nuevaVentana = new JFrame("Fisuras Games");
            nuevaVentana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            nuevaVentana.setSize(800, 800);
            nuevaVentana.setResizable(false);
            nuevaVentana.setLocationRelativeTo(null);
            nuevaVentana.setContentPane(new MenuPrincipal(nuevaVentana));
            nuevaVentana.setVisible(true);
        });
    }

}
