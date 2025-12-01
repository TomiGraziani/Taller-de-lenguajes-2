package excepcion;

public class DatosIncompletosException extends Exception {
    private static final long serialVersionUID = 1L;

    public DatosIncompletosException(String message) {
        super(message);
    }
}
