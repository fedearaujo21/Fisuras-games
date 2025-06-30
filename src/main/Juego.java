package main;

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
        final int FPS = 60;
        final long NS_POR_FRAME = 1_000_000_000 / FPS;
        long ultimaActualizacion = System.nanoTime();

        while (enEjecucion) {
            long ahora = System.nanoTime();
            if (ahora - ultimaActualizacion >= NS_POR_FRAME) {
                actualizar();
                repaint();
                ultimaActualizacion = ahora;
            }
        }
    }

    // Este método lo deben sobrescribir los juegos concretos
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
}
