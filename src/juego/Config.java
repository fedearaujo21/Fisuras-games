package juego;

public class Config {
    public static boolean pantallaCompleta = false;
    public static boolean sonidoActivado = true;
    public static String skin = "original";
    public static String pistaMusical = "original";

    public static int teclaArribaJugador1 = java.awt.event.KeyEvent.VK_UP;
    public static int teclaAbajoJugador1 = java.awt.event.KeyEvent.VK_DOWN;
    public static int teclaArribaJugador2 = java.awt.event.KeyEvent.VK_W;
    public static int teclaAbajoJugador2 = java.awt.event.KeyEvent.VK_S;

    public static void resetDefaults() {
        pantallaCompleta = false;
        sonidoActivado = true;
        skin = "original";
        pistaMusical = "original";
        teclaArribaJugador1 = java.awt.event.KeyEvent.VK_UP;
        teclaAbajoJugador1 = java.awt.event.KeyEvent.VK_DOWN;
        teclaArribaJugador2 = java.awt.event.KeyEvent.VK_W;
        teclaAbajoJugador2 = java.awt.event.KeyEvent.VK_S;
    }
}
