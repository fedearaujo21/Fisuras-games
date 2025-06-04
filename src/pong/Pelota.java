package pong;

import java.awt.*;
import java.io.IOException;
import java.util.Random;
import javax.sound.sampled.*;
import java.io.File;
import java.util.Map;

public class Pelota {
    private int x, y;
    private int tamaño = 12;
    private int velocidadX = 4, velocidadY = 4;
    private final int factor = 5; // margen de desvío
    private final Map<String, String> sonidoParedes = Map.of(
            "original","src/pong/recursos/Boing1.wav",
            "techno","src/pong/recursos/Boing2.wav",
            "8bit","src/pong/recursos/Boing3.wav"
    );
    // Map es como un HasMap pero inmutable, pero como lo uso para guardar nombres no voy a necesitar mutabilidad
    private final Map<String, String> sonidoPaletas = Map.of(
            "original","src/pong/recursos/Pared1.wav",
            "techno","src/pong/recursos/Pared2.wav",
            "8bit","src/pong/recursos/Pared3.wav"
    );
    private Clip pared;
    private Clip paleta;
    private Choque choque = new Choque();


    public Pelota(int x, int y) {
        this.x = x;
        this.y = y;
        direccionAleatoria();


        //cargo sonido de las paredes
        try {
            File archivoSonido = new File(sonidoParedes.get(Config.pistaMusical));
            //System.out.println("¿Existe archivo? " + archivoSonido.exists()+ Config.pistaMusical);
            //File archivoSonido = new File("../Sonidos/Boing1.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(archivoSonido);

            pared = AudioSystem.getClip();
            pared.open(audioStream);
        } catch(UnsupportedAudioFileException | LineUnavailableException |IOException e){
            e.printStackTrace();
        }

        // cargo sonido para las paletas
        try {
            File archivoSonido = new File(sonidoPaletas.get(Config.pistaMusical));
            //System.out.println("¿Existe archivo? " + archivoSonido.exists() + Config.pistaMusical);
            //File archivoSonido = new File("../Sonidos/Boing1.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(archivoSonido);

            paleta = AudioSystem.getClip();
            paleta.open(audioStream);
        } catch(UnsupportedAudioFileException | LineUnavailableException |IOException e){
            e.printStackTrace();
        }
        //pared.start();
    }

    public void actualizar(Paleta j1, Paleta j2) {
        x += velocidadX;
        y += velocidadY;
        Random entropia = new Random();

        // Rebote contra los bordes superior/inferior
        if (y <= 0 || y + Config.frameSuperior + tamaño >= 600) {
            velocidadY *= -1;

            if (pared.isRunning())
                pared.stop();
            pared.setFramePosition(0); // reinicia el audio al comienzo
            pared.start();
        }

        // Rebote con las paletas
        if (getRect().intersects(j1.getRect()) && choque.tangible){
            velocidadX *= -1;

            if (paleta.isRunning())
                paleta.stop();
            paleta.setFramePosition(0); // reinicia el audio al comienzo
            paleta.start();

            if (j1.getBajando() && velocidadY > -25){
                velocidadY += entropia.nextInt(factor) + 1;
            }
            if (j1.getSubiendo() && velocidadY < 25){
                velocidadY -= entropia.nextInt(factor) + 1;
            } // si se quiere subir la dificultad se puede aumentar el factor

            velocidadY += entropia.nextInt(factor - 1) - 2;
            // esta linea evita que la pelota se mantenga horizontal;

            // choque en el borde
            Thread rebote = new Thread(choque);
            rebote.start();
        }
        if (getRect().intersects(j2.getRect()) && choque.tangible){
            velocidadX *= -1;

            paleta.setFramePosition(0); // reinicia el audio al comienzo
            paleta.start();

            if (j2.getBajando() && velocidadY > -25){
                velocidadY += entropia.nextInt(factor) + 1;
            }
            if (j2.getSubiendo() && velocidadY < 25){
                velocidadY -= entropia.nextInt(factor) + 1;
            }

            velocidadY += entropia.nextInt(factor - 1) - 2;
            // esta linea evita que la pelota se mantenga horizontal;

            // choque en el borde
            Thread rebote = new Thread(choque);
            rebote.start();
        }
    }

    public void dibujar(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillOval(x, y, tamaño, tamaño);
    }

    public void reiniciar() {
        x = 400 - tamaño / 2;
        y = 300 - tamaño / 2;
        direccionAleatoria();
    }

    private void direccionAleatoria() {
        Random r = new Random();
        velocidadX = r.nextBoolean() ? 4 : -4;
        velocidadY = r.nextBoolean() ? 4 : -4;
    }

    public int getX() {
        return x;
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, tamaño, tamaño);
    }

    public int getY(){return this.y;}
}

class Choque implements Runnable{
    public static boolean tangible = true;

    @Override
    public void run() {
        tangible = false;
        try {
            Thread.sleep(500); // Pausa de 2 segundos
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tangible = true;
    }
}
