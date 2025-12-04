package vista;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import enumerativo.Genero;
import modelo.DatosPersonales;
import modelo.Pelicula;
import modelo.Resenia;
import modelo.Usuario;
import dto.DatosPersonalesDTO;
import dto.PeliculaDTO;
import dto.ReseniaDTO;
import dto.UsuarioRegistroDTO;

public class ConsolaView {
    private final Scanner scanner = new Scanner(System.in);

    public int mostrarMenuPrincipal() {
        System.out.println("\n=== Plataforma Streaming ===");
        System.out.println("1. Registrar Datos Personales");
        System.out.println("2. Registrar Usuario");
        System.out.println("3. Registrar Película");
        System.out.println("4. Listar Usuarios");
        System.out.println("5. Listar Películas");
        System.out.println("6. Registrar Reseña");
        System.out.println("7. Aprobar Reseña");
        System.out.println("0. Salir");
        System.out.print("Opción: ");
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            scanner.nextLine();
            return -1;
        } finally {
            scanner.nextLine();
        }
    }

    public DatosPersonalesDTO solicitarDatosPersonales() {
        System.out.println("\n=== Registrar Datos Personales ===");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Apellido: ");
        String apellido = scanner.nextLine();
        System.out.print("DNI: ");
        try {
            long dni = scanner.nextLong();
            scanner.nextLine();
            return new DatosPersonalesDTO(nombre, apellido, dni);
        } catch (InputMismatchException e) {
            scanner.nextLine();
            System.out.println("❌ Error: DNI inválido.");
            return null;
        }
    }

    public UsuarioRegistroDTO solicitarUsuarioRegistro(List<DatosPersonales> personas) {
        System.out.println("\n=== Registrar Usuario ===");
        System.out.println("Seleccione una persona para asociar:");
        for (int i = 0; i < personas.size(); i++) {
            System.out.println((i + 1) + ". " + personas.get(i));
        }
        System.out.print("Número: ");
        try {
            int num = scanner.nextInt();
            scanner.nextLine();
            if (num < 1 || num > personas.size()) {
                System.out.println("❌ Error: Número inválido.");
                return null;
            }
            DatosPersonales seleccionada = personas.get(num - 1);
            System.out.print("Nombre de usuario: ");
            String nombreUsuario = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Contraseña: ");
            String contrasenia = scanner.nextLine();
            return new UsuarioRegistroDTO(seleccionada, nombreUsuario, email, contrasenia);
        } catch (InputMismatchException e) {
            scanner.nextLine();
            System.out.println("❌ Error: Número inválido.");
            return null;
        }
    }

    public PeliculaDTO solicitarDatosPelicula() {
        System.out.println("\n=== Registrar Película ===");
        System.out.print("Título: ");
        String titulo = scanner.nextLine();
        System.out.print("Director: ");
        String director = scanner.nextLine();
        System.out.print("Duración (minutos): ");
        try {
            double duracion = scanner.nextDouble();
            scanner.nextLine();
            System.out.println("Seleccione género:");
            for (Genero g : Genero.values()) {
                System.out.println("- " + g);
            }
            System.out.print("Género: ");
            String genero = scanner.nextLine().toUpperCase();
            System.out.print("Resumen (opcional): ");
            String resumen = scanner.nextLine();
            return new PeliculaDTO(titulo, director, resumen, duracion, genero);
        } catch (InputMismatchException e) {
            scanner.nextLine();
            System.out.println("❌ Error: Duración inválida.");
            return null;
        }
    }

    public int solicitarOrdenUsuarios() {
        System.out.println("Ordenar por: 1) NombreUsuario  2) Email");
        try {
            int op = scanner.nextInt();
            scanner.nextLine();
            return op;
        } catch (InputMismatchException e) {
            scanner.nextLine();
            return -1;
        }
    }

    public int solicitarOrdenPeliculas() {
        System.out.println("Ordenar por: 1) Título  2) Género  3) Duración");
        try {
            int op = scanner.nextInt();
            scanner.nextLine();
            return op;
        } catch (InputMismatchException e) {
            scanner.nextLine();
            return -1;
        }
    }

    public ReseniaDTO solicitarResenia(List<Pelicula> pelis) {
        System.out.println("\n=== Registrar Reseña ===");
        System.out.print("Usuario: ");
        String nombreUsuario = scanner.nextLine();
        System.out.print("Contraseña: ");
        String pass = scanner.nextLine();

        if (pelis.isEmpty()) {
            System.out.println("⚠️ No hay películas registradas.");
            return null;
        }

        System.out.println("Seleccione una película:");
        for (int i = 0; i < pelis.size(); i++) {
            System.out.println((i + 1) + ". " + pelis.get(i));
        }

        System.out.print("Número: ");
        try {
            int num = scanner.nextInt();
            scanner.nextLine();
            if (num < 1 || num > pelis.size()) {
                System.out.println("❌ Opción inválida.");
                return null;
            }
            Pelicula seleccionada = pelis.get(num - 1);
            System.out.print("Calificación (1 a 10): ");
            int calif = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Comentario: ");
            String comentario = scanner.nextLine();
            return new ReseniaDTO(nombreUsuario, pass, calif, comentario, seleccionada);
        } catch (InputMismatchException e) {
            scanner.nextLine();
            System.out.println("❌ Error: Opción inválida.");
            return null;
        }
    }

    public Integer solicitarReseniaAAprobar(List<Resenia> lista) {
        System.out.println("\n=== Aprobar Reseñas ===");
        if (lista.isEmpty()) {
            System.out.println("⚠️ No hay reseñas pendientes.");
            return null;
        }
        for (Resenia r : lista) {
            System.out.println(r);
        }
        System.out.print("Ingrese ID de la reseña a aprobar: ");
        try {
            int id = scanner.nextInt();
            scanner.nextLine();
            return id;
        } catch (InputMismatchException e) {
            scanner.nextLine();
            System.out.println("❌ Error: ID inválido.");
            return null;
        }
    }

    public boolean confirmar(String mensaje) {
        System.out.print(mensaje + " (s/n): ");
        return scanner.nextLine().equalsIgnoreCase("s");
    }

    public void mostrarUsuarios(List<Usuario> usuarios) {
        if (usuarios.isEmpty()) {
            System.out.println("⚠️ No hay usuarios registrados.");
            return;
        }
        usuarios.forEach(System.out::println);
    }

    public void mostrarPeliculas(List<Pelicula> pelis) {
        if (pelis.isEmpty()) {
            System.out.println("⚠️ No hay películas registradas.");
            return;
        }
        pelis.forEach(System.out::println);
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }
}
