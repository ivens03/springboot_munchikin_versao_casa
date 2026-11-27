package ivens.versao.casa.munchkin.carta.service.nivel.regras;

import ivens.versao.casa.munchkin.carta.model.OpcaoDeNivel;
import ivens.versao.casa.munchkin.carta.service.nivel.EfeitoDeNivel;
import ivens.versao.casa.munchkin.carta.service.nivel.SituacaoDoJogador;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RegraGestoDeCarinhoInesperadoTest {

    private final RegraGestoDeCarinhoInesperado regra = new RegraGestoDeCarinhoInesperado();

    @Test
    void cartas() {
        assertThat(regra.cartas()).containsExactly("gesto_de_carinho_inesperado");
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
