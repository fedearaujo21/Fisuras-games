package pong;

import java.awt.*;
import java.io.IOException;
import java.net.URL; // Importar URL
import java.util.Objects;
import java.util.Random;
import javax.sound.sampled.*;
// import java.io.File; // Ya no es necesario si cargas como recurso
// import java.util.Map; // Los mapas no son estrictamente necesarios para la carga así

public class Pelota {
    private int x, y;
    private int tamano = 12;
    private int velocidadX = 4, velocidadY = 4;
    private final int factor = 5; // margen de desvío

    // Estos mapas no son usados en la carga actual, por lo que podrían eliminarse
    // si no tienen otro propósito. Si los quieres mantener, podrías cambiar
    // los valores a solo el nombre del archivo, como ya los tienes para el switch.
    /*
    private final Map<String, String> sonidoParedes = Map.of(
            "original","Boing1.wav", // Solo el nombre del archivo
            "techno","Boing2.wav",
            "8bit","Boing3.wav"
    );
    private final Map<String, String> sonidoPaletas = Map.of(
            "original","Pared1.wav",
            "techno","Pared2.wav",
            "8bit","Pared3.wav"
    );
    */

    private Clip pared;
    private Clip paleta;
    private Choque choque = new Choque();


    public Pelota(int x, int y) {
        this.x = x;
        this.y = y;
        direccionAleatoria();

        // Cargar sonido de las paredes
        try {
            // La ruta debe ser relativa a la raíz del classpath,
            // que si tus archivos están en src/main/resources/pong/recursos/,
            // entonces la ruta dentro del JAR será /pong/recursos/nombre.wav
            URL paredUrl = getClass().getResource("/pong/recursos/" + nombreSonido(Config.pistaMusical, "pared"));
            if (paredUrl == null) {
                // Si la URL es null, el recurso no se encontró.
                // Esto es crucial para depurar.
                System.err.println("No se encontró el recurso de sonido para la pared: /pong/recursos/" + nombreSonido(Config.pistaMusical, "pared"));
            } else {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(paredUrl);
                pared = AudioSystem.getClip();
                pared.open(audioStream);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }

        // Cargar sonido de las paletas
        try {
            URL paletaUrl = getClass().getResource("/pong/recursos/" + nombreSonido(Config.pistaMusical, "paleta"));
            if (paletaUrl == null) {
                System.err.println("No se encontró el recurso de sonido para la paleta: /pong/recursos/" + nombreSonido(Config.pistaMusical, "paleta"));
            } else {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(paletaUrl);
                paleta = AudioSystem.getClip();
                paleta.open(audioStream);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }

    }

    // Tu método nombreSonido está bien para obtener solo el nombre del archivo
    private String nombreSonido(String estilo, String tipo) {
        if (tipo.equals("pared")) {
            return switch (estilo) {
                case "techno" -> "Boing2.wav";
                case "8bit" -> "Boing3.wav";
                default -> "Boing1.wav";
            };
        } else { // tipo.equals("paleta")
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

            if(Config.sonidoActivado && pared != null) { // Añadir verificación de null
                if (pared.isRunning())
                    pared.stop();
                pared.setFramePosition(0); // reinicia el audio al comienzo
                pared.start();
            }
        }

        // Rebote con las paletas
        if (getRect().intersects(j1.getRect()) && choque.tangible){
            velocidadX *= -1;

            if(Config.sonidoActivado && paleta != null) { // Añadir verificación de null
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

            if(Config.sonidoActivado && paleta != null) { // Añadir verificación de null
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
        // Mejor usar .equals() para comparar Strings, no ==
        if ("original".equals(Config.skin))
            g.setColor(Color.WHITE);
        else if ("techno".equals(Config.skin)) // Usar else if
            g.setColor(Color.RED);
        else if ("tropical".equals(Config.skin)) // Usar else if
            g.setColor(Color.PINK);
        else // Definir un color por defecto si el skin no coincide
            g.setColor(Color.WHITE);

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
            Thread.sleep(500); // Pausa de 500 milisegundos
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tangible = true;
    }
}