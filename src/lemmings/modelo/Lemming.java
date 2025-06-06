package lemmings.modelo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

import java.awt.*;

public class Lemming {
    // Hago listas para cada frame
    private List<BufferedImage> framesCaminar;
    private List<BufferedImage> framesCaida;
    private List<BufferedImage> framesExcavar;


    private int frameActual = 0;
    private int frameTick = 0;
    private int frameDelay = 8; // cuanto menor, más rápido cambia de frame
    private EstadoLemming estado = EstadoLemming.CAMINANDO;

    public enum EstadoLemming {
        CAMINANDO,
        CAYENDO,
        EXCAVANDO
    }

    private int x, y;
    private int direccion = 1;
    private Mapa mapa;
    private int lemmingWidth = 16;
    private int lemmingHeight = 32;
    private long tiempoCreacion;
    private boolean fueUsado = false;
    private static final int MAX_STEP_HEIGHT = 12;

    private Habilidad habilidadActiva;
    private int ticksHabilidad = 0;
    private static final int MAX_FALL_ADJUST = 5;
    private static final int TICKS_POR_ACCION_HABILIDAD = 5;

    private static final int TICKS_POR_MOVIMIENTO_NORMAL = 2;
    private int ticksMovimientoNormal = 0;

    private int ticksEnAire = 0; // Contador de ticks que el Lemming lleva en el aire
    private static final int UMBRAL_CAIDA_MINERO = 10;

    public Lemming(int x, int y, Mapa mapa) {
        this.x = x;
        this.y = y;
        this.mapa = mapa;
        this.tiempoCreacion = System.currentTimeMillis();
        cargarFramesCaida();
        cargarFramesCaminata();
        cargarFramesCaida();
        cargarFramesExcavar();

    }

    private void cargarFramesExcavar() {
        framesExcavar = new ArrayList<>();
        try {
            BufferedImage spriteSheet = ImageIO.read(new File("src/lemmings/recursos/gokuCavando.png"));

            int anchoFrame = 32;
            int altoFrame = 64;
            int espacio = 8;

            for (int i = 0; i < 3; i++) {
                int x = i * (anchoFrame + espacio);
                BufferedImage frame = spriteSheet.getSubimage(x, 0, anchoFrame, altoFrame);

                // Eliminar color de fondo (violeta o verde)
                BufferedImage transparente = new BufferedImage(anchoFrame, altoFrame, BufferedImage.TYPE_INT_ARGB);
                for (int y = 0; y < altoFrame; y++) {
                    for (int xx = 0; xx < anchoFrame; xx++) {
                        int color = frame.getRGB(xx, y);
                        if (color == 0xFFF800F8 || color == 0xFF00FF00) {
                            transparente.setRGB(xx, y, 0x00000000);
                        } else {
                            transparente.setRGB(xx, y, color);
                        }
                    }
                }

                framesExcavar.add(transparente);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarFramesCaida() {
        framesCaida = new ArrayList<>();
        try {
            BufferedImage spriteSheet = ImageIO.read(new File("src/lemmings/recursos/gokuVolador.png"));

            int anchoFrame = 16;
            int altoFrame = 32;
            int espacio = 8;

            for (int i = 0; i < 3; i++) {
                int x = i * (anchoFrame + espacio);
                BufferedImage frame = spriteSheet.getSubimage(x, 0, anchoFrame, altoFrame);

                // Procesar transparencia igual que antes
                BufferedImage transparente = new BufferedImage(anchoFrame, altoFrame, BufferedImage.TYPE_INT_ARGB);
                for (int y = 0; y < altoFrame; y++) {
                    for (int xx = 0; xx < anchoFrame; xx++) {
                        int color = frame.getRGB(xx, y);
                        if (color == 0xFFF800F8 || color == 0xFF00FF00) {
                            transparente.setRGB(xx, y, 0x00000000);
                        } else {
                            transparente.setRGB(xx, y, color);
                        }
                    }
                }

                framesCaida.add(transparente);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarFramesCaminata() {
        framesCaminar = new ArrayList<>();
        try {
            BufferedImage spriteSheet = ImageIO.read(new File("src/lemmings/recursos/gokuWalker.png"));

            int anchoFrame = 16;
            int altoFrame = 32;
            int espacio = 6;

            for (int i = 0; i < 4; i++) {
                int x = i * (anchoFrame + espacio);
                BufferedImage frame = spriteSheet.getSubimage(x, 0, anchoFrame, altoFrame);

                // Crear imagen con canal alfa (transparencia)
                BufferedImage transparente = new BufferedImage(anchoFrame, altoFrame, BufferedImage.TYPE_INT_ARGB);
                for (int y = 0; y < altoFrame; y++) {
                    for (int xx = 0; xx < anchoFrame; xx++) {
                        int color = frame.getRGB(xx, y);
                        // Si es verde puro o violeta puro → hacerlo transparente
                        if (color == 0xFFF800F8 || color == 0xFF00FF00) {
                            transparente.setRGB(xx, y, 0x00000000); // transparente
                        } else {
                            transparente.setRGB(xx, y, color);
                        }


                    }
                }
                framesCaminar.add(transparente);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void caminar() {
        long ahora = System.currentTimeMillis();
        boolean colisionTemporalmenteDesactivada = (ahora - tiempoCreacion) < 500;

        frameTick++;
        if (frameTick >= frameDelay) {
            frameActual = (frameActual + 1) % framesCaminar.size();
            frameTick = 0;
        }

        // Verificamos si hay suelo debajo
        boolean haySueloDebajo = false;
        for (int i = 0; i < lemmingWidth; i++) {
            if (x + i >= 0 && x + i < mapa.getAncho() && y + lemmingHeight >= 0 && y + lemmingHeight < mapa.getAlto()) {
                if (!colisionTemporalmenteDesactivada && mapa.hayColision(x + i, y + lemmingHeight)) {
                    haySueloDebajo = true;
                    break;
                }
            } else {
                haySueloDebajo = false;
                break;
            }
        }

        if (!haySueloDebajo) {
            y += 1;
            // Si el Lemming está en el aire (cayendo), incrementamos el contador
            ticksEnAire++;

            // 🔁 Prioridad al estado EXCAVANDO si es minero, incluso en el aire
            if (habilidadActiva instanceof HabilidadMinero) {
                estado = EstadoLemming.EXCAVANDO;
            } else {
                estado = EstadoLemming.CAYENDO;
            }

            if (habilidadActiva instanceof HabilidadMinero && ticksEnAire > UMBRAL_CAIDA_MINERO) {
                desactivarHabilidad();
            }
            return;
        } else {
            ticksEnAire = 0;
            if (habilidadActiva instanceof HabilidadMinero) {
                estado = EstadoLemming.EXCAVANDO;
            } else {
                estado = EstadoLemming.CAMINANDO;
            }
        }

        if (habilidadActiva != null) {
            ticksHabilidad++;
            if (ticksHabilidad >= TICKS_POR_ACCION_HABILIDAD) {
                if (habilidadActiva instanceof HabilidadMinero) {
                    ((HabilidadMinero) habilidadActiva).excavar(this);
                }
                ticksHabilidad = 0;
            }

            if (habilidadActiva instanceof HabilidadMinero) {
                return;
            }
        }

        if (habilidadActiva == null) {
            ticksMovimientoNormal++;
            if (ticksMovimientoNormal < TICKS_POR_MOVIMIENTO_NORMAL) {
                return;
            }
            ticksMovimientoNormal = 0;

            int siguienteX = x + direccion;
            int nuevaY = y;
            boolean puedeAvanzar = false;

            for (int step = 0; step <= MAX_STEP_HEIGHT; step++) {
                int yCandidatoCabeza = y - step;
                int yCandidatoPies = y - step + lemmingHeight;

                boolean colisionHorizontalEnCandidato = false;
                int checkXEdge = siguienteX + (direccion == 1 ? (lemmingWidth - 1) : 0);
                for (int i = 0; i < lemmingHeight; i++) {
                    if (checkXEdge >= 0 && checkXEdge < mapa.getAncho() && yCandidatoCabeza + i >= 0 && yCandidatoCabeza + i < mapa.getAlto()) {
                        if (mapa.hayColision(checkXEdge, yCandidatoCabeza + i)) {
                            colisionHorizontalEnCandidato = true;
                            break;
                        }
                    } else {
                        colisionHorizontalEnCandidato = true;
                        break;
                    }
                }

                boolean hayTerrenoDebajoEnSiguientePos = false;
                for (int i = 0; i < lemmingWidth; i++) {
                    int checkX = siguienteX + i;
                    if (checkX >= 0 && checkX < mapa.getAncho() && yCandidatoPies >= 0 && yCandidatoPies < mapa.getAlto()) {
                        if (mapa.hayColision(checkX, yCandidatoPies)) {
                            hayTerrenoDebajoEnSiguientePos = true;
                            break;
                        }
                    }
                }

                if (!colisionHorizontalEnCandidato && hayTerrenoDebajoEnSiguientePos) {
                    puedeAvanzar = true;
                    nuevaY = yCandidatoCabeza;
                    break;
                }
            }

            if (puedeAvanzar) {
                x = siguienteX;
                y = nuevaY;
            } else {
                direccion *= -1;
            }
        }

        for (int fallStep = 0; fallStep < MAX_FALL_ADJUST; fallStep++) {
            boolean sigueCayendo = false;
            for (int i = 0; i < lemmingWidth; i++) {
                int checkX = x + i;
                int checkY = y + lemmingHeight;

                if (checkX >= 0 && checkX < mapa.getAncho() && checkY >= 0 && checkY < mapa.getAlto()) {
                    if (!mapa.hayColision(checkX, checkY)) {
                        sigueCayendo = true;
                        break;
                    }
                } else {
                    sigueCayendo = true;
                    break;
                }
            }

            if (sigueCayendo) {
                y += 1;
            } else {
                break;
            }

            if (y + lemmingHeight >= mapa.getAlto()) {
                break;
            }
        }
    }


    public void dibujar(Graphics g) {
        BufferedImage frame;
        switch (estado) {
            case CAMINANDO -> frame = framesCaminar.get(frameActual);
            case CAYENDO -> frame = framesCaida.get(frameActual % framesCaida.size());
            case EXCAVANDO -> frame = framesExcavar.get(frameActual % framesExcavar.size());
            default -> frame = framesCaminar.get(0);
        }

        int offsetX = 0;

// Si está excavando y el frame es más ancho, lo ajustamos
        if (estado == EstadoLemming.EXCAVANDO) {
            offsetX = (frame.getWidth() - lemmingWidth) / 2;
        }

        if (direccion == 1) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.drawImage(frame, x + frame.getWidth() - offsetX, y, -frame.getWidth(), frame.getHeight(), null);
        } else {
            g.drawImage(frame, x - offsetX, y, null);
        }



    }

    public Habilidad getHabilidadActiva() { return habilidadActiva; }
    public void setHabilidadActiva(Habilidad habilidadActiva) {
        if(fueUsado == false) {
            this.habilidadActiva = habilidadActiva;
            this.ticksHabilidad = 0;
        }
    }
    public void desactivarHabilidad() {
        this.habilidadActiva = null;
        this.ticksHabilidad = 0;
    }
    public void setFueUsado(boolean fueUsado){
        this.fueUsado = fueUsado;
    };

    public boolean getFueUsado(){
        return this.fueUsado;
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getDireccion() { return direccion; }
    public int getLemmingWidth() { return lemmingWidth; }
    public int getLemmingHeight() { return lemmingHeight; }
}
