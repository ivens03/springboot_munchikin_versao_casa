package ivens.versao.casa.munchkin.munchkin.service.raca;

import ivens.versao.casa.munchkin.munchkin.exception.MunchkinNaoEncontradoException;
import ivens.versao.casa.munchkin.munchkin.model.Munchkin;
import ivens.versao.casa.munchkin.munchkin.model.Raca;
import ivens.versao.casa.munchkin.munchkin.service.MunchkinService;
import ivens.versao.casa.munchkin.munchkin.service.raca.regrasRacas.RacaService;
import ivens.versao.casa.munchkin.munchkin.service.raca.regrasRacas.RegraAnao;
import ivens.versao.casa.munchkin.munchkin.service.raca.regrasRacas.RegraCentauro;
import ivens.versao.casa.munchkin.munchkin.service.raca.regrasRacas.RegraElfo;
import ivens.versao.casa.munchkin.munchkin.service.raca.regrasRacas.RegraGnomo;
import ivens.versao.casa.munchkin.munchkin.service.raca.regrasRacas.RegraHalfling;
import ivens.versao.casa.munchkin.munchkin.service.raca.regrasRacas.RegraHomemLagarto;
import ivens.versao.casa.munchkin.munchkin.service.raca.regrasRacas.RegraHumano;
import ivens.versao.casa.munchkin.munchkin.service.raca.regrasRacas.RegraOrc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RacaServiceTest {

    private static final List<RegraDeRaca> TODAS_AS_REGRAS = List.of(
            new RegraHumano(), new RegraHalfling(), new RegraElfo(), new RegraOrc(),
            new RegraHomemLagarto(), new RegraGnomo(), new RegraCentauro(), new RegraAnao());

    @Mock
    private MunchkinService munchkinService;

    private RacaService racaService;

    @BeforeEach
    void setUp() {
        racaService = new RacaService(munchkinService, TODAS_AS_REGRAS);
    }

    @ParameterizedTest
    @EnumSource(Raca.class)
    void cadaRacaTemSuaRegra(Raca raca) {
        assertThat(racaService.regraDe(raca).raca()).isEqualTo(raca);
    }

    @Test
    void racaSemRegraImpedeSubirOService() {
        List<RegraDeRaca> semAnao = List.of(new RegraHumano(), new RegraElfo());

        assertThatThrownBy(() -> new RacaService(munchkinService, semAnao))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageStartingWith("Raça sem regra:");
    }

    @Test
    void racaComDuasRegrasImpedeSubirOService() {
        List<RegraDeRaca> elfoDuplicado = List.of(new RegraElfo(), new RegraElfo());

        assertThatThrownBy(() -> new RacaService(munchkinService, elfoDuplicado))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Raça com mais de uma regra: ELFO");
    }

    @Test
    void trocarRacaMudaARacaDoMunchkin() {
        when(munchkinService.buscar(1L)).thenReturn(new Munchkin("Ivens"));

        Munchkin resultado = racaService.trocarRaca(1L, Raca.ELFO);

        assertThat(resultado.getRaca()).isEqualTo(Raca.ELFO);
    }

    @Test
    void perderRacaVoltaParaHumano() {
        Munchkin munchkin = new Munchkin("Ivens");
        munchkin.setRaca(Raca.ANAO);
        when(munchkinService.buscar(1L)).thenReturn(munchkin);

        Munchkin resultado = racaService.perderRaca(1L);

        assertThat(resultado.getRaca()).isEqualTo(Raca.HUMANO);
    }

    @Test
    void trocarRacaDeMunchkinInexistenteLancaExcecao() {
        when(munchkinService.buscar(99L)).thenThrow(new MunchkinNaoEncontradoException(99L));

        assertThatThrownBy(() -> racaService.trocarRaca(99L, Raca.ORC))
                .isInstanceOf(MunchkinNaoEncontradoException.class);
    }
}
