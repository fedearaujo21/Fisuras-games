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

    private final int anchoNube = 59;
    private final int altoNube = 38;
    private int offsetVisualNube = 0;

    public Entrada(int x, int y) {
        this.x = x;
        this.y = y;
        cargarFrames();
    }

    private void cargarFrames() {
        framesEntrada = new ArrayList<>();
        try {
            BufferedImage spriteSheet = ImageIO.read(new File("src/lemmings/recursos/frameEntrada.png"));
            int espacio = 8;

            for (int i = 0; i < 2; i++) {
                int xPos = i * (anchoNube + espacio);
                BufferedImage frame = spriteSheet.getSubimage(xPos, 0, anchoNube, altoNube);

                BufferedImage transparente = new BufferedImage(anchoNube, altoNube, BufferedImage.TYPE_INT_ARGB);
                for (int yy = 0; yy < altoNube; yy++) {
                    for (int xx = 0; xx < anchoNube; xx++) {
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

        g.drawImage(framesEntrada.get(frameActual),
                x + offsetVisualNube,
                y,
                anchoNube,
                altoNube,
                null);
    }

    public Lemming spawnear(Mapa mapa) {
        int gokuWidth = 16;
        int gokuHeight = 32;

        int gokuX = x + (anchoNube - gokuWidth) / 2;
        int gokuY = y + altoNube - gokuHeight;

        System.out.println("[SPAWN] Entrada en x=" + x + ", y=" + y);
        System.out.println("[SPAWN] Goku lemming creado en x=" + gokuX + ", y=" + gokuY);

        return new Lemming(gokuX, gokuY, mapa);
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
