package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class IsoListManager
{
    private Yeah yeahInstance;

    public IsoListManager(Yeah  instance)
    {
        this.yeahInstance = instance;
    }

    public void IsoList(String WinVersion, String Architecture, String Lang) {
        String all_url = "https://api.uupdump.net/listid.php?&sortByDate=1";
        HttpClient client = HttpClient.newHttpClient();
//        StringBuilder result = new StringBuilder();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(all_url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonDocument = objectMapper.readTree(response.body());

                JsonNode responseElement = jsonDocument.get("response");
                    JsonNode buildsElement = responseElement.get("builds");
                        for (JsonNode build : buildsElement) {
                            JsonNode titleElement = build.get("title");
                            JsonNode archElement = build.get("arch");
                            JsonNode uuidElement = build.get("uuid");

                            String title = titleElement.asText();
                            String arch = archElement.asText();
                            String uuid = uuidElement.asText();

                            if (title.startsWith(WinVersion)
                                && arch.equals(Architecture))
                            {
                                String item = title;
                                yeahInstance.addItemToComboBox(item);
                                yeahInstance.setUuid(uuid);
                            }
                        }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}