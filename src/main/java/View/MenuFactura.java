package View;

import modelos.Factura;
import modelos.Item;
import servicios.FacturaServicio;
import servicios.ItemServicio;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class MenuFactura {
    private final FacturaServicio fServicio;

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");


    public MenuFactura(FacturaServicio fServicio) {
        this.fServicio = fServicio;
    }

    public void mostrarMenu() {
        JFrame frame = new JFrame("Menú de Facturas");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));
        panel.setPreferredSize(new Dimension(300, 400));

        String[] opciones = {"Consultar factura", "Anular factura", "Volver"};

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
            case "Consultar factura":
                consultarFactura();
                break;

            case "Anular factura":
                anularFactura();
                break;

            case "Volver":
                return;

            default:
                break;
        }
    }

    private void consultarFactura() {
        JPanel panelBusqueda = new JPanel(new GridLayout(1, 2));
        JTextField tfNumeroFactura = new JTextField();

        panelBusqueda.add(new JLabel("Número de factura:"));
        panelBusqueda.add(tfNumeroFactura);

        int optionBusqueda = JOptionPane.showConfirmDialog(
                null,
                panelBusqueda,
                "Buscar Factura",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (optionBusqueda == JOptionPane.CANCEL_OPTION) {
            mostrarMenu();
            return;
        }

        if (optionBusqueda == JOptionPane.OK_OPTION) {
            String numeroFactura = tfNumeroFactura.getText().trim();

            try {
                if (numeroFactura.isEmpty()) {
                    mostrarError("Debe ingresar el número de la factura.");
                    return;
                }

                Map<Factura, Map<Item, Integer>> facturaItems = fServicio.porNumeroFactura(numeroFactura);

                Factura factura = facturaItems.keySet().iterator().next();
                Map<Item, Integer> itemsCantidad = facturaItems.get(factura);

                mostrarDetallesFactura(factura, itemsCantidad);

            } catch (SQLException e) {
                mostrarError("Error al buscar la factura: " + e.getMessage());
            }
        }

        mostrarMenu();
    }


    private void mostrarDetallesFactura(Factura factura, Map<Item, Integer> itemsCantidad) {
        StringBuilder detallesFactura = new StringBuilder();
        detallesFactura.append("Detalles de la Factura\n");
        detallesFactura.append("----------------------------\n");
        detallesFactura.append("Número de factura: ").append(factura.getNumeroFactura()).append("\n");
        detallesFactura.append("Paciente: ").append(factura.getPaciente().getNombreCompleto()).append("\n");
        detallesFactura.append("Recepcionista: ").append(factura.getRecepcionista().getNombreCompleto()).append("\n");
        detallesFactura.append("Fecha: ").append(factura.getFechaEmision().format(dateFormatter)).append("\n");
        detallesFactura.append("Método de Pago: ").append(factura.getMetodoPago().getNombre()).append("\n");
        detallesFactura.append("Subtotal: $").append(String.format("%.2f", factura.getTotalPago() / 1.13)).append("\n");
        detallesFactura.append("Impuesto (13%): $").append(String.format("%.2f", factura.getTotalPago() - factura.getTotalPago() / 1.13)).append("\n");
        detallesFactura.append("Total: $").append(String.format("%.2f", factura.getTotalPago())).append("\n\n");
        detallesFactura.append("Items:\n");

        for (Map.Entry<Item, Integer> entry : itemsCantidad.entrySet()) {
            Item item = entry.getKey();
            int cantidad = entry.getValue();
            float precio = item.getPrecio();
            detallesFactura.append(String.format("- %s x%d: $%.2f\n", item.getNombre(), cantidad, cantidad * precio));
        }

        JOptionPane.showMessageDialog(null, detallesFactura.toString(), "Detalles de la Factura", JOptionPane.INFORMATION_MESSAGE);
    }





    private void anularFactura() {
        JPanel panelBusqueda = new JPanel(new GridLayout(1, 2));
        JTextField tfNumeroFactura = new JTextField();

        panelBusqueda.add(new JLabel("Número de factura:"));
        panelBusqueda.add(tfNumeroFactura);

        int optionBusqueda = JOptionPane.showConfirmDialog(
                null,
                panelBusqueda,
                "Buscar Factura para Anular",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (optionBusqueda == JOptionPane.CANCEL_OPTION) {
            mostrarMenu();
            return;
        }

        if (optionBusqueda == JOptionPane.OK_OPTION) {
            String numeroFactura = tfNumeroFactura.getText().trim();

            try {
                if (numeroFactura.isEmpty()) {
                    mostrarError("Debe ingresar el número de la factura.");
                    return;
                }

                Map<Factura, Map<Item, Integer>> facturaItems = fServicio.porNumeroFactura(numeroFactura);

                Factura factura = facturaItems.keySet().iterator().next();
                Map<Item, Integer> itemsCantidad = facturaItems.get(factura);

                mostrarDetallesFactura(factura, itemsCantidad);

                int confirmacion = JOptionPane.showConfirmDialog(
                        null,
                        "¿Estás seguro de que deseas anular esta factura y sus ítems?",
                        "Confirmar Anulación",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirmacion == JOptionPane.YES_OPTION) {
                    
                    fServicio.anularFactura(numeroFactura);
                    JOptionPane.showMessageDialog(null, "Factura anulada correctamente.");
                }

            } catch (SQLException e) {
                mostrarError("Error al buscar/anular la factura: " + e.getMessage());
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
