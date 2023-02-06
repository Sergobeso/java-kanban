package server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private String serverUrl;
    String API_TOKEN;


    public KVTaskClient (String url) throws IOException, InterruptedException {
        this.serverUrl = url;
        URI uri = URI.create(serverUrl + KVServer.PORT + "/register");

        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        API_TOKEN = (String) httpResponse.body();
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        URI uri = URI.create(serverUrl + KVServer.PORT + "/save" + key + "?API_TOKEN=" + API_TOKEN);

        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();
        HttpClient client = HttpClient.newHttpClient();

        HttpResponse httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

    }

    public String load (String key) throws IOException, InterruptedException {
        URI uri = URI.create(serverUrl + KVServer.PORT + "/load" + key + "?API_TOKEN=" + API_TOKEN);

        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        return httpResponse.body();
    }
}
