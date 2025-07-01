package pong;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import javax.sound.sampled.*;
import java.io.File;
import java.util.Map;

public class Pelota {
    private int x, y;
    private int tamano = 12;
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
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                    Objects.requireNonNull(getClass().getResourceAsStream("/pong/recursos/" + nombreSonido(Config.pistaMusical, "pared")))
            );
            pared = AudioSystem.getClip();
            pared.open(audioStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }

        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                    Objects.requireNonNull(getClass().getResourceAsStream("/pong/recursos/" + nombreSonido(Config.pistaMusical, "paleta")))
            );
            paleta = AudioSystem.getClip();
            paleta.open(audioStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }

    }
    private String nombreSonido(String estilo, String tipo) {
        if (tipo.equals("pared")) {
            return switch (estilo) {
                case "techno" -> "Boing2.wav";
                case "8bit" -> "Boing3.wav";
                default -> "Boing1.wav";
            };
        } else {
            return switch (estilo) {
                case "techno" -> "Pared2.wav";
                case "8bit" -> "Pared3.wav";
                default -> "Pared1.wav";
            };
        }
    }

    public void actualizar(Paleta j1, Paleta j2) {
        x += velocidadX;
        y += velocidadY;
        Random entropia = new Random();

        // Rebote contra los bordes superior/inferior
        if (y <= 0 || y + Config.frameSuperior + tamano >= 600) {
            velocidadY *= -1;

            if(Config.sonidoActivado) {
                if (pared.isRunning())
                    pared.stop();
                pared.setFramePosition(0); // reinicia el audio al comienzo
                pared.start();
            }
        }

        // Rebote con las paletas
        if (getRect().intersects(j1.getRect()) && choque.tangible){
            velocidadX *= -1;

            if(Config.sonidoActivado) {
                if (paleta.isRunning())
                    paleta.stop();
                paleta.setFramePosition(0); // reinicia el audio al comienzo
                paleta.start();
            }

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

            if(Config.sonidoActivado) {
                paleta.setFramePosition(0); // reinicia el audio al comienzo
                paleta.start();
            }

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
        if (Config.skin == "original")
            g.setColor(Color.WHITE);
        if (Config.skin == "techno")
            g.setColor(Color.RED);
        if (Config.skin == "tropical")
            g.setColor(Color.PINK);

        g.fillOval(x, y, tamano, tamano);
    }

    public void reiniciar() {
        x = 400 - tamano / 2;
        y = 300 - tamano / 2;
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
        return new Rectangle(x, y, tamano, tamano);
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
