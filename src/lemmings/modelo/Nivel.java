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
    private static int frecuenciaSpawn; // menor = más rápido
    private long ultimoSpawnTime = 0;



    public Nivel (int nivelNum, String nombre, BufferedImage mapaImagen) {
        this.nivelNum = nivelNum;
        this.nombre = nombre;
        this.tiempo = 60;
        frecuenciaSpawn = 50;

        switch (nivelNum) {
            case 1:
                this.cantidadLem = 5;
                this.cantidadSpawns = this.cantidadLem;
                this.objetivoLemmings = 2;// Color negro del fondo en Nivel1.png
                this.salida = new Salida(575, 222, 40, 40);
                this.entrada = new Entrada(270,60);
                this.mapa = new Mapa(mapaImagen, COLOR_FONDO);
                this.lemmings = new ArrayList<>();
                this.stockHabilidades = new Stock();
                this.tiempoInicio = System.currentTimeMillis();
                stockHabilidades.añadirHabilidad("KameHameHa",5);
                stockHabilidades.añadirHabilidad("Constructor",5);
                stockHabilidades.añadirHabilidad("AutoBomba",5);
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
                this.salida = new Salida(232, 245, 80, 80);
                this.entrada = new Entrada(230,15);
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
                this.cantidadLem = 20;
                this.cantidadSpawns = this.cantidadLem;
                this.objetivoLemmings = 5;
                this.salida = new Salida(580,230 , 80, 80);
                this.entrada = new Entrada(150,10);
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
            case 4:
                this.cantidadLem = 5;
                this.cantidadSpawns = this.cantidadLem;
                this.objetivoLemmings = 5;
                this.salida = new Salida(740,212 , 80, 80);
                this.entrada = new Entrada(150,10);
                this.mapa = new Mapa(mapaImagen, COLOR_FONDO);
                this.lemmings = new ArrayList<>();
                this.stockHabilidades = new Stock();
                this.tiempoInicio = System.currentTimeMillis();
                stockHabilidades.añadirHabilidad("AutoBomba", 5);
                stockHabilidades.añadirHabilidad("KameHameHa", 5);
                stockHabilidades.añadirHabilidad("Constructor" , 5);
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

        // 2. Spawneo progresivo de lemmings según frecuenciaSpawn
        if (cantidadLem == 0) {
            nivelCompletado = true;
        } else if (lemmings.size() < cantidadSpawns) {
            int delayEntreSpawns = (int)((100 - frecuenciaSpawn) * 30); // frecuenciaSpawn: 50-99 → delay: 1500-30ms
            if (ahora - ultimoSpawnTime >= delayEntreSpawns) {
                int lemmingX = entrada.getX();
                int lemmingY = entrada.getY();
//                lemmings.add(new Lemming(lemmingX, lemmingY, mapa));
                lemmings.add(entrada.spawnear(mapa));

                ultimoSpawnTime = ahora;
            }
        }


        // 3. Movimiento y detección de eventos
        Iterator<Lemming> it = lemmings.iterator();
        while (it.hasNext()) {
            Lemming l = it.next();
            l.caminar(lemmings);

            // Llegó a la salida
            if (salida.haAlcanzado(l)) {
                lemmingsSalvados++;
                cantidadSpawns--;
                cantidadLem--;
                System.out.println("¡Lemming salvado! Total salvados: " + lemmingsSalvados);
                it.remove();
                continue;
            }

            if (l.getHabilidadActiva() instanceof HabilidadAutoBomba autoBomba) {
                long tiempoActual = System.currentTimeMillis();
                if(tiempoActual - l.getTiempoInicioAutoBomba() >= 3000){
                    autoBomba.explotar(l.getX(),l.getY(),30);
                    lemmingsMuertos--;
                    cantidadSpawns--;
                    cantidadLem--;
                    System.out.println("Un lemming EXPLOTO");
                    it.remove();
                }
            }
            if (nivelNum == 4) { // sólo en Nivel 4 si querés
                boolean murioPorAgua = l.getX() >= 0 && l.getX() <= 570 && l.getY() >= 235 && l.getY() <= 300;
                if (murioPorAgua) {
                    System.out.println("Lemming murió por agua");
                    cantidadSpawns -= 1;
                    cantidadLem -= 1;
                    it.remove();
                }
            }
            // Muere por caída (si no tiene paracaídas)
            if (!(l.getHabilidadActiva() instanceof HabilidadParacaidas)) {

                if (l.getY() > 318 || (l.getTicksEnAire() > 109 && l.getImpacto()) ) {
                    lemmingsMuertos--;
                    cantidadSpawns--;
                    cantidadLem--;
                    System.out.println("Un lemming ha muerto por caída");
                    it.remove();
                }
            }

        }
    }


    public void dibujar(Graphics g){
        mapa.dibujar(g);

        // DIBUJA LA ENTRADA (nube animada)
        entrada.dibujar(g);

        for (Lemming l : lemmings) {
            l.dibujar(g);
        }

        if(nivelCompletado){
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
            g.drawString("Lemmings muertos:" + this.lemmingsMuertos, mapa.getAncho() / 2 - 110, mapa.getAlto() / 2 + 60);
        }

        salida.dibujar(g);

        g.setColor(Color.WHITE);
        g.drawString("Lemmings: " + lemmingsSpawneados + "/" + cantidadLem, 10, 20);
        g.drawString("Mineros: " + stockHabilidades.getCantidad("Minero"), 10, 40);
    }


    public boolean asignarHabilidad(Lemming lemming, String nombreHabilidad) {
        Habilidad habilidad = null;

        if (lemming.getHabilidadActiva() != null) { // Si ya hay una habilidad activa
            if (nombreHabilidad.equals("AutoBomba")) { // Si la nueva habilidad es AutoBomba
                if (!(lemming.getHabilidadActiva() instanceof HabilidadBloqueador)) {
                    System.out.println("El Lemming ya tiene una habilidad activa que no puede ser interrumpida por AutoBomba.");
                    return false;
                }
            } else {
                System.out.println("El Lemming ya tiene una habilidad activa, no se puede asignar otra.");
                return false;
            }
        }
        switch (nombreHabilidad) {
            case "Minero":
                habilidad = new HabilidadMinero(this.mapa);
                break;
            case "Paracaidas":
                habilidad = new HabilidadParacaidas();
                break;
            case "Bloqueador":
                habilidad = new HabilidadBloqueador();
                cantidadLem--;
                break;
            case "AutoBomba":
                habilidad = new HabilidadAutoBomba(this.mapa);
                break;
            case "Constructor":
                habilidad = new HabilidadConstructor(this.mapa);
                break;
            case "KameHameHa":
                habilidad = new HabilidadKameHameHa(this.mapa);
                break;
            // Agrega más habilidades acá
        }
        if (habilidad != null && stockHabilidades.getCantidad(nombreHabilidad) > 0) {
            if (habilidad.activar(lemming)) {
                stockHabilidades.consumirHabilidad(nombreHabilidad);
                System.out.println("Habilidad '" + nombreHabilidad + "' activada.");
                return true;
            } else {
                System.out.println("No se pudo activar la habilidad '" + nombreHabilidad + "'.");
            }
        } else {
            System.out.println("No hay stock de la habilidad '" + nombreHabilidad + "' o es inválida.");
        }
        return false;
    }


    public void aumentarFrecuenciaSpawn() {
        if (frecuenciaSpawn < 99) frecuenciaSpawn++;
    }

    public void disminuirFrecuenciaSpawn() {
        if (frecuenciaSpawn > 50) frecuenciaSpawn--;
    }

    public static int getFrecuenciaSpawn() {return frecuenciaSpawn;}
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

