package lemmings.modelo;

import lemmings.control.AudioPlayer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Salida {
    private int x, y;
    private int ancho = 86;
    private int alto = 80;
    private AudioPlayer sonidoSalida;
    private List<BufferedImage> framesSalida;
    private int frameActual = 0;
    private int frameTick = 0;
    private final int frameDelay = 15;

    public Salida(int x, int y, int ancho, int alto) {
        this.x = x;
        this.y = y;
        this.ancho = 30;
        this.alto = 30;
        cargarFrames();
    }

    private void cargarFrames() {
        framesSalida = new ArrayList<>();
        try {
            BufferedImage spriteSheet = ImageIO.read(getClass().getResourceAsStream("/lemmings/recursos/frameSalida.png"));
            int espacio = 8;

            for (int i = 0; i < 2; i++) {
                int xFrame = i * (86 + espacio);
                BufferedImage frame = spriteSheet.getSubimage(xFrame, 0, 86, 80);

                BufferedImage limpio = new BufferedImage(86, 80, BufferedImage.TYPE_INT_ARGB);
                for (int yy = 0; yy < 80; yy++) {
                    for (int xx = 0; xx < 86; xx++) {
                        int color = frame.getRGB(xx, yy);
                        if (color == 0xFFF800F8 || color == 0xFF00FF00) {
                            limpio.setRGB(xx, yy, 0x00000000);
                        } else {
                            limpio.setRGB(xx, yy, color);
                        }
                    }
                }

                framesSalida.add(limpio);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dibujar(Graphics g) {
        if (framesSalida.isEmpty()) return;

        g.drawImage(framesSalida.get(frameActual), x - 7, y - 5, 50, 46, null);

        frameTick++;
        if (frameTick >= frameDelay) {
            frameActual = (frameActual + 1) % framesSalida.size();
            frameTick = 0;
        }
    }

    public boolean haAlcanzado(Lemming lemming) {
        return lemming.getX() < this.x + this.ancho &&
                lemming.getX() + lemming.getLemmingWidth() > this.x &&
                lemming.getY() < this.y + this.alto &&
                lemming.getY() + lemming.getLemmingHeight() > this.y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
}
