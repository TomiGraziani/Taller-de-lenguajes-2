package excepcion;

public class ConsultaApiException extends Exception {
    private static final long serialVersionUID = 1L;

    public ConsultaApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConsultaApiException(String message) {
        super(message);
    }
}
