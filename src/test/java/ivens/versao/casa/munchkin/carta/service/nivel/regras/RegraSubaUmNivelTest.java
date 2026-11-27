package ivens.versao.casa.munchkin.carta.service.nivel.regras;

import ivens.versao.casa.munchkin.carta.model.OpcaoDeNivel;
import ivens.versao.casa.munchkin.carta.service.nivel.EfeitoDeNivel;
import ivens.versao.casa.munchkin.carta.service.nivel.SituacaoDoJogador;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RegraSubaUmNivelTest {

    private final RegraSubaUmNivel regra = new RegraSubaUmNivel();

    @Test
    void cartas() {
        assertThat(regra.cartas()).hasSize(21).contains("1000_pecas_de_ouro", "pagar_a_pizza", "violencia_desnecessaria");
    }

    @Test
    void sobeUmNivelSemCondicaoNemAlvo() {
        assertThat(regra.opcoes()).containsExactly(OpcaoDeNivel.SUBIR_1_NIVEL);
        assertThat(regra.precisaDeAlvo()).isFalse();
        assertThat(regra.impedimentoDaCarta(new SituacaoDoJogador(0, List.of(0), 10))).isEmpty();
        assertThat(regra.impedimentoDaCarta(new SituacaoDoJogador(9, List.of(-2), 10))).isEmpty();
        assertThat(regra.efeito(OpcaoDeNivel.SUBIR_1_NIVEL)).isEqualTo(EfeitoDeNivel.SUBIR_1);
    }
}
