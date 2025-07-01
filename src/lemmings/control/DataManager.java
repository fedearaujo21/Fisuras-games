package lemmings.control;

import java.sql.*;
import java.util.ArrayList;

public class DataManager {

    public DataManager(){}

    public static void insert(String nombreTabla, String nombreUsuario, int puntaje){
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:data/lemmings.db");
            System.out.println("Conectado a SQLite");

            //verifico que la tabla exista
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS ranking"+ nombreTabla + " (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, puntos INTEGER)");

            //inserto el valor
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO ranking" + nombreTabla + " (nombre, puntos) VALUES(?, ?)");
            pstmt.setString(1, nombreUsuario);
            pstmt.setInt(2, puntaje);
            pstmt.executeUpdate();

            //calculo que no haya mas de 10 en el ranking

            int count = 0;
            int idUltimaPosicion = 0;
            ResultSet rsContador = stmt.executeQuery("SELECT * FROM ranking" + nombreTabla + " ORDER BY puntos DESC");
            while (rsContador.next()) {
                count++;
                idUltimaPosicion = rsContador.getInt("id");
            }
            System.out.println("Cantidad de filas: " + count);
            rsContador.close();

            if(count > 10) {
                pstmt = conn.prepareStatement("DELETE FROM ranking" + nombreTabla +" WHERE id = ?");
                pstmt.setInt(1, idUltimaPosicion);
                pstmt.executeUpdate();
            }
            pstmt.close();
            stmt.close();

            /*while (rs.next()) {
                System.out.println("Nivel: " + rs.getString("nivel") + ", Puntos: " + rs.getInt("puntos"));
            }*/

            //elimino la fila excedente en caso de existir


            conn.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static ArrayList<String> getRanking(String nombreTabla){
        ArrayList<String> salida = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:data/lemmings.db");
            System.out.println("Conectado a SQLite");
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT nombre, puntos FROM ranking" + nombreTabla + " ORDER BY puntos DESC");

            while (rs.next()){
                 salida.add(rs.getString("nombre") + "            " + Integer.toString(rs.getInt("puntos")));
            }

            rs.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return salida;
    }

    public static void eliminarTabla(String nombreTabla) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:data/lemmings.db");
            System.out.println("Conectado a SQLite");

            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DROP TABLE IF EXISTS ranking" + nombreTabla);

            System.out.println("Tabla ranking" + nombreTabla + " eliminada (si existía)");

            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Error al eliminar tabla: " + e.getMessage());
        }
    }

}