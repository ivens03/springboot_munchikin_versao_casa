package ivens.versao.casa.munchkin.partida.service;

import ivens.versao.casa.munchkin.carta.model.Carta;
import ivens.versao.casa.munchkin.carta.model.TipoDeCarta;
import ivens.versao.casa.munchkin.carta.service.BaralhoService;
import ivens.versao.casa.munchkin.jogo.exception.NivelDeVitoriaInvalidoException;
import ivens.versao.casa.munchkin.jogo.model.TipoDeVitoria;
import ivens.versao.casa.munchkin.jogo.model.Vitoria;
import ivens.versao.casa.munchkin.jogo.service.Embaralhador;
import ivens.versao.casa.munchkin.jogo.service.VitoriaService;
import ivens.versao.casa.munchkin.munchkin.model.Munchkin;
import ivens.versao.casa.munchkin.munchkin.service.MunchkinService;
import ivens.versao.casa.munchkin.partida.exception.ForaDaHoraDeVenderOuroException;
import ivens.versao.casa.munchkin.partida.exception.OrdemDaMaoInvalidaException;
import ivens.versao.casa.munchkin.partida.exception.PartidaNaoEncontradaException;
import ivens.versao.casa.munchkin.partida.model.JogadorDaPartida;
import ivens.versao.casa.munchkin.partida.model.Partida;
import ivens.versao.casa.munchkin.partida.repository.PartidaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/** Repository de verdade (é só um mapa em memória); o resto mockado. */
class PartidaServiceTest {

    private final MunchkinService munchkinService = mock(MunchkinService.class);
    private final BaralhoService baralhoService = mock(BaralhoService.class);
    private final VitoriaService vitoriaService = mock(VitoriaService.class);
    private final PartidaRepository partidaRepository = new PartidaRepository();
    private final PartidaService partidaService = new PartidaService(partidaRepository, munchkinService, baralhoService,
            vitoriaService);

    private final Vitoria classica = new Vitoria(TipoDeVitoria.CLASSICA, 10);
    private final Munchkin jogador = new Munchkin("Jogador");
    private final Munchkin bot1 = new Munchkin("Bot 1");
    private final Munchkin bot2 = new Munchkin("Bot 2");
    private final List<Carta> maoDoJogador = List.of(carta("a"), carta("b"), carta("c"));
    private final List<Carta> maoDoBot1 = List.of(carta("d"));
    private final List<Carta> maoDoBot2 = List.of(carta("e"));

    @BeforeEach
    void mesaComDoisBots() {
        when(vitoriaService.definir(TipoDeVitoria.CLASSICA, null)).thenReturn(classica);
        when(munchkinService.novoJogador()).thenReturn(jogador);
        when(munchkinService.novosBots(2)).thenReturn(List.of(bot1, bot2));
        when(baralhoService.distribuirMaosIniciais(3)).thenReturn(List.of(maoDoJogador, maoDoBot1, maoDoBot2));
    }

    @Test
    void partidaNovaComecaNaRodadaDePreparacaoComAsMaosDistribuidas() {
        Partida partida = partidaService.criarComMaquina(2, TipoDeVitoria.CLASSICA, null);

        assertThat(partida.getRodada()).isZero();
        assertThat(partidaService.emPreparacao(partida)).isTrue();
        assertThat(partida.getVitoria()).isEqualTo(classica);
        assertThat(partida.getJogador().getMunchkin()).isEqualTo(jogador);
        assertThat(partida.getJogador().getMao()).isEqualTo(maoDoJogador);
        assertThat(partida.getOponentes()).extracting(JogadorDaPartida::getMunchkin).containsExactly(bot1, bot2);
        assertThat(partida.getOponentes()).extracting(JogadorDaPartida::getMao).containsExactly(maoDoBot1, maoDoBot2);
        assertThat(partidaService.buscar(partida.getId())).isSameAs(partida);
    }

    @Test
    void naPreparacaoOJogadorAindaNaoDeuOkEOsBotsJaDeram() {
        Partida partida = partidaService.criarComMaquina(2, TipoDeVitoria.CLASSICA, null);

        assertThat(partida.getJogador().isPronto()).isFalse();
        assertThat(partida.getJogador().isBot()).isFalse();
        assertThat(partida.getOponentes()).allMatch(JogadorDaPartida::isPronto).allMatch(JogadorDaPartida::isBot);
    }

    @Test
    void nivelDeVitoriaInvalidoNaoChegaADistribuirAsCartas() {
        when(vitoriaService.definir(TipoDeVitoria.PERSONALIZADA, 101))
                .thenThrow(new NivelDeVitoriaInvalidoException(101));

        assertThatThrownBy(() -> partidaService.criarComMaquina(2, TipoDeVitoria.PERSONALIZADA, 101))
                .isInstanceOf(NivelDeVitoriaInvalidoException.class);
        verifyNoInteractions(baralhoService);
    }

    @Test
    void comOOkDoJogadorETodosOsBotsProntosComecaARodada1() {
        Partida partida = partidaService.criarComMaquina(2, TipoDeVitoria.CLASSICA, null);

        partidaService.confirmarPronto(partida.getId());

        assertThat(partida.getJogador().isPronto()).isTrue();
        assertThat(partida.getRodada()).isEqualTo(1);
        assertThat(partidaService.emPreparacao(partida)).isFalse();
    }

    @Test
    void enquantoFaltaOOkDeAlguemContinuaNaPreparacao() {
        // multiplayer: o outro jogador ainda está se organizando
        JogadorDaPartida amigo = new JogadorDaPartida(new Munchkin("Amigo"), maoDoBot1, false);
        Partida partida = partidaRepository.salvar(new Partida(UUID.randomUUID(), classica,
                List.of(new JogadorDaPartida(jogador, maoDoJogador, false), amigo)));

        partidaService.confirmarPronto(partida.getId());

        assertThat(partida.getJogador().isPronto()).isTrue();
        assertThat(partida.getRodada()).isZero();
        assertThat(partidaService.emPreparacao(partida)).isTrue();
    }

    @Test
    void okRepetidoDepoisDaPreparacaoNaoPulaRodada() {
        Partida partida = partidaService.criarComMaquina(2, TipoDeVitoria.CLASSICA, null);

        partidaService.confirmarPronto(partida.getId());
        partidaService.confirmarPronto(partida.getId());

        assertThat(partida.getRodada()).isEqualTo(1);
    }

    @Test
    void reordenarPoeAsCartasNaOrdemNova() {
        Partida partida = partidaService.criarComMaquina(2, TipoDeVitoria.CLASSICA, null);

        partidaService.reordenarMaoDoJogador(partida.getId(), List.of(2, 0, 1));

        assertThat(partida.getJogador().getMao()).containsExactly(carta("c"), carta("a"), carta("b"));
        assertThat(partida.getOponentes().getFirst().getMao()).isEqualTo(maoDoBot1);
    }

    @Test
    void reordenarTambemValeDepoisDaPreparacao() {
        Partida partida = partidaService.criarComMaquina(2, TipoDeVitoria.CLASSICA, null);
        partidaService.confirmarPronto(partida.getId());

        partidaService.reordenarMaoDoJogador(partida.getId(), List.of(1, 2, 0));

        assertThat(partida.getJogador().getMao()).containsExactly(carta("b"), carta("c"), carta("a"));
    }

    static Stream<List<Integer>> ordensInvalidas() {
        return Stream.of(List.of(0, 1), List.of(0, 1, 2, 3), List.of(0, 0, 1), List.of(0, 1, 3), List.of(-1, 0, 1));
    }

    @ParameterizedTest
    @MethodSource("ordensInvalidas")
    void ordemQueNaoTrazCadaCartaUmaVezEInvalidaENaoMexeNaMao(List<Integer> ordem) {
        Partida partida = partidaService.criarComMaquina(2, TipoDeVitoria.CLASSICA, null);

        assertThatThrownBy(() -> partidaService.reordenarMaoDoJogador(partida.getId(), ordem))
                .isInstanceOf(OrdemDaMaoInvalidaException.class);
        assertThat(partida.getJogador().getMao()).isEqualTo(maoDoJogador);
    }

    @Test
    void naPreparacaoEHoraDeVenderOuroEDepoisDelaNao() {
        Partida partida = partidaService.criarComMaquina(2, TipoDeVitoria.CLASSICA, null);
        assertThat(partidaService.horaDeVenderOuro(partida)).isTrue();

        partidaService.confirmarPronto(partida.getId());

        // ainda não existe turno: fora da preparação não há "vez do jogador"
        assertThat(partidaService.horaDeVenderOuro(partida)).isFalse();
    }

    @Test
    void naPreparacaoVendeOuroDoJogadorComONivelDaVitoriaDaPartida() {
        Partida partida = partidaService.criarComMaquina(2, TipoDeVitoria.CLASSICA, null);

        partidaService.venderOuroPorNivel(partida.getId());

        verify(munchkinService).venderOuroPorNivel(jogador, 10);
    }

    @Test
    void foraDaHoraNaoVendeOuro() {
        Partida partida = partidaService.criarComMaquina(2, TipoDeVitoria.CLASSICA, null);
        partidaService.confirmarPronto(partida.getId());

        assertThatThrownBy(() -> partidaService.venderOuroPorNivel(partida.getId()))
                .isInstanceOf(ForaDaHoraDeVenderOuroException.class);
        verify(munchkinService, never()).venderOuroPorNivel(any(), anyInt());
    }

    @Test
    void botaoDeVenderSoComAHoraCertaEComOMunchkinServiceDeixando() {
        Partida partida = partidaService.criarComMaquina(2, TipoDeVitoria.CLASSICA, null);
        when(munchkinService.podeVenderOuroPorNivel(jogador, 10)).thenReturn(true);
        assertThat(partidaService.jogadorPodeVenderOuro(partida)).isTrue();

        when(munchkinService.podeVenderOuroPorNivel(jogador, 10)).thenReturn(false);
        assertThat(partidaService.jogadorPodeVenderOuro(partida)).isFalse();

        when(munchkinService.podeVenderOuroPorNivel(jogador, 10)).thenReturn(true);
        partidaService.confirmarPronto(partida.getId());
        assertThat(partidaService.jogadorPodeVenderOuro(partida)).isFalse();
    }

    @Test
    void partidaQueNaoExisteNaoEEncontrada() {
        UUID id = UUID.randomUUID();

        assertThatThrownBy(() -> partidaService.buscar(id)).isInstanceOf(PartidaNaoEncontradaException.class);
        assertThatThrownBy(() -> partidaService.confirmarPronto(id)).isInstanceOf(PartidaNaoEncontradaException.class);
        assertThatThrownBy(() -> partidaService.reordenarMaoDoJogador(id, List.of(0)))
                .isInstanceOf(PartidaNaoEncontradaException.class);
    }
    private static Carta carta(String nome) {
        return new Carta(TipoDeCarta.TESOURO, "tesouro/" + nome + "_tesouro.jpg");
    }
}
