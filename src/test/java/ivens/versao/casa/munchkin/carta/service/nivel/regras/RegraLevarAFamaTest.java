package ivens.versao.casa.munchkin.carta.service.nivel.regras;

import ivens.versao.casa.munchkin.carta.model.ImpedimentoDeUso;
import ivens.versao.casa.munchkin.carta.service.nivel.SituacaoDoJogador;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RegraLevarAFamaTest {

    private final RegraLevarAFama regra = new RegraLevarAFama();

    /** Combate ainda não existe. */
    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 5, 9})
    void porEnquantoNuncaDaParaUsar(int nivel) {
        assertThat(regra.cartas()).containsExactly("levar_a_fama");
        assertThat(regra.impedimentoDaCarta(new SituacaoDoJogador(nivel, List.of(0, 3), 10)))
                .contains(ImpedimentoDeUso.SO_QUANDO_UM_ADVERSARIO_VENCE_COMBATE);
    }
}
