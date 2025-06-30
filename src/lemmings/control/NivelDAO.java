package lemmings.control;

import lemmings.modelo.*;

import java.sql.*;

public class NivelDAO {

    public static NivelInfo obtenerNivelPorNumero(int numero) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:data/lemmings.db")) {

            String query = "SELECT * FROM niveles WHERE numero = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, numero);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Parsear entrada
                String[] partesEntrada = rs.getString("entrada").split(",");
                Entrada entrada = new Entrada(
                        Integer.parseInt(partesEntrada[0]),
                        Integer.parseInt(partesEntrada[1])
                );

                // Parsear salida
                String[] partesSalida = rs.getString("salida").split(",");
                Salida salida = new Salida(
                        Integer.parseInt(partesSalida[0]),
                        Integer.parseInt(partesSalida[1]),
                        Integer.parseInt(partesSalida[2]),
                        Integer.parseInt(partesSalida[3])
                );

                // Parsear habilidades
                Stock stock = new Stock();
                String[] habilidades = rs.getString("habilidades").split(",");
                for (String h : habilidades) {
                    String[] partes = h.split(":");
                    String nombreHab = partes[0];
                    int cantidad = Integer.parseInt(partes[1]);
                    stock.anadirHabilidad(nombreHab, cantidad);
                }

                return new NivelInfo(
                        rs.getInt("numero"),
                        rs.getString("nombre"),
                        rs.getString("rutaImagen"),
                        rs.getString("rutaMusica"),
                        rs.getString("rutaVista"),
                        entrada,
                        salida,
                        stock,
                        rs.getInt("tiempo"),
                        rs.getInt("frecuenciaSpawn"),
                        rs.getInt("cantidadLem"),
                        rs.getInt("objetivoLemmings")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener nivel de la base de datos: " + e.getMessage());
        }

        return null;
    }
}
