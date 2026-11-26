package ivens.versao.casa.munchkin.munchkin.service.raca;

import ivens.versao.casa.munchkin.munchkin.model.Raca;
import ivens.versao.casa.munchkin.munchkin.service.raca.regrasRacas.RegraHalfling;
import org.junit.jupiter.api.Test;

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
