package servicio;

import modelo.Usuario;

public class RespuestaOperacion {
    private final boolean exito;
    private final String mensaje;
    private final Usuario usuario;

    public RespuestaOperacion(boolean exito, String mensaje) {
        this(exito, mensaje, null);
    }

    public RespuestaOperacion(boolean exito, String mensaje, Usuario usuario) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.usuario = usuario;
    }

    public boolean esExitoso() {
        return exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
