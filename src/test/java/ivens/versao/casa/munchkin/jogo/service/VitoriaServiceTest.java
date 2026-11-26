package ivens.versao.casa.munchkin.jogo.service;

import ivens.versao.casa.munchkin.jogo.exception.NivelDeVitoriaInvalidoException;
import ivens.versao.casa.munchkin.jogo.model.TipoDeVitoria;
import ivens.versao.casa.munchkin.jogo.model.Vitoria;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VitoriaServiceTest {

    private final VitoriaService vitoriaService = new VitoriaService();

    @Test
    void classicaVenceNoNivelDez() {
        assertThat(vitoriaService.definir(TipoDeVitoria.CLASSICA, null))
                .isEqualTo(new Vitoria(TipoDeVitoria.CLASSICA, 10));
    }

    @Test
    void superMunchkinVenceNoNivelVinte() {
        assertThat(vitoriaService.definir(TipoDeVitoria.SUPER_MUNCHKIN, null))
                .isEqualTo(new Vitoria(TipoDeVitoria.SUPER_MUNCHKIN, 20));
    }

    @Test
    void nivelEscolhidoSoContaNaPersonalizada() {
        assertThat(vitoriaService.definir(TipoDeVitoria.CLASSICA, 50).nivel()).isEqualTo(10);
        assertThat(vitoriaService.definir(TipoDeVitoria.SUPER_MUNCHKIN, 500).nivel()).isEqualTo(20);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 15, 99, 100})
    void personalizadaVenceNoNivelEscolhidoDeUmACem(int nivel) {
        assertThat(vitoriaService.definir(TipoDeVitoria.PERSONALIZADA, nivel))
                .isEqualTo(new Vitoria(TipoDeVitoria.PERSONALIZADA, nivel));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 101})
    void personalizadaForaDeUmACemEInvalida(int nivel) {
        assertThatThrownBy(() -> vitoriaService.definir(TipoDeVitoria.PERSONALIZADA, nivel))
                .isInstanceOf(NivelDeVitoriaInvalidoException.class)
                .hasMessageContaining("de 1 a 100");
    }

    @Test
    void personalizadaSemNivelEInvalida() {
        assertThatThrownBy(() -> vitoriaService.definir(TipoDeVitoria.PERSONALIZADA, null))
                .isInstanceOf(NivelDeVitoriaInvalidoException.class);
    }
}
