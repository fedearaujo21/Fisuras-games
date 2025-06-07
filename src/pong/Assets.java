package pong;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

public class Assets {
    public static BufferedImage bananaDerecha;
    public static BufferedImage bananaIzquierda;
    public static BufferedImage bladeDerecha;
    public static BufferedImage bladeIzquierda;

    public static void init() {
        try {
            //el Objects.requireNonNull hace magia xd
            bananaDerecha = ImageIO.read(Objects.requireNonNull(Assets.class.getResource("/pong/recursos/BananaDerecha.png")));
            bananaIzquierda = ImageIO.read(Objects.requireNonNull(Assets.class.getResource("/pong/recursos/BananaIzquierda.png")));
            bladeDerecha = ImageIO.read(Objects.requireNonNull(Assets.class.getResource("/pong/recursos/bladeDerecha.jpg")));
            bladeIzquierda = ImageIO.read(Objects.requireNonNull(Assets.class.getResource("/pong/recursos/bladeIzquierda.jpg")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

