package lemmings.modelo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PrimitiveIterator;

import lemmings.control.AudioPlayer;
import lemmings.modelo.Lemming;

import javax.imageio.ImageIO;

public class Nivel {
    private int nivelNum;
    private long tiempo;
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
    private AudioPlayer sonidoSalida = new AudioPlayer("/lemmings/recursos/SonidoSalida.wav");
    private AudioPlayer sonidoExplosion = new AudioPlayer("/lemmings/recursos/SonidoExplosion.wav");
    private boolean nivelCompletado = false;

    //private boolean puertaAbierta = false;
    private long tiempoInicio;
    private boolean lemmingsSpawneados = false;
    private int lemmingsSalvados = 0;
    private int lemmingsMuertos = 0;
    private static int frecuenciaSpawn; // menor = más rápido
    private long ultimoSpawnTime = 0;


    public Nivel(int nivelNum, String nombre, BufferedImage mapaImagen) {
        this.nivelNum = nivelNum;
        this.nombre = nombre;
        this.mapa = new Mapa(mapaImagen, COLOR_FONDO);
        this.lemmings = new ArrayList<>();
        this.stockHabilidades = new Stock();
        this.tiempoInicio = System.currentTimeMillis();

        cargarConfiguracionDesdeArchivo("/lemmings/recursos/nivel" + nivelNum + ".txt");
    }

    private void cargarConfiguracionDesdeArchivo(String path) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(path)))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.startsWith("tiempo=")) this.tiempo = Integer.parseInt(linea.split("=")[1]);
                else if (linea.startsWith("frecuenciaSpawn=")) frecuenciaSpawn = Integer.parseInt(linea.split("=")[1]);
                else if (linea.startsWith("cantidadLem=")) this.cantidadLem = Integer.parseInt(linea.split("=")[1]);
                else if (linea.startsWith("objetivoLemmings=")) this.objetivoLemmings = Integer.parseInt(linea.split("=")[1]);
                else if (linea.startsWith("entrada=")) {
                    String[] partes = linea.split("=")[1].split(",");
                    this.entrada = new Entrada(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]));
                } else if (linea.startsWith("salida=")) {
                    String[] partes = linea.split("=")[1].split(",");
                    this.salida = new Salida(
                            Integer.parseInt(partes[0]),
                            Integer.parseInt(partes[1]),
                            Integer.parseInt(partes[2]),
                            Integer.parseInt(partes[3]));
                } else if (linea.startsWith("habilidades=")) {
                    String[] habilidades = linea.split("=")[1].split(",");
                    for (String h : habilidades) {
                        String[] partes = h.split(":");
                        String nombreHab = partes[0];
                        int cantidad = Integer.parseInt(partes[1]);
                        stockHabilidades.añadirHabilidad(nombreHab, cantidad);
                    }
                }
            }
            this.cantidadSpawns = this.cantidadLem;
            mapa.limpiarArea(salida.getX(), salida.getY(), salida.getAncho(), salida.getAlto(), COLOR_FONDO);
            mapa.activarColisiones(false);
        } catch (IOException e) {
            e.printStackTrace();
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
                sonidoSalida.play();
                lemmingsSalvados++;
                cantidadSpawns--;
                cantidadLem--;
                System.out.println("¡Lemming salvado! Total salvados: " + lemmingsSalvados);
                it.remove();
                continue;
            }

            if (l.getHabilidadActiva() instanceof HabilidadAutoBomba autoBomba) {
                long tiempoActual = System.currentTimeMillis();
                if(tiempoActual - l.getTiempoInicioAutoBomba() >= 3800){
                    autoBomba.explotar(l.getX(),l.getY(),30);
                    sonidoExplosion.play();
                    lemmingsMuertos++;
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
                    lemmingsMuertos++;
                    it.remove();
                }
            }
            // Muere por caída (si no tiene paracaídas)
            if (l.getY() > 318) {
                lemmingsMuertos++;
                cantidadSpawns--;
                cantidadLem--;
                System.out.println("Un lemming ha muerto por caída");
                it.remove();
            }
            if (!(l.getHabilidadActiva() instanceof HabilidadParacaidas) && (l.getTicksEnAire() > 109 && l.getImpacto())){
                    lemmingsMuertos++;
                    cantidadSpawns--;
                    cantidadLem--;
                    System.out.println("Un lemming ha muerto por caída");
                    it.remove();
            }
        }
        if (tiempo < (ahora - tiempoInicio))
            nivelCompletado = true;
    }


    public void dibujar(Graphics g){
        long ahora = System.currentTimeMillis();
        mapa.dibujar(g);
        salida.dibujar(g);
        // DIBUJA LA ENTRADA (nube animada)
        entrada.dibujar(g);

        for (Lemming l : lemmings) {
            l.dibujar(g);
        }

        if (nivelCompletado) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int panelAncho = 400;
            int panelAlto = 260;
            int panelX = (mapa.getAncho() - panelAncho) / 2;
            int panelY = (mapa.getAlto() - panelAlto) / 2;

            // Fondo oscuro semitransparente
            g2d.setColor(new Color(0, 0, 0, 170));
            g2d.fillRect(0, 0, mapa.getAncho(), mapa.getAlto());

            // Cuadro del mensaje
            g2d.setColor(new Color(30, 30, 30, 220));
            g2d.fillRoundRect(panelX, panelY, panelAncho, panelAlto, 20, 20);
            g2d.setColor(Color.YELLOW);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(panelX, panelY, panelAncho, panelAlto, 20, 20);

            // Título y subtítulo
            String titulo = objetivoLemmings <= lemmingsSalvados && ahora - tiempoInicio < tiempo
                    ? "¡Felicitaciones!" : "¡Perdiste!";
            String subtitulo = "";

            if (titulo.equals("¡Felicitaciones!")) {
                nivelAprobado = true;
                subtitulo = "Nivel completado";
            } else {
                nivelAprobado = false;
                if (tiempo < (ahora - tiempoInicio))
                    subtitulo = "Se acabó el tiempo";
                else
                    subtitulo = "Necesitás salvar al menos: " + this.objetivoLemmings;
            }

            g2d.setFont(new Font("Arial", Font.BOLD, 32));
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(titulo, panelX + (panelAncho - fm.stringWidth(titulo)) / 2, panelY + 45);

            g2d.setFont(new Font("Arial", Font.PLAIN, 20));
            fm = g2d.getFontMetrics();
            g2d.drawString(subtitulo, panelX + (panelAncho - fm.stringWidth(subtitulo)) / 2, panelY + 75);

            // Lemmings salvados/muertos
            String s1 = "Lemmings salvados: " + this.lemmingsSalvados;
            String s2 = "Lemmings muertos: " + (this.lemmingsMuertos);
            g2d.drawString(s1, panelX + 30, panelY + 110);
            g2d.drawString(s2, panelX + 30, panelY + 135);

            // Imagen decorativa
            try {
                BufferedImage goku;
                if (nivelAprobado) {
                    goku = ImageIO.read(new File("src/lemmings/recursos/gokuFeliz.png"));
                } else {
                    goku = ImageIO.read(new File("src/lemmings/recursos/gokuTriste.png"));
                }

                int nuevoAncho = 100;
                int nuevoAlto = 100;

                Image gokuEscalado = goku.getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH);

                int imgX = panelX + (panelAncho - nuevoAncho) / 2;
                int imgY = panelY + panelAlto - nuevoAlto - 10;

                g2d.drawImage(gokuEscalado, imgX, imgY, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }




        //g.drawString("Mineros: " + stockHabilidades.getCantidad("Minero"), 10, 40);
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
    public int getLemmingsSalvados(){return this.lemmingsSalvados;}
    public int getObjetivoLemmings(){return this.objetivoLemmings;}
    public long getTiempo(){
        long ahora = System.currentTimeMillis();
        return (ahora - tiempoInicio) / 1000;
    }
    public long getTiempoLimite(){
        return (this.tiempo / (1000 * 60));
    }
    //public void pausar() { /* ... */ }
    //public void completarNVL() { /* ... */ }
}

