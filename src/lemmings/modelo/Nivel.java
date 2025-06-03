package lemmings.modelo;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Nivel {
    private int nivelNum;
    private int tiempo;
    private String nombre;
    private int cantidadLem;
    private Mapa mapa;
    private List<Lemming> lemmings;
    private Entrada entrada;
    private static final int COLOR_FONDO = 0xFF000000;

    private boolean puertaAbierta = false;
    private long tiempoInicio;
    private boolean lemmingsSpawneados = false;

    public Nivel (int nivelNum, String nombre, BufferedImage mapaImagen) {
        this.nivelNum = nivelNum;
        this.nombre = nombre;
        this.tiempo = 60;
        this.cantidadLem = 10;
        this.mapa = new Mapa(mapaImagen, COLOR_FONDO);
        this.lemmings = new ArrayList<>();
        this.entrada = new Entrada(300,80);
        this.tiempoInicio = System.currentTimeMillis();

        mapa.activarColisiones(false);  // Desactiva colisiones temporalmente
    }

    public void actualizar() {
        long ahora = System.currentTimeMillis();

        // 1. Desactiva colisiones los primeros 500ms
        if (ahora - tiempoInicio < 500) {
            mapa.activarColisiones(false);
        } else {
            mapa.activarColisiones(true);
        }

        // 2. Spawneo progresivo de lemmings
        if (lemmings.size() < cantidadLem) {
            if (ahora - tiempoInicio > lemmings.size() * 1000) { // uno por segundo
                int lemmingX = entrada.getX();
                int lemmingY = entrada.getY();
                lemmings.add(new Lemming(lemmingX, lemmingY, mapa));
            }
        }

        // 3. Movimiento
        for (Lemming l : lemmings) {
            l.caminar();
        }
    }

    public void dibujar(Graphics g){
        mapa.dibujar(g);
        for (Lemming l : lemmings) {
            l.dibujar(g);
        }
    }
}

