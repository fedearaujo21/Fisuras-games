package lemmings.vista;

import lemmings.control.AudioPlayer;
import lemmings.modelo.Lemming;
import lemmings.modelo.Nivel;
import lemmings.modelo.BotonHabilidad;
import lemmings.modelo.*;
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


public class PanelLemmings extends JPanel implements Runnable{
    private Nivel nivel;
    private Thread hilo;
    private AudioPlayer musicaFondo;
    private int nivelNum = 1;
    private List<BotonHabilidad> botonesHabilidad = new ArrayList<>();
    private BotonHabilidad botonSeleccionado = null;
//    private boolean esperandoClick;
    private int interframe = 16;
    private BotonHabilidad botonPresionadoMomentaneo = null;
    private long tiempoBotonPresionado = 0;
    private Rectangle botonHome;
    private Rectangle botonConfig;
    private Rectangle botonReiniciar;
    private BufferedImage iconoHome, iconoConfig, iconoReiniciar;

    // NUEVO ENUM para el estado del juego
    private enum EstadoJuego { JUGANDO, ESPERANDO_CLICK }
    private EstadoJuego estadoJuego = EstadoJuego.JUGANDO;

    public PanelLemmings(JFrame ventana, int setNivel){
        setPreferredSize(new Dimension(800,600));
        setFocusable(true);
        setBackground(Color.black);
        this.nivelNum = setNivel;
        //aca se cargan los niveles

        cargarNivel(nivelNum);

        if (musicaFondo != null) {
            musicaFondo.loop(); // Empieza a reproducir la música en bucle
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mx = e.getX();
                int my = e.getY();

                // <<< CAMBIO ACÁ >>>
                if (estadoJuego == EstadoJuego.ESPERANDO_CLICK) {
                    if (nivel.getNivelAprobado()) {
                        cargarNivel(nivelNum + 1);
                    } else {
                        cargarNivel(nivelNum);
                    }
                    estadoJuego = EstadoJuego.JUGANDO;
                    nivel.setNivelCompletado(false);  // 👈 Ahora sí, reseteamos acá
                    return;
                }

                if (botonHome.contains(mx, my)) {
                    volverAlMenuPrincipal();
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
                    volverAlMenuPrincipal();
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

    private void volverAlMenuPrincipal() {
        if (musicaFondo != null) {
            musicaFondo.close(); // Detener la música si está sonando
        }

        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.dispose(); // Cerrar la ventana actual del juego

        // Crear una nueva ventana con el menú principal, bien configurada
        SwingUtilities.invokeLater(() -> {
            JFrame nuevaVentana = new JFrame("Fisuras");
            nuevaVentana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            nuevaVentana.setContentPane(new MenuPrincipal(nuevaVentana));
            nuevaVentana.setSize(800, 600);
            nuevaVentana.setResizable(false);
            nuevaVentana.setLocationRelativeTo(null);
            nuevaVentana.setVisible(true);
        });
    }




    private void reiniciarNivel() {
        cargarNivel(nivelNum);
    }


    public void cargarNivel(int numero){
        try {
            if (musicaFondo != null) {
                musicaFondo.close();
            }

            switch (numero) {
                case 1:
                    nivelNum = 1;
                    nivel = new Nivel(1, "Nivel 1", ImageIO.read(getClass().getResourceAsStream("/lemmings/recursos/Nivel1.png")));
                    musicaFondo = new AudioPlayer("/lemmings/recursos/MusicaNivel1.wav");
                    break;
                case 2:
                    nivelNum = 2;
                    nivel = new Nivel(2, "Nivel 2", ImageIO.read(getClass().getResourceAsStream("/lemmings/recursos/Nivel2.png")));
                    musicaFondo = new AudioPlayer("/lemmings/recursos/MusicaNivel2.wav");
                    break;
                case 3:
                    nivelNum = 3;
                    nivel = new Nivel(3,"Nivel 3",ImageIO.read(getClass().getResourceAsStream("/lemmings/recursos/Nivel3.png")));
                    musicaFondo = new AudioPlayer("/lemmings/recursos/MusicaNivel3.wav");
                    break;
                case 4:
                    nivelNum = 4;
                    nivel = new Nivel(4,"Nivel 4",ImageIO.read(getClass().getResourceAsStream("/lemmings/recursos/Nivel4.png")));
                    musicaFondo = new AudioPlayer("/lemmings/recursos/MusicaNivel4.wav");
                    break;
                default:
                    System.out.println("No hay más niveles.");
                    return;
            }

            inicializarBotonesHabilidad();

            if (musicaFondo != null && !Config.mute) {
                musicaFondo.setVolume(Config.volumen);
                System.out.println(Config.volumen);
                musicaFondo.loop(); // Empieza a reproducir la música en bucle
            }

            if (hilo == null || !hilo.isAlive()) {
                hilo = new Thread(this);
                hilo.start();
            }

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
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(nivel != null){
            nivel.dibujar(g);
        } else {
            g.setColor(Color.RED);
            g.drawString("Nivel no cargado", 350, 300);
        }
        // Fondo del panel de habilidades
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 320, getWidth(), 80);  // ajustá la altura si querés

        // Luego dibujás los botones (ya lo tenés):
        for (BotonHabilidad boton : botonesHabilidad) {
            boolean seleccionado = boton == botonSeleccionado ||
                    (boton == botonPresionadoMomentaneo && System.currentTimeMillis() - tiempoBotonPresionado < 150);
            boton.dibujar(g, seleccionado);
        }
        if (iconoHome != null) g.drawImage(iconoHome, botonHome.x, botonHome.y, botonHome.width, botonHome.height, null);
        if (iconoReiniciar != null) g.drawImage(iconoReiniciar, botonReiniciar.x, botonReiniciar.y, botonReiniciar.width, botonReiniciar.height, null);
        if (iconoConfig != null) g.drawImage(iconoConfig, botonConfig.x, botonConfig.y, botonConfig.width, botonConfig.height, null);

        if (System.currentTimeMillis() - tiempoBotonPresionado >= 150) {
            botonPresionadoMomentaneo = null;
        }
        // Fondo suavizado
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0, 0, 0, 120)); // negro semi-transparente
        g2d.fillRoundRect(getWidth() - 220, 330, 210, 50, 15, 15); // fondo con bordes redondeados

        g2d.setColor(Color.YELLOW);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(getWidth() - 220, 330, 210, 50, 15, 15);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Salvar: " + nivel.getObjetivoLemmings() + "/" + nivel.getCantidadLem(), getWidth() - 210, 350);

        long tiempoActual = nivel.getTiempo() / 60;
        long segundos = nivel.getTiempo() % 60;
        long tiempoMax = nivel.getTiempoLimite();
        g2d.drawString(String.format("Tiempo: %02d:%02d / %d min", tiempoActual, segundos, tiempoMax), getWidth() - 210, 370);
    }


    @Override
    public void run() {
        while (true) {
            if (estadoJuego == EstadoJuego.JUGANDO && !nivel.getNivelCompletado()) {
                nivel.actualizar();
            }

            repaint();

            // Cuando se completa el nivel por primera vez, cambiamos al modo espera de clic
            if (nivel.getNivelCompletado() && estadoJuego == EstadoJuego.JUGANDO) {
                estadoJuego = EstadoJuego.ESPERANDO_CLICK;
                // NO ponemos nivel.setNivelCompletado(false);
                System.out.println("Nivel completado. Esperando clic para continuar...");
            }

            if (this.botonSeleccionado != null && "acelerar".equals(this.botonSeleccionado.getNombre()))
                interframe = 6;
            else
                interframe = 16;

            try {
                Thread.sleep(interframe);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            while (this.botonSeleccionado != null && "Pausa".equals(this.botonSeleccionado.getNombre())) {
                try {
                    Thread.sleep(interframe);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
