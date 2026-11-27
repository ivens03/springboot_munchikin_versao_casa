package ivens.versao.casa.munchkin.carta.repository;

import ivens.versao.casa.munchkin.carta.model.Carta;
import ivens.versao.casa.munchkin.carta.model.TipoDeCarta;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** Usa as imagens vazias de src/test/resources/cartas_teste/ no lugar das cartas de verdade. */
class CartaRepositoryTest {

    private final CartaRepository repository = new CartaRepository("classpath:cartas_teste/");

    @Test
    void cadaImagemDaMasmorraEUmaCartaInclusiveAsCopias() {
        assertThat(repository.listar(TipoDeCarta.MASMORRA)).containsExactly(
                new Carta(TipoDeCarta.MASMORRA, "masmorra/1_monstros/dragao_2_masmorra.jpg"),
                new Carta(TipoDeCarta.MASMORRA, "masmorra/1_monstros/dragao_masmorra.jpg"));
    }

    @Test
    void cartasEmBrancoEArquivosQueNaoSaoJpgFicamDeFora() {
        assertThat(repository.listar(TipoDeCarta.MASMORRA))
                .extracting(Carta::imagem)
                .noneMatch(imagem -> imagem.contains("cartas_em_branco") || imagem.endsWith(".png"));
    }

    @Test
    void tipoSemPastaNaoTemCartas() {
        assertThat(repository.listar(TipoDeCarta.DUNGEON)).isEmpty();
    }

    @Test
    void tesouroSoTemCartasDaPastaDeTesouro() {
        assertThat(repository.listar(TipoDeCarta.TESOURO))
                .containsExactly(new Carta(TipoDeCarta.TESOURO, "tesouro/1_itens/espada_tesouro.jpg"));
    }
}
