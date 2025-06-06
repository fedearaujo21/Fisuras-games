package lemmings.modelo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
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
    private Salida salida;
    private static final int COLOR_FONDO = 0xFF000000;
    private Stock stockHabilidades;

    private boolean nivelCompletado = false;

    //private boolean puertaAbierta = false;
    private long tiempoInicio;
    private boolean lemmingsSpawneados = false;
    private int lemmingsSalvados = 0;

    public Nivel (int nivelNum, String nombre, BufferedImage mapaImagen) {
        this.nivelNum = nivelNum;
        this.nombre = nombre;
        this.tiempo = 60;

        switch (nivelNum) {
            case 1:
                this.cantidadLem = 5; // Color negro del fondo en Nivel1.png
                this.salida = new Salida(560, 210, 80, 80);
                this.entrada = new Entrada(300,80);
                this.mapa = new Mapa(mapaImagen, COLOR_FONDO);
                this.lemmings = new ArrayList<>();
                this.stockHabilidades = new Stock();
                this.tiempoInicio = System.currentTimeMillis();
                stockHabilidades.añadirHabilidad("Minero",5);
                stockHabilidades.añadirHabilidad("Paracaidas", 5);
                mapa.limpiarArea(salida.getX(), salida.getY(), salida.getAncho(), salida.getAlto(), COLOR_FONDO);
                mapa.activarColisiones(false);
                break;

            case 2:
                break;
            default:
                this.salida = new Salida(650, 460, 80, 100);
        }

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
        if(cantidadLem == 0){
            nivelCompletado = true;
        }
        else if (lemmings.size() < cantidadLem) {
            if (ahora - tiempoInicio > lemmings.size() * 3000) { // uno por segundo
                int lemmingX = entrada.getX();
                int lemmingY = entrada.getY();
                lemmings.add(new Lemming(lemmingX, lemmingY, mapa));
            }
        }

        // 3. Movimiento
        Iterator<Lemming> it = lemmings.iterator();
        while (it.hasNext()) {
            Lemming l = it.next();
            l.caminar();
            // Comprobar si el Lemming llegó a la salida
            if (salida.haAlcanzado(l)) {
                lemmingsSalvados++;
                cantidadLem -= 1;
                System.out.println("¡Lemming salvado! Total salvados: " + lemmingsSalvados);
                it.remove(); // Eliminar el Lemming de la lista
            }
        }


    }

    public void dibujar(Graphics g){
        mapa.dibujar(g);
        for (Lemming l : lemmings) {
            l.dibujar(g);
        }
        salida.dibujar(g);
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
                case "Paracaidas":
                    habilidad = new HabilidadParacaidas();
                    break;
                // Agregar más habilidades en el futuro
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
    public boolean getNivelCompletado(){return this.nivelCompletado;};
    public void reiniciar() { /* ... */ }
    public void pausar() { /* ... */ }
    public void completarNVL() { /* ... */ }
}

