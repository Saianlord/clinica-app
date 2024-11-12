package modelos;

import java.time.LocalDate;

public class Recepcionista extends Usuario{
    public Recepcionista(String id, String nombre, String apellido, String telefono, LocalDate fechaNacimiento) {
        super(id, nombre, apellido, telefono, fechaNacimiento);
    }
}
