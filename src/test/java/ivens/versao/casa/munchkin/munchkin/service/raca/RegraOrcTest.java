package ivens.versao.casa.munchkin.munchkin.service.raca;

import ivens.versao.casa.munchkin.munchkin.model.Raca;
import ivens.versao.casa.munchkin.munchkin.service.raca.regrasRacas.RegraOrc;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class RegraOrcTest {

    private final RegraOrc regra = new RegraOrc();

    @Test
    void orcPodeIgnorarMaldicao() {
        assertThat(regra.podeIgnorarMaldicao()).isTrue();
    }

    /** Regra da casa: nível pode ser negativo, então em qualquer nível o Orc paga 1 para ignorar a Maldição. */
    @ParameterizedTest
    @ValueSource(ints = {-5, -1, 0, 1, 2, 9, 10, 15, 16})
    void orcSemprePerdeUmNivelAoIgnorarMaldicaoMesmoFicandoNegativo(int nivelAtual) {
        assertThat(regra.niveisPerdidosAoIgnorarMaldicao(nivelAtual)).isEqualTo(1);
    }

    @ParameterizedTest
    @CsvSource({"true,11,1", "true,10,0", "true,1,0", "false,11,0"})
    void orcSobeNivelExtraVencendoSozinhoPorMaisDeDez(boolean sozinho, int diferenca, int niveisExtras) {
        assertThat(regra.niveisExtrasAoVencer(sozinho, diferenca)).isEqualTo(niveisExtras);
    }

    @Test
    void orcMantemORestoPadrao() {
        assertThat(regra.raca()).isEqualTo(Raca.ORC);
        assertThat(regra.bonusParaFugir(10)).isZero();
    }
}
