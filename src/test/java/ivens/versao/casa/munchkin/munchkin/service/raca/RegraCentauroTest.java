package ivens.versao.casa.munchkin.munchkin.service.raca;

import ivens.versao.casa.munchkin.munchkin.model.Raca;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static ivens.versao.casa.munchkin.munchkin.service.raca.RegraDeRaca.ILIMITADO;
import static org.assertj.core.api.Assertions.assertThat;

class RegraCentauroTest {

    private final RegraCentauro regra = new RegraCentauro();

    @Test
    void centauroUsaDoisCalcados() {
        assertThat(regra.limiteDeCalcados()).isEqualTo(2);
    }

    @Test
    void centauroTemQualquerNumeroDeMontarias() {
        assertThat(regra.limiteDeMontarias()).isEqualTo(ILIMITADO);
    }

    @Test
    void centauroMantemORestoPadrao() {
        assertThat(regra.raca()).isEqualTo(Raca.CENTAURO);
        assertThat(regra.limiteDeItensGrandes()).isEqualTo(1);
    }
}
