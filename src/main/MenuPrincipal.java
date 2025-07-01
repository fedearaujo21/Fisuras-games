package main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MenuPrincipal extends Panel {

    private final JFrame ventana;
    private JTextField buscador;
    private List<CardJuego> cards;
    private JPanel contenedorJuegos;

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

        // Panel central (buscador + cards)
        JPanel panelCentro = new JPanel();
        panelCentro.setLayout(new BorderLayout());
        panelCentro.setBackground(Color.BLACK);

        // Buscador
        buscador = new JTextField();
        buscador.setPreferredSize(new Dimension(300, 30));
        buscador.setMaximumSize(new Dimension(300, 30));
        buscador.setFont(new Font("Arial", Font.PLAIN, 14));
        buscador.setHorizontalAlignment(SwingConstants.LEFT);

        JPanel panelBuscador = new JPanel();
        panelBuscador.setBackground(Color.BLACK);
        panelBuscador.add(buscador);
        panelCentro.add(panelBuscador, BorderLayout.NORTH);

        // Cards de juegos
//        contenedorJuegos = new JPanel();
//        contenedorJuegos.setBackground(Color.BLACK);
//        contenedorJuegos.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 20));
        contenedorJuegos = new JPanel();
        contenedorJuegos.setBackground(Color.BLACK);
        contenedorJuegos.setLayout(new BoxLayout(contenedorJuegos, BoxLayout.X_AXIS));

// Panel contenedor con scroll horizontal
        JScrollPane scrollPane = new JScrollPane(contenedorJuegos, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(600, 300)); // 280 en lugar de 250
        scrollPane.setBorder(null);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16); // velocidad de scroll

        cards = new ArrayList<>();

        cards.add(new CardJuego("Lemmings", "/lemmings/recursos/previewLemmings.png", () -> LanzadorLemming.iniciar(ventana)));
        cards.add(new CardJuego("Pong", "/lemmings/recursos/previewPong.png", () -> new LanzadorPong(ventana)));
        cards.add(new CardJuego("POO Game", "/lemmings/recursos/previewPOO.png", () -> LanzadorPoo.iniciar(ventana)));
        cards.add(new CardJuego("POO Game", "/lemmings/recursos/previewPOO.png", () -> LanzadorPoo.iniciar(ventana)));
        cards.add(new CardJuego("POO Game", "/lemmings/recursos/previewPOO.png", () -> LanzadorPoo.iniciar(ventana)));
        cards.add(new CardJuego("POO Game", "/lemmings/recursos/previewPOO.png", () -> LanzadorPoo.iniciar(ventana)));

        for (CardJuego card : cards) {
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setOpaque(false); // fondo negro visible
            wrapper.setBorder(new EmptyBorder(10, 10, 20, 10)); // márgenes: arriba, izq, abajo, der
            wrapper.add(card, BorderLayout.CENTER);
            contenedorJuegos.add(wrapper);
        }


        panelCentro.add(scrollPane, BorderLayout.CENTER);
//        panelCentro.add(contenedorJuegos, BorderLayout.CENTER);
        add(panelCentro, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel("Desarrollado por Fisuras Team", SwingConstants.CENTER);
        footer.setForeground(Color.LIGHT_GRAY);
        footer.setFont(new Font("Arial", Font.ITALIC, 12));
        footer.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(footer, BorderLayout.SOUTH);

        // Acción de búsqueda
        buscador.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filtrar();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filtrar();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filtrar();
            }
        });


        // Ventana
        ventana.setTitle("Fisuras Games");
        ventana.setResizable(false);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setContentPane(this);
        ventana.pack();
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    private void filtrar() {
        String texto = buscador.getText().trim().toLowerCase();
        contenedorJuegos.removeAll();
        for (CardJuego card : cards) {
            if (card.getNombre().toLowerCase().contains(texto)) {
                contenedorJuegos.add(card);
            }
        }
        contenedorJuegos.revalidate();
        contenedorJuegos.repaint();
    }
}
