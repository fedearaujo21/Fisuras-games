package main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MenuPrincipal extends Panel {

    private final JFrame ventana;

    public MenuPrincipal(JFrame ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Logo superior
        JLabel logo = new JLabel();
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource("/lemmings/recursos/logoFisuras.png"));
        Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(300, -1, Image.SCALE_SMOOTH);
        logo.setIcon(new ImageIcon(imagenEscalada));
        logo.setBorder(new EmptyBorder(20, 0, 10, 0));
        add(logo, BorderLayout.NORTH);

        // Cards de juegos
        JPanel contenedorJuegos = new JPanel();
        contenedorJuegos.setBackground(Color.BLACK);
        contenedorJuegos.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 20));

        List<CardJuego> cards = new ArrayList<>();

        cards.add(new CardJuego("Lemmings", "/lemmings/recursos/previewLemmings.png", () -> LanzadorLemming.iniciar(ventana)));
        cards.add(new CardJuego("Pong", "/lemmings/recursos/previewPong.png", () -> new LanzadorPong(ventana)));

        for (CardJuego card : cards) {
            contenedorJuegos.add(card);
        }

        add(contenedorJuegos, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel("Desarrollado por Fisuras Team", SwingConstants.CENTER);
        footer.setForeground(Color.LIGHT_GRAY);
        footer.setFont(new Font("Arial", Font.ITALIC, 12));
        footer.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(footer, BorderLayout.SOUTH);

        ventana.setTitle("Fisuras Games");
        ventana.setResizable(false);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setContentPane(this);
        ventana.pack();
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }
}
