package excepcion;

public class CargaArchivoException extends Exception {
    private static final long serialVersionUID = 1L;

    public CargaArchivoException(String message, Throwable cause) {
        super(message, cause);
    }

    public CargaArchivoException(String message) {
        super(message);
    }
}
