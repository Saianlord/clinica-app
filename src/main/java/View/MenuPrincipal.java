package View;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipal {
    private final MenuCitas menuCitas;
    private final MenuUsuarios menuUsuarios;
    private final MenuItems menuItems;
    private final MenuFactura menuFactura;

    public MenuPrincipal(MenuCitas menuCitas, MenuUsuarios menuUsuarios, MenuItems menuItems, MenuFactura menuFactura) {
        this.menuCitas = menuCitas;
        this.menuUsuarios = menuUsuarios;
        this.menuItems = menuItems;
        this.menuFactura = menuFactura;
    }

    public void mostrarMenu() {
        JFrame frame = new JFrame("Menú Principal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 10, 10));  
        panel.setPreferredSize(new Dimension(300, 400));

        String[] opciones = {
                "Administrar usuarios",
                "Administrar citas",
                "Administrar Items",
                "Administrar Facturas",
                "Salir"
        };

        for (String opcion : opciones) {
            JButton boton = new JButton(opcion);
            boton.setBackground(Color.CYAN);
            boton.setForeground(Color.BLACK);
            boton.setFont(new Font("Arial", Font.BOLD, 16));
            boton.setFocusPainted(false);

            boton.addActionListener(e -> {
                procesarSeleccion(opcion);
            });

            panel.add(boton);
        }

        frame.add(panel);
        frame.setVisible(true);
    }

    private void procesarSeleccion(String seleccion) {
        switch (seleccion) {
            case "Administrar usuarios":
                menuUsuarios.mostrarMenu();
                break;

            case "Administrar citas":
                menuCitas.mostrarMenu();
                break;

            case "Administrar Items":
                menuItems.mostrarMenu();
                break;

            case "Administrar Facturas":
                menuFactura.mostrarMenu();
                break;  

            case "Salir":
                JOptionPane.showMessageDialog(null, "¡Hasta luego!");
                System.exit(0);
                break;

            default:
                JOptionPane.showMessageDialog(null, "Operación cancelada.");
                System.exit(0);
        }
    }
}
