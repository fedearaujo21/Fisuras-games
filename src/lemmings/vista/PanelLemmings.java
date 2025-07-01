package lemmings.vista;

import lemmings.control.AudioPlayer;
import lemmings.control.Escalador;
import lemmings.control.NivelDAO;
import lemmings.modelo.Lemming;
import lemmings.modelo.Nivel;
import lemmings.modelo.BotonHabilidad;
import lemmings.modelo.*;
import main.Juego;
import main.LanzadorLemming;
import main.MenuPrincipal;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
//Para detectar coordenadas
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;


public class PanelLemmings extends Juego{
    private Nivel nivel;
    //private Thread hilo;
    private static AudioPlayer musicaFondo;
    private int nivelNum = 1;
    private List<BotonHabilidad> botonesHabilidad = new ArrayList<>();
    private BotonHabilidad botonSeleccionado = null;
//    private boolean esperandoClick;
    private BotonHabilidad botonPresionadoMomentaneo = null;
    private long tiempoBotonPresionado = 0;
    private Rectangle botonHome;
    private Rectangle botonConfig;
    private Rectangle botonReiniciar;
    private BufferedImage iconoHome, iconoConfig, iconoReiniciar;
    //private double escalaX;
    //private double escalaY;
    //private boolean juegoActivo = false;

    // NUEVO ENUM para el estado del juego
    private enum EstadoJuego { JUGANDO, ESPERANDO_CLICK, PAUSA }
    private EstadoJuego estadoJuego = EstadoJuego.JUGANDO;

    public PanelLemmings(int setNivel, JFrame ventana){
        super("Lemmings");
        setVentana(ventana);

        setPreferredSize(new Dimension(800,600));
        setFocusable(true);
        setBackground(Color.black);
        this.nivelNum = setNivel;

        Dimension tamanoReal;
        if (Config.pantallaCompleta) {
            tamanoReal = Toolkit.getDefaultToolkit().getScreenSize();
        } else {
            tamanoReal = new Dimension(800, 600);
        }
        Escalador.inicializarEscalas(tamanoReal.width, tamanoReal.height);


        cargarNivel(nivelNum);


        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mx = Escalador.desescalarX(e.getX());
                int my = Escalador.desescalarY(e.getY());

                // <<< CAMBIO ACÁ >>>
                if (estadoJuego == EstadoJuego.ESPERANDO_CLICK) {
                    if (nivel.getNivelAprobado()) {
                        cargarNivel(nivelNum + 1);
                    } else {
                        cargarNivel(nivelNum);
                    }
                    estadoJuego = EstadoJuego.JUGANDO;
                    nivel.setNivelCompletado(false);
                    return;
                }

                if (botonHome.contains(mx, my)) {
                    volverAlMenu();
                    detener();
                    return;
                }
                if (botonReiniciar.contains(mx, my)) {
                    reiniciarNivel();
                    return;
                }
                if (botonConfig.contains(mx, my)) {
                    volverAConfiguracion();
                    return;
                }

                for (BotonHabilidad boton : botonesHabilidad) {
                    if (boton.contienePunto(mx, my)) {
                        String nombre = boton.getNombre();

                        if (nombre.equals("Mas")) {
                            nivel.aumentarFrecuenciaSpawn();
                            repaint();
                            return;
                        } else if (nombre.equals("Menos")) {
                            nivel.disminuirFrecuenciaSpawn();
                            repaint();
                            return;
                        }

                        if (nombre.equals("acelerar")) {
                            if (Config.interframe == 16) {
                                Config.interframe = 6;
                                System.out.println("Velocidad x2 activada");
                            } else {
                                Config.interframe = 16;
                                System.out.println("Velocidad normal");
                            }
                            return;
                        }


                        if (nombre.equals("Play")) {
                            Config.interframe = 16;
                            estadoJuego = EstadoJuego.JUGANDO;
                            return;
                        }

                        if (nombre.equals("Pausa")) {
                            estadoJuego = EstadoJuego.PAUSA;
                            return;
                        }

                        // Si es una habilidad válida, seleccionar el botón
                        botonSeleccionado = boton;
                        System.out.println("Seleccionaste habilidad: " + nombre);
                        return;
                    }
                }

                // 2. Si no hizo clic en un botón, verificar si hizo clic en un lemming
                for (Lemming lemming : nivel.getLemmings()) {
                    if (mx >= lemming.getX() && mx <= lemming.getX() + lemming.getLemmingWidth() &&
                            my >= lemming.getY() && my <= lemming.getY() + lemming.getLemmingHeight()) {

                        if (e.getButton() == MouseEvent.BUTTON1) {
                            // Elegimos la habilidad según el botón seleccionado
                            if (botonSeleccionado != null) {
                                String habilidad = botonSeleccionado.getNombre();
                                nivel.asignarHabilidad(lemming, habilidad);
                                //lemming.setFueUsado(true);
                                System.out.println("Asignada habilidad: " + habilidad);
                            }
                            break;
                        }
                    }
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    volverAlMenu();
                    detener();
                } else if (e.getKeyCode() == KeyEvent.VK_R) {
                    reiniciarNivel();
                } else if (e.getKeyCode() == KeyEvent.VK_C) {
                    volverAConfiguracion();
                }
            }
        });
        setFocusable(true);
        requestFocusInWindow();

    }
    public static void detenerMusica() {
        if (musicaFondo != null) {
            musicaFondo.close();
            musicaFondo = null;
        }
    }

    private void volverAConfiguracion() {
        if (musicaFondo != null) {
            musicaFondo.close(); // Detiene la música actual
        }

        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.dispose(); // Cierra la ventana actual

        SwingUtilities.invokeLater(() -> {
            JFrame nuevaVentana = new JFrame("Fisuras Games");
            nuevaVentana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            nuevaVentana.setSize(800, 600);
            nuevaVentana.setResizable(false);
            nuevaVentana.setLocationRelativeTo(null);
            LanzadorLemming.iniciar(nuevaVentana); // Volver a la configuración inicial
        });
    }







    private void reiniciarNivel() {
        cargarNivel(nivelNum);
    }


    public void cargarNivel(int numero) {
        try {
            detenerMusica();

            NivelInfo info = NivelDAO.obtenerNivelPorNumero(numero);
            if (info == null) {
                System.out.println("Nivel no encontrado en la base de datos.");
                return;
            }

            nivelNum = info.getNumero();

            BufferedImage imagen = ImageIO.read(getClass().getResourceAsStream(info.getRutaImagen()));
            nivel = new Nivel(info, imagen);

            musicaFondo = new AudioPlayer(info.getRutaMusica());

            nivel.getLemmings().clear();
            inicializarBotonesHabilidad();

            if (musicaFondo != null && !Config.mute) {
                musicaFondo.setVolume(Config.volumen);
                musicaFondo.loop(); // Reproducir música en bucle
            }

            iniciar();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void inicializarBotonesHabilidad() {
        botonesHabilidad.clear();
        int xInicial = 2;
        int yBoton = 321;
        int ancho = 42;
        int alto = 76;
        int espacio = 44;


        try {
            botonesHabilidad.add(new BotonHabilidad(xInicial + 0 * espacio, yBoton, ancho, alto, ImageIO.read(getClass().getResourceAsStream("/lemmings/recursos/iconoAutoBomba.png")), "AutoBomba", nivel.getStockHabilidades()));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 1 * espacio, yBoton, ancho, alto, ImageIO.read(getClass().getResourceAsStream("/lemmings/recursos/iconoParacaidas.png")), "Paracaidas", nivel.getStockHabilidades()));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 2 * espacio, yBoton, ancho, alto, ImageIO.read(getClass().getResourceAsStream("/lemmings/recursos/iconoConstructor.png")), "Constructor", nivel.getStockHabilidades()));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 3 * espacio, yBoton, ancho, alto, ImageIO.read(getClass().getResourceAsStream("/lemmings/recursos/iconoBloqueador.png")), "Bloqueador", nivel.getStockHabilidades()));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 4 * espacio, yBoton, ancho, alto, ImageIO.read(getClass().getResourceAsStream("/lemmings/recursos/iconoKameHameHa.png")), "KameHameHa", nivel.getStockHabilidades()));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 5 * espacio, yBoton, ancho, alto, ImageIO.read(getClass().getResourceAsStream("/lemmings/recursos/iconoMinero.png")), "Minero", nivel.getStockHabilidades()));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 6 * espacio, yBoton, ancho, alto, ImageIO.read(getClass().getResourceAsStream("/lemmings/recursos/iconoMas.png")), "Mas", nivel.getStockHabilidades()));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 7 * espacio, yBoton, ancho, alto, ImageIO.read(getClass().getResourceAsStream("/lemmings/recursos/iconoMenos.png")), "Menos", nivel.getStockHabilidades()));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 8 * espacio, yBoton, ancho, alto, ImageIO.read(getClass().getResourceAsStream("/lemmings/recursos/iconoAcelerar.png")), "acelerar", nivel.getStockHabilidades()));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 9 * espacio, yBoton, ancho, alto, ImageIO.read(getClass().getResourceAsStream("/lemmings/recursos/iconoPausa.png")), "Pausa", nivel.getStockHabilidades()));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 10 * espacio, yBoton, ancho, alto, ImageIO.read(getClass().getResourceAsStream("/lemmings/recursos/iconoPlay.png")), "Play", nivel.getStockHabilidades()));
            iconoHome = ImageIO.read(new File("src/lemmings/recursos/iconoHome.png"));
            iconoReiniciar = ImageIO.read(new File("src/lemmings/recursos/iconoReiniciar.png"));
            iconoConfig = ImageIO.read(new File("src/lemmings/recursos/iconoConfigurar.png"));

            botonHome = new Rectangle(10, 10, 32, 32);
            botonConfig = new Rectangle(50, 10, 32, 32);
            botonReiniciar = new Rectangle(90, 10, 32, 32);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actualizar() {
            if (estadoJuego == EstadoJuego.JUGANDO && !nivel.getNivelCompletado()) {
                nivel.actualizar();
            }

            repaint();

            if (nivel.getNivelCompletado() && estadoJuego == EstadoJuego.JUGANDO) {
                estadoJuego = EstadoJuego.ESPERANDO_CLICK;
                System.out.println("Nivel completado. Esperando clic para continuar...");
            }

            while (this.botonSeleccionado != null && estadoJuego == EstadoJuego.PAUSA) {
                try {
                    Thread.sleep(Config.interframe);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    detener();
                }
            }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create(); // Copia segura para transformaciones

        // Aplicar escalado general definido en Escalador (ya lo seteaste en el constructor)
        g2d.scale(Escalador.escalaX, Escalador.escalaY);

        // Dibujo del nivel (mapa, lemmings, entrada, salida)
        if (nivel != null) {
            nivel.dibujar(g2d);
        } else {
            g2d.setColor(Color.RED);
            g2d.drawString("Nivel no cargado", 350, 300);
        }

        // Dibujo del fondo gris del panel inferior
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 320, 800, 80);

        // Dibujo de botones de habilidad
        for (BotonHabilidad boton : botonesHabilidad) {
            boolean seleccionado = boton == botonSeleccionado ||
                    (boton == botonPresionadoMomentaneo && System.currentTimeMillis() - tiempoBotonPresionado < 150);
            boton.dibujar(g2d, seleccionado);
        }

        // Iconos (home, reiniciar, config)
        if (iconoHome != null)
            g2d.drawImage(iconoHome, botonHome.x, botonHome.y, botonHome.width, botonHome.height, null);
        if (iconoReiniciar != null)
            g2d.drawImage(iconoReiniciar, botonReiniciar.x, botonReiniciar.y, botonReiniciar.width, botonReiniciar.height, null);
        if (iconoConfig != null)
            g2d.drawImage(iconoConfig, botonConfig.x, botonConfig.y, botonConfig.width, botonConfig.height, null);

        // Limpiar selección momentánea si ya pasó el tiempo
        if (System.currentTimeMillis() - tiempoBotonPresionado >= 150) {
            botonPresionadoMomentaneo = null;
        }

        // HUD
        g2d.setColor(new Color(0, 0, 0, 120)); // fondo negro transparente
        g2d.fillRoundRect(580, 330, 210, 50, 15, 15);

        g2d.setColor(Color.YELLOW);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(580, 330, 210, 50, 15, 15);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Salvar: " + nivel.getObjetivoLemmings() + "/" + nivel.getCantidadLem(), 590, 350);

        long tiempoActual = nivel.getTiempo() / 60;
        long segundos = nivel.getTiempo() % 60;
        long tiempoMax = nivel.getTiempoLimite();
        g2d.drawString(String.format("Tiempo: %02d:%02d / %d min", tiempoActual, segundos, tiempoMax), 590, 370);

        g2d.dispose(); // Liberar recursos
    }

}

