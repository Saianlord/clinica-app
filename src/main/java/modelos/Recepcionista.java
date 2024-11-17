package modelos;

import java.time.LocalDate;

public class Recepcionista extends Usuario{
    public Recepcionista(long id, String nombre, String apellido, String telefono, LocalDate fechaNacimiento) {
        super(id, 3,  nombre, apellido, telefono, fechaNacimiento);
    }

    public Recepcionista() {
    }
}
