package jp.massango.winfetch;

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import static jp.massango.winfetch.Yeah.Logg;

public class UUPDownload
{
    public static CompletableFuture<Void> downloadFileAsync(String fileUrl, String fileName2, Runnable onComplete)
    {
        return CompletableFuture.runAsync(() -> {
            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fileUrl))
                    .GET()
                    .build();

            String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);

            // ISO_FOLDER/UUPs 作成
            Path isoFolderPath = Paths.get(System.getProperty("user.dir"), "ISO_FOLDER");
            Path uupsFolderPath = isoFolderPath.resolve("UUPs");


            try {
                Files.createDirectories(uupsFolderPath);
                Path filePath = uupsFolderPath.resolve(fileName2);

                client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                        .thenAccept(response -> {
                            try (InputStream inputStream = response.body();
                                 FileOutputStream outputStream = new FileOutputStream(filePath.toFile())) {

                                byte[] buffer = new byte[8192];
                                int bytesRead;

                                Logg(fileName2 + "をダウンロードしています");

                                while ((bytesRead = inputStream.read(buffer)) != -1) {
                                    outputStream.write(buffer, 0, bytesRead);
                                }

                                SwingUtilities.invokeLater(onComplete);
                            } catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }).join();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
