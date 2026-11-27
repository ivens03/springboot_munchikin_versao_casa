package ivens.versao.casa.munchkin.partida.service;

import ivens.versao.casa.munchkin.bot.service.BotService;
import ivens.versao.casa.munchkin.bot.service.JogadaDeNivel;
import ivens.versao.casa.munchkin.carta.model.Carta;
import ivens.versao.casa.munchkin.carta.model.TipoDeCarta;
import ivens.versao.casa.munchkin.carta.exception.UsoDeCartaNaoPermitidoException;
import ivens.versao.casa.munchkin.carta.model.ImpedimentoDeUso;
import ivens.versao.casa.munchkin.carta.model.OpcaoDeNivel;
import ivens.versao.casa.munchkin.carta.service.BaralhoService;
import ivens.versao.casa.munchkin.carta.service.nivel.EfeitoDeNivel;
import ivens.versao.casa.munchkin.carta.service.nivel.SituacaoDoJogador;
import ivens.versao.casa.munchkin.carta.service.nivel.SubaDeNivelService;
import ivens.versao.casa.munchkin.carta.service.nivel.UsoDeCarta;
import ivens.versao.casa.munchkin.jogo.exception.NivelDeVitoriaInvalidoException;
import ivens.versao.casa.munchkin.jogo.model.TipoDeVitoria;
import ivens.versao.casa.munchkin.jogo.model.Vitoria;
import ivens.versao.casa.munchkin.jogo.service.Embaralhador;
import ivens.versao.casa.munchkin.jogo.service.VitoriaService;
import ivens.versao.casa.munchkin.munchkin.model.Munchkin;
import ivens.versao.casa.munchkin.munchkin.service.MunchkinService;
import ivens.versao.casa.munchkin.partida.exception.AlvoInvalidoException;
import ivens.versao.casa.munchkin.partida.exception.CartaForaDaMaoException;
import ivens.versao.casa.munchkin.partida.exception.ForaDaHoraDeVenderOuroException;
import ivens.versao.casa.munchkin.partida.exception.OrdemDaMaoInvalidaException;
import ivens.versao.casa.munchkin.partida.exception.PartidaNaoEncontradaException;
import ivens.versao.casa.munchkin.partida.model.EfeitoDaJogada.MaoDescartada;
import ivens.versao.casa.munchkin.partida.model.EfeitoDaJogada.MudancaDeNivel;
import ivens.versao.casa.munchkin.partida.model.JogadaDeCarta;
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
    private final SubaDeNivelService subaDeNivelService = mock(SubaDeNivelService.class);
    private final BotService botService = mock(BotService.class);
    private final Embaralhador embaralhador = mock(Embaralhador.class);
    private final PartidaRepository partidaRepository = new PartidaRepository();
    private final PartidaService partidaService = new PartidaService(partidaRepository, munchkinService, baralhoService,
            vitoriaService, subaDeNivelService, botService, embaralhador);

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
        // mudar de nível de verdade, para o histórico guardar o antes e o depois
        doAnswer(chamada -> {
            Munchkin munchkin = chamada.getArgument(0);
            munchkin.setNivel(munchkin.getNivel() + chamada.<Integer>getArgument(1));
            return null;
        }).when(munchkinService).mudarNivel(any(), anyInt());
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
    void cadaJogadorGanhaUmRetratoSorteadoSemRepetir() {
        Carta elfo = new Carta(TipoDeCarta.MASMORRA, "masmorra/5_racas/elfo_masmorra.jpg");
        Carta mago = new Carta(TipoDeCarta.MASMORRA, "masmorra/6_classes/mago_masmorra.jpg");
        Carta orc = new Carta(TipoDeCarta.MASMORRA, "masmorra/5_racas/orc_masmorra.jpg");
        when(baralhoService.cartasDePersonagem()).thenReturn(List.of(elfo, mago, orc));
        when(embaralhador.embaralhar(List.of(elfo, mago, orc))).thenReturn(List.of(orc, elfo, mago));

        Partida partida = partidaService.criarComMaquina(2, TipoDeVitoria.CLASSICA, null);

        assertThat(partida.getJogadores()).extracting(JogadorDaPartida::getRetrato).containsExactly(orc, elfo, mago);
    }

    @Test
    void comMaisJogadoresQueRetratosAListaRecomecaESemRetratosNinguemTem() {
        Carta elfo = new Carta(TipoDeCarta.MASMORRA, "masmorra/5_racas/elfo_masmorra.jpg");
        when(baralhoService.cartasDePersonagem()).thenReturn(List.of(elfo));
        when(embaralhador.embaralhar(List.of(elfo))).thenReturn(List.of(elfo));

        assertThat(partidaService.criarComMaquina(2, TipoDeVitoria.CLASSICA, null).getJogadores())
                .extracting(JogadorDaPartida::getRetrato).containsOnly(elfo);

        when(baralhoService.cartasDePersonagem()).thenReturn(List.of());
        assertThat(partidaService.criarComMaquina(2, TipoDeVitoria.CLASSICA, null).getJogadores())
                .extracting(JogadorDaPartida::getRetrato).containsOnlyNulls();
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

    // ---------- cartas de nível ----------

    /** Jogador no nível 0, bots nos níveis 2 e -1, vitória no 10. */
    private final SituacaoDoJogador situacao = new SituacaoDoJogador(0, List.of(2, -1), 10);

    private Partida partidaComNiveis() {
        bot1.setNivel(2);
        bot2.setNivel(-1);
        return partidaService.criarComMaquina(2, TipoDeVitoria.CLASSICA, null);
    }

    @Test
    void usosDaMaoSaoDeCadaCartaNaOrdemComONivelDeTodos() {
        Partida partida = partidaComNiveis();
        UsoDeCarta liberada = new UsoDeCarta(List.of(OpcaoDeNivel.SUBIR_1_NIVEL), false, null);
        UsoDeCarta semUso = UsoDeCarta.impedida(ImpedimentoDeUso.CARTA_SEM_USO);
        when(subaDeNivelService.uso(carta("a"), situacao)).thenReturn(liberada);
        when(subaDeNivelService.uso(carta("b"), situacao)).thenReturn(semUso);
        when(subaDeNivelService.uso(carta("c"), situacao)).thenReturn(liberada);

        assertThat(partidaService.usosDaMaoDoJogador(partida)).containsExactly(liberada, semUso, liberada);
    }

    @Test
    void usarCartaSobeONivelEACartaVaiParaODescarte() {
        Partida partida = partidaComNiveis();
        when(subaDeNivelService.usar(carta("b"), OpcaoDeNivel.SUBIR_1_NIVEL, situacao)).thenReturn(EfeitoDeNivel.SUBIR_1);

        JogadaDeCarta jogada = partidaService.usarCartaDeNivel(partida.getId(), 1, OpcaoDeNivel.SUBIR_1_NIVEL, null);

        verify(munchkinService).mudarNivel(jogador, 1);
        assertThat(partida.getJogador().getMao()).containsExactly(carta("a"), carta("c"));
        assertThat(partida.getDescarte(TipoDeCarta.TESOURO)).containsExactly(carta("b"));
        assertThat(jogada).isEqualTo(new JogadaDeCarta(0, partida.getJogador(), carta("b"), null,
                List.of(new MudancaDeNivel(partida.getJogador(), 0, 1))));
        assertThat(partida.getHistorico()).containsExactly(jogada);
    }

    @Test
    void descartarAMaoLevaAsOutrasCartasParaODescarte() {
        Partida partida = partidaComNiveis();
        when(subaDeNivelService.usar(carta("a"), OpcaoDeNivel.DESCARTAR_A_MAO_E_SUBIR_2_NIVEIS, situacao))
                .thenReturn(new EfeitoDeNivel(2, 0, true));

        JogadaDeCarta jogada =
                partidaService.usarCartaDeNivel(partida.getId(), 0, OpcaoDeNivel.DESCARTAR_A_MAO_E_SUBIR_2_NIVEIS, null);

        verify(munchkinService).mudarNivel(jogador, 2);
        assertThat(jogada.efeitos()).containsExactly(new MaoDescartada(partida.getJogador(), 2),
                new MudancaDeNivel(partida.getJogador(), 0, 2));
        assertThat(partida.getJogador().getMao()).isEmpty();
        // a carta jogada fica no topo, depois da mão
        assertThat(partida.getDescarte(TipoDeCarta.TESOURO)).containsExactly(carta("b"), carta("c"), carta("a"));
    }

    @Test
    void roubarTiraUmNivelDoOponenteEscolhido() {
        Partida partida = partidaComNiveis();
        when(subaDeNivelService.precisaDeAlvo(carta("c"))).thenReturn(true);
        when(subaDeNivelService.usar(carta("c"), OpcaoDeNivel.ROUBAR_1_NIVEL, situacao)).thenReturn(new EfeitoDeNivel(1, 1, false));

        JogadaDeCarta jogada = partidaService.usarCartaDeNivel(partida.getId(), 2, OpcaoDeNivel.ROUBAR_1_NIVEL, 1);

        verify(munchkinService).mudarNivel(jogador, 1);
        verify(munchkinService).mudarNivel(bot2, -1);
        JogadorDaPartida alvo = partida.getOponentes().get(1);
        assertThat(jogada.alvo()).isSameAs(alvo);
        assertThat(jogada.efeitos()).containsExactly(new MudancaDeNivel(partida.getJogador(), 0, 1),
                new MudancaDeNivel(alvo, -1, -2));
    }

    @Test
    void historicoGuardaAsJogadasNaOrdemComARodada() {
        Partida partida = partidaComNiveis();
        when(subaDeNivelService.usar(any(), any(), any())).thenReturn(EfeitoDeNivel.SUBIR_1);

        partidaService.usarCartaDeNivel(partida.getId(), 0, OpcaoDeNivel.SUBIR_1_NIVEL, null);
        partidaService.confirmarPronto(partida.getId());
        partidaService.usarCartaDeNivel(partida.getId(), 0, OpcaoDeNivel.SUBIR_1_NIVEL, null);

        assertThat(partida.getHistorico()).extracting(JogadaDeCarta::rodada, JogadaDeCarta::carta)
                .containsExactly(org.assertj.core.groups.Tuple.tuple(0, carta("a")),
                        org.assertj.core.groups.Tuple.tuple(1, carta("b")));
    }

    @Test
    void cartaRecusadaNaoEntraNoHistorico() {
        Partida partida = partidaComNiveis();
        when(subaDeNivelService.usar(carta("a"), OpcaoDeNivel.SUBIR_1_NIVEL, situacao))
                .thenThrow(new UsoDeCartaNaoPermitidoException("a", ImpedimentoDeUso.NIVEL_DA_VITORIA));

        assertThatThrownBy(() -> partidaService.usarCartaDeNivel(partida.getId(), 0, OpcaoDeNivel.SUBIR_1_NIVEL, null))
                .isInstanceOf(UsoDeCartaNaoPermitidoException.class);
        assertThat(partida.getHistorico()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("alvosInvalidos")
    void cartaComAlvoPrecisaDeUmOponente(Integer alvo) {
        Partida partida = partidaComNiveis();
        when(subaDeNivelService.precisaDeAlvo(carta("c"))).thenReturn(true);

        assertThatThrownBy(() -> partidaService.usarCartaDeNivel(partida.getId(), 2, OpcaoDeNivel.ROUBAR_1_NIVEL, alvo))
                .isInstanceOf(AlvoInvalidoException.class);
        assertThat(partida.getJogador().getMao()).hasSize(3);
        verify(munchkinService, never()).mudarNivel(any(), anyInt());
    }

    static Stream<Integer> alvosInvalidos() {
        return Stream.of(null, -1, 2);
    }

    @ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(ints = {-1, 3})
    void posicaoForaDaMaoNaoUsa(int posicao) {
        Partida partida = partidaComNiveis();

        assertThatThrownBy(() -> partidaService.usarCartaDeNivel(partida.getId(), posicao, OpcaoDeNivel.SUBIR_1_NIVEL, null))
                .isInstanceOf(CartaForaDaMaoException.class);
    }

    @Test
    void cartaImpedidaNaoSaiDaMaoNemMudaNivel() {
        Partida partida = partidaComNiveis();
        when(subaDeNivelService.usar(carta("a"), OpcaoDeNivel.SUBIR_1_NIVEL, situacao))
                .thenThrow(new UsoDeCartaNaoPermitidoException("a", ImpedimentoDeUso.NIVEL_DA_VITORIA));

        assertThatThrownBy(() -> partidaService.usarCartaDeNivel(partida.getId(), 0, OpcaoDeNivel.SUBIR_1_NIVEL, null))
                .isInstanceOf(UsoDeCartaNaoPermitidoException.class);
        assertThat(partida.getJogador().getMao()).hasSize(3);
        assertThat(partida.getDescarte(TipoDeCarta.TESOURO)).isEmpty();
        verify(munchkinService, never()).mudarNivel(any(), anyInt());
    }

    @Test
    void cartaDeNivelValeNaPreparacaoEDepoisDela() {
        Partida partida = partidaComNiveis();
        when(subaDeNivelService.usar(any(), any(), any())).thenReturn(EfeitoDeNivel.SUBIR_1);

        partidaService.usarCartaDeNivel(partida.getId(), 0, OpcaoDeNivel.SUBIR_1_NIVEL, null);
        partidaService.confirmarPronto(partida.getId());
        partidaService.usarCartaDeNivel(partida.getId(), 0, OpcaoDeNivel.SUBIR_1_NIVEL, null);

        verify(munchkinService, org.mockito.Mockito.times(2)).mudarNivel(jogador, 1);
    }

    @Test
    void cartaDeTesteVaiParaOFimDaMaoDoJogador() {
        Partida partida = partidaService.criarComMaquina(2, TipoDeVitoria.CLASSICA, null);
        Carta pizza = new Carta(TipoDeCarta.TESOURO, "tesouro/3_suba_de_nivel/pagar_a_pizza_tesouro.jpg");
        when(baralhoService.buscar(pizza.imagem())).thenReturn(pizza);

        assertThat(partidaService.darCartaDeTesteAoJogador(partida.getId(), pizza.imagem())).isEqualTo(pizza);
        assertThat(partida.getJogador().getMao()).containsExactly(carta("a"), carta("b"), carta("c"), pizza);
        assertThat(partida.getOponentes()).extracting(JogadorDaPartida::getMao).containsExactly(maoDoBot1, maoDoBot2);
    }

    @Test
    void partidaQueNaoExisteNaoEEncontrada() {
        UUID id = UUID.randomUUID();

        assertThatThrownBy(() -> partidaService.buscar(id)).isInstanceOf(PartidaNaoEncontradaException.class);
        assertThatThrownBy(() -> partidaService.confirmarPronto(id)).isInstanceOf(PartidaNaoEncontradaException.class);
        assertThatThrownBy(() -> partidaService.reordenarMaoDoJogador(id, List.of(0)))
                .isInstanceOf(PartidaNaoEncontradaException.class);
    }

    // ---------- bots usando cartas de nível ----------

    /** Bot 1 no nível 2 vê o jogador no 0 e o Bot 2 no -1. */
    private final SituacaoDoJogador situacaoDoBot1 = new SituacaoDoJogador(2, List.of(0, -1), 10);
    /** Bot 2 no nível -1 vê o jogador no 0 e o Bot 1 no 2. */
    private final SituacaoDoJogador situacaoDoBot2 = new SituacaoDoJogador(-1, List.of(0, 2), 10);

    /** O sorteio põe o Bot 2 antes do Bot 1. */
    private Partida partidaComOBot2SorteadoPrimeiro() {
        Partida partida = partidaComNiveis();
        when(embaralhador.<JogadorDaPartida>embaralhar(anyList()))
                .thenReturn(List.copyOf(partida.getOponentes()).reversed());
        return partida;
    }

    @Test
    void jogaOBotSorteadoQueTemCartaParaUsar() {
        Partida partida = partidaComOBot2SorteadoPrimeiro();
        when(botService.temCartaDeNivelParaUsar(maoDoBot2, situacaoDoBot2)).thenReturn(false);
        when(botService.temCartaDeNivelParaUsar(maoDoBot1, situacaoDoBot1)).thenReturn(true);
        when(botService.escolherCartaDeNivel(maoDoBot1, situacaoDoBot1))
                .thenReturn(Optional.of(new JogadaDeNivel(0, OpcaoDeNivel.SUBIR_1_NIVEL)));
        when(subaDeNivelService.usar(carta("d"), OpcaoDeNivel.SUBIR_1_NIVEL, situacaoDoBot1)).thenReturn(EfeitoDeNivel.SUBIR_1);

        JogadaDoBot jogada = partidaService.jogadaDeBot(partida.getId()).orElseThrow();

        verify(munchkinService).mudarNivel(bot1, 1);
        assertThat(jogada.oponente()).isZero();
        JogadorDaPartida bot = partida.getOponentes().getFirst();
        assertThat(jogada.jogada()).isEqualTo(new JogadaDeCarta(0, bot, carta("d"), null,
                List.of(new MudancaDeNivel(bot, 2, 3))));
        assertThat(partida.getHistorico()).containsExactly(jogada.jogada());
        assertThat(partida.getOponentes().getFirst().getMao()).isEmpty();
        assertThat(partida.getDescarte(TipoDeCarta.TESOURO)).containsExactly(carta("d"));
    }

    @Test
    void botRoubaDeQuemOBotServiceEscolheEntreTodosOsOutros() {
        Partida partida = partidaComOBot2SorteadoPrimeiro();
        when(botService.temCartaDeNivelParaUsar(maoDoBot2, situacaoDoBot2)).thenReturn(true);
        when(botService.escolherCartaDeNivel(maoDoBot2, situacaoDoBot2))
                .thenReturn(Optional.of(new JogadaDeNivel(0, OpcaoDeNivel.ROUBAR_1_NIVEL)));
        when(subaDeNivelService.precisaDeAlvo(carta("e"))).thenReturn(true);
        when(botService.escolherAlvo(List.of(0, 2))).thenReturn(0); // o jogador da tela
        when(subaDeNivelService.usar(carta("e"), OpcaoDeNivel.ROUBAR_1_NIVEL, situacaoDoBot2))
                .thenReturn(new EfeitoDeNivel(1, 1, false));

        JogadaDoBot jogada = partidaService.jogadaDeBot(partida.getId()).orElseThrow();

        verify(munchkinService).mudarNivel(bot2, 1);
        verify(munchkinService).mudarNivel(jogador, -1);
        assertThat(jogada.oponente()).isEqualTo(1);
        assertThat(jogada.jogada().alvo()).isSameAs(partida.getJogador());
    }

    @Test
    void botQueGuardaACartaNaoJoga() {
        Partida partida = partidaComOBot2SorteadoPrimeiro();
        when(botService.temCartaDeNivelParaUsar(maoDoBot2, situacaoDoBot2)).thenReturn(true);
        when(botService.escolherCartaDeNivel(maoDoBot2, situacaoDoBot2)).thenReturn(Optional.empty());

        assertThat(partidaService.jogadaDeBot(partida.getId())).isEmpty();
        assertThat(partida.getOponentes().get(1).getMao()).hasSize(1);
        verify(munchkinService, never()).mudarNivel(any(), anyInt());
    }

    @Test
    void semBotComCartaNinguemJoga() {
        Partida partida = partidaComOBot2SorteadoPrimeiro();

        assertThat(partidaService.jogadaDeBot(partida.getId())).isEmpty();
        verify(botService, never()).escolherCartaDeNivel(any(), any());
    }

    @Test
    void depoisDaPreparacaoOsBotsAindaNaoJogam() {
        Partida partida = partidaComOBot2SorteadoPrimeiro();
        partidaService.confirmarPronto(partida.getId());

        assertThat(partidaService.jogadaDeBot(partida.getId())).isEmpty();
        verifyNoInteractions(botService);
    }

    private static Carta carta(String nome) {
        return new Carta(TipoDeCarta.TESOURO, "tesouro/" + nome + "_tesouro.jpg");
    }
}
