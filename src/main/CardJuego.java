package main;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class CardJuego extends JPanel {
    private final String nombre;

    public CardJuego(String nombre, String rutaPreview, Runnable accion) {
        this.nombre = nombre;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(180, 250));
        setBackground(Color.BLACK);
        setBorder(new LineBorder(Color.YELLOW, 2));

        JLabel titulo = new JLabel(nombre, SwingConstants.CENTER);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 14));
        titulo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(titulo, BorderLayout.NORTH);

        JLabel preview = new JLabel();
        preview.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource(rutaPreview));
        Image imagen = iconoOriginal.getImage().getScaledInstance(160, 190, Image.SCALE_SMOOTH);
        preview.setIcon(new ImageIcon(imagen));
        add(preview, BorderLayout.CENTER);

        JButton btn = new JButton("Jugar");
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(Color.LIGHT_GRAY);
        btn.setForeground(Color.BLACK);
        btn.addActionListener(e -> accion.run());
        add(btn, BorderLayout.SOUTH);
    }

    public String getNombre() {
        return nombre;
    }
}

