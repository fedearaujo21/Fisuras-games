package lemmings.control;

import java.io.File; // Importa la clase File
import java.sql.*;
import java.util.ArrayList;

public class DataManager {

    private static final String DB_FOLDER = "data";
    private static final String DB_NAME = "lemmings.db";
    private static final String DB_PATH = DB_FOLDER + File.separator + DB_NAME; // Usa File.separator para compatibilidad OS

    public DataManager(){
        // Asegurarse de que la carpeta 'data' exista al inicializar o antes de cualquier operación DB
        ensureDataFolderExists();
    }

    private static void ensureDataFolderExists() {
        File dataDir = new File(DB_FOLDER);
        if (!dataDir.exists()) {
            if (dataDir.mkdirs()) { // Crea la carpeta 'data' y cualquier padre necesario
                System.out.println("Carpeta '" + DB_FOLDER + "' creada en: " + dataDir.getAbsolutePath());
            } else {
                System.err.println("Error: No se pudo crear la carpeta '" + DB_FOLDER + "'.");
            }
        }
    }

    public static void insert(String nombreTabla, String nombreUsuario, int puntaje){
        ensureDataFolderExists(); // Asegura que la carpeta exista antes de intentar conectar
        try {
            // Imprime la ruta absoluta para depuración
            System.out.println("Intentando conectar a la base de datos en: " + new File(DB_PATH).getAbsolutePath());
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
            System.out.println("Conectado a SQLite");

            // Verifico que la tabla exista
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS ranking" + nombreTabla + " (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, puntos INTEGER)");

            // Inserto el valor
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO ranking" + nombreTabla + " (nombre, puntos) VALUES(?, ?)");
            pstmt.setString(1, nombreUsuario);
            pstmt.setInt(2, puntaje);
            pstmt.executeUpdate();

            // Calculo que no haya más de 10 en el ranking
            int count = 0;
            int idUltimaPosicion = 0;
            // Ordena por puntos de forma ascendente para encontrar el que tiene menos puntos si hay más de 10
            ResultSet rsContador = stmt.executeQuery("SELECT id, puntos FROM ranking" + nombreTabla + " ORDER BY puntos ASC");
            while (rsContador.next()) {
                count++;
                // Si el contador es mayor a 10, el último registro (el de menor puntaje) será el que se eliminará
                if (count > 10) {
                    idUltimaPosicion = rsContador.getInt("id");
                    break; // Salir una vez que encontramos el primero a eliminar
                }
            }
            System.out.println("Cantidad de filas: " + count);
            rsContador.close();

            if(count > 10) {
                pstmt = conn.prepareStatement("DELETE FROM ranking" + nombreTabla +" WHERE id = ?");
                pstmt.setInt(1, idUltimaPosicion);
                pstmt.executeUpdate();
                System.out.println("Se eliminó la entrada con ID: " + idUltimaPosicion + " para mantener el ranking en 10.");
            }
            pstmt.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("Error en insert DataManager: " + e.getMessage()); // Usa System.err para errores
            e.printStackTrace(); // Imprime el stack trace para depuración detallada
        }
    }

    public static ArrayList<String> getRanking(String nombreTabla){
        ArrayList<String> salida = new ArrayList<>();
        ensureDataFolderExists(); // Asegura que la carpeta exista antes de intentar conectar
        try {
            System.out.println("Intentando conectar a la base de datos en: " + new File(DB_PATH).getAbsolutePath());
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
            System.out.println("Conectado a SQLite");
            Statement stmt = conn.createStatement();

            // Verifica si la tabla existe antes de intentar consultarla
            DatabaseMetaData md = conn.getMetaData();
            ResultSet tables = md.getTables(null, null, "ranking" + nombreTabla, null);
            if (!tables.next()) {
                // La tabla no existe, no hay ranking para mostrar
                System.out.println("La tabla 'ranking" + nombreTabla + "' no existe.");
                tables.close();
                conn.close();
                return salida; // Retorna un ArrayList vacío
            }
            tables.close(); // Cierra el ResultSet de tables

            ResultSet rs = stmt.executeQuery("SELECT nombre, puntos FROM ranking" + nombreTabla + " ORDER BY puntos DESC");

            while (rs.next()){
                salida.add(rs.getString("nombre") + "      " + Integer.toString(rs.getInt("puntos")));
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("Error en getRanking DataManager: " + e.getMessage()); // Usa System.err para errores
            e.printStackTrace(); // Imprime el stack trace para depuración detallada
        }
        return salida;
    }
}