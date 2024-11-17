package Util;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnection {

    private static final String DATABASE_URL = "jdbc:sqlite:baseDatos/clinica_base_datos.db";
    private static Connection connection;

    private SQLiteConnection() {
    }

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(DATABASE_URL);
                showAutoClosingMessage("Conexión a SQLite establecida.", "Conexión Exitosa", JOptionPane.INFORMATION_MESSAGE);
                return connection;
            } catch (SQLException e) {
                showAutoClosingMessage("Error al conectar a la base de datos: " + e.getMessage(),
                        "Error de Conexión", JOptionPane.ERROR_MESSAGE);
            }
        }
        return connection;
    }

    private static void showAutoClosingMessage(String message, String title, int messageType) {
        JOptionPane pane = new JOptionPane(message, messageType, JOptionPane.DEFAULT_OPTION);
        javax.swing.JDialog dialog = pane.createDialog(title);
        dialog.setModal(false);
        dialog.setVisible(true);

        new Timer(10000, e -> dialog.dispose()).start();
    }

}
