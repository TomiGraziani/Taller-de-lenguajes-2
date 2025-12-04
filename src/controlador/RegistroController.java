package controlador;

import servicio.AutenticacionService;
import servicio.RespuestaOperacion;
import ventana.FormularioRegistro;
import ventana.Main;
import ventana.PanelRegistro;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegistroController {

    private final Main mainWindow;
    private final PanelRegistro vista;
    private final AutenticacionService servicio;

    public RegistroController(Main mainWindow, PanelRegistro vista, AutenticacionService servicio) {
        this.mainWindow = mainWindow;
        this.vista = vista;
        this.servicio = servicio;
        configurarEventos();
    }

    private void configurarEventos() {
        vista.getFormularioRegistro().getBtnRegistrar().addActionListener(e -> manejarRegistro());
        vista.getBtnVolver().addActionListener(e -> mainWindow.mostrarLogin());
        vista.getBtnVolver().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                vista.cambiarColorVolver(new Color(60, 0, 200));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                vista.cambiarColorVolver(Color.BLUE);
            }
        });
    }

    private void manejarRegistro() {
        FormularioRegistro formulario = vista.getFormularioRegistro();
        RespuestaOperacion respuesta = servicio.registrarUsuario(formulario.obtenerDatosRegistro());

        if (!respuesta.esExitoso()) {
            formulario.mostrarError(respuesta.getMensaje());
            return;
        }

        formulario.mostrarMensaje(respuesta.getMensaje());
        formulario.limpiarCampos();
        mainWindow.mostrarLogin();
    }
}
