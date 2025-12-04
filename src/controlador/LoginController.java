package controlador;

import servicio.AutenticacionService;
import servicio.RespuestaOperacion;
import ventana.FormularioLogin;
import ventana.Main;
import ventana.PanelLogin;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginController {

    private final Main mainWindow;
    private final PanelLogin vista;
    private final AutenticacionService servicio;

    public LoginController(Main mainWindow, PanelLogin vista, AutenticacionService servicio) {
        this.mainWindow = mainWindow;
        this.vista = vista;
        this.servicio = servicio;
        configurarEventos();
    }

    private void configurarEventos() {
        FormularioLogin formulario = vista.getFormularioLogin();
        formulario.getBotonIngresar().addActionListener(e -> manejarIngreso());

        vista.getBtnRegistrar().addActionListener(e -> mainWindow.mostrarRegistro());
        vista.getBtnRegistrar().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                vista.cambiarColorRegistrar(new Color(60, 0, 200));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                vista.cambiarColorRegistrar(Color.BLUE);
            }
        });
    }

    private void manejarIngreso() {
        FormularioLogin formulario = vista.getFormularioLogin();
        String email = formulario.getEmail();
        String contrasenia = formulario.getContrasenia();

        RespuestaOperacion respuesta = servicio.autenticar(email, contrasenia);
        if (!respuesta.esExitoso()) {
            formulario.mostrarError(respuesta.getMensaje());
            return;
        }

        formulario.mostrarMensaje(respuesta.getMensaje());
        formulario.limpiarCampos();
        mainWindow.abrirPlataforma(respuesta.getUsuario());
    }
}
