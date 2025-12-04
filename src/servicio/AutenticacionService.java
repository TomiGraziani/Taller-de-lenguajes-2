package servicio;

import dao.implementacion.DatosPersonalesDAOjdbc;
import dao.implementacion.UsuarioDAOjdbc;
import dto.RegistroFormularioDTO;
import modelo.DatosPersonales;
import modelo.Usuario;
import validacion.ValidadorContrasenia;
import validacion.ValidadorDNI;
import validacion.ValidadorEmail;
import validacion.ValidadorNombre;

public class AutenticacionService {

    private final UsuarioDAOjdbc usuarioDAO;
    private final DatosPersonalesDAOjdbc datosPersonalesDAO;
    private final ValidadorNombre validadorNombre = new ValidadorNombre();
    private final ValidadorDNI validadorDNI = new ValidadorDNI();
    private final ValidadorEmail validadorEmail = new ValidadorEmail();
    private final ValidadorContrasenia validadorContrasenia = new ValidadorContrasenia();

    public AutenticacionService(UsuarioDAOjdbc usuarioDAO, DatosPersonalesDAOjdbc datosPersonalesDAO) {
        this.usuarioDAO = usuarioDAO;
        this.datosPersonalesDAO = datosPersonalesDAO;
    }

    public RespuestaOperacion autenticar(String email, String contrasenia) {
        if (email == null || email.isBlank() || contrasenia == null || contrasenia.isBlank()) {
            return new RespuestaOperacion(false, "Debe completar Email y Contraseña");
        }

        Usuario usuario = usuarioDAO.buscarPorCredenciales(email, contrasenia);
        if (usuario == null) {
            return new RespuestaOperacion(false, "Credenciales inválidas.");
        }

        return new RespuestaOperacion(true, "¡Bienvenido " + usuario.getNombreUsuario() + "!", usuario);
    }

    public RespuestaOperacion registrarUsuario(RegistroFormularioDTO dto) {
        RespuestaOperacion validacion = validarDatos(dto);
        if (!validacion.esExitoso()) {
            return validacion;
        }

        long dni = Long.parseLong(dto.getDni());

        datosPersonalesDAO.insertar(dto.getNombre(), dto.getApellido(), dni);
        DatosPersonales nuevo = datosPersonalesDAO.buscarPorDNI(dni);
        usuarioDAO.insertar(dto.getNombreUsuario(), dto.getEmail(), dto.getContrasenia(), nuevo.getId());

        return new RespuestaOperacion(true, "Usuario registrado correctamente");
    }

    private RespuestaOperacion validarDatos(RegistroFormularioDTO dto) {
        if (dto.getNombre().isBlank() || dto.getApellido().isBlank() || dto.getDni().isBlank()
                || dto.getNombreUsuario().isBlank() || dto.getEmail().isBlank() || dto.getContrasenia().isBlank()) {
            return new RespuestaOperacion(false, "Todos los campos son obligatorios");
        }

        if (!validadorNombre.esValido(dto.getNombre()) || !validadorNombre.esValido(dto.getApellido())) {
            return new RespuestaOperacion(false, validadorNombre.getMensajeError());
        }

        if (!validadorDNI.esValido(dto.getDni())) {
            return new RespuestaOperacion(false, validadorDNI.getMensajeError());
        }

        if (!validadorNombre.esValido(dto.getNombreUsuario())) {
            return new RespuestaOperacion(false, "El nombre de usuario no es válido.");
        }

        if (!validadorEmail.esValido(dto.getEmail())) {
            return new RespuestaOperacion(false, validadorEmail.getMensajeError());
        }

        if (!validadorContrasenia.esValido(dto.getContrasenia())) {
            return new RespuestaOperacion(false, validadorContrasenia.getMensajeError());
        }

        long dniNumero;
        try {
            dniNumero = Long.parseLong(dto.getDni());
        } catch (NumberFormatException ex) {
            return new RespuestaOperacion(false, "DNI inválido");
        }

        if (datosPersonalesDAO.buscarPorDNI(dniNumero) != null) {
            return new RespuestaOperacion(false, "El DNI ya se encuentra registrado");
        }
        if (usuarioDAO.buscarPorEmail(dto.getEmail()) != null) {
            return new RespuestaOperacion(false, "El email ya se encuentra registrado");
        }
        if (usuarioDAO.buscarPorNombreUsuario(dto.getNombreUsuario()) != null) {
            return new RespuestaOperacion(false, "El nombre de usuario ya existe");
        }

        return new RespuestaOperacion(true, "Validación exitosa");
    }
}
