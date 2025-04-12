package jp.massango.winfetch;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static jp.massango.winfetch.Yeah.Logg;

public class ConvertUupStart
{
    public static void StartUUP() throws IOException, InterruptedException
    {
        String cmdUrl = "https://raw.githubusercontent.com/abbodi1406/BatUtil/master/uup-converter-wimlib/convert-UUP.cmd";
        String binZipUrl = "https://github.com/abbodi1406/BatUtil/archive/refs/heads/master.zip";

        Path isoFolder = Paths.get(System.getProperty("user.dir"), "ISO_FOLDER");
        Files.createDirectories(isoFolder);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest cmdRequest = HttpRequest.newBuilder()
                .uri(URI.create(cmdUrl))
                .build();
        Path cmdPath = isoFolder.resolve("convert-UUP.cmd");
        client.send(cmdRequest, HttpResponse.BodyHandlers.ofFile(cmdPath));

        HttpRequest zipRequest = HttpRequest.newBuilder()
                .uri(URI.create(binZipUrl))
                .build();
        Path zipPath = Files.createTempFile("batutil", ".zip");
        client.send(zipRequest, HttpResponse.BodyHandlers.ofFile(zipPath));

        try (java.util.zip.ZipInputStream zipStream = new java.util.zip.ZipInputStream(Files.newInputStream(zipPath))) {
            java.util.zip.ZipEntry entry;
            while ((entry = zipStream.getNextEntry()) != null) {
                System.out.println("ZIP内のファイル名: " + entry.getName());

                if (entry.getName().startsWith("BatUtil-master/uup-converter-wimlib/bin/") && !entry.isDirectory()) {
                    String relativeString = entry.getName().substring("BatUtil-master/uup-converter-wimlib/".length());
                    Path outPath = isoFolder.resolve(relativeString);
                    Files.createDirectories(outPath.getParent());
                    Files.copy(zipStream, outPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zipStream.closeEntry();
            }
        }

        new ProcessBuilder("cmd.exe", "/c", "start", "\"\"", "convert-UUP.cmd")
                .directory(isoFolder.toFile())
                .start();

        Logg("convert-UUP.cmdが実行されました。");
        Logg("コマンドプロンプトが立ち上がったら、画面に「Press 0 or q to exit.」と表示されるのをお待ちください。その表示が出たら、キーボードで「0」または「q」を押して閉じてください。", Color.YELLOW);
        Logg("カレントディレクトリ/ISO_FOLDER/〇〇.isoのようにISOファイルが出力されます。");
    }
}
