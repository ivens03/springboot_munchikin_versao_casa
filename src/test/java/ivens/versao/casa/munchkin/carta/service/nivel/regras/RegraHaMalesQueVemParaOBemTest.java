package ivens.versao.casa.munchkin.carta.service.nivel.regras;

import ivens.versao.casa.munchkin.carta.model.ImpedimentoDeUso;
import ivens.versao.casa.munchkin.carta.service.nivel.SituacaoDoJogador;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RegraHaMalesQueVemParaOBemTest {

    private final RegraHaMalesQueVemParaOBem regra = new RegraHaMalesQueVemParaOBem();

    /** Escudeiro e Montaria ainda não existem. */
    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 5, 9})
    void porEnquantoNuncaDaParaUsar(int nivel) {
        assertThat(regra.cartas()).containsExactly("ha_males_que_vem_para_o_bem");
        assertThat(regra.impedimentoDaCarta(new SituacaoDoJogador(nivel, List.of(0, 3), 10)))
                .contains(ImpedimentoDeUso.SO_DEPOIS_DE_PERDER_ESCUDEIRO_OU_MONTARIA);
    }
}
