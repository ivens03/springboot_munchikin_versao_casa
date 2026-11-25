package ivens.versao.casa.munchkin.munchkin.service.raca;

import ivens.versao.casa.munchkin.munchkin.model.Raca;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static ivens.versao.casa.munchkin.munchkin.service.raca.RegraDeRaca.ILIMITADO;
import static org.assertj.core.api.Assertions.assertThat;

class RegraGnomoTest {

    private final RegraGnomo regra = new RegraGnomo();

    @Test
    void monstrosTratamOGnomoComoHalfling() {
        assertThat(regra.raca()).isEqualTo(Raca.GNOMO);
        assertThat(regra.racaParaOsMonstros()).isEqualTo(Raca.HALFLING);
    }

    @ParameterizedTest
    @CsvSource({"false,true", "true,false"})
    void gnomoUsaMonstroComoIlusaoSoEmCombateSemAjuda(boolean comAjuda, boolean pode) {
        assertThat(regra.podeUsarMonstroComoIlusao(comAjuda)).isEqualTo(pode);
    }

    @ParameterizedTest
    @CsvSource({
            "Gládio,false,1",
            "nunchaku,false,1",
            "  Garrafa,false,1",
            "Gládio,true,0",
            "Espada,false,0",
            "'',false,0"
    })
    void gnomoGanhaMaisUmEmItemNaoUsoUnicoComGouN(String item, boolean usoUnico, int bonus) {
        assertThat(regra.bonusDoItem(item, usoUnico)).isEqualTo(bonus);
    }

    @ParameterizedTest
    @CsvSource({"Nariz Flutuante,true", "Monstro do NARIZ,true", "Dragão,false"})
    void gnomoFogeAutomaticamenteDeMonstroComNarizNoNome(String monstro, boolean foge) {
        assertThat(regra.fogeAutomaticamente(monstro)).isEqualTo(foge);
    }
}
