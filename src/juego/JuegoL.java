package juego;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;

public class JuegoL extends Frame implements Runnable{

    private final int width = 800, height = 600;
    private String rutaImagen = "src/Niveles/Nivel1.jpg";
    private boolean ejecutando;
    private Panel escenario = new Panel();
    private Image imagen;



    public JuegoL(){

        setTitle("Fisuras Lemmings");
        setSize(width, height);
        setResizable(false);
        setLayout(new BorderLayout());
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                ejecutando = false;
                dispose();
            }
        });

        PanelConImagen fondo = new PanelConImagen(rutaImagen);
        escenario.setSize(800,319);
        escenario.setLayout(null);
        escenario.add(fondo);
        this.add(BorderLayout.CENTER, escenario);

        ejecutando = true;
        Thread bucle = new Thread(this);
        bucle.run();
    }

    public void run(){
        while (ejecutando) {
            // en pong los graficos estaban calculados en otra clase
            //algo.actualizar();    // lógica de movimiento
            //algo.repaint();       // redibujar todo

            try {
                Thread.sleep(16);  // ~60 FPS
            } catch (InterruptedException e) {
                System.out.println("Error en el loop: " + e.getMessage());
            }
        }
    }
}

class PanelConImagen extends Panel {
    private Image imagen;

    public PanelConImagen(String rutaImagen) {
        try {
            imagen = ImageIO.read(new File(rutaImagen));
        } catch (IOException e) {
            System.out.println("Mongo DB: " + rutaImagen);
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (imagen != null) {
            // Dibuja la imagen ocupando todo el tamaño del panel
            g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
