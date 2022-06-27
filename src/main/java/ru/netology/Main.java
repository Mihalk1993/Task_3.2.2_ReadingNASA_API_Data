package ru.netology;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class Main {

    public static ObjectMapper mapper = new ObjectMapper();

    private static final String URL = "https://api.nasa.gov/planetary/apod?api_key=dMVBM8BRtgOI9moklxjAZCmJorkGb5nijALFscR8";

    public static void main(String[] args) throws IOException {

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        HttpGet request = new HttpGet(URL);
        CloseableHttpResponse response = httpClient.execute(request);

        NasaResponse nasaResponse = mapper.readValue(
                response.getEntity().getContent(), new TypeReference<>() {
                });

        String imageUrl = nasaResponse.getUrl();
        String[] imageUrlParts = imageUrl.split("/");
        String fileName = imageUrlParts[imageUrlParts.length - 1];

//      По заданию (пункт 7. " ... еще один http-запрос с помощью уже созданного httpClient.")
        HttpGet imageRequest = new HttpGet(imageUrl);
        CloseableHttpResponse imageResponse = httpClient.execute(imageRequest);

        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        fileOutputStream.write(imageResponse.getEntity().getContent().readAllBytes());

// Using NIO
//        URL url = new URL(imageUrl);
//        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
//        FileOutputStream fileOutputStream2 = new FileOutputStream(fileName);
//
//        fileOutputStream2.getChannel()
//                .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

// Using Apache Commons IO
//        URL url = new URL(imageUrl);
//        File file = new File(fileName);
//
//        FileUtils.copyURLToFile(url, file);
    }
}
