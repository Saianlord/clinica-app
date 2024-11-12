package modelos;

import java.time.LocalDate;

public class Odontologo extends Usuario{
    public Odontologo(String id, String nombre, String apellido, String telefono, LocalDate fechaNacimiento) {
        super(id, nombre, apellido, telefono, fechaNacimiento);
    }
}
