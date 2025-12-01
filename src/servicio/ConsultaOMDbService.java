package servicio;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import excepcion.ConsultaApiException;

public class ConsultaOMDbService {

    private static final String API_KEY = "3cce61fa";

    public ResultadoOMDb consultarPelicula(String titulo) throws ConsultaApiException {
        try {
            String url = "https://www.omdbapi.com/?t=" + titulo.replace(" ", "+") + "&apikey=" + API_KEY;
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            if (!body.contains("\"Response\":\"True\"")) {
                throw new ConsultaApiException("Película no encontrada en OMDb");
            }

            return new ResultadoOMDb(
                    extraerCampo(body, "Title"),
                    extraerCampo(body, "Year"),
                    extraerCampo(body, "Plot"),
                    extraerCampo(body, "Poster")
            );
        } catch (ConsultaApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ConsultaApiException("Error al consultar la API: " + e.getMessage(), e);
        }
    }

    private String extraerCampo(String json, String campo) {
        Pattern p = Pattern.compile("\"" + campo + "\":\"(.*?)\"");
        Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    public record ResultadoOMDb(String titulo, String anio, String sinopsis, String poster) {}
}
