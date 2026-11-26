package ivens.versao.casa.munchkin.tela;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tira print de uma tela com o Chrome headless e compara pixel a pixel com a imagem base
 * em src/test/resources/telas/<tela>.png. Com -Dtelas.atualizar=true regrava a imagem base.
 */
public final class ComparadorDeTela {

    public static final int LARGURA = 1280;
    public static final int ALTURA = 720;

    /** Diferença máxima por canal (0-255) para o pixel ainda contar como igual. */
    private static final int TOLERANCIA_CANAL = 16;
    /** Fração máxima de pixels diferentes (antialiasing). */
    private static final double TOLERANCIA_PIXELS = 0.005;

    private static final Path PASTA_BASE = Path.of("src/test/resources/telas");
    private static final Path PASTA_SAIDA = Path.of("target/telas");

    private ComparadorDeTela() {
    }

    public static void comparar(String tela, String url) throws Exception {
        Files.createDirectories(PASTA_SAIDA);
        Path atual = PASTA_SAIDA.resolve(tela + ".png");
        tirarPrint(url, atual);

        Path base = PASTA_BASE.resolve(tela + ".png");
        if (Boolean.getBoolean("telas.atualizar")) {
            Files.createDirectories(PASTA_BASE);
            Files.copy(atual, base, StandardCopyOption.REPLACE_EXISTING);
            return;
        }
        assertTrue(Files.exists(base),
                "Imagem base não encontrada: " + base + " (gere com ./mvnw test -Dtelas.atualizar=true)");

        BufferedImage esperada = ImageIO.read(base.toFile());
        BufferedImage obtida = ImageIO.read(atual.toFile());
        assertEquals(esperada.getWidth(), obtida.getWidth(), "Largura do print difere da imagem base");
        assertEquals(esperada.getHeight(), obtida.getHeight(), "Altura do print difere da imagem base");

        BufferedImage diff = new BufferedImage(esperada.getWidth(), esperada.getHeight(), BufferedImage.TYPE_INT_RGB);
        long diferentes = 0;
        for (int y = 0; y < esperada.getHeight(); y++) {
            for (int x = 0; x < esperada.getWidth(); x++) {
                int a = esperada.getRGB(x, y);
                int b = obtida.getRGB(x, y);
                if (diferente(a, b)) {
                    diferentes++;
                    diff.setRGB(x, y, 0xFF0000);
                } else {
                    diff.setRGB(x, y, (a & 0xFEFEFE) >> 1);
                }
            }
        }

        double fracao = (double) diferentes / ((long) esperada.getWidth() * esperada.getHeight());
        if (fracao > TOLERANCIA_PIXELS) {
            Path arquivoDiff = PASTA_SAIDA.resolve(tela + "-diff.png");
            ImageIO.write(diff, "png", arquivoDiff.toFile());
            fail(String.format("Tela '%s' diferente da base: %.3f%% dos pixels (limite %.3f%%). Diff em %s",
                    tela, fracao * 100, TOLERANCIA_PIXELS * 100, arquivoDiff));
        }
    }

    private static boolean diferente(int a, int b) {
        for (int deslocamento = 0; deslocamento <= 16; deslocamento += 8) {
            int ca = (a >> deslocamento) & 0xFF;
            int cb = (b >> deslocamento) & 0xFF;
            if (Math.abs(ca - cb) > TOLERANCIA_CANAL) {
                return true;
            }
        }
        return false;
    }

    private static void tirarPrint(String url, Path destino) throws IOException, InterruptedException {
        Files.deleteIfExists(destino);
        Path perfil = Files.createTempDirectory("chrome-tela");
        Process chrome = new ProcessBuilder(
                "google-chrome", "--headless", "--disable-gpu", "--no-proxy-server",
                "--hide-scrollbars", "--force-device-scale-factor=1",
                // sem animação: a tela já aparece no estado final (o print não depende do tempo)
                "--force-prefers-reduced-motion",
                "--user-data-dir=" + perfil.toAbsolutePath(),
                "--window-size=" + LARGURA + "," + ALTURA,
                "--screenshot=" + destino.toAbsolutePath(),
                url)
                .redirectErrorStream(true)
                .redirectOutput(PASTA_SAIDA.resolve("chrome.log").toFile())
                .start();
        if (!chrome.waitFor(60, TimeUnit.SECONDS)) {
            chrome.destroyForcibly();
            fail("Chrome headless não terminou o print de " + url);
        }
        assertTrue(Files.exists(destino), "Chrome não gerou o print de " + url + " (ver target/telas/chrome.log)");
    }
}
