package lemmings.modelo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lemmings.control.AudioPlayer;
import lemmings.control.DataManager;
import lemmings.vista.PanelLemmings;

import javax.imageio.ImageIO;
import javax.swing.*;

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
    private long tiempoInicio;
    //private boolean lemmingsSpawneados = false;
    private int lemmingsSalvados = 0;
    private int lemmingsMuertos = 0;
    private static int frecuenciaSpawn;
    private long ultimoSpawnTime = 0;
    private int puntos = 0;

    private JTextField campoNombre;
    private JButton botonGuardarRanking;
    private JButton botonContinuar;
    private List<JLabel> labelsRanking = new ArrayList<>();
    private static boolean rankingMostrado = false;


    public Nivel(NivelInfo info, BufferedImage mapaImagen) {
        this.nivelNum = info.getNumero();
        this.nombre = info.getNombre();
        this.tiempo = info.getTiempo();
        frecuenciaSpawn = info.getFrecuenciaSpawn();
        this.cantidadLem = info.getCantidadLem();
        this.objetivoLemmings = info.getObjetivoLemmings();
        this.entrada = info.getEntrada();
        this.salida = info.getSalida();
        this.stockHabilidades = info.getStock();
        this.mapa = new Mapa(mapaImagen, COLOR_FONDO);
        this.lemmings = new ArrayList<>();
        this.tiempoInicio = System.currentTimeMillis();
        this.cantidadSpawns = this.cantidadLem;

        // Limpiar salida
        int anchoLimpieza = Math.min(mapa.getAncho() - salida.getX(), salida.getAncho());
        int altoLimpieza = Math.min(mapa.getAlto() - salida.getY(), salida.getAlto());
        mapa.limpiarArea(salida.getX(), salida.getY(), anchoLimpieza, altoLimpieza, COLOR_FONDO);
        mapa.activarColisiones(false);
    }

    public void inicializarRankingUI(PanelLemmings panelJuego) {
        campoNombre = new JTextField(10);
        campoNombre.setBounds(300, 400, 150, 25);
        campoNombre.setVisible(false);
        panelJuego.add(campoNombre);

        botonGuardarRanking = new JButton("Guardar");
        botonGuardarRanking.setBounds(460, 400, 100, 25);
        botonGuardarRanking.setVisible(false);
        panelJuego.add(botonGuardarRanking);

        botonGuardarRanking.addActionListener(e -> {
            String nombre = campoNombre.getText().trim();
            if (!nombre.isEmpty()) {
                DataManager.insert("Lemmings" + Integer.toString(nivelNum), nombre, puntos);
                campoNombre.setVisible(false);
                botonGuardarRanking.setVisible(false);
                rankingMostrado = true;
                panelJuego.revalidate();
                panelJuego.repaint();

                mostrarRanking(panelJuego);
            }
        });
    }



    public void actualizar() {
        long ahora = System.currentTimeMillis();

        if (ahora - tiempoInicio < 500) {
            mapa.activarColisiones(false);
        } else {
            mapa.activarColisiones(true);
        }

        if (cantidadLem == 0) {
            nivelCompletado = true;
        } else if (lemmings.size() < cantidadSpawns) {
            int delayEntreSpawns = (int)((100 - frecuenciaSpawn) * 30);
            if (ahora - ultimoSpawnTime >= delayEntreSpawns) {
                lemmings.add(entrada.spawnear(mapa));
                ultimoSpawnTime = ahora;
            }
        }

        Iterator<Lemming> it = lemmings.iterator();
        while (it.hasNext()) {
            Lemming l = it.next();
            l.caminar(lemmings);

            if (salida.haAlcanzado(l)) {
                sonidoSalida.play();
                lemmingsSalvados++;
                cantidadSpawns--;
                cantidadLem--;
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
                    it.remove();
                }
            }
            if (nivelNum == 4) {
                boolean murioPorAgua = l.getX() >= 0 && l.getX() <= 570 && l.getY() >= 235 && l.getY() <= 300;
                if (murioPorAgua) {
                    cantidadSpawns--;
                    cantidadLem--;
                    lemmingsMuertos++;
                    it.remove();
                }
            }
            if (l.getY() > 318) {
                lemmingsMuertos++;
                cantidadSpawns--;
                cantidadLem--;
                it.remove();
            }
            if (!(l.getHabilidadActiva() instanceof HabilidadParacaidas) && (l.getTicksEnAire() > 109 && l.getImpacto())){
                lemmingsMuertos++;
                cantidadSpawns--;
                cantidadLem--;
                it.remove();
            }
        }
        if (tiempo < (ahora - tiempoInicio))
            nivelCompletado = true;
    }

    public void dibujar(Graphics g){
        mapa.dibujar(g);
        salida.dibujar(g);
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

            g2d.setColor(new Color(0, 0, 0, 170));
            g2d.fillRect(0, 0, mapa.getAncho(), mapa.getAlto());

            g2d.setColor(new Color(30, 30, 30, 220));
            g2d.fillRoundRect(panelX, panelY, panelAncho, panelAlto, 20, 20);
            g2d.setColor(Color.YELLOW);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(panelX, panelY, panelAncho, panelAlto, 20, 20);

            String titulo = objetivoLemmings <= lemmingsSalvados && (System.currentTimeMillis() - tiempoInicio) < tiempo
                    ? "¡Felicitaciones!" : "¡Perdiste!";
            String subtitulo = "";

            if (titulo.equals("¡Felicitaciones!")) {
                nivelAprobado = true;
                subtitulo = "Nivel completado";

                if (puntos == 0)
                    puntos = lemmingsSalvados * 1000 - (int)((System.currentTimeMillis() - tiempoInicio) / 10);

                if (!rankingMostrado) {
                    campoNombre.setVisible(true);
                    botonGuardarRanking.setVisible(true);
                    campoNombre.requestFocus();
                    rankingMostrado = true;
                }

            } else {
                nivelAprobado = false;
                if (tiempo < (System.currentTimeMillis() - tiempoInicio))
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

            String s1 = "Lemmings salvados: " + this.lemmingsSalvados;
            String s2 = "Lemmings muertos: " + this.lemmingsMuertos;
            g2d.drawString(s1, panelX + 30, panelY + 110);
            g2d.drawString(s2, panelX + 30, panelY + 135);

            try {
                BufferedImage goku = ImageIO.read(getClass().getResourceAsStream(
                        nivelAprobado ? "/lemmings/recursos/gokuFeliz.png" : "/lemmings/recursos/gokuTriste.png"
                ));
                Image gokuEscalado = goku.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                int imgX = panelX + (panelAncho - 100) / 2;
                int imgY = panelY + panelAlto - 100 - 10;
                g2d.drawImage(gokuEscalado, imgX, imgY, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean asignarHabilidad(Lemming lemming, String nombreHabilidad) {
        Habilidad habilidad = null;

        if (lemming.getHabilidadActiva() != null) {
            if (nombreHabilidad.equals("AutoBomba")) {
                if (!(lemming.getHabilidadActiva() instanceof HabilidadBloqueador)) {
                    return false;
                }
            } else {
                return false;
            }
        }

        switch (nombreHabilidad) {
            case "Minero": habilidad = new HabilidadMinero(this.mapa); break;
            case "Paracaidas": habilidad = new HabilidadParacaidas(); break;
            case "Bloqueador": habilidad = new HabilidadBloqueador(); break;
            case "AutoBomba": habilidad = new HabilidadAutoBomba(this.mapa); break;
            case "Constructor": habilidad = new HabilidadConstructor(this.mapa); break;
            case "KameHameHa": habilidad = new HabilidadKameHameHa(this.mapa); break;
        }

        if (habilidad != null && stockHabilidades.getCantidad(nombreHabilidad) > 0) {
            if (habilidad.activar(lemming)) {
                stockHabilidades.consumirHabilidad(nombreHabilidad);
                return true;
            }
        }
        return false;
    }

    private void mostrarRanking(PanelLemmings panelJuego) {
        // Fondo semitransparente
        JLabel fondoRanking = new JLabel();
        fondoRanking.setOpaque(true);
        fondoRanking.setBackground(new Color(0, 0, 0, 180));  // Negro con alfa 150
        fondoRanking.setBounds(280, 10, 350, 350);  // Ajustá el tamaño según lo que necesites
        panelJuego.add(fondoRanking);
        fondoRanking.setVisible(true);

        // Traer el fondo al frente antes de los labels
        //panelJuego.setComponentZOrder(fondoRanking, panelJuego.getComponentCount() - 1);

        List<String> ranking = DataManager.getRanking("Lemmings" + nivelNum);
        int y = 20;
        for (String entry : ranking) {
            JLabel label = new JLabel(entry);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Verdana", Font.BOLD | Font.ITALIC, 25));
            label.setBounds(300, y, 300, 50);
            panelJuego.add(label);
            labelsRanking.add(label);
            label.setVisible(true);
            y += 30;
        }

        panelJuego.add(fondoRanking);
        botonContinuar = new JButton("Continuar");
        botonContinuar.setBounds(460, 400, 100, 25);
        botonContinuar.setVisible(true);
        panelJuego.add(botonContinuar);

        botonContinuar.addActionListener(e -> {
            botonContinuar.setVisible(false);
            fondoRanking.setVisible(false);
            for (JLabel i : labelsRanking) {
                i.setVisible(false);
            }
            rankingMostrado = true;
            panelJuego.setEstadoEsperando_Click();
            panelJuego.repaint();
            panelJuego.revalidate();
        });

        panelJuego.repaint();
        panelJuego.revalidate();
    }

    public static void reiniciarRankingMostrado() {rankingMostrado = false;}
    public void aumentarFrecuenciaSpawn() { if (frecuenciaSpawn < 99) frecuenciaSpawn++; }
    public void disminuirFrecuenciaSpawn() { if (frecuenciaSpawn > 50) frecuenciaSpawn--; }

    public static int getFrecuenciaSpawn() { return frecuenciaSpawn; }
    public boolean getNivelAprobado() { return this.nivelAprobado; }
    public Stock getStockHabilidades() { return stockHabilidades; }
    public List<Lemming> getLemmings() { return lemmings; }
    public Mapa getMapa() { return mapa; }
    public boolean getNivelCompletado() { return this.nivelCompletado; }
    public void setNivelCompletado(boolean valor) { this.nivelCompletado = valor; }
    public int getLemmingsSalvados() { return this.lemmingsSalvados; }
    public int getObjetivoLemmings() { return this.objetivoLemmings; }
    public int getCantidadLem() { return this.cantidadLem; }
    public long getTiempo() { return (System.currentTimeMillis() - tiempoInicio) / 1000; }
    public long getTiempoLimite() { return this.tiempo / (1000 * 60); }
}


