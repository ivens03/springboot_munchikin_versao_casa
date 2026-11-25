package ivens.versao.casa.munchkin.munchkin.service.fuga;

import ivens.versao.casa.munchkin.jogo.service.Dado;
import ivens.versao.casa.munchkin.munchkin.exception.MunchkinNaoEncontradoException;
import ivens.versao.casa.munchkin.munchkin.model.Munchkin;
import ivens.versao.casa.munchkin.munchkin.model.Raca;
import ivens.versao.casa.munchkin.munchkin.service.MunchkinService;
import ivens.versao.casa.munchkin.munchkin.service.raca.RacaService;
import ivens.versao.casa.munchkin.munchkin.service.raca.RegraElfo;
import ivens.versao.casa.munchkin.munchkin.service.raca.RegraHomemLagarto;
import ivens.versao.casa.munchkin.munchkin.service.raca.RegraHumano;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FugaServiceTest {

    @Mock
    private MunchkinService munchkinService;

    @Mock
    private RacaService racaService;

    @Mock
    private Dado dado;

    @InjectMocks
    private FugaService fugaService;

    private void munchkinDaRaca(Raca raca) {
        Munchkin munchkin = new Munchkin("Ivens");
        munchkin.setRaca(raca);
        when(munchkinService.buscar(1L)).thenReturn(munchkin);
    }

    @ParameterizedTest
    @CsvSource({"1,false", "4,false", "5,true", "6,true"})
    void humanoSoFogeTirandoCincoOuSeis(int valorDoDado, boolean fugiu) {
        munchkinDaRaca(Raca.HUMANO);
        when(racaService.regraDe(Raca.HUMANO)).thenReturn(new RegraHumano());
        when(dado.rolar()).thenReturn(valorDoDado);

        ResultadoDaFuga resultado = fugaService.tentarFugir(1L, 3);

        assertThat(resultado.dado()).isEqualTo(valorDoDado);
        assertThat(resultado.bonus()).isZero();
        assertThat(resultado.fugiu()).isEqualTo(fugiu);
    }

    @ParameterizedTest
    @CsvSource({"3,false", "4,true"})
    void elfoFogeComQuatroPorCausaDoMaisUm(int valorDoDado, boolean fugiu) {
        munchkinDaRaca(Raca.ELFO);
        when(racaService.regraDe(Raca.ELFO)).thenReturn(new RegraElfo());
        when(dado.rolar()).thenReturn(valorDoDado);

        ResultadoDaFuga resultado = fugaService.tentarFugir(1L, 3);

        assertThat(resultado.bonus()).isEqualTo(1);
        assertThat(resultado.total()).isEqualTo(valorDoDado + 1);
        assertThat(resultado.fugiu()).isEqualTo(fugiu);
    }

    @Test
    void homemLagartoFogeComTresDeMonstroNivelDezesseis() {
        munchkinDaRaca(Raca.HOMEM_LAGARTO);
        when(racaService.regraDe(Raca.HOMEM_LAGARTO)).thenReturn(new RegraHomemLagarto());
        when(dado.rolar()).thenReturn(3);

        ResultadoDaFuga resultado = fugaService.tentarFugir(1L, 16);

        assertThat(resultado.bonus()).isEqualTo(2);
        assertThat(resultado.fugiu()).isTrue();
    }

    @Test
    void fugaDeMunchkinInexistenteLancaExcecao() {
        when(munchkinService.buscar(99L)).thenThrow(new MunchkinNaoEncontradoException(99L));

        assertThatThrownBy(() -> fugaService.tentarFugir(99L, 3))
                .isInstanceOf(MunchkinNaoEncontradoException.class);
    }
}
