package pong;

import java.awt.*;
import java.awt.event.*;

public class Cancha extends Panel {
    private Pelota pelota;
    private Paleta jugador1;
    private Paleta jugador2;

    private int puntajeJ1 = 0;
    private int puntajeJ2 = 0;

    private boolean juegoTerminado = false;
    private String mensajeGanador = "";

    private Button botonReiniciar;

    private Image buffer;
    private Graphics bufferGraphics;

    public Cancha() {
        setBackground(Color.BLACK);

        // Crear objetos
        jugador1 = new Paleta(30, 250, Config.teclaArribaJugador1, Config.teclaAbajoJugador1, 1, false);
        jugador2 = new Paleta(750, 250, Config.teclaArribaJugador2, Config.teclaAbajoJugador2, Config.multiplicadorDificultad, true);
        pelota = new Pelota(390, 290);

        // Botón reiniciar (invisible al principio)
        botonReiniciar = new Button("Reiniciar");
        botonReiniciar.setBounds(getWidth() / 2 - 60, getHeight() / 2 + 40, 120, 30);
        botonReiniciar.setVisible(false);
        botonReiniciar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Reset
                puntajeJ1 = 0;
                puntajeJ2 = 0;
                juegoTerminado = false;
                mensajeGanador = "";
                pelota.reiniciar();
                botonReiniciar.setVisible(false);
                repaint();
            }
        });
        add(botonReiniciar);

    }

    public void actualizar() {
        if (juegoTerminado) return;

        jugador1.actualizar();
        //cpu
        if(Config.singleMode){
            if(pelota.getY() < jugador2.getY())
                jugador2.setCpuSubiendo(true);
            else{
                if(pelota.getY() > jugador2.getY()) {
                    jugador2.setCpuSubiendo(false);
                    jugador2.setCpuBajando(true);
                }else
                    jugador2.setCpuSubiendo(false);
            }
            jugador2.actualizar();
        }else
            jugador2.actualizar();

        pelota.actualizar(jugador1, jugador2);

        if (pelota.getX() < 0) {
            puntajeJ2++;
            pelota.reiniciar();
        }

        if (pelota.getX() > getWidth()) {
            puntajeJ1++;
            pelota.reiniciar();
        }

        if (puntajeJ1 == 10) {
            juegoTerminado = true;
            mensajeGanador = "¡Jugador 1 ganó!";
            botonReiniciar.setVisible(true);
        }

        if (puntajeJ2 == 10) {
            juegoTerminado = true;
            mensajeGanador = "¡Jugador 2 ganó!";
            botonReiniciar.setVisible(true);
        }

    }


    @Override
    public void paint(Graphics g) {
        if (buffer == null) {
            buffer = createImage(getWidth(), getHeight());
            bufferGraphics = buffer.getGraphics();
        }

        // Limpiar fondo
        bufferGraphics.setColor(Color.BLACK);
        bufferGraphics.fillRect(0, 0, getWidth(), getHeight());

        // Línea del medio
        bufferGraphics.setColor(Color.WHITE);
        bufferGraphics.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());

        // Dibujar elementos
        jugador1.dibujar(bufferGraphics);
        jugador2.dibujar(bufferGraphics);
        pelota.dibujar(bufferGraphics);

        // Dibujar puntaje
        bufferGraphics.setFont(new Font("Arial", Font.BOLD, 36));
        bufferGraphics.drawString(String.valueOf(puntajeJ1), getWidth() / 2 - 60, 50);
        bufferGraphics.drawString(String.valueOf(puntajeJ2), getWidth() / 2 + 30, 50);

        // Mensaje de fin
        if (juegoTerminado) {
            bufferGraphics.setColor(Color.RED);
            bufferGraphics.drawString(mensajeGanador, getWidth() / 2 - 150, getHeight() / 2);
        }

        // Dibujar el buffer en pantalla
        g.drawImage(buffer, 0, Config.frameSuperior, this);
    }
    @Override
    public void update(Graphics g) {
        paint(g); // evitar parpadeo por el borrado automático de AWT
    }


    public void teclaPresionada(KeyEvent e) {
        jugador1.teclaPresionada(e);
        jugador2.teclaPresionada(e);
    }

    public void teclaSoltada(KeyEvent e) {
        jugador1.teclaSoltada(e);
        jugador2.teclaSoltada(e);
    }
}
