package servicio;

import modelo.*;
import enumerativo.Genero;
import comparador.*;
import dao.implementacion.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PlataformaService {

    private final Scanner sc = new Scanner(System.in);
    

    // --- OPCIÓN 1: REGISTRAR DATOS PERSONALES ---
    public void registrarDatosPersonales(DatosPersonalesDAOjdbc datosDAO) {
        System.out.println("\n=== Registrar Datos Personales ===");
        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Apellido: ");
        String apellido = sc.nextLine();
        System.out.print("DNI: ");
        long dni = sc.nextLong(); sc.nextLine();
        
        List<String> errores = new ArrayList<String>();

        //if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚ ]+") || !apellido.matches("[a-zA-ZáéíóúÁÉÍÓÚ ]+")) {
        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚ]+( [a-zA-ZáéíóúÁÉÍÓÚ]+)*") || !apellido.matches("[a-zA-ZáéíóúÁÉÍÓÚ]+( [a-zA-ZáéíóúÁÉÍÓÚ]+)*")) {
        	errores.add("❌ Error: Nombre o Apellido con caracteres inválidos.");
        }
        
        //No debe de haber un DNI igual
        String dniStr = String.valueOf(dni);
        if (dniStr.length() != 11 || datosDAO.buscarPorDNI(dni) != null) {
        	errores.add("❌ Error: DNI inválido.");
        }
        
        //Si hay errores los muestra por pantalla
        if (errores.size() != 0) {
        	for (int i = 0; i < errores.size(); i++) {
                System.out.println(errores.get(i));
            }
        	return;
        }

        System.out.println("\nDatos ingresados:");
        System.out.println(nombre + " " + apellido + " - DNI: " + dni);
        System.out.print("¿Confirmar registro? (s/n): ");
        if (sc.nextLine().equalsIgnoreCase("s")) {
        	datosDAO.insertar(nombre, apellido, dni);
        } else {
            System.out.println("Registro cancelado.");
        }
    }

    // --- OPCIÓN 2: REGISTRAR USUARIO ---
    public void registrarUsuario(DatosPersonalesDAOjdbc datosDAO, UsuarioDAOjdbc usuarioDAO) {
        System.out.println("\n=== Registrar Usuario ===");

        List<DatosPersonales> personas = datosDAO.listarTodos();
        if (personas.isEmpty()) {
            System.out.println("⚠️ No hay datos personales cargados. Primero registre una persona.");
            return;
        }

        System.out.println("Seleccione una persona para asociar:");
        for (int i = 0; i < personas.size(); i++) {
            System.out.println((i + 1) + ". " + personas.get(i));
        }

        System.out.print("Número: ");
        int num = sc.nextInt(); sc.nextLine();
        if (num < 1 || num > personas.size()) {
            System.out.println("❌ Error: Número inválido.");
            return;
        }

        DatosPersonales seleccionada = personas.get(num - 1);
        List<String> errores = new ArrayList<String>();

        System.out.print("Nombre de usuario: ");
        String nombreUsuario = sc.nextLine();

        System.out.print("Email: ");
        String email = sc.nextLine();
        if (!email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
        	errores.add("❌ Error: Formato de email inválido.");
        }

        //La contraseña no puede estar vacio, ni contener espacios
        System.out.print("Contraseña: ");
        String contrasenia = sc.nextLine();
        if (!contrasenia.matches("^[^\\s]+$")) {
            errores.add("❌ Error: La contraseña no puede estar vacía ni contener espacios.");
        }
        
        //Si hay errores los muestra por pantalla
        if (errores.size() != 0) {
        	for (int i = 0; i < errores.size(); i++) {
                System.out.println(errores.get(i));
            }
        	return;
        }

        System.out.println("\nUsuario: " + nombreUsuario + " (" + email + ")");
        System.out.print("¿Confirmar registro? (s/n): ");
        if (sc.nextLine().equalsIgnoreCase("s")) {
            usuarioDAO.insertar(nombreUsuario, email, contrasenia, seleccionada.getId());
        } else {
            System.out.println("Registro cancelado.");
        }
    }

    // --- OPCIÓN 3: REGISTRAR PELÍCULA ---
    public void registrarPelicula(PeliculaDAOjdbc peliculaDAO) {
        System.out.println("\n=== Registrar Película ===");
        System.out.print("Título: ");
        String titulo = sc.nextLine();
        System.out.print("Director: ");
        String director = sc.nextLine();
        System.out.print("Duración (minutos): ");
        double duracion = sc.nextDouble(); sc.nextLine();

        System.out.println("Seleccione género:");
        for (Genero g : Genero.values()) {
            System.out.println("- " + g);
        }
        System.out.print("Género: ");
        String genero = sc.nextLine().toUpperCase();

        if (!existeGenero(genero)) {
            System.out.println("❌ Género inválido.");
            return;
        }

        System.out.print("Resumen (opcional): ");
        String resumen = sc.nextLine();

        System.out.println("\nPelícula: " + titulo + " (" + genero + "), Dir: " + director + ", Duración: " + duracion);
        System.out.print("¿Confirmar registro? (s/n): ");
        if (sc.nextLine().equalsIgnoreCase("s")) {
            peliculaDAO.insertar(titulo, director, resumen, duracion, genero);
        } else {
            System.out.println("Registro cancelado.");
        }
    }

    private boolean existeGenero(String g) {
        for (Genero gen : Genero.values()) {
            if (gen.name().equalsIgnoreCase(g)) return true;
        }
        return false;
    }

    // --- OPCIÓN 4: LISTAR USUARIOS ---
    public void listarUsuarios(UsuarioDAOjdbc usuarioDAO) {
        System.out.println("\n=== Listado de Usuarios ===");
        List<Usuario> usuarios = usuarioDAO.listarTodos();
        if (usuarios.isEmpty()) {
            System.out.println("⚠️ No hay usuarios registrados.");
            return;
        }

        System.out.println("Ordenar por: 1) NombreUsuario  2) Email");
        int op = sc.nextInt(); sc.nextLine();

        switch (op) {
        	case 1 : usuarios.sort(new ComparadorPorNombreDeUsuario());
        		break;
            case 2 : usuarios.sort(new ComparadorPorEmail());
            	break;
            default : System.out.println("Error: Orden no identidicado");
        };

        usuarios.forEach(System.out::println);
    }

    // --- OPCIÓN 5: LISTAR PELÍCULAS ---
    public void listarPeliculas(PeliculaDAOjdbc peliculaDAO) {
        System.out.println("\n=== Listado de Películas ===");
        List<Pelicula> pelis = peliculaDAO.listarTodas();
        if (pelis.isEmpty()) {
            System.out.println("⚠️ No hay películas registradas.");
            return;
        }

        System.out.println("Ordenar por: 1) Título  2) Género  3) Duración");
        int op = sc.nextInt(); sc.nextLine();
        
        //pelis.sort(new ComparadorPorGenero());
        //

        switch (op) {
    	case 1 : pelis.sort(new ComparadorPorTitulo());
    		break;
        case 2 : pelis.sort(new ComparadorPorGenero());
        	break;
        case 3 : pelis.sort(new ComparadorPorDuracion());
    		break;
        default : System.out.println("Error: Orden no identidicado");
    };

        pelis.forEach(System.out::println);
    }

    // --- OPCIÓN 6: REGISTRAR RESENIA ---
    public void registrarResenia(UsuarioDAOjdbc usuarioDAO, PeliculaDAOjdbc peliculaDAO, ReseniaDAOjdbc reseniaDAO) {
        System.out.println("\n=== Registrar Reseña ===");
        System.out.print("Usuario: ");
        String nombreUsuario = sc.nextLine();
        System.out.print("Contraseña: ");
        String pass = sc.nextLine();

        Usuario u = usuarioDAO.buscarPorCredenciales(nombreUsuario, pass);
        if (u == null) {
            System.out.println("❌ Credenciales inválidas.");
            return;
        }

        List<Pelicula> pelis = peliculaDAO.listarTodas();
        if (pelis.isEmpty()) {
            System.out.println("⚠️ No hay películas registradas.");
            return;
        }

        System.out.println("Seleccione una película:");
        for (int i = 0; i < pelis.size(); i++) {
            System.out.println((i + 1) + ". " + pelis.get(i));
        }

        System.out.print("Número: ");
        int num = sc.nextInt(); sc.nextLine();
        if (num < 1 || num > pelis.size()) {
            System.out.println("❌ Opción inválida.");
            return;
        }

        Audiovisual seleccionada = pelis.get(num - 1);

        System.out.print("Calificación (1 a 10): ");
        int calif = sc.nextInt(); sc.nextLine();
        System.out.print("Comentario: ");
        String comentario = sc.nextLine();

        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        // Resenia r = new Resenia(0, calif, comentario, fecha, u.getId(), seleccionada.getId(), false);

        System.out.println("\nReseña: " + calif + "/10 - " + comentario);
        System.out.print("¿Confirmar registro? (s/n): ");
        if (sc.nextLine().equalsIgnoreCase("s")) {
            reseniaDAO.insertar(calif, comentario, 0, fecha, u.getId(), seleccionada.getId());
        } else {
            System.out.println("Registro cancelado.");
        }
    }

    // --- OPCIÓN 7: APROBAR RESEÑA ---
    public void aprobarResenia(ReseniaDAOjdbc reseniaDAO) {
        System.out.println("\n=== Aprobar Reseñas ===");
        List<Resenia> lista = reseniaDAO.listarNoAprobadas();
        if (lista.isEmpty()) {
            System.out.println("⚠️ No hay reseñas pendientes.");
            return;
        }

        for (Resenia r : lista) {
            System.out.println(r);
        }

        System.out.print("Ingrese ID de la reseña a aprobar: ");
        int id = sc.nextInt(); sc.nextLine();

        System.out.print("¿Confirmar aprobación? (s/n): ");
        if (sc.nextLine().equalsIgnoreCase("s")) {
            reseniaDAO.aprobar(id);
        } else {
            System.out.println("Operación cancelada.");
        }
    }
}
