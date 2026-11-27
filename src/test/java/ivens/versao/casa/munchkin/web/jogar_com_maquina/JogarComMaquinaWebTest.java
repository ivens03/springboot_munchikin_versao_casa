package ivens.versao.casa.munchkin.web.jogar_com_maquina;

import ivens.versao.casa.munchkin.carta.dto.CartaDto;
import ivens.versao.casa.munchkin.carta.dto.CartaDtoMapper;
import ivens.versao.casa.munchkin.carta.dto.CartaFechadaDto;
import ivens.versao.casa.munchkin.carta.dto.MaoFechadaDto;
import ivens.versao.casa.munchkin.carta.dto.OpcaoDto;
import ivens.versao.casa.munchkin.carta.dto.UsoDeCartaDto;
import ivens.versao.casa.munchkin.carta.dto.UsoDeCartaDtoMapper;
import ivens.versao.casa.munchkin.carta.exception.UsoDeCartaNaoPermitidoException;
import ivens.versao.casa.munchkin.carta.model.ImpedimentoDeUso;
import ivens.versao.casa.munchkin.carta.model.OpcaoDeNivel;
import ivens.versao.casa.munchkin.carta.service.nivel.UsoDeCarta;
import ivens.versao.casa.munchkin.carta.model.Carta;
import ivens.versao.casa.munchkin.carta.model.TipoDeCarta;
import ivens.versao.casa.munchkin.jogo.dto.VitoriaDto;
import ivens.versao.casa.munchkin.jogo.dto.VitoriaDtoMapper;
import ivens.versao.casa.munchkin.jogo.exception.NivelDeVitoriaInvalidoException;
import ivens.versao.casa.munchkin.jogo.model.TipoDeVitoria;
import ivens.versao.casa.munchkin.jogo.model.Vitoria;
import ivens.versao.casa.munchkin.munchkin.dto.MunchkinDto;
import ivens.versao.casa.munchkin.munchkin.dto.MunchkinDtoMapper;
import ivens.versao.casa.munchkin.munchkin.dto.OponenteDto;
import ivens.versao.casa.munchkin.munchkin.exception.VendaDeOuroNaoPermitidaException;
import ivens.versao.casa.munchkin.munchkin.model.Munchkin;
import ivens.versao.casa.munchkin.munchkin.service.MunchkinService;
import ivens.versao.casa.munchkin.partida.dto.DescarteDto;
import ivens.versao.casa.munchkin.partida.dto.DescarteDtoMapper;
import ivens.versao.casa.munchkin.partida.dto.JogadaDeCartaDtoMapper;
import ivens.versao.casa.munchkin.partida.dto.JogadaNaMesaDto;
import ivens.versao.casa.munchkin.partida.dto.JogadaNaMesaDto.EfeitoNaMesaDto;
import ivens.versao.casa.munchkin.partida.dto.ParticipanteDto;
import ivens.versao.casa.munchkin.partida.dto.ParticipanteDto.SeloDto;
import ivens.versao.casa.munchkin.partida.dto.JogadaDto;
import ivens.versao.casa.munchkin.partida.dto.RodadaDto;
import ivens.versao.casa.munchkin.partida.dto.RodadaDtoMapper;
import ivens.versao.casa.munchkin.partida.exception.AlvoInvalidoException;
import ivens.versao.casa.munchkin.partida.exception.CartaForaDaMaoException;
import ivens.versao.casa.munchkin.partida.exception.ForaDaHoraDeVenderOuroException;
import ivens.versao.casa.munchkin.partida.exception.OrdemDaMaoInvalidaException;
import ivens.versao.casa.munchkin.partida.exception.PartidaNaoEncontradaException;
import ivens.versao.casa.munchkin.partida.model.JogadorDaPartida;
import ivens.versao.casa.munchkin.partida.model.Partida;
import ivens.versao.casa.munchkin.partida.service.PartidaService;
import ivens.versao.casa.munchkin.partida.service.JogadaDoBot;
import ivens.versao.casa.munchkin.partida.model.EfeitoDaJogada.MudancaDeNivel;
import ivens.versao.casa.munchkin.partida.model.JogadaDeCarta;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(JogarComMaquinaWeb.class)
class JogarComMaquinaWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PartidaService partidaService;

    @MockitoBean
    private MunchkinService munchkinService;

    @MockitoBean
    private MunchkinDtoMapper munchkinDtoMapper;

    @MockitoBean
    private CartaDtoMapper cartaDtoMapper;

    @MockitoBean
    private VitoriaDtoMapper vitoriaDtoMapper;

    @MockitoBean
    private RodadaDtoMapper rodadaDtoMapper;

    @MockitoBean
    private UsoDeCartaDtoMapper usoDeCartaDtoMapper;

    @MockitoBean
    private JogadaDeCartaDtoMapper jogadaDeCartaDtoMapper;

    @MockitoBean
    private DescarteDtoMapper descarteDtoMapper;

    private final Vitoria classica = new Vitoria(TipoDeVitoria.CLASSICA, 10);
    private final Munchkin jogador = new Munchkin("Jogador");
    private final Munchkin bot = new Munchkin("Bot 1");
    private final List<Carta> maoDoJogador = List.of(new Carta(TipoDeCarta.TESOURO, "tesouro/1_itens/espada_tesouro.jpg"));
    private final List<Carta> maoDoBot = List.of(new Carta(TipoDeCarta.MASMORRA, "masmorra/5_racas/elfo_masmorra.jpg"));
    private final Partida partida = new Partida(UUID.fromString("7f000000-0000-0000-0000-000000000001"), classica,
            List.of(new JogadorDaPartida(jogador, maoDoJogador, false), new JogadorDaPartida(bot, maoDoBot, true)));
    private final String urlDaMesa = "/jogar-com-maquina/" + partida.getId();

    @Test
    void novaPartidaCriaAMesaEVaiParaElaComADistribuicao() throws Exception {
        when(partidaService.criarComMaquina(1, TipoDeVitoria.CLASSICA, null)).thenReturn(partida);

        mockMvc.perform(get("/jogar-com-maquina").param("bots", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(urlDaMesa))
                .andExpect(flash().attribute("distribuindo", true));
    }

    @Test
    void vitoriaPersonalizadaVaiComONivelEscolhido() throws Exception {
        when(partidaService.criarComMaquina(1, TipoDeVitoria.PERSONALIZADA, 37)).thenReturn(partida);

        mockMvc.perform(get("/jogar-com-maquina").param("bots", "1")
                        .param("vitoria", "PERSONALIZADA").param("nivel", "37"))
                .andExpect(redirectedUrl(urlDaMesa));
    }

    @Test
    void nivelDeVitoriaInvalidoEPedidoInvalido() throws Exception {
        when(partidaService.criarComMaquina(1, TipoDeVitoria.PERSONALIZADA, 101))
                .thenThrow(new NivelDeVitoriaInvalidoException(101));

        mockMvc.perform(get("/jogar-com-maquina").param("bots", "1")
                        .param("vitoria", "PERSONALIZADA").param("nivel", "101"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void tipoDeVitoriaDesconhecidoEPedidoInvalido() throws Exception {
        mockMvc.perform(get("/jogar-com-maquina").param("bots", "1").param("vitoria", "RAPIDA"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(partidaService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "11", "-1", "dois"})
    void quantidadeDeBotsForaDoLimiteEPedidoInvalido(String bots) throws Exception {
        mockMvc.perform(get("/jogar-com-maquina").param("bots", bots))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(partidaService);
    }

    @Test
    void semQuantidadeDeBotsEPedidoInvalido() throws Exception {
        mockMvc.perform(get("/jogar-com-maquina"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(partidaService);
    }

    @Test
    void mesaMostraARodadaOJogadorASuaMaoEOsBotsComAMaoFechada() throws Exception {
        MunchkinDto jogadorDto = munchkinDto("Jogador");
        MunchkinDto botDto = munchkinDto("Bot 1");
        List<CartaDto> maoDoJogadorDto = List.of(new CartaDto("tesouro", "Espada",
                "/img/cartas/tesouro/1_itens/espada_tesouro.jpg", "/img/cartas/traseira/verso_tesouro.jpg"));
        MaoFechadaDto maoDoBotDto = new MaoFechadaDto(
                List.of(new CartaFechadaDto("masmorra", "/img/cartas/traseira/verso_masmorra.jpg")), 1, 0);
        RodadaDto rodadaDto = new RodadaDto(0, true, false);
        VitoriaDto classicaDto = new VitoriaDto("Clássica", 10);

        when(partidaService.buscar(partida.getId())).thenReturn(partida);
        when(partidaService.emPreparacao(partida)).thenReturn(true);
        when(rodadaDtoMapper.paraDto(partida, true)).thenReturn(rodadaDto);
        when(vitoriaDtoMapper.paraDto(classica)).thenReturn(classicaDto);
        when(munchkinService.poderDeCombate(jogador)).thenReturn(0);
        when(munchkinService.poderDeCombate(bot)).thenReturn(0);
        when(munchkinDtoMapper.paraDto(jogador, 0)).thenReturn(jogadorDto);
        when(munchkinDtoMapper.paraDto(bot, 0)).thenReturn(botDto);
        when(cartaDtoMapper.paraDto(maoDoJogador)).thenReturn(maoDoJogadorDto);
        when(cartaDtoMapper.paraMaoFechada(maoDoBot)).thenReturn(maoDoBotDto);
        List<UsoDeCarta> usos = List.of(new UsoDeCarta(List.of(OpcaoDeNivel.SUBIR_1_NIVEL), false, null));
        List<UsoDeCartaDto> usosDto = List.of(new UsoDeCartaDto(List.of("SUBIR_1_NIVEL"), false, null));
        List<OpcaoDto> opcoesDto = List.of(new OpcaoDto("SUBIR_1_NIVEL", "Subir 1 nível"));
        when(partidaService.usosDaMaoDoJogador(partida)).thenReturn(usos);
        when(usoDeCartaDtoMapper.paraDto(usos)).thenReturn(usosDto);
        when(usoDeCartaDtoMapper.todasAsOpcoes()).thenReturn(opcoesDto);

        mockMvc.perform(get(urlDaMesa))
                .andExpect(status().isOk())
                .andExpect(view().name("jogar_com_maquina/jogar_com_maquina"))
                .andExpect(model().attribute("partidaId", partida.getId()))
                .andExpect(model().attribute("rodada", rodadaDto))
                .andExpect(model().attribute("vitoria", classicaDto))
                .andExpect(model().attribute("jogador", jogadorDto))
                .andExpect(model().attribute("podeVenderOuro", false))
                .andExpect(model().attribute("mao", maoDoJogadorDto))
                .andExpect(model().attribute("usos", usosDto))
                .andExpect(model().attribute("opcoesDeUso", opcoesDto))
                .andExpect(model().attribute("oponentes", List.of(new OponenteDto(botDto, maoDoBotDto))))
                .andExpect(model().attributeDoesNotExist("distribuindo"))
                .andExpect(content().string(containsString(
                        "data-bots-jogam=\"/jogar-com-maquina/" + partida.getId() + "/bots/jogar\"")));
    }

    @Test
    void depoisDaPreparacaoOsBotsNaoJogam() throws Exception {
        mesaDepoisDaPreparacao();

        mockMvc.perform(get(urlDaMesa))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("data-bots-jogam"))));
    }

    @Test
    void botQueJogaDeixaAJogadaParaAMesaEDizQuemFoi() throws Exception {
        JogadaDeCarta resultado = new JogadaDeCarta(0, partida.getOponentes().getFirst(), maoDoBot.getFirst(), null,
                List.of(new MudancaDeNivel(partida.getOponentes().getFirst(), 0, 1)));
        when(partidaService.jogadaDeBot(partida.getId())).thenReturn(Optional.of(new JogadaDoBot(0, resultado)));
        when(partidaService.buscar(partida.getId())).thenReturn(partida);
        when(jogadaDeCartaDtoMapper.naMesa(resultado, partida.getOponentes())).thenReturn(jogadaDoBot1());

        mockMvc.perform(post(urlDaMesa + "/bots/jogar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.oponente").value(0))
                .andExpect(flash().attribute("jogada", jogadaDoBot1()));
    }

    @Test
    void semJogadaDeBotNaoTemConteudo() throws Exception {
        when(partidaService.jogadaDeBot(partida.getId())).thenReturn(Optional.empty());

        mockMvc.perform(post(urlDaMesa + "/bots/jogar"))
                .andExpect(status().isNoContent())
                .andExpect(flash().attributeCount(0));
    }

    @Test
    void depoisDaPreparacaoAMesaNaoMostraRodadaNemAMensagemDeInicio() throws Exception {
        mesaDepoisDaPreparacao();

        mockMvc.perform(get(urlDaMesa))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("class=\"rodada"))));
    }

    @Test
    void logoDepoisDoOkQueFechaAPreparacaoAMesaDesejaBoaSorte() throws Exception {
        mesaDepoisDaPreparacao();

        mockMvc.perform(get(urlDaMesa).flashAttr("comecou", true))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("rodada--comecou")))
                .andExpect(content().string(containsString("Que vença o melhor Munchkin!")));
    }

    @Test
    void comOuroParaVenderAsInfosDoJogadorTemOBotaoDeVender() throws Exception {
        mesaDepoisDaPreparacao();
        when(partidaService.jogadorPodeVenderOuro(partida)).thenReturn(true);

        mockMvc.perform(get(urlDaMesa))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("action=\"" + urlDaMesa + "/vender-ouro\"")))
                .andExpect(content().string(containsString("Vender 1000 de ouro (+1 nível)")));
    }

    @Test
    void semOuroParaVenderNaoTemOBotao() throws Exception {
        mesaDepoisDaPreparacao();

        mockMvc.perform(get(urlDaMesa))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("vender-ouro"))));
    }

    @Test
    void venderOuroVoltaParaAMesa() throws Exception {
        mockMvc.perform(post(urlDaMesa + "/vender-ouro"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(urlDaMesa));

        verify(partidaService).venderOuroPorNivel(partida.getId());
    }

    @Test
    void vendaDeOuroNaoPermitidaEPedidoInvalido() throws Exception {
        when(partidaService.venderOuroPorNivel(partida.getId()))
                .thenThrow(new VendaDeOuroNaoPermitidaException("Jogador", 999, 0, 10));

        mockMvc.perform(post(urlDaMesa + "/vender-ouro"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void venderOuroForaDaHoraEPedidoInvalido() throws Exception {
        when(partidaService.venderOuroPorNivel(partida.getId())).thenThrow(new ForaDaHoraDeVenderOuroException(1));

        mockMvc.perform(post(urlDaMesa + "/vender-ouro"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cartaDaMaoLevaComoPodeSerUsadaEAJanelaTemOsBotoesEOsOponentes() throws Exception {
        mesaDepoisDaPreparacao();
        when(cartaDtoMapper.paraDto(maoDoJogador)).thenReturn(List.of(new CartaDto("tesouro", "Roubar 1 nivel",
                "/img/cartas/tesouro/3_suba_de_nivel/roubar_1_nivel_tesouro.jpg", "/img/cartas/traseira/verso_tesouro.jpg")));
        when(usoDeCartaDtoMapper.paraDto(List.of()))
                .thenReturn(List.of(new UsoDeCartaDto(List.of("ROUBAR_1_NIVEL"), true, null)));
        when(usoDeCartaDtoMapper.todasAsOpcoes()).thenReturn(List.of(new OpcaoDto("ROUBAR_1_NIVEL", "Roubar 1 nível")));

        mockMvc.perform(get(urlDaMesa))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-opcoes=\"ROUBAR_1_NIVEL\"")))
                .andExpect(content().string(containsString("data-alvo=\"true\"")))
                .andExpect(content().string(not(containsString("data-impedimento"))))
                .andExpect(content().string(containsString("action=\"" + urlDaMesa + "/usar-carta\"")))
                .andExpect(content().string(containsString("value=\"ROUBAR_1_NIVEL\"")))
                .andExpect(content().string(containsString("Bot 1 (nível 0)")));
    }

    @Test
    void cartaImpedidaLevaOMotivo() throws Exception {
        mesaDepoisDaPreparacao();
        when(cartaDtoMapper.paraDto(maoDoJogador)).thenReturn(List.of(new CartaDto("tesouro", "Espada",
                "/img/cartas/tesouro/1_itens/espada_tesouro.jpg", "/img/cartas/traseira/verso_tesouro.jpg")));
        when(usoDeCartaDtoMapper.paraDto(List.of()))
                .thenReturn(List.of(new UsoDeCartaDto(List.of(), false, "Esta carta ainda não pode ser usada no jogo.")));

        mockMvc.perform(get(urlDaMesa))
                .andExpect(status().isOk())
                // sem opção o Thymeleaf nem escreve o data-opcoes (o usar_carta.js trata como vazio)
                .andExpect(content().string(not(containsString("data-opcoes"))))
                .andExpect(content().string(containsString(
                        "data-impedimento=\"Esta carta ainda não pode ser usada no jogo.\"")));
    }

    @Test
    void usarCartaVoltaParaAMesaComAJogada() throws Exception {
        JogadaDeCarta resultado = new JogadaDeCarta(0, partida.getJogador(), maoDoJogador.getFirst(),
                partida.getOponentes().getFirst(), List.of());
        JogadaNaMesaDto naMesa = new JogadaNaMesaDto(null, "tesouro", "Roubar 1 nivel", "/img/x.jpg",
                "Você usou Roubar 1 nivel", List.of());
        when(partidaService.usarCartaDeNivel(partida.getId(), 0, OpcaoDeNivel.ROUBAR_1_NIVEL, 0)).thenReturn(resultado);
        when(partidaService.buscar(partida.getId())).thenReturn(partida);
        when(jogadaDeCartaDtoMapper.naMesa(resultado, partida.getOponentes())).thenReturn(naMesa);

        mockMvc.perform(post(urlDaMesa + "/usar-carta").param("posicao", "0").param("opcao", "ROUBAR_1_NIVEL")
                        .param("alvo", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(urlDaMesa))
                .andExpect(flash().attribute("jogada", naMesa));
    }

    @Test
    void usarCartaSemAlvoNaoMandaAlvo() throws Exception {
        when(partidaService.buscar(partida.getId())).thenReturn(partida);

        mockMvc.perform(post(urlDaMesa + "/usar-carta").param("posicao", "2").param("opcao", "SUBIR_1_NIVEL"))
                .andExpect(status().is3xxRedirection());

        verify(partidaService).usarCartaDeNivel(partida.getId(), 2, OpcaoDeNivel.SUBIR_1_NIVEL, null);
    }

    @Test
    void cartaJogadaApareceNaMesaComQuemJogouOEfeitoEODescarte() throws Exception {
        mesaDepoisDaPreparacao();

        mockMvc.perform(get(urlDaMesa).flashAttr("jogada", jogadaDoBot1()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "class=\"jogada\" role=\"status\" data-oponente=\"0\" data-pilha=\"tesouro\"")))
                .andExpect(content().string(containsString("Bot 1 usou Pagar a pizza: subiu do nível 0 para o 1.")))
                .andExpect(content().string(comEspacos(
                        "class=\"jogada__efeito jogada__efeito--sobe\" data-oponente=\"0\">Nível 0 → 1</li>")));
    }

    @Test
    void cartaDoJogadorDaTelaNaoTemOponente() throws Exception {
        mesaDepoisDaPreparacao();

        mockMvc.perform(get(urlDaMesa).flashAttr("jogada", new JogadaNaMesaDto(null, "tesouro", "Pagar a pizza",
                        "/img/x.jpg", "Você usou Pagar a pizza: subiu do nível 0 para o 1.",
                        List.of(new EfeitoNaMesaDto(null, "sobe", "Nível 0 → 1")))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("class=\"jogada\" role=\"status\" data-pilha=\"tesouro\"")))
                .andExpect(content().string(comEspacos("class=\"jogada__efeito jogada__efeito--sobe\">Nível 0 → 1</li>")));
    }

    @Test
    void semJogadaNaoTemCartaNoCentro() throws Exception {
        mesaDepoisDaPreparacao();

        mockMvc.perform(get(urlDaMesa))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("class=\"jogada\""))));
    }

    @Test
    void descartesMostramOTopoEAJanelaComTodasMasVazioNaoAbre() throws Exception {
        mesaDepoisDaPreparacao();
        CartaDto pizza = new CartaDto("tesouro", "Pagar a pizza", "/img/cartas/tesouro/pizza.jpg", "/img/v.jpg");
        CartaDto espada = new CartaDto("tesouro", "Espada", "/img/cartas/tesouro/espada.jpg", "/img/v.jpg");
        when(descarteDtoMapper.paraDto(partida)).thenReturn(List.of(new DescarteDto("dungeon", "Dungeon", List.of()),
                new DescarteDto("tesouro", "Tesouro", List.of(pizza, espada))));

        mockMvc.perform(get(urlDaMesa))
                .andExpect(status().isOk())
                .andExpect(model().attribute("descartes", hasSize(2)))
                .andExpect(content().string(containsString("aria-label=\"Descarte de Dungeon: 0 cartas\" disabled")))
                .andExpect(content().string(comEspacos("<img class=\"descarte__topo\" src=\"/img/cartas/tesouro/pizza.jpg\"")))
                .andExpect(content().string(comEspacos(
                        "<img class=\"descarte__anterior\" src=\"/img/cartas/tesouro/espada.jpg\"")))
                .andExpect(content().string(containsString("Descarte de Tesouro · 2 cartas")))
                .andExpect(content().string(not(containsString("Descarte de Dungeon ·"))));
    }

    @Test
    void historicoApareceNaMesaComAUltimaPrimeiro() throws Exception {
        mesaDepoisDaPreparacao();
        List<JogadaDto> historico = List.of(
                new JogadaDto(1, "Bot 1", "Bot 1 usou Roubar 1 nivel em você: subiu do nível 1 para o 2 e você desceu do nível 0 para o -1.",
                        "Roubar 1 nivel", "/img/cartas/tesouro/3_suba_de_nivel/roubar_1_nivel_tesouro.jpg",
                        new ParticipanteDto("Bot 1", "/img/cartas/masmorra/6_classes/mago_masmorra.jpg",
                                List.of(new SeloDto("sobe", "Nível 1 → 2"))),
                        List.of(new ParticipanteDto("Você", null, List.of(new SeloDto("desce", "Nível 0 → -1"))))),
                new JogadaDto(0, "Você", "Você usou Pagar a pizza: subiu do nível 0 para o 1.", "Pagar a pizza",
                        "/img/cartas/tesouro/3_suba_de_nivel/pagar_a_pizza_tesouro.jpg",
                        new ParticipanteDto("Você", null, List.of(new SeloDto("sobe", "Nível 0 → 1"))), List.of()));
        when(jogadaDeCartaDtoMapper.historico(partida.getHistorico())).thenReturn(historico);

        String pagina = mockMvc.perform(get(urlDaMesa))
                .andExpect(status().isOk())
                .andExpect(model().attribute("historico", historico))
                .andExpect(content().string(containsString("Histórico completo · 2 jogadas")))
                .andExpect(content().string(containsString("Bot 1 usou Roubar 1 nivel em você")))
                // em imagens: retrato de quem jogou, a carta, a seta e quem foi afetado, com os selos
                .andExpect(content().string(comEspacos(
                        "class=\"participante__retrato\" src=\"/img/cartas/masmorra/6_classes/mago_masmorra.jpg\"")))
                .andExpect(content().string(comEspacos("class=\"selo selo--desce\">Nível 0 → -1</li>")))
                .andExpect(content().string(containsString("class=\"lance__seta\"")))
                // sem retrato, a inicial
                .andExpect(content().string(comEspacos("class=\"participante__retrato participante__retrato--vazio\">V</span>")))
                .andExpect(content().string(not(containsString("Nenhuma carta jogada ainda."))))
                .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);

        // fileira: 2 quadrados com jogada (a última primeiro) e 8 vagas antes do "Tudo"
        assertThat(org.springframework.util.StringUtils.countOccurrencesOf(pagina, "class=\"historico__jogada\"")).isEqualTo(2);
        // a seta só na jogada com afetado: uma no quadrado e outra no histórico completo
        assertThat(org.springframework.util.StringUtils.countOccurrencesOf(pagina, "class=\"lance__seta\"")).isEqualTo(2);
        assertThat(org.springframework.util.StringUtils.countOccurrencesOf(pagina, "class=\"historico__vaga\"")).isEqualTo(8);
        assertThat(pagina.indexOf("Rodada 1: Bot 1 usou")).isLessThan(pagina.indexOf("Rodada 0: Você usou"));
    }

    @Test
    void historicoVazioDizQueNinguemJogou() throws Exception {
        mesaDepoisDaPreparacao();

        mockMvc.perform(get(urlDaMesa))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Histórico completo · 0 jogadas")))
                .andExpect(content().string(containsString("Nenhuma carta jogada ainda.")));
    }

    @Test
    void opcaoQueNaoExisteEPedidoInvalido() throws Exception {
        mockMvc.perform(post(urlDaMesa + "/usar-carta").param("posicao", "0").param("opcao", "VOAR"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(partidaService);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void usoDeCartaRecusadoEPedidoInvalido(int caso) throws Exception {
        RuntimeException recusa = switch (caso) {
            case 0 -> new UsoDeCartaNaoPermitidoException("pagar_a_pizza", ImpedimentoDeUso.NIVEL_DA_VITORIA);
            case 1 -> new CartaForaDaMaoException(9, 1);
            default -> new AlvoInvalidoException(null, 1);
        };
        when(partidaService.usarCartaDeNivel(partida.getId(), 0, OpcaoDeNivel.SUBIR_1_NIVEL, null)).thenThrow(recusa);

        mockMvc.perform(post(urlDaMesa + "/usar-carta").param("posicao", "0").param("opcao", "SUBIR_1_NIVEL"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void semJogoTesteCartasNaoTemPainelNemEndpointDeCartaDeTeste() throws Exception {
        mesaDepoisDaPreparacao();

        mockMvc.perform(get(urlDaMesa))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Pegar carta (teste)"))));
        mockMvc.perform(post(urlDaMesa + "/teste/pegar-carta").param("caminho", "tesouro/x.jpg"))
                .andExpect(status().isNotFound());
    }

    @Test
    void maoVaziaFicaMarcadaParaAsInformacoesFicaremAMostra() throws Exception {
        mesaDepoisDaPreparacao();

        mockMvc.perform(get(urlDaMesa))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("class=\"mao mao--vazia\"")));
    }

    @Test
    void maoComCartaNaoEVazia() throws Exception {
        mesaDepoisDaPreparacao();
        when(cartaDtoMapper.paraDto(maoDoJogador)).thenReturn(List.of(new CartaDto("tesouro", "Espada",
                "/img/cartas/tesouro/1_itens/espada_tesouro.jpg", "/img/cartas/traseira/verso_tesouro.jpg")));
        when(usoDeCartaDtoMapper.paraDto(List.of()))
                .thenReturn(List.of(new UsoDeCartaDto(List.of(), false, "Esta carta ainda não pode ser usada no jogo.")));

        mockMvc.perform(get(urlDaMesa))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("mao--vazia"))));
    }

    /** Partida na rodada 1, com os mappers devolvendo o mínimo para a página renderizar. */
    private void mesaDepoisDaPreparacao() {
        when(partidaService.buscar(partida.getId())).thenReturn(partida);
        when(partidaService.emPreparacao(partida)).thenReturn(false);
        when(rodadaDtoMapper.paraDto(partida, false)).thenReturn(new RodadaDto(1, false, true));
        when(vitoriaDtoMapper.paraDto(classica)).thenReturn(new VitoriaDto("Clássica", 10));
        when(munchkinDtoMapper.paraDto(jogador, 0)).thenReturn(munchkinDto("Jogador"));
        when(munchkinDtoMapper.paraDto(bot, 0)).thenReturn(munchkinDto("Bot 1"));
        when(cartaDtoMapper.paraDto(maoDoJogador)).thenReturn(List.of());
        when(cartaDtoMapper.paraMaoFechada(maoDoBot)).thenReturn(new MaoFechadaDto(List.of(), 0, 0));
    }

    @Test
    void mesaDePartidaQueNaoExisteNaoEEncontrada() throws Exception {
        UUID id = UUID.randomUUID();
        when(partidaService.buscar(id)).thenThrow(new PartidaNaoEncontradaException(id));

        mockMvc.perform(get("/jogar-com-maquina/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void idQueNaoEUuidEPedidoInvalido() throws Exception {
        mockMvc.perform(get("/jogar-com-maquina/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void okQueFechaAPreparacaoVoltaParaAMesaComAMensagemDeInicio() throws Exception {
        when(partidaService.confirmarPronto(partida.getId())).thenReturn(partida);
        when(partidaService.emPreparacao(partida)).thenReturn(false);

        mockMvc.perform(post(urlDaMesa + "/pronto"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(urlDaMesa))
                .andExpect(flash().attribute("comecou", true));
    }

    @Test
    void okComAlguemAindaSeOrganizandoVoltaParaAMesaSemAMensagem() throws Exception {
        when(partidaService.confirmarPronto(partida.getId())).thenReturn(partida);
        when(partidaService.emPreparacao(partida)).thenReturn(true);

        mockMvc.perform(post(urlDaMesa + "/pronto"))
                .andExpect(redirectedUrl(urlDaMesa))
                .andExpect(flash().attributeCount(0));
    }

    @Test
    void ordemNovaDaMaoEGuardadaSemDevolverPagina() throws Exception {
        mockMvc.perform(post(urlDaMesa + "/mao").param("ordem", "2", "0", "1"))
                .andExpect(status().isNoContent());

        verify(partidaService).reordenarMaoDoJogador(partida.getId(), List.of(2, 0, 1));
    }

    @Test
    void ordemInvalidaDaMaoEPedidoInvalido() throws Exception {
        doThrow(new OrdemDaMaoInvalidaException(List.of(0, 0), 2))
                .when(partidaService).reordenarMaoDoJogador(partida.getId(), List.of(0, 0));

        mockMvc.perform(post(urlDaMesa + "/mao").param("ordem", "0", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void semOrdemEPedidoInvalido() throws Exception {
        mockMvc.perform(post(urlDaMesa + "/mao"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(partidaService);
    }

    /** O HTML contém o trecho, com qualquer quebra de linha ou recuo entre os atributos (o template quebra linhas). */
    private static Matcher<String> comEspacos(String trecho) {
        String regex = Arrays.stream(trecho.split(" ")).map(Pattern::quote).collect(Collectors.joining("\\s+"));
        return matchesPattern("(?s).*" + regex + ".*");
    }

    private static JogadaNaMesaDto jogadaDoBot1() {
        return new JogadaNaMesaDto(0, "tesouro", "Pagar a pizza", "/img/cartas/tesouro/pizza.jpg",
                "Bot 1 usou Pagar a pizza: subiu do nível 0 para o 1.",
                List.of(new EfeitoNaMesaDto(0, "sobe", "Nível 0 → 1")));
    }

    private static MunchkinDto munchkinDto(String nome) {
        return new MunchkinDto(nome, "Humano", 0, 0, 400,
                "/img/moes/moes_humanas_esquerda.png", "/img/moes/moes_humanas_direita.png",
                "/img/moes/moes_costas_humano_esquerda.png", "/img/moes/moes_costas_humano_direita.png");
    }
}
