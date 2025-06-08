package lemmings.modelo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Entrada {
    private int x, y;

    private List<BufferedImage> framesEntrada;
    private int frameActual = 0;
    private int frameTick = 0;
    private final int frameDelay = 10;

    public Entrada(int x, int y) {
        this.x = x;
        this.y = y;
        cargarFrames();
    }

    private void cargarFrames() {
        framesEntrada = new ArrayList<>();
        try {
            BufferedImage spriteSheet = ImageIO.read(new File("src/lemmings/recursos/frameEntrada.png"));
            int anchoFrame = 59;
            int altoFrame = 38;
            int espacio = 8;

            for (int i = 0; i < 2; i++) {
                int xPos = i * (anchoFrame + espacio);
                BufferedImage frame = spriteSheet.getSubimage(xPos, 0, anchoFrame, altoFrame);

                BufferedImage transparente = new BufferedImage(anchoFrame, altoFrame, BufferedImage.TYPE_INT_ARGB);
                for (int yy = 0; yy < altoFrame; yy++) {
                    for (int xx = 0; xx < anchoFrame; xx++) {
                        int color = frame.getRGB(xx, yy);
                        if (color == 0xFFF800F8 || color == 0xFF00FF00) {
                            transparente.setRGB(xx, yy, 0x00000000);
                        } else {
                            transparente.setRGB(xx, yy, color);
                        }
                    }
                }

                framesEntrada.add(transparente);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dibujar(Graphics g) {
        frameTick++;
        if (frameTick >= frameDelay) {
            frameActual = (frameActual + 1) % framesEntrada.size();
            frameTick = 0;
        }
        g.drawImage(framesEntrada.get(frameActual), x, y, null);
    }

    public Lemming spawnear(Mapa mapa) {
        int anchoNube = 59;
        int altoNube = 38;
        int gokuWidth = 16;
        int gokuHeight = 16;

        int gokuX = x + (anchoNube - gokuWidth) / 2;
        int gokuY = y + altoNube - gokuHeight / 2;

        return new Lemming(gokuX, gokuY - 20, mapa);
    }


    public int getX() { return x; }
    public int getY() { return y; }
}
