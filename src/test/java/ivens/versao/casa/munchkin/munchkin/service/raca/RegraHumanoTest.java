package ivens.versao.casa.munchkin.munchkin.service.raca;

import ivens.versao.casa.munchkin.munchkin.model.Raca;
import ivens.versao.casa.munchkin.jogo.service.RegrasBasicas;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static ivens.versao.casa.munchkin.munchkin.service.raca.RegraDeRaca.ILIMITADO;
import static org.assertj.core.api.Assertions.assertThat;

class RegraHumanoTest {

    private final RegraHumano regra = new RegraHumano();

    @Test
    void humanoNaoTemHabilidade() {
        assertThat(regra.raca()).isEqualTo(Raca.HUMANO);
        assertThat(regra.racaParaOsMonstros()).isEqualTo(Raca.HUMANO);
        assertThat(regra.bonusParaFugir(20)).isZero();
        assertThat(regra.podeTentarFugirDeNovoDescartandoCarta()).isFalse();
        assertThat(regra.fogeAutomaticamente("Nariz Flutuante")).isFalse();
        assertThat(regra.niveisPorAjudarAMatar(2)).isZero();
        assertThat(regra.niveisExtrasAoVencer(true, 20)).isZero();
        assertThat(regra.podeUsarMonstroComoIlusao(false)).isFalse();
        assertThat(regra.bonusDoItem("Gladio", false)).isZero();
        assertThat(regra.usosDeItemDeUsoUnicoParaAjudarMonstro()).isEqualTo(1);
        assertThat(regra.podeIgnorarMaldicao()).isFalse();
        assertThat(regra.vendeUmItemPorTurnoPeloDobro()).isFalse();
    }

    @Test
    void humanoTemOsLimitesPadrao() {
        assertThat(regra.limiteDeItensGrandes()).isEqualTo(1);
        assertThat(regra.limiteDeCalcados()).isEqualTo(1);
        assertThat(regra.limiteDeMontarias()).isEqualTo(1);
        assertThat(regra.limiteDeCartasNaMao()).isEqualTo(RegrasBasicas.LIMITE_DE_CARTAS_NA_MAO).isEqualTo(5);
    }
}
