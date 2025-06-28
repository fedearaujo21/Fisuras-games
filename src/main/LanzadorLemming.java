package main;

import lemmings.modelo.Config;
import lemmings.vista.PanelLemmings;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LanzadorLemming{

    private static JPanel cardPanel;
    private static CardLayout cardLayout;

    public static void iniciar(JFrame ventana){

        ventana.getContentPane().removeAll(); // Quita todos los componentes
        ventana.repaint();
        ventana.revalidate();
        ventana.setLayout(new BorderLayout());

        ventana.setTitle("Configuración del Juego - Lemmings Z");
        ventana.setSize(800, 600);
        //ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); mepa que ya esta implementado
        ventana.setLocationRelativeTo(null); // Centra la ventana

        // Panel principal con layout vertical
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // padding

        // Selector de nivel
        String[] niveles = {"Nivel 1", "Nivel 2", "Nivel 3", "Nivel 4"};
        JComboBox<String> selectorNivel = new JComboBox<>(niveles);

        //Card Layout para mostrar los niveles
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(crearSlide("src/lemmings/recursos/Nivel1View.png"), "Nivel 1");
        cardPanel.add(crearSlide("src/lemmings/recursos/Nivel2View.png"), "Nivel 2");
        cardPanel.add(crearSlide("src/lemmings/recursos/Nivel3View.png"), "Nivel 3");
        cardPanel.add(crearSlide("src/lemmings/recursos/Nivel4View.png"), "Nivel 4");

        ventana.add(cardPanel, BorderLayout.CENTER);

        // Cambiar slide al seleccionar nivel
        selectorNivel.addActionListener(e -> {
            String nivelSeleccionado = (String) selectorNivel.getSelectedItem();
            cardLayout.show(cardPanel, nivelSeleccionado);
        });

        //
        // Mute
        JPanel mutePanel = new JPanel();
        mutePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JCheckBox muteCheck = new JCheckBox("Mute");
        mutePanel.add(muteCheck);

        // Volumen
        JPanel volumenPanel = new JPanel();
        volumenPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        volumenPanel.add(new JLabel("Volumen:"));
        JSlider volumenSlider = new JSlider(0, 100, 50);
        volumenSlider.setMajorTickSpacing(25);
        volumenSlider.setPaintTicks(true);
        volumenSlider.setPaintLabels(true);
        volumenPanel.add(volumenSlider);

        // Botón Aceptar
        JButton aceptarButton = new JButton("Aceptar");
        aceptarButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        aceptarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Config.mute = muteCheck.isSelected();
                Config.volumen = volumenSlider.getValue();
                ventana.dispose(); // cerrar la ventana de configuración

                JFrame juegoFrame = new JFrame("Lemmings Z");
                juegoFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                juegoFrame.setSize(800, 600);
                juegoFrame.setResizable(false);
                juegoFrame.setLocationRelativeTo(null);

                PanelLemmings juego = new PanelLemmings(/*juegoFrame,*/ selectorNivel.getSelectedIndex() + 1);
                juegoFrame.setContentPane(juego);
                juegoFrame.setVisible(true);

                System.out.println(selectorNivel.getSelectedIndex());
            }
        });

        // Agregar todo al panel principal
        panelPrincipal.add(selectorNivel);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));
        panelPrincipal.add(mutePanel);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));
        panelPrincipal.add(volumenPanel);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 20)));
        panelPrincipal.add(aceptarButton);


        ventana.add(panelPrincipal, BorderLayout.SOUTH);
        ventana.setVisible(true);
    }

    private static JPanel crearSlide(String rutaImagen) {
        JPanel panel = new JPanel();
        JLabel label = new JLabel();

        ImageIcon icono = new ImageIcon(rutaImagen);
        label.setIcon(icono);
        panel.add(label);

        return panel;
    }
}
