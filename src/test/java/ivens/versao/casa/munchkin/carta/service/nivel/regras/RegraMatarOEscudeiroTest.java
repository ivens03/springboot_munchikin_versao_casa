package ivens.versao.casa.munchkin.carta.service.nivel.regras;

import ivens.versao.casa.munchkin.carta.model.ImpedimentoDeUso;
import ivens.versao.casa.munchkin.carta.service.nivel.SituacaoDoJogador;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RegraMatarOEscudeiroTest {

    private final RegraMatarOEscudeiro regra = new RegraMatarOEscudeiro();

    /** Escudeiro ainda não existe. */
    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 5, 9})
    void porEnquantoNuncaDaParaUsar(int nivel) {
        assertThat(regra.cartas()).containsExactly("matar_o_escudeiro");
        assertThat(regra.impedimentoDaCarta(new SituacaoDoJogador(nivel, List.of(0, 3), 10)))
                .contains(ImpedimentoDeUso.SO_COM_ESCUDEIRO_EM_JOGO);
    }
}
