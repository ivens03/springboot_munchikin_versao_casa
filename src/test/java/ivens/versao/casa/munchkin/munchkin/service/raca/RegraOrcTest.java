package ivens.versao.casa.munchkin.munchkin.service.raca;

import ivens.versao.casa.munchkin.munchkin.model.Raca;
import ivens.versao.casa.munchkin.munchkin.service.raca.regrasRacas.RegraOrc;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class RegraOrcTest {

    private final RegraOrc regra = new RegraOrc();

    @Test
    void orcPodeIgnorarMaldicao() {
        assertThat(regra.podeIgnorarMaldicao()).isTrue();
    }

    @ParameterizedTest
    @CsvSource({"1,0", "2,1", "9,1"})
    void orcPerdeUmNivelAoIgnorarMaldicaoMenosNoNivelUm(int nivelAtual, int niveisPerdidos) {
        assertThat(regra.niveisPerdidosAoIgnorarMaldicao(nivelAtual)).isEqualTo(niveisPerdidos);
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
