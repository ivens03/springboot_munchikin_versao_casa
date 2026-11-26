package ivens.versao.casa.munchkin.carta.repository;

import ivens.versao.casa.munchkin.carta.model.Carta;
import ivens.versao.casa.munchkin.carta.model.TipoDeCarta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;

/**
 * Catálogo das cartas: cada imagem em static/img/cartas/<tipo>/ é uma carta do baralho
 * (as cópias _2, _3... são cartas repetidas). As cartas em branco ficam de fora.
 */
@Repository
public class CartaRepository {

    private static final String PASTA_DAS_CARTAS_EM_BRANCO = "cartas_em_branco/";

    private final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    private final String pastaDasCartas;

    public CartaRepository(@Value("${jogo.cartas.pasta:classpath:static/img/cartas/}") String pastaDasCartas) {
        this.pastaDasCartas = pastaDasCartas;
    }

    public List<Carta> listar(TipoDeCarta tipo) {
        try {
            String raiz = resolver.getResource(pastaDasCartas).getURL().toString();
            Resource[] imagens = resolver.getResources(pastaDasCartas + tipo.getPasta() + "/**/*.jpg");
            return Arrays.stream(imagens)
                    .map(imagem -> caminhoRelativo(raiz, imagem))
                    .filter(caminho -> !caminho.contains(PASTA_DAS_CARTAS_EM_BRANCO))
                    .sorted()
                    .map(caminho -> new Carta(tipo, caminho))
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível ler as cartas de " + tipo.getNome(), e);
        }
    }

    private static String caminhoRelativo(String raiz, Resource imagem) {
        try {
            return imagem.getURL().toString().substring(raiz.length());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
