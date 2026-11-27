package ivens.versao.casa.munchkin.carta.dto;

import ivens.versao.casa.munchkin.carta.model.Carta;
import ivens.versao.casa.munchkin.carta.model.TipoDeCarta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CartaDtoMapperTest {

    private final CartaDtoMapper mapper = new CartaDtoMapper();

    @Test
    void cartaDeMasmorraTemImagemEVersoDeMasmorra() {
        CartaDto dto = mapper.paraDto(new Carta(TipoDeCarta.MASMORRA, "masmorra/5_racas/elfo_masmorra.jpg"));

        assertThat(dto).isEqualTo(new CartaDto("masmorra", "Elfo",
                "/img/cartas/masmorra/5_racas/elfo_masmorra.jpg", "/img/cartas/traseira/verso_masmorra.jpg"));
    }

    @Test
    void cartaDeTesouroTemVersoDeTesouro() {
        CartaDto dto = mapper.paraDto(new Carta(TipoDeCarta.TESOURO, "tesouro/1_itens/espada_tesouro.jpg"));

        assertThat(dto.tipo()).isEqualTo("tesouro");
        assertThat(dto.verso()).isEqualTo("/img/cartas/traseira/verso_tesouro.jpg");
    }

    @ParameterizedTest
    @CsvSource({
            "masmorra/5_racas/homem_lagarto_2_masmorra.jpg,MASMORRA,Homem lagarto",
            "masmorra/1_monstros/joao_do_caixao_masmorra.jpg,MASMORRA,Joao do caixao",
            "tesouro/2_uso_unico/pocao_de_cura_tesouro.jpg,TESOURO,Pocao de cura"
    })
    void nomeVemDoArquivoSemTipoNemNumeroDaCopia(String imagem, TipoDeCarta tipo, String nome) {
        assertThat(mapper.nome(new Carta(tipo, imagem))).isEqualTo(nome);
    }

    @Test
    void maoFechadaTemSoOsVersosEContaCadaPilha() {
        MaoFechadaDto mao = mapper.paraMaoFechada(List.of(
                new Carta(TipoDeCarta.TESOURO, "tesouro/1_itens/espada_tesouro.jpg"),
                new Carta(TipoDeCarta.MASMORRA, "masmorra/5_racas/elfo_masmorra.jpg"),
                new Carta(TipoDeCarta.MASMORRA, "masmorra/5_racas/orc_masmorra.jpg")));

        assertThat(mao.cartas()).containsExactly(
                new CartaFechadaDto("tesouro", "/img/cartas/traseira/verso_tesouro.jpg"),
                new CartaFechadaDto("masmorra", "/img/cartas/traseira/verso_masmorra.jpg"),
                new CartaFechadaDto("masmorra", "/img/cartas/traseira/verso_masmorra.jpg"));
        assertThat(mao.total()).isEqualTo(3);
        assertThat(mao.cartasDeMasmorra()).isEqualTo(2);
        assertThat(mao.cartasDeTesouro()).isEqualTo(1);
    }

    @Test
    void maoFechadaVaziaTemTudoZerado() {
        MaoFechadaDto mao = mapper.paraMaoFechada(List.of());

        assertThat(mao.total()).isZero();
        assertThat(mao.cartasDeMasmorra()).isZero();
        assertThat(mao.cartasDeTesouro()).isZero();
    }

    @Test
    void converteAListaNaMesmaOrdem() {
        List<CartaDto> dtos = mapper.paraDto(List.of(
                new Carta(TipoDeCarta.TESOURO, "tesouro/1_itens/espada_tesouro.jpg"),
                new Carta(TipoDeCarta.MASMORRA, "masmorra/5_racas/elfo_masmorra.jpg")));

        assertThat(dtos).extracting(CartaDto::nome).containsExactly("Espada", "Elfo");
    }

    @Test
    void catalogoSeparadoPorPilhaEPastaNaOrdemDasPastasSemRepetirCopia() {
        List<Carta> cartas = List.of(
                new Carta(TipoDeCarta.TESOURO, "tesouro/3_suba_de_nivel/pagar_a_pizza_tesouro.jpg"),
                new Carta(TipoDeCarta.MASMORRA, "masmorra/10_especiais/x_masmorra.jpg"),
                new Carta(TipoDeCarta.MASMORRA, "masmorra/5_racas/elfo_masmorra.jpg"),
                new Carta(TipoDeCarta.MASMORRA, "masmorra/5_racas/elfo_2_masmorra.jpg"),
                new Carta(TipoDeCarta.TESOURO, "tesouro/3_suba_de_nivel/1000_pecas_de_ouro_tesouro.jpg"));

        List<GrupoDeCartasDto> grupos = mapper.paraGrupos(cartas);

        assertThat(grupos).extracting(GrupoDeCartasDto::nome)
                .containsExactly("Masmorra · Racas", "Masmorra · Especiais", "Tesouro · Suba de nivel");
        assertThat(grupos.get(0).cartas()).containsExactly(new CartaDoCatalogoDto("masmorra/5_racas/elfo_masmorra.jpg",
                "Elfo", "/img/cartas/masmorra/5_racas/elfo_masmorra.jpg"));
        assertThat(grupos.get(2).cartas()).extracting(CartaDoCatalogoDto::nome)
                .containsExactly("1000 pecas de ouro", "Pagar a pizza");
    }

    @Test
    void nomeDoGrupoSemONumeroDaPasta() {
        assertThat(mapper.nomeDoGrupo(new Carta(TipoDeCarta.TESOURO, "tesouro/3_suba_de_nivel/x_tesouro.jpg")))
                .isEqualTo("Tesouro · Suba de nivel");
        assertThat(mapper.nomeDoGrupo(new Carta(TipoDeCarta.TESOURO, "tesouro/x_tesouro.jpg")))
                .isEqualTo("Tesouro · Outras");
    }
}
