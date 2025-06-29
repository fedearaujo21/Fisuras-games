package lemmings.modelo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.*;

import lemmings.control.Escalador;

public class Lemming {
    // Hago listas para cada frame
    private List<BufferedImage> framesCaminar;
    private List<BufferedImage> framesCaida;
    private List<BufferedImage> framesExcavar;
    private List<BufferedImage> framesParacaidas;
    private List<BufferedImage> framesBloqueador;
    private List<BufferedImage> framesKameCarga;
    private List<BufferedImage> framesKameDisparo;
    private List<BufferedImage> framesConstructor;
    private List<BufferedImage> framesAutoBomba;

    private int frameKameActual = 0;
    private int frameTickKame = 0;
    private final int frameDelayKame = 6;

    private int frameActual = 0;
    private int frameTick = 0;
    private int frameDelay = 8; // cuanto menor, más rápido cambia de frame
    private EstadoLemming estado = EstadoLemming.CAMINANDO;

    public enum EstadoLemming {
        CAMINANDO,
        CAYENDO,
        EXCAVANDO,
        BLOQUEANDO,
        AUTOBOMBA,
        CONSTRUYENDO,
        KAMEHAMEHA_CARGANDO,
        KAMEHAMEHA_DISPARANDO
    }

    private int x, y;
    private int direccion = 1;
    private Mapa mapa;
    private int lemmingWidth = 16;
    private int lemmingHeight = 25;
    private long tiempoCreacion;
    private static final int MAX_STEP_HEIGHT = 12;
    private long tiempoInicioAutoBomba = -1;
    private Habilidad habilidadActiva;
    private int ticksHabilidad = 0;
    private static final int MAX_FALL_ADJUST = 5;
    private static final int TICKS_POR_ACCION_HABILIDAD = 5;

    private static final int TICKS_POR_MOVIMIENTO_NORMAL = 2;
    private int ticksMovimientoNormal = 0;

    private int bloquesConstruidos = 0; // Contador de bloques construidos
    private static final int TICKS_POR_ACCION_CONSTRUCTOR = 10;

    private int ticksEnAire = 0; // Contador de ticks que el Lemming lleva en el aire
    private static final int UMBRAL_CAIDA_MINERO = 10;

    public Lemming(int x, int y, Mapa mapa) {
        this.x = x;
        this.y = y;
        this.mapa = mapa;
        this.tiempoCreacion = System.currentTimeMillis();
        cargarFramesCaida();
        cargarFramesCaminata();
        cargarFramesExcavar();
        cargarFramesParacaidas();
        cargarFramesBloqueador();
        cargarFramesKamehameha();
        cargarFramesConstructor();
        cargarFramesAutoBomba();

    }
    private void cargarFramesAutoBomba() {
        framesAutoBomba = new ArrayList<>();

        try {
            BufferedImage spriteSheet = ImageIO.read(new File("src/lemmings/recursos/gokuAutoBomba.png"));
            int ancho = 32;
            int alto = 32;
            int espacio = 8;

            for (int i = 0; i < 5; i++) {
                int xPos = i * (ancho + espacio);
                BufferedImage frameCrudo = spriteSheet.getSubimage(xPos, 0, ancho, alto);

                BufferedImage transparente = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
                for (int yy = 0; yy < alto; yy++) {
                    for (int xx = 0; xx < ancho; xx++) {
                        int color = frameCrudo.getRGB(xx, yy);

                        // Limpieza del color violeta y fucsia
                        if (color == 0xFFFF00FF || color == 0xFFF800F8) {
                            transparente.setRGB(xx, yy, 0x00000000);
                        } else {
                            transparente.setRGB(xx, yy, color);
                        }
                    }
                }

                framesAutoBomba.add(transparente);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void cargarFramesConstructor() {
        framesConstructor = new ArrayList<>();
        try {
            BufferedImage spriteSheet = ImageIO.read(new File("src/lemmings/recursos/gokuConstructor.png"));
            int anchoFrame = 32;
            int altoFrame = 32;
            int espacio = 8;

            for (int i = 0; i < 8; i++) {
                int xPos = i * (anchoFrame + espacio);
                BufferedImage frameCrudo = spriteSheet.getSubimage(xPos, 0, anchoFrame, altoFrame);
                BufferedImage limpio = hacerTransparente(frameCrudo, new int[]{0xFFF800F8, 0xFFFF00FF, 0xFF00FF00});
                framesConstructor.add(limpio);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void cargarFramesKamehameha() {
        framesKameCarga = new ArrayList<>();
        framesKameDisparo = new ArrayList<>();

        try {
            BufferedImage carga = ImageIO.read(new File("src/lemmings/recursos/gokuCargando.png"));
            BufferedImage disparo = ImageIO.read(new File("src/lemmings/recursos/gokuDisparando.png"));

            // Carga: 16x32 + 8 espacio
            int anchoCarga = 16;
            int altoCarga = 32;
            int espacio = 8;

            for (int i = 0; i < 3; i++) {
                int xPos = i * (anchoCarga + espacio);
                BufferedImage frameCrudo = carga.getSubimage(xPos, 0, anchoCarga, altoCarga);
                BufferedImage limpio = hacerTransparente(frameCrudo, new int[]{0xFFF800F8, 0xFFFF00FF}); // violeta y fucsia
                framesKameCarga.add(limpio);
            }

            // Disparo: 32x32 + 8 espacio
            int anchoDisparo = 32;
            int altoDisparo = 32;
            for (int i = 0; i < 3; i++) {
                int xPos = i * (anchoDisparo + espacio);
                BufferedImage frameCrudo = disparo.getSubimage(xPos, 0, anchoDisparo, altoDisparo);
                BufferedImage limpio = hacerTransparente(frameCrudo, new int[]{0xFFF800F8, 0xFFFF00FF}); // violeta y fucsia
                framesKameDisparo.add(limpio);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void cargarFramesBloqueador() {
        framesBloqueador = new ArrayList<>();
        try {
            BufferedImage spriteSheet = ImageIO.read(new File("src/lemmings/recursos/gokuBloqueador.png"));

            int anchoFrame = 32;
            int altoFrame = 32;
            int espacio = 8;

            for (int i = 0; i < 2; i++) { // ⚠️ Cambiar si hay más frames
                int x = i * (anchoFrame + espacio);
                BufferedImage frame = spriteSheet.getSubimage(x, 0, anchoFrame, altoFrame);

                // Quitar fondo verde si es necesario
                BufferedImage transparente = new BufferedImage(anchoFrame, altoFrame, BufferedImage.TYPE_INT_ARGB);
                for (int y = 0; y < altoFrame; y++) {
                    for (int xx = 0; xx < anchoFrame; xx++) {
                        int color = frame.getRGB(xx, y);
                        if (color == 0xFF00FF00 || color == 0xFFF800F8) {
                            transparente.setRGB(xx, y, 0x00000000);
                        } else {
                            transparente.setRGB(xx, y, color);
                        }
                    }
                }

                framesBloqueador.add(transparente);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void cargarFramesParacaidas() {
        framesParacaidas = new ArrayList<>();
        try {
            BufferedImage spriteSheet = ImageIO.read(new File("src/lemmings/recursos/gokuParacaidas.png"));

            int anchoFrame = 16;
            int altoFrame = 25;
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

                framesParacaidas.add(transparente);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public void setFrameParacaidas() {
        if (framesParacaidas != null && !framesParacaidas.isEmpty()) {
            frameActual = (frameActual + 1) % framesParacaidas.size();
        }
    }
    public void caminar(List<Lemming> todosLosLemmings) {

        if (estado == EstadoLemming.AUTOBOMBA) {
            if (tiempoInicioAutoBomba == -1) {
                tiempoInicioAutoBomba = System.currentTimeMillis();
            }

            // Animación: cambiar frame cada 150 ms
            long duracion = System.currentTimeMillis() - tiempoInicioAutoBomba;
            if (duracion / 150 > frameActual && frameActual < framesAutoBomba.size() - 1) {
                frameActual++;
            }

            return; // No camina ni nada más
        }

        if (estado == EstadoLemming.BLOQUEANDO) {
            frameTick++;
            if (frameTick >= frameDelay) {
                frameActual = (frameActual + 1) % framesBloqueador.size();
                frameTick = 0;
            }
            return;
        }

        if (estado == EstadoLemming.CONSTRUYENDO) {
            // Un constructor se queda quieto mientras construye.
            // La animación del constructor podría ser estática o específica.
            // Aquí puedes actualizar el frame para una animación de construcción si la tienes.
            // Si no, simplemente retorna para que no se mueva horizontalmente.
            frameTick++;
            if (frameTick >= frameDelay) {
                // Si tienes frames específicos para construir, úsalos aquí
                // Por ejemplo: frameActual = (frameActual + 1) % framesConstruyendo.size();
                frameActual = (frameActual + 1) % framesCaminar.size(); // O usa frames de caminar si no hay específicos
                frameTick = 0;
            }

            ticksHabilidad++; // Incrementa los ticks para la acción de habilidad
            if (ticksHabilidad >= TICKS_POR_ACCION_CONSTRUCTOR) {
                if (habilidadActiva instanceof HabilidadConstructor) {
                    ((HabilidadConstructor) habilidadActiva).construir(this);
                }
                ticksHabilidad = 0; // Reinicia el contador de ticks
            }
            return; // El constructor no se mueve horizontalmente en cada tick normal
        }

        if (estado == EstadoLemming.KAMEHAMEHA_CARGANDO || estado == EstadoLemming.KAMEHAMEHA_DISPARANDO) {
            if (habilidadActiva instanceof HabilidadKameHameHa) {
                ((HabilidadKameHameHa) habilidadActiva).aplicarKameHameHa(this);
            }
            // No se mueve horizontalmente ni verticalmente
            return;
        }

        long ahora = System.currentTimeMillis();
        boolean colisionTemporalmenteDesactivada = (ahora - tiempoCreacion) < 100;

        frameTick++;
        if (frameTick >= frameDelay) {
            int totalFrames = switch (estado) {
                case CAMINANDO -> framesCaminar.size();
                case CAYENDO -> framesCaida.size();
                case EXCAVANDO -> framesExcavar.size();
                case BLOQUEANDO -> framesBloqueador.size();
                default -> 1;
            };
            frameActual = (frameActual + 1) % totalFrames;
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
            ticksEnAire++;
            if (habilidadActiva instanceof HabilidadMinero && ticksEnAire > UMBRAL_CAIDA_MINERO) {
                desactivarHabilidad();
            }
            if (habilidadActiva instanceof HabilidadMinero) {
                estado = EstadoLemming.EXCAVANDO;
            } else {
                estado = EstadoLemming.CAYENDO;
            }
            if (habilidadActiva instanceof HabilidadParacaidas) {
                ((HabilidadParacaidas) habilidadActiva).aplicarSiCayendo(this, ticksEnAire);
            } else {
                y += 1;
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
        if (habilidadActiva == null || habilidadActiva instanceof HabilidadParacaidas) {
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
                for (Lemming l : todosLosLemmings) {
                    if (l == this) continue;
                    if (l.estado == EstadoLemming.BLOQUEANDO) {
                        Rectangle rectEste = new Rectangle(siguienteX, nuevaY, lemmingWidth, lemmingHeight);
                        Rectangle rectOtro = new Rectangle(l.x, l.y, lemmingWidth, lemmingHeight);
                        if (rectEste.intersects(rectOtro)) {
                            direccion *= -1;
                            return;
                        }
                    }
                }
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
    private void actualizarFrameKame() {
        frameTickKame++;
        if (frameTickKame >= frameDelayKame) {
            frameKameActual++;
            frameTickKame = 0;
        }
    }
    private BufferedImage hacerTransparente(BufferedImage original, int[] coloresTransparente) {
        BufferedImage nueva = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < original.getHeight(); y++) {
            for (int x = 0; x < original.getWidth(); x++) {
                int color = original.getRGB(x, y);
                boolean esTransparente = false;
                for (int ct : coloresTransparente) {
                    if (color == ct) {
                        esTransparente = true;
                        break;
                    }
                }
                nueva.setRGB(x, y, esTransparente ? 0x00000000 : color);
            }
        }
        return nueva;
    }
    public void incrementarY() {
        y += 1;
    }

    public void dibujar(Graphics g) {
        BufferedImage frame;
        switch (estado) {
            case CAMINANDO -> frame = framesCaminar.get(frameActual);
            case CAYENDO -> frame = framesCaida.get(frameActual % framesCaida.size());
            case EXCAVANDO -> frame = framesExcavar.get(frameActual % framesExcavar.size());
            case BLOQUEANDO -> frame = framesBloqueador.get(frameActual % framesBloqueador.size());
            case CONSTRUYENDO -> frame = framesConstructor.get(frameActual % framesConstructor.size());
            case AUTOBOMBA -> frame = framesAutoBomba.get(Math.min(frameActual, framesAutoBomba.size() - 1));
            case KAMEHAMEHA_CARGANDO -> {
                actualizarFrameKame();
                frame = framesKameCarga.get(frameKameActual % framesKameCarga.size());
            }
            case KAMEHAMEHA_DISPARANDO -> {
                actualizarFrameKame();
                frame = framesKameDisparo.get(frameKameActual % framesKameDisparo.size());
            }
            default -> frame = framesCaminar.get(0);
        }

        int offsetX = 0;
        int offsetY = frame.getHeight() - lemmingHeight;

        if (estado == EstadoLemming.EXCAVANDO || estado == EstadoLemming.BLOQUEANDO) {
            offsetX = (frame.getWidth() - lemmingWidth) / 2;
        }

        Graphics2D g2d = (Graphics2D) g;

        if (estado == EstadoLemming.CAYENDO && habilidadActiva instanceof HabilidadParacaidas) {
            if (frameActual >= framesParacaidas.size()) {
                frameActual = 0;
            }
            frame = framesParacaidas.get(frameActual);

            if (direccion == 1) {
                g2d.drawImage(frame, x + frame.getWidth(), y - offsetY, -frame.getWidth(), frame.getHeight(), null);
            } else {
                g2d.drawImage(frame, x, y - offsetY, frame.getWidth(), frame.getHeight(), null);
            }
            return;
        }

        if (direccion == 1) {
            g2d.drawImage(frame, x + frame.getWidth() - offsetX, y - offsetY, -frame.getWidth(), frame.getHeight(), null);
        } else {
            g2d.drawImage(frame, x - offsetX, y - offsetY, frame.getWidth(), frame.getHeight(), null);
        }

        // Si está disparando el KameHameHa
        if (habilidadActiva instanceof HabilidadKameHameHa kameHameHa) {
            int ticks = kameHameHa.getTicksActivo();
            int duracionCarga = kameHameHa.getDuracionCargaTicks();
            int duracionViaje = kameHameHa.getDuracionViajeTicks();

            if (ticks > duracionCarga) {
                int origenX = x + lemmingWidth - 4;
                int origenY = y + lemmingHeight - 4;
                int puntoImpactoX = kameHameHa.getPuntoImpactoX();
                int puntoImpactoY = kameHameHa.getPuntoImpactoY();

                float progreso = (float) (ticks - duracionCarga) / duracionViaje;
                progreso = Math.min(1.0f, progreso);

                int rayoX = origenX + (int) ((puntoImpactoX - origenX) * progreso);
                int rayoY = origenY + (int) ((puntoImpactoY - origenY) * progreso);

                g2d.setColor(new Color(0, 255, 255, 180));
                g2d.setStroke(new BasicStroke(10));
                g2d.drawLine(origenX, origenY, rayoX, rayoY);
                g2d.fillOval(rayoX - 5, rayoY - 5, 20, 20);
            }
        }
    }




    public int getTicksEnAire() {
        return ticksEnAire;
    }

    public Habilidad getHabilidadActiva() { return habilidadActiva; }
    public void setHabilidadActiva(Habilidad habilidadActiva) {
        this.habilidadActiva = habilidadActiva;
        if (habilidadActiva instanceof HabilidadAutoBomba) {
            this.estado = EstadoLemming.AUTOBOMBA;
            this.tiempoInicioAutoBomba = System.currentTimeMillis();
        }
        else if (habilidadActiva instanceof HabilidadBloqueador) {
            this.estado = EstadoLemming.BLOQUEANDO;
        }
        else if (habilidadActiva instanceof HabilidadConstructor) { // ¡Nuevo!
            this.estado = EstadoLemming.CONSTRUYENDO;
        }
        else if (habilidadActiva instanceof HabilidadKameHameHa) { // ¡Nuevo!
            this.estado = EstadoLemming.KAMEHAMEHA_CARGANDO;
        }
        this.ticksHabilidad = 0;
    }

    public void desactivarHabilidad() {
        this.habilidadActiva = null;
        this.ticksHabilidad = 0;
    }
    public EstadoLemming getEstado() {
        return estado;
    }

    public boolean getImpacto(){
        for (int i = 0; i < lemmingWidth; i++) {
            if (x + i >= 0 && x + i < mapa.getAncho() && y + lemmingHeight >= 0 && y + lemmingHeight < mapa.getAlto()) {
                if (mapa.hayColision(x + i, y + lemmingHeight)) {
                    return true;
                }
            }
        }
        return false;
    }
    public int getBloquesConstruidos() {
        return this.bloquesConstruidos;
    }

    public void aumentarBloquesConstruidos() {
        this.bloquesConstruidos++;
    }

    public void resetBloquesConstruidos() {
        this.bloquesConstruidos = 0;
    }


    public long getTiempoInicioAutoBomba(){return this.tiempoInicioAutoBomba;}
    public int getTicksHabilidad(){return this.ticksHabilidad;}
    public void setEstado(EstadoLemming estado){this.estado = estado;}
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getDireccion() { return direccion; }
    public int getLemmingWidth() { return lemmingWidth; }
    public int getLemmingHeight() { return lemmingHeight; }

}
