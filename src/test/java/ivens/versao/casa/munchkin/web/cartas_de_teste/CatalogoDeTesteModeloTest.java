package ivens.versao.casa.munchkin.web.cartas_de_teste;

import ivens.versao.casa.munchkin.carta.dto.CartaDtoMapper;
import ivens.versao.casa.munchkin.carta.dto.GrupoDeCartasDto;
import ivens.versao.casa.munchkin.carta.model.Carta;
import ivens.versao.casa.munchkin.carta.model.TipoDeCarta;
import ivens.versao.casa.munchkin.carta.service.BaralhoService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CatalogoDeTesteModeloTest {

    private final BaralhoService baralhoService = mock(BaralhoService.class);
    private final CartaDtoMapper cartaDtoMapper = mock(CartaDtoMapper.class);
    private final CatalogoDeTesteModelo modelo = new CatalogoDeTesteModelo(baralhoService, cartaDtoMapper);

    @Test
    void catalogoInteiroSeparadoPorGrupo() {
        List<Carta> cartas = List.of(new Carta(TipoDeCarta.TESOURO, "tesouro/3_suba_de_nivel/x_tesouro.jpg"));
        List<GrupoDeCartasDto> grupos = List.of(new GrupoDeCartasDto("Tesouro · Suba de nivel", List.of()));
        when(baralhoService.todasAsCartas()).thenReturn(cartas);
        when(cartaDtoMapper.paraGrupos(cartas)).thenReturn(grupos);

        assertThat(modelo.cartasDeTeste()).isEqualTo(grupos);
    }
}
