package ivens.versao.casa.munchkin.munchkin.service.raca;

import ivens.versao.casa.munchkin.munchkin.model.Raca;
import ivens.versao.casa.munchkin.munchkin.service.raca.regrasRacas.RegraElfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class RegraElfoTest {

    private final RegraElfo regra = new RegraElfo();

    @Test
    void elfoGanhaMaisUmParaFugir() {
        assertThat(regra.bonusParaFugir(1)).isEqualTo(1);
        assertThat(regra.bonusParaFugir(20)).isEqualTo(1);
    }

    @ParameterizedTest
    @CsvSource({"0,0", "1,1", "3,3"})
    void elfoGanhaUmNivelPorMonstroQueAjudouAMatar(int monstros, int niveis) {
        assertThat(regra.niveisPorAjudarAMatar(monstros)).isEqualTo(niveis);
    }

    @Test
    void elfoMantemOsLimitesPadrao() {
        assertThat(regra.raca()).isEqualTo(Raca.ELFO);
        assertThat(regra.limiteDeItensGrandes()).isEqualTo(1);
        assertThat(regra.limiteDeCartasNaMao()).isEqualTo(5);
    }
}
