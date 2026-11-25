package ivens.versao.casa.munchkin.munchkin.service.raca;

import ivens.versao.casa.munchkin.munchkin.model.Raca;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static ivens.versao.casa.munchkin.munchkin.service.raca.RegraDeRaca.ILIMITADO;
import static org.assertj.core.api.Assertions.assertThat;

class RegraHalflingTest {

    private final RegraHalfling regra = new RegraHalfling();

    @Test
    void halflingVendeUmItemPorTurnoPeloDobro() {
        assertThat(regra.vendeUmItemPorTurnoPeloDobro()).isTrue();
    }

    @Test
    void halflingPodeTentarFugirDeNovoDescartandoCarta() {
        assertThat(regra.podeTentarFugirDeNovoDescartandoCarta()).isTrue();
    }

    @Test
    void halflingNaoTemBonusParaFugir() {
        assertThat(regra.raca()).isEqualTo(Raca.HALFLING);
        assertThat(regra.bonusParaFugir(10)).isZero();
    }
}
