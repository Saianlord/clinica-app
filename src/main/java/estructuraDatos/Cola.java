package estructuraDatos;

import modelos.*;
import servicios.CitaServicio;
import servicios.FacturaServicio;
import servicios.ItemServicio;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cola {

    private Nodo cima;
    private Nodo cola;
    private final CitaServicio cServicio;
    private final FacturaServicio fServicio;
    private final ItemServicio iServicio;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");


    public Cola(CitaServicio cServicio, FacturaServicio fServicio, ItemServicio iServicio) {
        this.cServicio = cServicio;
        this.fServicio = fServicio;
        this.iServicio = iServicio;
        this.cima = null;
        this.cola = null;
    }

    public boolean esVacia() {
        return cima == null;
    }

    private void cargarCitas() {
        try {
            cima = null;
            cola = null;

            List<Cita> citas = cServicio.porFecha(LocalDate.now());
            if (!citas.isEmpty()) {
                for (Cita cita : citas) {
                    Nodo nuevoNodo = new Nodo(cita);
                    if (esVacia()) {
                        cima = nuevoNodo;
                        cola = nuevoNodo;
                    } else {
                        cola.setSiguiente(nuevoNodo);
                        cola = nuevoNodo;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void listarNodos() {
        cargarCitas();

        if (esVacia()) {
            JOptionPane.showMessageDialog(null, "La cola está vacía.");
            return;
        }

        JDialog dialog = new JDialog();
        dialog.setTitle("Listado de Citas");
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 400);

        StringBuilder listado = new StringBuilder();
        listado.append(String.format("%-5s %-20s %-20s %-12s %-8s\n",
                "ID", "Paciente", "Odontólogo", "Fecha", "Hora"));
        listado.append("--------------------------------------------------------------\n");

        Nodo actual = cima;
        while (actual != null) {
            listado.append(actual.getCita().toString()).append("\n");
            actual = actual.getSiguiente();
        }

        JTextArea textArea = new JTextArea(listado.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JButton atenderCitaButton = new JButton("Atender Cita Más Reciente");
        atenderCitaButton.addActionListener(e -> {
            dialog.dispose();
            atenderCita();
        });

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(atenderCitaButton, BorderLayout.SOUTH);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public void atenderCita() {
        if (esVacia()) {
            JOptionPane.showMessageDialog(null, "No hay citas para atender.");
            return;
        }

        JDialog dialog = new JDialog();
        dialog.setTitle("Atender Cita");
        dialog.setLayout(new GridLayout(4, 2));
        dialog.setSize(400, 200);

        JTextField tfHoraFinal = new JTextField();
        JTextField tfObservaciones = new JTextField();
        JTextField tfTotalPago = new JTextField();

        dialog.add(new JLabel("Hora Final (hh:mm AM/PM):"));
        dialog.add(tfHoraFinal);
        dialog.add(new JLabel("Observaciones:"));
        dialog.add(tfObservaciones);
        dialog.add(new JLabel("Total Pago:"));
        dialog.add(tfTotalPago);

        JButton confirmarButton = new JButton("Confirmar");
        confirmarButton.addActionListener(e -> {
            try {
                String horaFinalTexto = tfHoraFinal.getText().trim();
                String observaciones = tfObservaciones.getText().trim();
                String totalPagoTexto = tfTotalPago.getText().trim();

                if (horaFinalTexto.isEmpty() || observaciones.isEmpty() || totalPagoTexto.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Todos los campos son obligatorios.");
                    return;
                }

                Cita citaActual = cima.getCita();
                citaActual.setHoraFinal(LocalTime.parse(horaFinalTexto, timeFormatter));
                citaActual.setObservaciones(observaciones);

                Factura factura = new Factura();
                factura.setCita(citaActual);
                factura.setPaciente(citaActual.getPaciente());
                factura.setRecepcionista(citaActual.getRecepcionista());
                factura.setTotalPago(Float.parseFloat(totalPagoTexto));

                dialog.dispose();
                gestionarItems(factura);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Formato de hora incorrecto. Debe ser HH:mm.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "El monto de pago debe ser un número válido.");
            }
        });

        JButton cancelarButton = new JButton("Cancelar");
        cancelarButton.addActionListener(e -> dialog.dispose());

        dialog.add(confirmarButton);
        dialog.add(cancelarButton);

        dialog.setModal(true);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }


    public void gestionarItems(Factura factura) {
        HashMap<Long, String> items = new HashMap<>();
        boolean deseaContinuar = true;

        while (deseaContinuar) {
            JPanel panel = crearPanelItems();
            int opcionTipo = mostrarOpcionAgregarItem();

            if (opcionTipo != JOptionPane.YES_OPTION) break;

            JTextArea taItems = crearListadoItems();
            JTextField tfIdItem = new JTextField();
            JTextField tfCantidad = new JTextField();
            agregarComponentesPanel(panel, taItems, tfIdItem, tfCantidad);

            if (mostrarDialogoAgregarItem(panel) == JOptionPane.CANCEL_OPTION) break;

            try {
                procesarItem(tfIdItem, tfCantidad, items);
                if (!preguntarContinuar()) deseaContinuar = false;
            } catch (NumberFormatException e) {
                mostrarMensajeError("La cantidad debe ser un número válido.");
            } catch (Exception e) {
                mostrarMensajeError("Error al procesar el item: " + e.getMessage());
            }
        }

        calcularTotal(factura, items);
    }

    private JPanel crearPanelItems() {
        return new JPanel(new GridLayout(3, 2));
    }

    private int mostrarOpcionAgregarItem() {
        return JOptionPane.showOptionDialog(
                null,
                "¿Desea agregar un producto o servicio?",
                "Seleccionar Tipo",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Sí", "No"},
                "No"
        );
    }

    private JTextArea crearListadoItems() {
        StringBuilder listado = new StringBuilder();
        listado.append(String.format("%-5s %-10s %-20s %-20s %-12s\n",
                "Id", "Tipo", "Nombre", "Descripción", "Precio"));
        listado.append("--------------------------------------------------------\n");

        try {
            for (Item item : iServicio.listarItemsActivos()) {
                String tipoItem = (item.getTipo() == 0) ? "Servicio" : "Producto";
                listado.append(String.format("%-5d %-10s %-20s %-20s %-12s\n",
                        item.getId(), tipoItem, item.getNombre(), item.getDescripcion(), "$" + item.getPrecio()));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        JTextArea taItems = new JTextArea(listado.toString());
        taItems.setEditable(false);
        return taItems;
    }

    private void agregarComponentesPanel(JPanel panel, JTextArea taItems, JTextField tfIdItem, JTextField tfCantidad) {
        panel.add(new JLabel("Items Activos:"));
        panel.add(new JScrollPane(taItems));
        panel.add(new JLabel("Ingrese ID del Item:"));
        panel.add(tfIdItem);
        panel.add(new JLabel("Ingrese Cantidad:"));
        panel.add(tfCantidad);
    }

    private int mostrarDialogoAgregarItem(JPanel panel) {
        return JOptionPane.showConfirmDialog(
                null,
                panel,
                "Agregar Item",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
    }

    private void procesarItem(JTextField tfIdItem, JTextField tfCantidad, HashMap<Long, String> items) throws Exception {
        String idItem = tfIdItem.getText().trim();
        String cantidadTexto = tfCantidad.getText().trim();

        if (idItem.isEmpty() || cantidadTexto.isEmpty()) {
            throw new IllegalArgumentException("Todos los campos son obligatorios.");
        }

        long id = Long.parseLong(idItem);
        int cantidad = Integer.parseInt(cantidadTexto);

        if (items.containsKey(id)) {
            String[] valueSplit = items.get(id).split(",");
            int cantidadActual = Integer.parseInt(valueSplit[0]);
            float precio = Float.parseFloat(valueSplit[1]);
            cantidad += cantidadActual;
            items.put(id, cantidad + "," + precio);
        } else {
            float precio = iServicio.porId(id).getPrecio();
            items.put(id, cantidad + "," + precio);
        }

        mostrarMensajeExito("Item añadido con éxito.");
    }

    private boolean preguntarContinuar() {
        return JOptionPane.showConfirmDialog(
                null,
                "¿Desea agregar otro item?",
                "Continuar",
                JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION;
    }

    private void calcularTotal(Factura factura, Map<Long, String> items) {
        float total = calcularSubtotal(factura.getTotalPago(), items);
        total += calcularImpuesto(total);
        factura.setTotalPago(total);

        facturar(factura, items);
    }

    private float calcularSubtotal(float subtotalInicial, Map<Long, String> items) {
        float subtotal = subtotalInicial;

        for (Map.Entry<Long, String> entry : items.entrySet()) {
            String[] valueSplit = entry.getValue().split(",");
            int cantidad = Integer.parseInt(valueSplit[0]);
            float precio = Float.parseFloat(valueSplit[1]);
            subtotal += cantidad * precio;
        }

        return subtotal;
    }

    private float calcularImpuesto(float total) {
        return total * 0.13f;
    }

    private void facturar(Factura factura, Map<Long, String> items) {
        MetodoPago metodoPago = seleccionarMetodoPago();
        if (metodoPago == null) return;

        factura.setMetodoPago(metodoPago);

        if (confirmarGenerarFactura()) {
            try {
                guardarFactura(factura, items);
            } catch (Exception e) {
                mostrarMensajeError("Error al generar la factura: " + e.getMessage());
            }
        }
    }

    private MetodoPago seleccionarMetodoPago() {
        MetodoPago[] metodosPago = MetodoPago.values();
        String[] opciones = new String[metodosPago.length];

        for (int i = 0; i < metodosPago.length; i++) {
            opciones[i] = metodosPago[i].getNombre();
        }

        int opcion = JOptionPane.showOptionDialog(
                null,
                "Seleccione el método de pago",
                "Método de Pago",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        return (opcion == -1) ? null : metodosPago[opcion];
    }

    private boolean confirmarGenerarFactura() {
        int confirmacion = JOptionPane.showConfirmDialog(
                null,
                "¿Desea generar la factura?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );
        return confirmacion == JOptionPane.YES_OPTION;
    }

    private void guardarFactura(Factura factura, Map<Long, String> items) throws Exception {
        FacturaResultado facturaResultado = fServicio.guardarFactura(factura, items);
        cima.getCita().setEstado(EstadoCita.COMPLETADA);
        cServicio.guardarCita(cima.getCita());
        mostrarMensajeExito("Factura generada con éxito.");
        mostrarDetallesFactura(factura, facturaResultado, items);

    }

    private void mostrarMensajeError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje);
    }

    private void mostrarMensajeExito(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje);
    }

    private void mostrarDetallesFactura(Factura factura, FacturaResultado facturaResultado, Map<Long, String> items) {
        StringBuilder detallesFactura = new StringBuilder();
        detallesFactura.append("Factura Generada\n");
        detallesFactura.append("----------------------------\n");
        detallesFactura.append("Número de factura: ").append(facturaResultado.getNumeroFactura()).append("\n");
        detallesFactura.append("Paciente: ").append(factura.getPaciente().getNombreCompleto()).append("\n");
        detallesFactura.append("Recepcionista: ").append(factura.getRecepcionista().getNombreCompleto()).append("\n");
        detallesFactura.append("Fecha: ").append(factura.getFechaEmision().format(dateFormatter)).append("\n");
        detallesFactura.append("Método de Pago: ").append(factura.getMetodoPago().getNombre()).append("\n");
        detallesFactura.append("Subtotal: $").append(String.format("%.2f", factura.getTotalPago() / 1.13)).append("\n");
        detallesFactura.append("Impuesto (13%): $").append(String.format("%.2f", factura.getTotalPago() - factura.getTotalPago() / 1.13)).append("\n");
        detallesFactura.append("Total: $").append(String.format("%.2f", factura.getTotalPago())).append("\n\n");
        detallesFactura.append("Items:\n");

        for (Map.Entry<Long, String> entry : items.entrySet()) {
            String[] valueSplit = entry.getValue().split(",");
            int cantidad = Integer.parseInt(valueSplit[0]);
            float precio = Float.parseFloat(valueSplit[1]);
            Item item = null;
            try {
                item = iServicio.porId(entry.getKey());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            detallesFactura.append(String.format("- %s x%d: $%.2f\n", item.getNombre(), cantidad, cantidad * precio));
        }

        JOptionPane.showMessageDialog(null, detallesFactura.toString(), "Detalles de la Factura", JOptionPane.INFORMATION_MESSAGE);
    }


}
