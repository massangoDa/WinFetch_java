package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.example.Yeah.Logg;

public class UUPLinkFetch
{
    private Yeah yeahInstance;
    private UUPDownload FileDownloader;


    public UUPLinkFetch(Yeah instance)
    {
        this.yeahInstance = instance;
    }

    public void uupFetch(String selectedItem, String uuid, Consumer<Integer> onProgressUpdate)
    {
        if (selectedItem != null)
        {
            String lang = yeahInstance.getLang();

            String targetUrl = "https://api.uupdump.net/get.php?id=" + uuid + "&lang=" + lang + "&edition=professional&noLinks=0";
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(targetUrl))
                    .GET()
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(responseBody -> {
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode responseElement = null;
                        try {
                            responseElement = objectMapper.readTree(responseBody).get("response");
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }

                        JsonNode filesElement = responseElement.get("files");

                        int totalFiles = filesElement.size();
                        AtomicInteger completedCount = new AtomicInteger(0);

                        Iterator<Map.Entry<String, JsonNode>> elements = filesElement.fields();
                        while (elements.hasNext()) {
                            Map.Entry<String, JsonNode> entry = elements.next();
                            String fileName = entry.getKey();
                            JsonNode file = entry.getValue();
                            String fileUrl = file.get("url").asText();

                            UUPDownload.downloadFileAsync(fileUrl, fileName, () -> {
                                int done = completedCount.incrementAndGet();
                                int progress = (int) ((double) done / totalFiles * 100);
                                SwingUtilities.invokeLater(() -> {
                                    onProgressUpdate.accept(progress);
                                });

                                if (done == totalFiles)
                                {
                                    Logg("ダウンロードが完了しました", Color.YELLOW);

                                }
                            });
                        }
                    });
        }
    }
}
