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
import java.util.concurrent.TimeUnit;

import static jp.massango.winfetch.Yeah.Logg;

public class ConvertUupStart
{
    public static void StartUUP() throws IOException, InterruptedException
    {
        String cmdUrl = "https://raw.githubusercontent.com/abbodi1406/BatUtil/master/uup-converter-wimlib/convert-UUP.cmd";
        String binZipUrl = "https://github.com/abbodi1406/BatUtil/archive/refs/heads/master.zip";

        Path isoFolder = Paths.get(System.getProperty("user.dir"), "ISO_FOLDER");
        Files.createDirectories(isoFolder);

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        Logg("convert-UUP.cmdをダウンロードしています。", Color.YELLOW);
        HttpRequest cmdRequest = HttpRequest.newBuilder()
                .uri(URI.create(cmdUrl))
                .build();
        Path cmdPath = isoFolder.resolve("convert-UUP.cmd");
        HttpResponse<Path> cmdResponse = client.send(cmdRequest, HttpResponse.BodyHandlers.ofFile(cmdPath));

        if (cmdResponse.statusCode() != 200) {
            throw new IOException("convert-UUP.cmdのダウンロードに失敗しました。ステータスコード: " + cmdResponse.statusCode());
        }
        Logg("convert-UUP.cmdがダウンロードされました: " + cmdPath.toString(), Color.GREEN);

        Logg("BatUtil-master.zipをダウンロードしています...", Color.YELLOW);
        HttpRequest zipRequest = HttpRequest.newBuilder()
                .uri(URI.create(binZipUrl))
                .build();
        Path zipPath = Files.createTempFile("batutil", ".zip");
        HttpResponse<Path> zipResponse = client.send(zipRequest, HttpResponse.BodyHandlers.ofFile(zipPath));

        if (zipResponse.statusCode() != 200) {
            throw new IOException("BatUtil-master.zipのダウンロードに失敗しました。ステータスコード: " + zipResponse.statusCode());
        }
        Logg("BatUtil-master.zipがダウンロードされました: " + zipPath.toString(), Color.GREEN);

        try (java.util.zip.ZipInputStream zipStream = new java.util.zip.ZipInputStream(Files.newInputStream(zipPath))) {
            java.util.zip.ZipEntry entry;
            while ((entry = zipStream.getNextEntry()) != null) {
                if (entry.getName().startsWith("BatUtil-master/uup-converter-wimlib/bin/") && !entry.isDirectory()) {
                    String relativeString = entry.getName().substring("BatUtil-master/uup-converter-wimlib/".length());
                    Path outPath = isoFolder.resolve(relativeString);
                    Files.createDirectories(outPath.getParent());
                    Files.copy(zipStream, outPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zipStream.closeEntry();
            }
        } catch (IOException e) {
            Logg("ZIPファイルの解凍中にエラーが発生しました: " + e.getMessage(), Color.RED);
            throw e;
        } finally {
            Files.deleteIfExists(zipPath);
        }
        Logg("ZIPファイルの解凍が完了しました。", Color.GREEN);

        // binフォルダが作成されるのを待つ
        Path binFolder = isoFolder.resolve("bin");
        Logg("binフォルダの作成を確認しています...", Color.YELLOW);
        boolean binExists = waitForBinFolder(binFolder, 30, 1000); // 最大30秒間待機

        if (!binExists) {
            throw new IOException("binフォルダが見つかりませんでした。解凍プロセスに問題がある可能性があります。");
        }
        Logg("binフォルダが正しく作成されました: " + binFolder.toString(), Color.GREEN);

        // convert-UUP.cmd 実行
        Logg("convert-UUP.cmdを実行しています...", Color.YELLOW);
        new ProcessBuilder("cmd.exe", "/c", "start", "\"\"", "convert-UUP.cmd")
                .directory(isoFolder.toFile())
                .start();

        Logg("convert-UUP.cmdが実行されました。");
        Logg("コマンドプロンプトが立ち上がったら、画面に「Press 0 or q to exit.」と表示されるのをお待ちください。その表示が出たら、キーボードで「0」または「q」を押して閉じてください。", Color.YELLOW);
        Logg("カレントディレクトリ/ISO_FOLDER/〇〇.isoのようにISOファイルが出力されます。");
    }

    private static boolean waitForBinFolder(Path binFolder, int maxWaitSeconds, int intervalMillis) {
        int waitedSeconds = 0;
        while (!Files.exists(binFolder)) {
            if (waitedSeconds >= maxWaitSeconds) {
                return false; // タイムアウト
            }
            try {
                TimeUnit.MILLISECONDS.sleep(intervalMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
            waitedSeconds += intervalMillis / 1000;
        }
        return true;
    }
}
