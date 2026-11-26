package ivens.versao.casa.munchkin.munchkin.service.raca;

import ivens.versao.casa.munchkin.munchkin.model.Raca;
import ivens.versao.casa.munchkin.munchkin.service.raca.regrasRacas.RegraAnao;
import org.junit.jupiter.api.Test;

import static ivens.versao.casa.munchkin.munchkin.service.raca.RegraDeRaca.ILIMITADO;
import static org.assertj.core.api.Assertions.assertThat;

class RegraAnaoTest {

    private final RegraAnao regra = new RegraAnao();

    @Test
    void anaoCarregaQualquerNumeroDeItensGrandes() {
        assertThat(regra.limiteDeItensGrandes()).isEqualTo(ILIMITADO);
    }

    @Test
    void anaoFicaComSeisCartasNaMao() {
        assertThat(regra.limiteDeCartasNaMao()).isEqualTo(6);
    }

    @Test
    void anaoNaoTemBonusParaFugir() {
        assertThat(regra.raca()).isEqualTo(Raca.ANAO);
        assertThat(regra.bonusParaFugir(10)).isZero();
    }
}
