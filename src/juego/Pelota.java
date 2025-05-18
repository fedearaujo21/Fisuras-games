package juego;

import java.awt.*;
import java.io.IOException;
import java.util.Random;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Pelota {
    private int x, y;
    private int tamaño = 12;
    private int velocidadX = 4, velocidadY = 4;
    private final int factor = 5; // margen de desvío
    private Clip clip;

    public Pelota(int x, int y) {
        this.x = x;
        this.y = y;
        direccionAleatoria();

        try {
            File archivoSonido = new File("src/Sonidos/Boing1.wav");
            System.out.println("¿Existe archivo? " + archivoSonido.exists());
            //File archivoSonido = new File("../Sonidos/Boing1.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(archivoSonido);

            clip = AudioSystem.getClip();
            clip.open(audioStream);
        } catch(UnsupportedAudioFileException | LineUnavailableException |IOException e){
            e.printStackTrace();
        }
        //clip.start();
    }

    public void actualizar(Paleta j1, Paleta j2) {
        x += velocidadX;
        y += velocidadY;
        Random entropia = new Random();

        // Rebote contra los bordes superior/inferior
        if (y <= 0 || y + tamaño >= 600) {
            velocidadY *= -1;

            System.out.println("Clip cargado: " + (clip != null));
            clip.setFramePosition(0); // reinicia el audio al comienzo
            clip.start();
        }

        // Rebote con las paletas
        if (getRect().intersects(j1.getRect())){
            velocidadX *= -1;

            System.out.println("Clip cargado: " + (clip != null));
            clip.setFramePosition(0); // reinicia el audio al comienzo
            clip.start();

            if (j1.getBajando() && velocidadY > -25){
                velocidadY += entropia.nextInt(factor) + 1;
            }
            if (j1.getSubiendo() && velocidadY < 25){
                velocidadY -= entropia.nextInt(factor) + 1;
            } // si se quiere subir la dificultad se puede aumentar el factor
        }
        if (getRect().intersects(j2.getRect())){
            velocidadX *= -1;

            clip.setFramePosition(0); // reinicia el audio al comienzo
            clip.start();

            if (j2.getBajando() && velocidadY > -25){
                velocidadY += entropia.nextInt(factor) + 1;
            }
            if (j2.getSubiendo() && velocidadY < 25){
                velocidadY -= entropia.nextInt(factor) + 1;
            }
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
}
