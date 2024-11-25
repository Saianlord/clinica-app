package modelos;

import java.time.LocalDate;

public class Item {
    private long id;
    private int activo;
    private int tipo;
    private String nombre;
    private String descripcion;
    private float precio;
    private LocalDate fechaRegistro;

    public Item(int tipo, String nombre, String descripcion, float precio) {
        this.activo = 1;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.fechaRegistro = LocalDate.now();
        this.tipo = tipo;
    }

    public Item() {
        this.activo = 1;
        this.fechaRegistro = LocalDate.now();

    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public int getActivo() {
        return activo;
    }

    public void setActivo(int activo) {
        this.activo = activo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public String toString() {
        return String.format("%-5s %-20s %-20s %-12s %-8s",
                id,
                tipo,
                nombre,
                descripcion,
                precio);
    }
}
