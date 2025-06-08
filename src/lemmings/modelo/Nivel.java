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
    private int cantidadSpawns;
    private Mapa mapa;
    private List<Lemming> lemmings;
    private Entrada entrada;
    private Salida salida;
    private static final int COLOR_FONDO = 0xFF000000;
    private Stock stockHabilidades;
    private int objetivoLemmings;
    private boolean nivelAprobado;

    private boolean nivelCompletado = false;

    //private boolean puertaAbierta = false;
    private long tiempoInicio;
    private boolean lemmingsSpawneados = false;
    private int lemmingsSalvados = 0;
    private int lemmingsMuertos = 0;

    public Nivel (int nivelNum, String nombre, BufferedImage mapaImagen) {
        this.nivelNum = nivelNum;
        this.nombre = nombre;
        this.tiempo = 60;

        switch (nivelNum) {
            case 1:
                this.cantidadLem = 5;
                this.cantidadSpawns = this.cantidadLem;
                this.objetivoLemmings = 2;// Color negro del fondo en Nivel1.png
                this.salida = new Salida(560, 210, 80, 80);
                this.entrada = new Entrada(300,80);
                this.mapa = new Mapa(mapaImagen, COLOR_FONDO);
                this.lemmings = new ArrayList<>();
                this.stockHabilidades = new Stock();
                this.tiempoInicio = System.currentTimeMillis();
                stockHabilidades.añadirHabilidad("Minero",5);
                stockHabilidades.añadirHabilidad("Paracaidas", 5);
                stockHabilidades.añadirHabilidad("Bloqueador",5);
                mapa.limpiarArea(salida.getX(), salida.getY(), salida.getAncho(), salida.getAlto(), COLOR_FONDO);
                mapa.activarColisiones(false);
                break;

            case 2:
                this.cantidadLem = 10;
                this.cantidadSpawns = this.cantidadLem;
                this.objetivoLemmings = 2;// Color negro del fondo en Nivel1.png
                this.salida = new Salida(232, 235, 80, 80);
                this.entrada = new Entrada(280,20);
                this.mapa = new Mapa(mapaImagen, COLOR_FONDO);
                this.lemmings = new ArrayList<>();
                this.stockHabilidades = new Stock();
                this.tiempoInicio = System.currentTimeMillis();
                stockHabilidades.añadirHabilidad("Minero",5);
                stockHabilidades.añadirHabilidad("Paracaidas", 5);
                stockHabilidades.añadirHabilidad("Bloqueador",10);
                mapa.limpiarArea(salida.getX(), salida.getY(), salida.getAncho(), salida.getAlto(), COLOR_FONDO);
                mapa.activarColisiones(false);
                break;
            case 3:
                this.cantidadLem = 5;
                this.cantidadSpawns = this.cantidadLem;
                this.objetivoLemmings = 5;
                this.salida = new Salida(580,200 , 80, 80);
                this.entrada = new Entrada(260,20);
                this.mapa = new Mapa(mapaImagen, COLOR_FONDO);
                this.lemmings = new ArrayList<>();
                this.stockHabilidades = new Stock();
                this.tiempoInicio = System.currentTimeMillis();
                stockHabilidades.añadirHabilidad("Minero",5);
                stockHabilidades.añadirHabilidad("Paracaidas", 5);
                stockHabilidades.añadirHabilidad("Bloqueador",10);
                mapa.limpiarArea(salida.getX(), salida.getY(), salida.getAncho(), salida.getAlto(), COLOR_FONDO);
                System.out.println("Entrada en: " + entrada);
                System.out.println("Salida en: " + salida.getX());
                mapa.activarColisiones(false);
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
        else if (lemmings.size() < cantidadSpawns) {
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
            l.caminar(lemmings);
            // Comprobar si el Lemming llegó a la salida
            if (salida.haAlcanzado(l)) {
                lemmingsSalvados++;
                cantidadSpawns -= 1;
                cantidadLem -= 1;
                System.out.println("¡Lemming salvado! Total salvados: " + lemmingsSalvados);
                it.remove(); // Eliminar el Lemming de la lista
            }
            if (!(l.getHabilidadActiva() instanceof HabilidadParacaidas)){
                if (l.getY() > 318 || l.getTicksEnAire() > 109) {
                    lemmingsMuertos--;
                    cantidadSpawns -= 1;
                    cantidadLem -= 1;
                    System.out.println("Un lemming a muerto por caida");
                    it.remove();
                }
            }
        }

    }

    public void dibujar(Graphics g){
        mapa.dibujar(g);
        for (Lemming l : lemmings) {
            l.dibujar(g);
        }
        if(nivelCompletado == true){
            g.setColor(new Color(0, 0, 0, 170)); // Fondo semitransparente
            g.fillRect(0, 0, mapa.getAncho(), mapa.getAlto());
            if(objetivoLemmings < lemmingsSalvados) {
                this.nivelAprobado = true;
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                g.drawString("¡Felicitaciones!", mapa.getAncho() / 2 - 150, mapa.getAlto() / 2 - 20);

                g.setFont(new Font("Arial", Font.PLAIN, 24));
                g.drawString("Nivel completado", mapa.getAncho() / 2 - 110, mapa.getAlto() / 2 + 20);
            } else{
                nivelAprobado = false;
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                g.drawString("¡Perdiste!", mapa.getAncho() / 2 - 150, mapa.getAlto() / 2 - 20);

                g.setFont(new Font("Arial", Font.PLAIN, 24));
                g.drawString("Se necesita un total de: "+this.objetivoLemmings+" para poder avanzar...", mapa.getAncho() / 2 - 110, mapa.getAlto() / 2 + 20);
            }
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Lemmings salvados:" + this.lemmingsSalvados, mapa.getAncho() / 2 - 110, mapa.getAlto() / 2 + 40);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Lemmings muertos:" + this.lemmingsMuertos, mapa.getAncho() / 2 - 110, mapa.getAlto() / 2 + 60);
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
                case "Bloqueador":
                    habilidad = new HabilidadBloqueador();
                    cantidadLem -= 1;
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


    public boolean getNivelAprobado(){return this.nivelAprobado;}
    public Stock getStockHabilidades() { return stockHabilidades; }
    public List<Lemming> getLemmings() { return lemmings; }
    public Mapa getMapa() { return mapa; }
    public boolean getNivelCompletado(){return this.nivelCompletado;}
    public void setNivelCompletado(boolean valor){this.nivelCompletado = valor;}
    public void reiniciar() { /* ... */ }
    public void pausar() { /* ... */ }
    //public void completarNVL() { /* ... */ }
}

