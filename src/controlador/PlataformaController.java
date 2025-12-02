package controlador;

import dao.FactoryDAO;
import dao.implementacion.DatosPersonalesDAOjdbc;
import dao.implementacion.PeliculaDAOjdbc;
import dao.implementacion.ReseniaDAOjdbc;
import dao.implementacion.UsuarioDAOjdbc;
import modelo.DatosPersonales;
import modelo.Pelicula;
import servicio.PlataformaService;
import vista.ConsolaView;
import vista.dto.DatosPersonalesDTO;
import vista.dto.PeliculaDTO;
import vista.dto.ReseniaDTO;
import vista.dto.UsuarioRegistroDTO;

import java.util.List;

public class PlataformaController {

    private final PlataformaService servicio;
    private final ConsolaView vista;
    private final FactoryDAO factory;

    public PlataformaController(PlataformaService servicio, ConsolaView vista, FactoryDAO factory) {
        this.servicio = servicio;
        this.vista = vista;
        this.factory = factory;
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
        String resultado = servicio.registrarDatosPersonales(factory.getDatosDAO(), dto);
        vista.mostrarMensaje(resultado);
    }

    private void registrarUsuario() {
        DatosPersonalesDAOjdbc datosDAO = factory.getDatosDAO();
        List<DatosPersonales> personas = datosDAO.listarTodos();
        if (personas.isEmpty()) {
            vista.mostrarMensaje("⚠️ No hay datos personales cargados. Primero registre una persona.");
            return;
        }
        UsuarioRegistroDTO dto = vista.solicitarUsuarioRegistro(personas);
        if (dto == null) {
            return;
        }
        String resultado = servicio.registrarUsuario(datosDAO, factory.getUsuarioDAO(), dto);
        vista.mostrarMensaje(resultado);
    }

    private void registrarPelicula() {
        PeliculaDTO dto = vista.solicitarDatosPelicula();
        if (dto == null) {
            return;
        }
        String resultado = servicio.registrarPelicula(factory.getPeliculaDAO(), dto);
        vista.mostrarMensaje(resultado);
    }

    private void listarUsuarios() {
        int criterio = vista.solicitarOrdenUsuarios();
        List<modelo.Usuario> usuarios = servicio.listarUsuarios(factory.getUsuarioDAO(), criterio);
        vista.mostrarUsuarios(usuarios);
    }

    private void listarPeliculas() {
        int criterio = vista.solicitarOrdenPeliculas();
        List<Pelicula> pelis = servicio.listarPeliculas(factory.getPeliculaDAO(), criterio);
        vista.mostrarPeliculas(pelis);
    }

    private void registrarResenia() {
        PeliculaDAOjdbc peliculaDAO = factory.getPeliculaDAO();
        List<Pelicula> pelis = peliculaDAO.listarTodas();
        ReseniaDTO dto = vista.solicitarResenia(pelis);
        if (dto == null) {
            return;
        }
        String resultado = servicio.registrarResenia(factory.getUsuarioDAO(), factory.getReseniaDAO(), dto);
        vista.mostrarMensaje(resultado);
    }

    private void aprobarResenia() {
        ReseniaDAOjdbc reseniaDAO = factory.getReseniaDAO();
        List<modelo.Resenia> lista = servicio.listarReseniasPendientes(reseniaDAO);
        Integer id = vista.solicitarReseniaAAprobar(lista);
        if (id == null) {
            return;
        }
        if (vista.confirmar("¿Confirmar aprobación?")) {
            String resultado = servicio.aprobarResenia(reseniaDAO, id);
            vista.mostrarMensaje(resultado);
        } else {
            vista.mostrarMensaje("Operación cancelada.");
        }
    }
}
