package lemmings.vista;

import lemmings.modelo.Lemming;
import lemmings.modelo.Mapa;
import lemmings.modelo.Nivel;

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


public class PanelLemmings extends JPanel implements Runnable{
    private Mapa mapa;
    private Nivel nivel;
    private Thread hilo;

    public PanelLemmings(JFrame ventana){
        setPreferredSize(new Dimension(800,600));
        setFocusable(true);
        setBackground(Color.black);
        //aca se cargan los niveles

        try {
            InputStream is = getClass().getResourceAsStream("/lemmings/recursos/Nivel1.png");
            BufferedImage img = ImageIO.read(is);
            //mapa = new Mapa(img);
            nivel = new Nivel(1, "Nivel 1", img);
        } catch (IOException e){
            e.printStackTrace();
        }

        Thread hilo = new Thread(this);
        hilo.start();
        //Para detectar coordenadas
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (Lemming lemming : nivel.getLemmings()) {
                    // Si el clic está dentro del área del Lemming
                    if (e.getX() >= lemming.getX() && e.getX() <= lemming.getX() + lemming.getLemmingWidth() &&
                            e.getY() >= lemming.getY() && e.getY() <= lemming.getY() + lemming.getLemmingHeight()) {
                        // Asignamos la habilidad "Minero" si se hace clic izquierdo
                        if (e.getButton() == MouseEvent.BUTTON1) { // Clic izquierdo
                            nivel.asignarHabilidad(lemming, "Minero");
                            break;
                        }
                    }
                }
                //repaint();
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                System.out.println("Coordenadas del click: (" + x + ", " + y + ")");
            }
        });


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

    }

    @Override
    public void run(){
        while(true){
            nivel.actualizar();
            repaint();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }



}
