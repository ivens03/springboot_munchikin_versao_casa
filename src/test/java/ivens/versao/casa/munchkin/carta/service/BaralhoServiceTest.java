package ivens.versao.casa.munchkin.carta.service;

import ivens.versao.casa.munchkin.carta.exception.CartaNaoEncontradaException;
import ivens.versao.casa.munchkin.carta.exception.CartasInsuficientesException;
import ivens.versao.casa.munchkin.carta.model.Carta;
import ivens.versao.casa.munchkin.carta.model.TipoDeCarta;
import ivens.versao.casa.munchkin.carta.repository.CartaRepository;
import ivens.versao.casa.munchkin.jogo.service.Embaralhador;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaralhoServiceTest {

    @Mock
    private CartaRepository cartaRepository;

    @Mock
    private Embaralhador embaralhador;

    @InjectMocks
    private BaralhoService baralhoService;

    private static List<Carta> baralho(TipoDeCarta tipo, int quantidade) {
        return IntStream.rangeClosed(1, quantidade)
                .mapToObj(n -> new Carta(tipo, tipo.getPasta() + "/carta_" + n + ".jpg"))
                .toList();
    }

    @Test
    void cartasDePersonagemSaoAsDeRacaEClasseUmaDeCada() {
        when(cartaRepository.listar(TipoDeCarta.MASMORRA)).thenReturn(List.of(
                new Carta(TipoDeCarta.MASMORRA, "masmorra/1_monstros/dragao_masmorra.jpg"),
                new Carta(TipoDeCarta.MASMORRA, "masmorra/5_racas/elfo_2_masmorra.jpg"),
                new Carta(TipoDeCarta.MASMORRA, "masmorra/5_racas/elfo_masmorra.jpg"),
                new Carta(TipoDeCarta.MASMORRA, "masmorra/6_classes/mago_masmorra.jpg"),
                new Carta(TipoDeCarta.MASMORRA, "masmorra/7_modificadores_de_raca_e_classe/super_munchkin_masmorra.jpg")));

        assertThat(baralhoService.cartasDePersonagem()).containsExactly(
                new Carta(TipoDeCarta.MASMORRA, "masmorra/5_racas/elfo_2_masmorra.jpg"),
                new Carta(TipoDeCarta.MASMORRA, "masmorra/6_classes/mago_masmorra.jpg"));
    }

    @Test
    void catalogoTemMasmorraEDepoisTesouro() {
        when(cartaRepository.listar(TipoDeCarta.TESOURO)).thenReturn(baralho(TipoDeCarta.TESOURO, 2));
        when(cartaRepository.listar(TipoDeCarta.MASMORRA)).thenReturn(baralho(TipoDeCarta.MASMORRA, 1));
        when(cartaRepository.listar(TipoDeCarta.DUNGEON)).thenReturn(List.of());

        assertThat(baralhoService.todasAsCartas()).containsExactly(new Carta(TipoDeCarta.MASMORRA, "masmorra/carta_1.jpg"),
                new Carta(TipoDeCarta.TESOURO, "tesouro/carta_1.jpg"), new Carta(TipoDeCarta.TESOURO, "tesouro/carta_2.jpg"));
    }

    @Test
    void buscaACartaDoCatalogoPelaImagem() {
        when(cartaRepository.listar(TipoDeCarta.TESOURO)).thenReturn(baralho(TipoDeCarta.TESOURO, 2));
        when(cartaRepository.listar(TipoDeCarta.MASMORRA)).thenReturn(baralho(TipoDeCarta.MASMORRA, 1));
        when(cartaRepository.listar(TipoDeCarta.DUNGEON)).thenReturn(List.of());

        assertThat(baralhoService.buscar("tesouro/carta_2.jpg")).isEqualTo(new Carta(TipoDeCarta.TESOURO, "tesouro/carta_2.jpg"));
        assertThatThrownBy(() -> baralhoService.buscar("tesouro/nao_existe.jpg"))
                .isInstanceOf(CartaNaoEncontradaException.class);
    }

    @Test
    void maoInicialTemCincoDeTesouroEDepoisCincoDeMasmorra() {
        when(cartaRepository.listar(TipoDeCarta.TESOURO)).thenReturn(baralho(TipoDeCarta.TESOURO, 20));
        when(cartaRepository.listar(TipoDeCarta.MASMORRA)).thenReturn(baralho(TipoDeCarta.MASMORRA, 30));
        when(embaralhador.embaralhar(anyList())).thenAnswer(invocacao -> invocacao.getArgument(0));

        List<Carta> mao = baralhoService.distribuirMaosIniciais(1).getFirst();

        assertThat(mao).hasSize(10);
        assertThat(mao.subList(0, 5)).extracting(Carta::tipo).containsOnly(TipoDeCarta.TESOURO);
        assertThat(mao.subList(5, 10)).extracting(Carta::tipo).containsOnly(TipoDeCarta.MASMORRA);
    }

    @Test
    void compraDoTopoDoBaralhoEmbaralhado() {
        List<Carta> tesouro = baralho(TipoDeCarta.TESOURO, 6);
        List<Carta> masmorra = baralho(TipoDeCarta.MASMORRA, 6);
        when(cartaRepository.listar(TipoDeCarta.TESOURO)).thenReturn(tesouro);
        when(cartaRepository.listar(TipoDeCarta.MASMORRA)).thenReturn(masmorra);
        when(embaralhador.embaralhar(tesouro)).thenReturn(tesouro.reversed());
        when(embaralhador.embaralhar(masmorra)).thenReturn(masmorra.reversed());

        List<Carta> mao = baralhoService.distribuirMaosIniciais(1).getFirst();

        assertThat(mao.getFirst()).isEqualTo(tesouro.getLast());
        assertThat(mao.get(5)).isEqualTo(masmorra.getLast());
    }

    @Test
    void cadaJogadorRecebeUmaMaoEEmbaralhaUmaVezSoPorPilha() {
        when(cartaRepository.listar(TipoDeCarta.TESOURO)).thenReturn(baralho(TipoDeCarta.TESOURO, 60));
        when(cartaRepository.listar(TipoDeCarta.MASMORRA)).thenReturn(baralho(TipoDeCarta.MASMORRA, 60));
        when(embaralhador.embaralhar(anyList())).thenAnswer(invocacao -> invocacao.getArgument(0));

        List<List<Carta>> maos = baralhoService.distribuirMaosIniciais(11);

        assertThat(maos).hasSize(11).allSatisfy(mao -> {
            assertThat(mao).hasSize(10);
            assertThat(mao.subList(0, 5)).extracting(Carta::tipo).containsOnly(TipoDeCarta.TESOURO);
            assertThat(mao.subList(5, 10)).extracting(Carta::tipo).containsOnly(TipoDeCarta.MASMORRA);
        });
        verify(embaralhador, times(2)).embaralhar(anyList());
    }

    @Test
    void nenhumaCartaSeRepeteEntreAsMaos() {
        when(cartaRepository.listar(TipoDeCarta.TESOURO)).thenReturn(baralho(TipoDeCarta.TESOURO, 60));
        when(cartaRepository.listar(TipoDeCarta.MASMORRA)).thenReturn(baralho(TipoDeCarta.MASMORRA, 60));
        when(embaralhador.embaralhar(anyList())).thenAnswer(invocacao -> invocacao.getArgument(0));

        List<Carta> todas = baralhoService.distribuirMaosIniciais(11).stream().flatMap(List::stream).toList();

        assertThat(todas).hasSize(110).doesNotHaveDuplicates();
    }

    @Test
    void baralhoComMenosCartasQueOsJogadoresPrecisamLancaExcecao() {
        when(cartaRepository.listar(TipoDeCarta.TESOURO)).thenReturn(baralho(TipoDeCarta.TESOURO, 14));

        assertThatThrownBy(() -> baralhoService.distribuirMaosIniciais(3))
                .isInstanceOf(CartasInsuficientesException.class)
                .hasMessage("Baralho de Tesouro tem 14 cartas, mas precisa comprar 15");
    }
}
