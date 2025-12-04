package controlador;

import dto.DatosPersonalesDTO;
import dto.PeliculaDTO;
import dto.ReseniaDTO;
import dto.UsuarioRegistroDTO;
import modelo.DatosPersonales;
import modelo.Pelicula;
import modelo.Resenia;
import modelo.Usuario;
import servicio.PlataformaService;
import vista.ConsolaView;

import java.util.List;

public class PlataformaController {

    private final PlataformaService servicio;
    private final ConsolaView vista;
    public PlataformaController(PlataformaService servicio, ConsolaView vista) {
        this.servicio = servicio;
        this.vista = vista;
    }

    public void iniciar() {
        int opcion;
        do {
            opcion = vista.mostrarMenuPrincipal();
            switch (opcion) {
                case 1 -> registrarDatosPersonales();
                case 2 -> registrarUsuario();
                case 3 -> registrarPelicula();
                case 4 -> listarUsuarios();
                case 5 -> listarPeliculas();
                case 6 -> registrarResenia();
                case 7 -> aprobarResenia();
                case 0 -> vista.mostrarMensaje("Saliendo...");
                default -> vista.mostrarMensaje("⚠️ Opción inválida.");
            }
        } while (opcion != 0);
    }

    private void registrarDatosPersonales() {
        DatosPersonalesDTO dto = vista.solicitarDatosPersonales();
        String resultado = servicio.registrarDatosPersonales(dto);
        vista.mostrarMensaje(resultado);
    }

    private void registrarUsuario() {
        List<DatosPersonales> personas = servicio.listarDatosPersonales();
        if (personas.isEmpty()) {
            vista.mostrarMensaje("⚠️ No hay datos personales cargados. Primero registre una persona.");
            return;
        }
        UsuarioRegistroDTO dto = vista.solicitarUsuarioRegistro(personas);
        if (dto == null) {
            return;
        }
        String resultado = servicio.registrarUsuario(dto);
        vista.mostrarMensaje(resultado);
    }

    private void registrarPelicula() {
        PeliculaDTO dto = vista.solicitarDatosPelicula();
        if (dto == null) {
            return;
        }
        String resultado = servicio.registrarPelicula(dto);
        vista.mostrarMensaje(resultado);
    }

    private void listarUsuarios() {
        int criterio = vista.solicitarOrdenUsuarios();
        List<Usuario> usuarios = servicio.listarUsuarios(criterio);
        vista.mostrarUsuarios(usuarios);
    }

    private void listarPeliculas() {
        int criterio = vista.solicitarOrdenPeliculas();
        List<Pelicula> pelis = servicio.listarPeliculas(criterio);
        vista.mostrarPeliculas(pelis);
    }

    private void registrarResenia() {
        List<Pelicula> pelis = servicio.listarPeliculas();
        ReseniaDTO dto = vista.solicitarResenia(pelis);
        if (dto == null) {
            return;
        }
        String resultado = servicio.registrarResenia(dto);
        vista.mostrarMensaje(resultado);
    }

    private void aprobarResenia() {
        List<Resenia> lista = servicio.listarReseniasPendientes();
        Integer id = vista.solicitarReseniaAAprobar(lista);
        if (id == null) {
            return;
        }
        if (vista.confirmar("¿Confirmar aprobación?")) {
            String resultado = servicio.aprobarResenia(id);
            vista.mostrarMensaje(resultado);
        } else {
            vista.mostrarMensaje("Operación cancelada.");
        }
    }
}
