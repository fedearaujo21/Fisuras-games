package lemmings.modelo;

import java.awt.*;

public class Lemming {
    private int x, y;
    private int direccion = 1; // 1=derecha, -1=izquierda
    private Mapa mapa;
    private int lemmingWidth = 6; // Ancho del Lemming (el tamaño de tu cuadrado verde)
    private int lemmingHeight = 9; // Alto del Lemming

    private static final int MAX_STEP_HEIGHT = 9; // Ajusta este valor. 1 o 2 es común para Lemmings.

    public Lemming(int x, int y, Mapa mapa) {
        this.x = x;
        this.y = y;
        this.mapa = mapa;
    }

    public void caminar(){
        // Siempre intenta caer si no hay terreno directamente debajo.
        // Verificamos si hay "vacío" justo debajo del Lemming (en el centro inferior o a los lados)
        boolean haySueloDebajo = false;
        for (int i = 0; i < lemmingWidth; i++) { // Revisa cada píxel a lo largo de la base
            if (mapa.hayColision(x + i, y + lemmingHeight)) {
                haySueloDebajo = true;
                break;
            }
        }

        if (!haySueloDebajo) {
            // No hay suelo debajo, el Lemming está en el aire, así que cae.
            y += 1;
            // Podríamos añadir lógica para detectar caídas largas (explosión, etc.) aquí
            return;
        }

        // Lógica de movimiento: Prioridad 2: Caminar Horizontalmente o Ajustarse a Pendientes
        int siguienteX = x + direccion;

        // Intentar moverse horizontalmente O subir una pequeña pendiente
        boolean puedeAvanzar = false;
        int y_ajustado = y; // La nueva Y si se ajusta a una pendiente

        // Revisamos varias alturas para ver si podemos "pisar" el terreno en la nueva X
        for (int step = 0; step <= MAX_STEP_HEIGHT; step++) {
            // Verificamos si la posición horizontal (siguienteX, y - step) está libre
            // Y si hay suelo justo debajo de esa nueva posición (siguienteX, y - step + lemmingHeight)
            // (Es decir, si hay una superficie transitable en la nueva X, a una altura permitida)

            boolean colisionHorizontal = false;
            for (int i = 0; i < lemmingHeight; i++) { // Revisa toda la altura del Lemming en la nueva X
                if (mapa.hayColision(siguienteX, y - step + i) || // Esquina izquierda de la nueva posición
                        mapa.hayColision(siguienteX + lemmingWidth - 1, y - step + i)) // Esquina derecha de la nueva posición
                {
                    colisionHorizontal = true;
                    break;
                }
            }

            // Si no hay colisión horizontal en la nueva X (a esta altura 'y - step')
            // Y hay terreno debajo de esa nueva posición (para caminar, no para caer en un agujero)
            boolean hayTerrenoDebajoEnSiguientePos = false;
            for (int i = 0; i < lemmingWidth; i++) {
                if (mapa.hayColision(siguienteX + i, y - step + lemmingHeight)) {
                    hayTerrenoDebajoEnSiguientePos = true;
                    break;
                }
            }

            if (!colisionHorizontal && hayTerrenoDebajoEnSiguientePos) {
                puedeAvanzar = true;
                y_ajustado = y - step; // Ajustamos la Y a la nueva altura del terreno
                break; // Encontramos una posición válida para avanzar, salimos del bucle
            }
        }


        if (puedeAvanzar) {
            // Si podemos avanzar, actualizamos la posición
            x = siguienteX;
            y = y_ajustado;
        } else {
            // Si no podemos avanzar (pared o pendiente demasiado alta), cambiamos de dirección
            direccion *= -1; // Rebota
        }

        // Después de mover horizontalmente o rebotar,
        // el Lemming podría haber quedado "flotando" un píxel sobre el terreno inclinado.
        // Forzamos a que "caiga" un píxel para pegarse al suelo si hay un vacío debajo.
        // Esto es importante para que "abrace" las pendientes.
        while (!mapa.hayColision(x + lemmingWidth / 2, y + lemmingHeight)) { // Comprueba el centro inferior
            y += 1;
            // Agrega una seguridad para evitar caídas infinitas si no hay suelo debajo
            if (y >= mapa.getAlto()) { // Si cae fuera del mapa
                break;
            }
        }
    }

    public void dibujar(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(x, y, lemmingWidth, lemmingHeight); // Dibuja el cuadrado verde con el tamaño definido
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}