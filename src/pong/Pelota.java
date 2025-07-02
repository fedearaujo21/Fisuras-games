package pong;

import java.awt.*;
import java.net.URL;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import javax.sound.sampled.*;

public class Pelota {
    private int x, y;
    private final int tamano = 12;
    private int velocidadX = 4, velocidadY = 4;
    private final int factor = 5; // margen de desvío

    private Clip pared;
    private Clip paleta;

    private final Choque choque = new Choque();

    public Pelota(int x, int y) {
        this.x = x;
        this.y = y;
        direccionAleatoria();

        // === Cargar sonido de pared ===
        pared = cargarClip("/pong/recursos/" + nombreSonido(Config.pistaMusical, "pared"));

        // === Cargar sonido de paleta ===
        paleta = cargarClip("/pong/recursos/" + nombreSonido(Config.pistaMusical, "paleta"));
    }

    /** Devuelve un Clip listo para reproducir, o null si el recurso no existe */
    private Clip cargarClip(String rutaRecurso) {
        try {
            URL url = Pelota.class.getResource(rutaRecurso);
            if (url == null) {
                // Ayuda a detectar rutas mal escritas dentro del jar
                throw new IllegalStateException("Recurso no encontrado: " + rutaRecurso);
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String nombreSonido(String estilo, String tipo) {
        if ("pared".equals(tipo)) {
            return switch (estilo) {
                case "techno" -> "Boing2.wav";
                case "8bit"   -> "Boing3.wav";
                default       -> "Boing1.wav";
            };
        } else { // paleta
            return switch (estilo) {
                case "techno" -> "Pared2.wav";
                case "8bit"   -> "Pared3.wav";
                default       -> "Pared1.wav";
            };
        }
    }

    public void actualizar(Paleta j1, Paleta j2) {
        x += velocidadX;
        y += velocidadY;
        Random entropia = new Random();

        // --- Rebote contra bordes superior / inferior ---
        if (y <= 0 || y + Config.frameSuperior + tamano >= 600) {
            velocidadY *= -1;
            reproducirClip(pared);
        }

        // --- Rebote con paleta izquierda ---
        if (getRect().intersects(j1.getRect()) && choque.tangible) {
            velocidadX *= -1;
            reproducirClip(paleta);

            if (j1.getBajando() && velocidadY > -25)
                velocidadY += entropia.nextInt(factor) + 1;
            if (j1.getSubiendo() && velocidadY < 25)
                velocidadY -= entropia.nextInt(factor) + 1;

            velocidadY += entropia.nextInt(factor - 1) - 2;
            new Thread(choque).start();
        }

        // --- Rebote con paleta derecha ---
        if (getRect().intersects(j2.getRect()) && choque.tangible) {
            velocidadX *= -1;
            reproducirClip(paleta);

            if (j2.getBajando() && velocidadY > -25)
                velocidadY += entropia.nextInt(factor) + 1;
            if (j2.getSubiendo() && velocidadY < 25)
                velocidadY -= entropia.nextInt(factor) + 1;

            velocidadY += entropia.nextInt(factor - 1) - 2;
            new Thread(choque).start();
        }
    }

    private void reproducirClip(Clip clip) {
        if (clip == null || !Config.sonidoActivado) return;
        if (clip.isRunning()) clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    public void dibujar(Graphics g) {
        if ("original".equals(Config.skin))
            g.setColor(Color.WHITE);
        else if ("techno".equals(Config.skin))
            g.setColor(Color.RED);
        else if ("tropical".equals(Config.skin))
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

    public int getX() { return x; }

    public int getY() { return y; }

    public Rectangle getRect() { return new Rectangle(x, y, tamano, tamano); }
}


/* --- Clase auxiliar para evitar rebotes múltiples demasiado rápidos --- */
class Choque implements Runnable {
    public static boolean tangible = true;

    @Override
    public void run() {
        tangible = false;
        try {
            Thread.sleep(500); // 0,5 s
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tangible = true;
    }
}
