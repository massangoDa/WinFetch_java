package org.example;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.example.Yeah.Logg;

public class ConvertUupStart
{
    public static void StartUUP() throws IOException, InterruptedException
    {
        String cmdUrl = "https://raw.githubusercontent.com/abbodi1406/BatUtil/master/uup-converter-wimlib/convert-UUP.cmd";
        String binUrl = "https://github.com/abbodi1406/BatUtil/archive/refs/heads/master.zip";

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest cmdRequest = HttpRequest.newBuilder()
                .uri(URI.create(cmdUrl))
                .build();
        HttpResponse<Path> cmdResponse = client.send(cmdRequest, HttpResponse.BodyHandlers.ofFile(Paths.get("convert-UUP.cmd")));

        HttpRequest binRequest = HttpRequest.newBuilder()
                .uri(URI.create(binUrl))
                .build();
        HttpResponse<Path> binResponse = client.send(binRequest, HttpResponse.BodyHandlers.ofFile(Paths.get("BatUtil-master.zip")));

        Path binZipPath = binResponse.body();
        Path binDir = Paths.get("BatUtil-master");
        Files.createDirectories(binDir);
        try (java.util.zip.ZipInputStream zipInputStream = new java.util.zip.ZipInputStream(Files.newInputStream(binZipPath))) {
            java.util.zip.ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                Path entryPath = binDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    Files.copy(zipInputStream, entryPath);
                }
                zipInputStream.closeEntry();
            }
        }


        String currentDir = System.getProperty("user.dir");
        String cmdPath = currentDir + File.separator + "ISO_FOLDER" + File.separator + "convert-UUP.cmd";

        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", cmdPath);
        builder.directory(new File(currentDir));
        builder.start();

        Logg("convert-UUP.cmdが実行されました。");
        Logg("コマンドプロンプトが立ち上がったら、画面に「Press 0 or q to exit.」と表示されるのをお待ちください。その表示が出たら、キーボードで「0」または「q」を押して閉じてください。", Color.YELLOW);
        Logg("カレントディレクトリ/ISO_FOLDER/〇〇.isoのようにISOファイルが出力されます。");
    }
}
