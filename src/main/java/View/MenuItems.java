package View;

import modelos.Item;
import servicios.ItemServicio;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class MenuItems {
    private final ItemServicio iService;

    public MenuItems(ItemServicio iService) {
        this.iService = iService;
    }

    public void mostrarMenu() {
        JFrame frame = new JFrame("Menú de Items");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));
        panel.setPreferredSize(new Dimension(300, 400));

        String[] opciones = {"Nuevo item", "Actualizar item", "Cambiar estado de item", "Volver"};

        for (String opcion : opciones) {
            JButton boton = new JButton(opcion);
            boton.setBackground(Color.CYAN);
            boton.setForeground(Color.BLACK);
            boton.setFont(new Font("Arial", Font.BOLD, 16));
            boton.setFocusPainted(false);

            boton.addActionListener(e -> {
                frame.dispose();
                procesarSeleccion(opcion);
            });

            panel.add(boton);
        }

        frame.add(panel);
        frame.setVisible(true);
    }

    private void procesarSeleccion(String seleccion) {
        switch (seleccion) {
            case "Nuevo item":
                itemForm(false);
                break;

            case "Actualizar item":
                itemForm(true);
                break;

            case "Cambiar estado de item":
                cambiarEstadoItem();
                break;

            case "Volver":
                return;

            default:
                break;
        }
    }

    private void itemForm(boolean esActualizar) {
        Item item = new Item();

        if (esActualizar) {
            JPanel panelBusqueda = new JPanel(new GridLayout(2, 2));
            JTextField tfId = new JTextField();
            JTextField tfNombre = new JTextField();

            panelBusqueda.add(new JLabel("ID del Item:"));
            panelBusqueda.add(tfId);
            panelBusqueda.add(new JLabel("Nombre del Item:"));
            panelBusqueda.add(tfNombre);

            int optionBusqueda = JOptionPane.showConfirmDialog(
                    null,
                    panelBusqueda,
                    "Buscar Item",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (optionBusqueda == JOptionPane.CANCEL_OPTION) {
                mostrarMenu();
                return;
            }

            if (optionBusqueda == JOptionPane.OK_OPTION) {
                String idTexto = tfId.getText().trim();
                String nombre = tfNombre.getText().trim();

                try {
                    if (!idTexto.isEmpty()) {
                        long id = Long.parseLong(idTexto);
                        item = iService.porId(id);

                        if (item == null) {
                            mostrarError("No se encontró un item con el ID proporcionado.");
                            itemForm(esActualizar);
                            return;
                        }
                    } else if (!nombre.isEmpty()) {
                        item = iService.porNombre(nombre);

                        if (item == null) {
                            mostrarError("No se encontró un item con el nombre proporcionado.");
                            itemForm(esActualizar);
                            return;
                        }
                    } else {
                        mostrarError("Debe ingresar el ID o el Nombre del item.");
                        itemForm(esActualizar);
                        return;
                    }
                } catch (NumberFormatException e) {
                    mostrarError("El ID debe ser un número válido.");
                    itemForm(esActualizar);
                    return;
                } catch (SQLException e) {
                    mostrarError("Error al buscar el item: " + e.getMessage());
                    itemForm(esActualizar);
                    return;
                }
            }
        }

        JPanel panel = new JPanel(new GridLayout(5, 2));
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Servicio", "Producto"});
        cbTipo.setSelectedIndex(item.getTipo() == 0 ? 0 : 1); 
        JTextField tfNombre = new JTextField(item.getNombre() != null ? item.getNombre() : "");
        JTextField tfDescripcion = new JTextField(item.getDescripcion() != null ? item.getDescripcion() : "");
        JTextField tfPrecio = new JTextField(item.getPrecio() != 0 ? String.valueOf(item.getPrecio()) : "");

        panel.add(new JLabel("Tipo:"));
        panel.add(cbTipo);
        panel.add(new JLabel("Nombre:"));
        panel.add(tfNombre);
        panel.add(new JLabel("Descripción:"));
        panel.add(tfDescripcion);
        panel.add(new JLabel("Precio:"));
        panel.add(tfPrecio);

        int option = JOptionPane.showConfirmDialog(
                null,
                panel,
                esActualizar ? "Actualizar Item" : "Nuevo Item",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (option == JOptionPane.CANCEL_OPTION) {
            mostrarMenu();
            return;
        }

        if (option == JOptionPane.OK_OPTION) {
            String nombre = tfNombre.getText().trim();
            String descripcion = tfDescripcion.getText().trim();
            String precioStr = tfPrecio.getText().trim();

            if (nombre.isEmpty() || descripcion.isEmpty() || precioStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
                itemForm(esActualizar);
            } else {
                int tipo = cbTipo.getSelectedIndex(); 
                float precio;

                try {
                    precio = Float.parseFloat(precioStr);
                } catch (NumberFormatException e) {
                    mostrarError("El precio debe ser un número válido.");
                    itemForm(esActualizar);
                    return;
                }

                item.setTipo(tipo);
                item.setNombre(nombre);
                item.setDescripcion(descripcion);
                item.setPrecio(precio);

                try {
                    if (esActualizar) {
                        iService.guardarItem(item);
                        JOptionPane.showMessageDialog(null, "Item actualizado correctamente.");
                    } else {
                        iService.guardarItem(item);
                        JOptionPane.showMessageDialog(null, "Item creado correctamente.");
                    }
                } catch (SQLException e) {
                    mostrarError("Se produjo un error al guardar el item: " + e.getMessage());
                }
            }
        }

        mostrarMenu();
    }

    private void cambiarEstadoItem() {
        JPanel panelBusqueda = new JPanel(new GridLayout(2, 2));
        JTextField tfId = new JTextField();
        JTextField tfNombre = new JTextField();

        panelBusqueda.add(new JLabel("ID del Item:"));
        panelBusqueda.add(tfId);
        panelBusqueda.add(new JLabel("Nombre del Item:"));
        panelBusqueda.add(tfNombre);

        int optionBusqueda = JOptionPane.showConfirmDialog(
                null,
                panelBusqueda,
                "Buscar Item",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (optionBusqueda == JOptionPane.CANCEL_OPTION) {
            mostrarMenu();
            return;
        }

        if (optionBusqueda == JOptionPane.OK_OPTION) {
            String idTexto = tfId.getText().trim();
            String nombre = tfNombre.getText().trim();
            Item item;

            try {
                if (!idTexto.isEmpty()) {
                    long id = Long.parseLong(idTexto);
                    item = iService.porId(id);

                    if (item == null) {
                        mostrarError("No se encontró un item con el ID proporcionado.");
                        return;
                    }
                } else if (!nombre.isEmpty()) {
                    item = iService.porNombre(nombre);

                    if (item == null) {
                        mostrarError("No se encontró un item con el nombre proporcionado.");
                        return;
                    }
                } else {
                    mostrarError("Debe ingresar el ID o el Nombre del item.");
                    return;
                }
            } catch (NumberFormatException e) {
                mostrarError("El ID debe ser un número válido.");
                return;
            } catch (SQLException e) {
                mostrarError("Error al buscar el item: " + e.getMessage());
                return;
            }

            JPanel panelEstado = new JPanel(new GridLayout(5, 2));
            panelEstado.add(new JLabel("Nombre:"));
            panelEstado.add(new JLabel(item.getNombre()));
            panelEstado.add(new JLabel("Descripción:"));
            panelEstado.add(new JLabel(item.getDescripcion()));
            panelEstado.add(new JLabel("Precio: "));
            panelEstado.add(new JLabel(String.valueOf(item.getPrecio())));
            panelEstado.add(new JLabel("Estado actual:"));

            String[] estados = {"Activo", "Inactivo"};
            JComboBox<String> cbEstado = new JComboBox<>(estados);
            cbEstado.setSelectedItem(item.getActivo() == 1 ? "Activo" : "Inactivo");
            panelEstado.add(cbEstado);

            int optionEstado = JOptionPane.showConfirmDialog(
                    null,
                    panelEstado,
                    "Cambiar Estado del Item",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (optionEstado == JOptionPane.CANCEL_OPTION) {
                mostrarMenu();
                return;
            }

            if (optionEstado == JOptionPane.OK_OPTION) {
                boolean nuevoEstado = cbEstado.getSelectedItem().equals("Activo");
                item.setActivo(nuevoEstado ? 1 : 0);

                try {
                    iService.guardarItem(item);
                    JOptionPane.showMessageDialog(null, "Estado del item actualizado correctamente.");
                } catch (SQLException e) {
                    mostrarError("Se produjo un error al actualizar el estado: " + e.getMessage());
                }
            }
        }

        mostrarMenu();
    }

    private void mostrarError(String mensaje) {
        JPanel errorPanel = new JPanel();
        errorPanel.setLayout(new BorderLayout());

        JLabel errorLabel = new JLabel("<html><font color='red'>" + mensaje + "</font></html>", JLabel.CENTER);
        errorPanel.add(errorLabel, BorderLayout.CENTER);
        JOptionPane.showMessageDialog(null, errorPanel, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
