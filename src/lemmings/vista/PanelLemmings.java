package lemmings.vista;

import lemmings.control.AudioPlayer;
import lemmings.modelo.*;
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
    private Mapa mapa;
    private Nivel nivel;
    private Thread hilo;
    private boolean alertaMostrada = false;
    private AudioPlayer musicaFondo;
    private int nivelNum = 1;
    private List<BotonHabilidad> botonesHabilidad = new ArrayList<>();
    private BotonHabilidad botonSeleccionado = null;


    public PanelLemmings(JFrame ventana, int setNivel){
        setPreferredSize(new Dimension(800,600));
        setFocusable(true);
        setBackground(Color.black);
        this.nivelNum = setNivel;
        //aca se cargan los niveles
        try {
            System.out.println("/lemmings/recursos/Nivel" + nivelNum + ".png");
            InputStream is = getClass().getResourceAsStream("/lemmings/recursos/Nivel" + nivelNum + ".png");
            BufferedImage img = ImageIO.read(is);
            musicaFondo = new AudioPlayer("/lemmings/recursos/MusicaNivel"+ nivelNum + ".wav");
            //mapa = new Mapa(img);
            nivel = new Nivel(1, "Nivel " + nivel, img);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread hilo = new Thread(this);
        hilo.start();

        if (musicaFondo != null && !Config.mute) {
            musicaFondo.setVolume(Config.volumen);
            System.out.println(Config.volumen);
            musicaFondo.loop(); // Empieza a reproducir la música en bucle
        }


        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mx = e.getX();
                int my = e.getY();

                // 1. Primero: ¿Hizo clic en algún botón?
                for (BotonHabilidad boton : botonesHabilidad) {
                    if (boton.contienePunto(mx, my)) {
                        botonSeleccionado = boton;
                        System.out.println("Seleccionaste habilidad: " + boton.getNombre());
                        return;
                    }
                }

                // 2. Si no hizo clic en un botón, verificar si hizo clic en un lemming
                for (Lemming lemming : nivel.getLemmings()) {
                    if (mx >= lemming.getX() && mx <= lemming.getX() + lemming.getLemmingWidth() &&
                            my >= lemming.getY() && my <= lemming.getY() + lemming.getLemmingHeight()) {

                        if (e.getButton() == MouseEvent.BUTTON1 && !lemming.getFueUsado()) {
                            // Elegimos la habilidad según el botón seleccionado
                            if (botonSeleccionado != null) {
                                String habilidad = botonSeleccionado.getNombre();
                                nivel.asignarHabilidad(lemming, habilidad);
                                lemming.setFueUsado(true);
                                System.out.println("Asignada habilidad: " + habilidad);
                            }
                            break;
                        }
                    }
                }
            }
        });


        try {
            // ACA HAY QUE SEGUIR AGREGANDO LAS IMAGENES DE LAS HABILIDADES
            BufferedImage icono1Hab = ImageIO.read(new File("src/lemmings/recursos/iconoManolo.png"));
            BufferedImage iconoParacaidas = ImageIO.read(new File("src/lemmings/recursos/iconoParacaidas.png"));
            BufferedImage icono3Hab = ImageIO.read(new File("src/lemmings/recursos/iconoManolo.png"));
            BufferedImage icono4Hab = ImageIO.read(new File("src/lemmings/recursos/iconoManolo.png"));
            BufferedImage icono5Hab = ImageIO.read(new File("src/lemmings/recursos/iconoManolo.png"));
            BufferedImage icono6Hab = ImageIO.read(new File("src/lemmings/recursos/iconoManolo.png"));
            BufferedImage icono7Hab = ImageIO.read(new File("src/lemmings/recursos/iconoManolo.png"));
            BufferedImage iconoMinero = ImageIO.read(new File("src/lemmings/recursos/iconoMinero.png"));
            BufferedImage icono9Hab = ImageIO.read(new File("src/lemmings/recursos/iconoManolo.png"));
            BufferedImage icono10Hab = ImageIO.read(new File("src/lemmings/recursos/iconoManolo.png"));
            BufferedImage icono11Hab = ImageIO.read(new File("src/lemmings/recursos/iconoManolo.png"));
            BufferedImage icono12Hab = ImageIO.read(new File("src/lemmings/recursos/iconoManolo.png"));
            BufferedImage icono13Hab = ImageIO.read(new File("src/lemmings/recursos/iconoManolo.png"));
            BufferedImage icono14Hab = ImageIO.read(new File("src/lemmings/recursos/iconoManolo.png"));

            int xInicial = 2;
            int yBoton = 321;
            int ancho = 42;
            int alto = 76;
            int espacio = 44;

            botonesHabilidad.add(new BotonHabilidad(xInicial + 0 * espacio, yBoton, ancho, alto, icono1Hab, "Primera"));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 1 * espacio, yBoton, ancho, alto, iconoParacaidas, "Paracaidas"));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 2 * espacio, yBoton, ancho, alto, icono3Hab, "Tercera"));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 3 * espacio, yBoton, ancho, alto, icono4Hab, "Cuarta"));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 4 * espacio, yBoton, ancho, alto, icono5Hab, "Quinta"));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 5 * espacio, yBoton, ancho, alto, icono6Hab, "Sexta"));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 6 * espacio, yBoton, ancho, alto, icono7Hab, "Septima"));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 7 * espacio, yBoton, ancho, alto, iconoMinero, "Minero"));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 8 * espacio, yBoton, ancho, alto, icono9Hab, "Novena"));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 9 * espacio, yBoton, ancho, alto, icono10Hab, "Decima"));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 10 * espacio, yBoton, ancho, alto, icono11Hab, "Onceava"));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 11 * espacio, yBoton, ancho, alto, icono12Hab, "Doceava"));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 12 * espacio, yBoton, ancho, alto, icono13Hab, "DecimoTra"));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 13 * espacio, yBoton, ancho, alto, icono14Hab, "DecimoCta"));


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
            boton.dibujar(g, boton == botonSeleccionado);
        }
    }

    @Override
    public void run(){
        while(true){
            nivel.actualizar();
            repaint();
            if (nivel.getNivelCompletado() && !alertaMostrada) {
                alertaMostrada = true;
                if (musicaFondo != null) {
                    musicaFondo.stop();
                    musicaFondo.close(); // Liberar recursos
                }
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(PanelLemmings.this,
                            "¡Todos los Lemmings salvados!\nNivel Completado.",
                            "¡Victoria!",
                            JOptionPane.INFORMATION_MESSAGE);
                    // añadir lógica para cargar el siguiente nivel o mostrar un menú.
                });

            }
            try {
                Thread.sleep(16);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }



}
