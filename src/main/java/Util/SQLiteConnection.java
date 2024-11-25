package Util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLiteConnection {

    private static String DATABASE_URL = "jdbc:sqlite:baseDatos/clinica_base_datos.db";
    private static HikariDataSource dataSource;

    private SQLiteConnection() {
    }

    public static Connection getConnection() {
        if (dataSource == null) {
            synchronized (SQLiteConnection.class) {
                if (dataSource == null) {
                    HikariConfig config = new HikariConfig();
                    config.setJdbcUrl(DATABASE_URL);
                    config.setMaximumPoolSize(1);
                    config.setMinimumIdle(1);
                    config.setConnectionTestQuery("SELECT 1");
                    dataSource = new HikariDataSource(config);
                }
            }
        }

        try {
            Connection connection = dataSource.getConnection();
            JOptionPane.showMessageDialog(null, "Conexión exitosa a la base de datos", "Conexión", JOptionPane.INFORMATION_MESSAGE);
            return connection;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos: " + e.getMessage(), "Error de conexión", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);
        }
    }

}
