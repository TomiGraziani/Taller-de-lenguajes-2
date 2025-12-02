package servicio;

import comparador.*;
import dao.implementacion.*;
import enumerativo.Genero;
import modelo.DatosPersonales;
import modelo.Pelicula;
import modelo.Resenia;
import modelo.Usuario;
import vista.dto.DatosPersonalesDTO;
import vista.dto.PeliculaDTO;
import vista.dto.ReseniaDTO;
import vista.dto.UsuarioRegistroDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PlataformaService {

    public String registrarDatosPersonales(DatosPersonalesDAOjdbc datosDAO, DatosPersonalesDTO datosDTO) {
        if (datosDTO == null) {
            return "❌ Datos personales incompletos.";
        }

        List<String> errores = new ArrayList<>();

        if (!datosDTO.getNombre().matches("[a-zA-ZáéíóúÁÉÍÓÚ]+( [a-zA-ZáéíóúÁÉÍÓÚ]+)*")
                || !datosDTO.getApellido().matches("[a-zA-ZáéíóúÁÉÍÓÚ]+( [a-zA-ZáéíóúÁÉÍÓÚ]+)*")) {
            errores.add("❌ Error: Nombre o Apellido con caracteres inválidos.");
        }

        String dniStr = String.valueOf(datosDTO.getDni());
        if (dniStr.length() != 11 || datosDAO.buscarPorDNI(datosDTO.getDni()) != null) {
            errores.add("❌ Error: DNI inválido.");
        }

        if (!errores.isEmpty()) {
            return String.join("\n", errores);
        }

        datosDAO.insertar(datosDTO.getNombre(), datosDTO.getApellido(), datosDTO.getDni());
        return "✅ Datos personales registrados con éxito.";
    }

    public String registrarUsuario(DatosPersonalesDAOjdbc datosDAO, UsuarioDAOjdbc usuarioDAO, UsuarioRegistroDTO dto) {
        if (dto == null) {
            return "❌ Datos de usuario incompletos.";
        }

        List<String> errores = new ArrayList<>();

        DatosPersonales seleccionada = dto.getDatosPersonales();
        if (seleccionada == null) {
            errores.add("❌ Error: Datos personales no válidos.");
        }

        if (!dto.getEmail().matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            errores.add("❌ Error: Formato de email inválido.");
        }

        if (!dto.getContrasenia().matches("^[^\\s]+$")) {
            errores.add("❌ Error: La contraseña no puede estar vacía ni contener espacios.");
        }

        if (!errores.isEmpty()) {
            return String.join("\n", errores);
        }

        usuarioDAO.insertar(dto.getNombreUsuario(), dto.getEmail(), dto.getContrasenia(), seleccionada.getId());
        return "✅ Usuario registrado con éxito.";
    }

    public String registrarPelicula(PeliculaDAOjdbc peliculaDAO, PeliculaDTO dto) {
        if (dto == null) {
            return "❌ Datos de película incompletos.";
        }

        if (!existeGenero(dto.getGenero())) {
            return "❌ Género inválido.";
        }

        peliculaDAO.insertar(dto.getTitulo(), dto.getDirector(), dto.getResumen(), dto.getDuracion(), dto.getGenero());
        return "✅ Película registrada con éxito.";
    }

    private boolean existeGenero(String g) {
        for (Genero gen : Genero.values()) {
            if (gen.name().equalsIgnoreCase(g)) return true;
        }
        return false;
    }

    public List<Usuario> listarUsuarios(UsuarioDAOjdbc usuarioDAO, int criterio) {
        List<Usuario> usuarios = usuarioDAO.listarTodos();
        switch (criterio) {
            case 1 -> usuarios.sort(new ComparadorPorNombreDeUsuario());
            case 2 -> usuarios.sort(new ComparadorPorEmail());
            default -> {}
        }
        return usuarios;
    }

    public List<Pelicula> listarPeliculas(PeliculaDAOjdbc peliculaDAO, int criterio) {
        List<Pelicula> pelis = peliculaDAO.listarTodas();
        switch (criterio) {
            case 1 -> pelis.sort(new ComparadorPorTitulo());
            case 2 -> pelis.sort(new ComparadorPorGenero());
            case 3 -> pelis.sort(new ComparadorPorDuracion());
            default -> {}
        }
        return pelis;
    }

    public String registrarResenia(UsuarioDAOjdbc usuarioDAO, ReseniaDAOjdbc reseniaDAO, ReseniaDTO dto) {
        if (dto == null) {
            return "❌ Datos de reseña incompletos.";
        }

        Usuario u = usuarioDAO.buscarPorCredenciales(dto.getNombreUsuario(), dto.getContrasenia());
        if (u == null) {
            return "❌ Credenciales inválidas.";
        }

        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        reseniaDAO.insertar(dto.getCalificacion(), dto.getComentario(), 0, fecha, u.getId(), dto.getSeleccionada().getId());
        return "✅ Reseña registrada con éxito.";
    }

    public String aprobarResenia(ReseniaDAOjdbc reseniaDAO, int id) {
        reseniaDAO.aprobar(id);
        return "✅ Reseña aprobada.";
    }

    public List<Resenia> listarReseniasPendientes(ReseniaDAOjdbc reseniaDAO) {
        return reseniaDAO.listarNoAprobadas();
    }
}
