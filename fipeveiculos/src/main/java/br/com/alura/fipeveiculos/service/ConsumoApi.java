package br.com.alura.fipeveiculos.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ConsumoApi {
    public String obterDados(String endereco) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endereco))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        String json = response.body();

        if (response.statusCode() == 200) {
            System.out.println("\nValor do JSON de retorno 200: " + json);
            return json;
        } else {
            json = null;
            System.out.println("\nValor do JSON de retorno 400: " + json);
            return json;
        }


//        String json = response.body();
//        return json;

//        String status = String.valueOf(response.statusCode());
//        return json + status;
    }
}
