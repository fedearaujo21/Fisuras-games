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
    private Nivel nivel;
    private Thread hilo;
    private AudioPlayer musicaFondo;
    private int nivelNum = 1;
    private List<BotonHabilidad> botonesHabilidad = new ArrayList<>();
    private BotonHabilidad botonSeleccionado = null;
    private boolean esperandoClick;
    private int interframe = 16;

    public PanelLemmings(JFrame ventana, int setNivel){
        setPreferredSize(new Dimension(800,600));
        setFocusable(true);
        setBackground(Color.black);
        this.nivelNum = setNivel;
        //aca se cargan los niveles

        cargarNivel(nivelNum);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mx = e.getX();
                int my = e.getY();

                if (esperandoClick) {
                    esperandoClick = false;
                    if(nivel.getNivelAprobado()){
                    cargarNivel(nivelNum + 1);
                    }else{
                        cargarNivel(nivelNum);
                    }
                    return;
                }

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
            BufferedImage icono11Hab = ImageIO.read(new File("src/lemmings/recursos/acelerarIcono.png"));
            BufferedImage icono12Hab = ImageIO.read(new File("src/lemmings/recursos/iconoManolo.png"));
            BufferedImage icono13Hab = ImageIO.read(new File("src/lemmings/recursos/iconoManolo.png"));
            BufferedImage icono14Hab = ImageIO.read(new File("src/lemmings/recursos/iconoManolo.png"));

            int xInicial = 2;
            int yBoton = 321;
            int ancho = 42;
            int alto = 76;
            int espacio = 44;

            botonesHabilidad.add(new BotonHabilidad(xInicial + 0 * espacio, yBoton, ancho, alto, icono1Hab, "Bloqueador"));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 1 * espacio, yBoton, ancho, alto, iconoParacaidas, "Paracaidas"));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 2 * espacio, yBoton, ancho, alto, icono3Hab, "Tercera"));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 3 * espacio, yBoton, ancho, alto, icono4Hab, "Cuarta"));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 4 * espacio, yBoton, ancho, alto, icono5Hab, "Quinta"));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 5 * espacio, yBoton, ancho, alto, icono6Hab, "Sexta"));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 6 * espacio, yBoton, ancho, alto, icono7Hab, "Septima"));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 7 * espacio, yBoton, ancho, alto, iconoMinero, "Minero"));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 8 * espacio, yBoton, ancho, alto, icono9Hab, "Novena"));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 9 * espacio, yBoton, ancho, alto, icono10Hab, "Decima"));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 10 * espacio, yBoton, ancho, alto, icono11Hab, "acelerar"));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 11 * espacio, yBoton, ancho, alto, icono12Hab, "Doceava"));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 12 * espacio, yBoton, ancho, alto, icono13Hab, "DecimoTra"));
            botonesHabilidad.add(new BotonHabilidad(xInicial + 13 * espacio, yBoton, ancho, alto, icono14Hab, "DecimoCta"));


        } catch (IOException e) {
            e.printStackTrace();
        }


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
                default:
                    System.out.println("No hay más niveles.");
                    return;
            }

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
            if (!nivel.getNivelCompletado()) {
                nivel.actualizar();
            }

            repaint();

            if (nivel.getNivelCompletado() && !esperandoClick) {
                // El nivel se completó, mostramos mensaje y esperamos clic
                esperandoClick = true;
                nivel.setNivelCompletado(false);
                System.out.println("Nivel completado. Esperando clic para continuar...");
            }


            if (this.botonSeleccionado != null && this.botonSeleccionado.getNombre() == "acelerar")
                interframe = 6;
            else
                interframe = 16;

            try {
                Thread.sleep(interframe);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }



}
