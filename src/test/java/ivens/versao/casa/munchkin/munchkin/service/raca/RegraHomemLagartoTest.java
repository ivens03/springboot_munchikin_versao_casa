package ivens.versao.casa.munchkin.munchkin.service.raca;

import ivens.versao.casa.munchkin.munchkin.model.Raca;
import ivens.versao.casa.munchkin.munchkin.service.raca.regrasRacas.RegraHomemLagarto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class RegraHomemLagartoTest {

    private final RegraHomemLagarto regra = new RegraHomemLagarto();

    @Test
    void itemDeUsoUnicoParaAjudarMonstroValeDuasVezes() {
        assertThat(regra.raca()).isEqualTo(Raca.HOMEM_LAGARTO);
        assertThat(regra.usosDeItemDeUsoUnicoParaAjudarMonstro()).isEqualTo(2);
    }

    @ParameterizedTest
    @CsvSource({"9,0", "10,1", "15,1", "16,2", "20,2"})
    void bonusParaFugirDependeDoNivelDoMonstro(int nivelDoMonstro, int bonus) {
        assertThat(regra.bonusParaFugir(nivelDoMonstro)).isEqualTo(bonus);
    }
}
