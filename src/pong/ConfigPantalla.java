package pong;
//import pong.Config;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ConfigPantalla extends Frame {
    private Checkbox pantallaCompletaCheck;
    private Checkbox sonidoCheck;
    private Checkbox cpu;
    private JSlider slider;
    private Label dificultad1, dificultad2, dificultad3;
    private Choice selectorSkin;
    private Choice selectorMusica;
    private Runnable onConfirm;
    private TextField teclaArribaJ1, teclaAbajoJ1, teclaArribaJ2, teclaAbajoJ2;
    private Button botonIniciar, botonReset;

    private int keyCodeArribaJ1;
    private int keyCodeAbajoJ1;
    private int keyCodeArribaJ2;
    private int keyCodeAbajoJ2;

    public ConfigPantalla(Runnable onConfirm) {
        setTitle("Configuración - Fisuras Pong");
        setSize(500, 400);
        setLayout(null);
        setVisible(true);
        setLocationRelativeTo(null);
        this.onConfirm = onConfirm;

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        // Pantalla completa
        pantallaCompletaCheck = new Checkbox("Pantalla Completa", Config.pantallaCompleta);
        pantallaCompletaCheck.setBounds(50, 50, 200, 20);
        add(pantallaCompletaCheck);

        // Sonido
        sonidoCheck = new Checkbox("Sonido Activado", Config.sonidoActivado);
        sonidoCheck.setBounds(50, 80, 200, 20);
        add(sonidoCheck);

        //cpu
        cpu = new Checkbox("CPU", Config.singleMode);
        cpu.setBounds(50, 210, 200, 20);
        this.add(cpu);


        // Skin
        Label skinLabel = new Label("Skin:");
        skinLabel.setBounds(50, 110, 50, 20);
        add(skinLabel);

        selectorSkin = new Choice();
        selectorSkin.add("original");
        selectorSkin.add("techno");
        selectorSkin.add("tropical");
        selectorSkin.setBounds(110, 110, 120, 20);
        selectorSkin.select(Config.skin);
        add(selectorSkin);

        // Música
        Label musicaLabel = new Label("Música:");
        musicaLabel.setBounds(50, 140, 50, 20);
        add(musicaLabel);

        selectorMusica = new Choice();
        selectorMusica.add("original");
        selectorMusica.add("techno");
        selectorMusica.add("8bit");
        selectorMusica.setBounds(110, 140, 120, 20);
        selectorMusica.select(Config.pistaMusical);
        add(selectorMusica);

        // Teclas Jugador 1
        Label j1Label = new Label("Jugador 1:");
        j1Label.setBounds(50, 180, 80, 20);
        add(j1Label);

        teclaArribaJ1 = crearCampoTecla(Config.teclaArribaJugador1, 140, 180, code -> keyCodeArribaJ1 = code, () -> keyCodeArribaJ1);
        add(teclaArribaJ1);

        teclaAbajoJ1 = crearCampoTecla(Config.teclaAbajoJugador1, 260, 180, code -> keyCodeAbajoJ1 = code, () -> keyCodeAbajoJ1);
        add(teclaAbajoJ1);

        // Teclas Jugador 2
        Label j2Label = new Label("Jugador 2:");
        j2Label.setBounds(50, 240, 80, 20);
        add(j2Label);

        teclaArribaJ2 = crearCampoTecla(Config.teclaArribaJugador2, 140, 240, code -> keyCodeArribaJ2 = code, () -> keyCodeArribaJ2);
        add(teclaArribaJ2);

        teclaAbajoJ2 = crearCampoTecla(Config.teclaAbajoJugador2, 260, 240, code -> keyCodeAbajoJ2 = code, () -> keyCodeAbajoJ2);
        add(teclaAbajoJ2);

        //slider de dificultad
        slider = new JSlider(JSlider.HORIZONTAL, 1, 3, 2);
        slider.setBounds(50,250,240, 20);
        slider.setVisible(false);
        this.add(slider);

        dificultad1 = new Label("Facil");
        dificultad2 = new Label("Medio");
        dificultad3 = new Label("Dificil");

        dificultad1.setBounds(50,230,60, 20);
        dificultad2.setBounds(150,230,60, 20);
        dificultad3.setBounds(255,230,60, 20);

        dificultad1.setVisible(false);
        dificultad2.setVisible(false);
        dificultad3.setVisible(false);

        this.add(dificultad1);
        this.add(dificultad2);
        this.add(dificultad3);

        // Botón iniciar
        botonIniciar = new Button("Iniciar Juego");
        botonIniciar.setBounds(100, 280, 120, 40);
        add(botonIniciar);

        botonIniciar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Guardar config
                Config.pantallaCompleta = pantallaCompletaCheck.getState();
                Config.sonidoActivado = sonidoCheck.getState();
                Config.singleMode = cpu.getState();
                Config.skin = selectorSkin.getSelectedItem();
                Config.pistaMusical = selectorMusica.getSelectedItem();

                Config.teclaArribaJugador1 = keyCodeArribaJ1;
                Config.teclaAbajoJugador1 = keyCodeAbajoJ1;
                if (Config.singleMode){
                    Config.teclaArribaJugador2 = 0;
                    Config.teclaAbajoJugador2 = 0;
                    Config.multiplicadorDificultad = slider.getValue();
                }else{
                    Config.teclaArribaJugador2 = keyCodeArribaJ2;
                    Config.teclaAbajoJugador2 = keyCodeAbajoJ2;
                    Config.multiplicadorDificultad = 1;
                }

                dispose();
                onConfirm.run();
            }
        });

        // Botón reset
        botonReset = new Button("RESET");
        botonReset.setBounds(250, 280, 120, 40);
        add(botonReset);

        botonReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Config.resetDefaults();
                actualizarVistaDesdeConfig();
            }
        });

        // esconde entradas de teclas para el jugador 2 si se activa cpu
        cpu.addItemListener(new ItemListener() {
            // tengo que usar un ItemListener porque cpu es de tipo checkbox, pero es basicamente lo mismo
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean activado = e.getStateChange() == ItemEvent.SELECTED;
                Config.singleMode = activado;
                teclaArribaJ2.setVisible(!Config.singleMode);
                teclaAbajoJ2.setVisible(!Config.singleMode);
                j2Label.setVisible(!Config.singleMode);

                slider.setVisible(Config.singleMode);
                dificultad1.setVisible(Config.singleMode);
                dificultad2.setVisible(Config.singleMode);
                dificultad3.setVisible(Config.singleMode);
            }
        });

    }

    private TextField crearCampoTecla(int keyCodeInicial, int x, int y, KeyCodeSetter setter, KeyCodeGetter getter) {
        TextField campo = new TextField(KeyEvent.getKeyText(keyCodeInicial), 5);
        campo.setBounds(x, y, 100, 20);
        campo.setEditable(false);

        setter.set(keyCodeInicial);

        campo.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                campo.setText(KeyEvent.getKeyText(code));
                setter.set(code);
            }
        });

        campo.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                campo.setText("");
            }

            public void focusLost(FocusEvent e) {
                if (campo.getText().isEmpty()) {
                    campo.setText(KeyEvent.getKeyText(getter.get()));
                }
            }
        });

        return campo;
    }

    private void actualizarVistaDesdeConfig() {
        pantallaCompletaCheck.setState(Config.pantallaCompleta);
        sonidoCheck.setState(Config.sonidoActivado);
        selectorSkin.select(Config.skin);
        selectorMusica.select(Config.pistaMusical);

        keyCodeArribaJ1 = Config.teclaArribaJugador1;
        keyCodeAbajoJ1 = Config.teclaAbajoJugador1;
        keyCodeArribaJ2 = Config.teclaArribaJugador2;
        keyCodeAbajoJ2 = Config.teclaAbajoJugador2;

        teclaArribaJ1.setText(KeyEvent.getKeyText(keyCodeArribaJ1));
        teclaAbajoJ1.setText(KeyEvent.getKeyText(keyCodeAbajoJ1));
        teclaArribaJ2.setText(KeyEvent.getKeyText(keyCodeArribaJ2));
        teclaAbajoJ2.setText(KeyEvent.getKeyText(keyCodeAbajoJ2));
    }

    // Interfaces funcionales internas para pasar comportamiento
    @FunctionalInterface
    private interface KeyCodeSetter {
        void set(int code);
    }

    @FunctionalInterface
    private interface KeyCodeGetter {
        int get();
    }
}
