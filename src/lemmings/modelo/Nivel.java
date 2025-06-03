package lemmings.modelo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import lemmings.modelo.Lemming;

public class Nivel {
    private int nivelNum;
    private int tiempo;
    private String nombre;
    private int cantidadLem;
    private Mapa mapa;
    private List<Lemming> lemmings;
    private Entrada entrada;
    private static final int COLOR_FONDO = 0xFF000000;
    private Stock stockHabilidades;

    private boolean puertaAbierta = false;
    private long tiempoInicio;
    private boolean lemmingsSpawneados = false;

    public Nivel (int nivelNum, String nombre, BufferedImage mapaImagen) {
        this.nivelNum = nivelNum;
        this.nombre = nombre;
        this.tiempo = 60;
        this.cantidadLem = 2;
        this.mapa = new Mapa(mapaImagen, COLOR_FONDO);
        this.lemmings = new ArrayList<>();
        this.entrada = new Entrada(300,80);
        this.tiempoInicio = System.currentTimeMillis();

        this.stockHabilidades = new Stock();
        stockHabilidades.añadirHabilidad("Minero",5);


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
            if (ahora - tiempoInicio > lemmings.size() * 3000) { // uno por segundo
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

        g.setColor(Color.WHITE);
        g.drawString("Lemmings: " + lemmingsSpawneados + "/" + cantidadLem, 10, 20);
        g.drawString("Mineros: " + stockHabilidades.getCantidad("Minero"), 10, 40);
    }

    public boolean asignarHabilidad(Lemming lemming, String nombreHabilidad) {
        if (stockHabilidades.consumirHabilidad(nombreHabilidad)) {
            Habilidad habilidad = null;
            switch (nombreHabilidad) {
                case "Minero":
                    habilidad = new HabilidadMinero(this.mapa);
                    break;
                // Agrega más casos para otras habilidades
            }

            if (habilidad != null && habilidad.activar(lemming)) {
                System.out.println("Habilidad '" + nombreHabilidad + "' asignada a Lemming.");
                return true;
            }
            else {
                stockHabilidades.añadirHabilidad(nombreHabilidad, 1); // Devuelve el uso si no se pudo activar
                System.out.println("No se pudo activar la habilidad '" + nombreHabilidad + "' en el Lemming.");
                return false;
            }
        }
        System.out.println("No quedan usos de '" + nombreHabilidad + "' en el stock del nivel.");
        return false;
    }
    public Stock getStockHabilidades() { return stockHabilidades; }
    public List<Lemming> getLemmings() { return lemmings; }
    public Mapa getMapa() { return mapa; }

    public void reiniciar() { /* ... */ }
    public void pausar() { /* ... */ }
    public void completarNVL() { /* ... */ }
}

