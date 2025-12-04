package dto;

public class RegistroFormularioDTO {
    private final String nombre;
    private final String apellido;
    private final String dni;
    private final String nombreUsuario;
    private final String email;
    private final String contrasenia;

    public RegistroFormularioDTO(String nombre, String apellido, String dni, String nombreUsuario, String email, String contrasenia) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.contrasenia = contrasenia;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getDni() {
        return dni;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getEmail() {
        return email;
    }

    public String getContrasenia() {
        return contrasenia;
    }
}
