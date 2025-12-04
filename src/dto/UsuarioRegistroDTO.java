package dto;

import modelo.DatosPersonales;

public class UsuarioRegistroDTO {
    private final DatosPersonales datosPersonales;
    private final String nombreUsuario;
    private final String email;
    private final String contrasenia;

    public UsuarioRegistroDTO(DatosPersonales datosPersonales, String nombreUsuario, String email, String contrasenia) {
        this.datosPersonales = datosPersonales;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.contrasenia = contrasenia;
    }

    public DatosPersonales getDatosPersonales() {
        return datosPersonales;
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
