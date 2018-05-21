package com.github.rowanleeder.simplecab;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;

class SimpleCabService {

    private String host;

    SimpleCabService(String host) {
        this.host = host;
    }

    /**
     * Get the count of trips made by specified cabs on a given date. Responses are cached for 15 minutes.
     *
     * @param medallions The medallion ids of the cabs.
     * @param pickupDate Date filter. Only count trips that were commenced on this date.
     * @return String the json response.
     * @throws IOException
     * @throws URISyntaxException
     */
    String getMedallionsSummary(String[] medallions, LocalDate pickupDate) throws IOException, URISyntaxException {
        return getMedallionsSummary(medallions, pickupDate, false);
    }

    /**
     * Get the count of trips made by specified cabs on a given date.
     *
     * @param medallions The medallion ids of the cabs.
     * @param pickupDate Date filter. Only count trips that were commenced on this date.
     * @param ignoreCache If true the cache will be bypassed.
     * @return String the json response.
     * @throws IOException
     * @throws URISyntaxException
     */
    String getMedallionsSummary(String[] medallions, LocalDate pickupDate, boolean ignoreCache) throws IOException, URISyntaxException {
        URI uri = UriComponentsBuilder.fromUri(new URI(host))
                .path("/cabs/trips/counts/")
                .queryParam("date", pickupDate)
                .queryParam("medallions", String.join(",", medallions))
                .queryParam("cache", !ignoreCache)
                .build()
                .toUri();

        return request(new HttpGet(uri));
    }

    /**
     * Request that the host clear its trip count cache.
     *
     * @throws IOException If the cache clearing fails or if there was a connection issue.
     * @throws URISyntaxException If the host is malformed.
     */
    void deleteCache() throws IOException, URISyntaxException {
        URI uri = UriComponentsBuilder.fromUri(new URI(host))
                .path("/cabs/trips/counts/")
                .build()
                .toUri();

        request(new HttpDelete(uri));
    }

    /**
     * Simple HTTP request wrapper.
     *
     * @param uri The url to call
     * @return The response. If it is json then it will be formatted.
     * @throws IOException If there is a connection issue, or a malformed request.
     * @throws IllegalArgumentException On a non-200 response.
     */
    private String request(HttpUriRequest uri) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();

        try {
            CloseableHttpResponse response = client.execute(uri);
            int status = response.getStatusLine().getStatusCode();

            if (status != 200) {
                throw new IllegalArgumentException(String.format("[%s] %s", status, response.getStatusLine().getReasonPhrase()));
            }

            String data = EntityUtils.toString(response.getEntity(), "UTF-8");

            // format the response if it is json.
            String type = response.getFirstHeader("Content-Type") + "";

            if (type.contains("application/json")) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonElement jsonElement = new JsonParser().parse(data);
                data = gson.toJson(jsonElement);
            }

            return data;
        } finally {
            client.close();
        }
    }
}
