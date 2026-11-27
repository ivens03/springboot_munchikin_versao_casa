package ivens.versao.casa.munchkin.web.jogar_com_maquina;

import ivens.versao.casa.munchkin.carta.dto.CartaDto;
import ivens.versao.casa.munchkin.carta.dto.CartaDtoMapper;
import ivens.versao.casa.munchkin.carta.dto.CartaFechadaDto;
import ivens.versao.casa.munchkin.carta.dto.MaoFechadaDto;
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
import ivens.versao.casa.munchkin.munchkin.model.Munchkin;
import ivens.versao.casa.munchkin.munchkin.service.MunchkinService;
import ivens.versao.casa.munchkin.partida.dto.RodadaDto;
import ivens.versao.casa.munchkin.partida.dto.RodadaDtoMapper;
import ivens.versao.casa.munchkin.partida.exception.OrdemDaMaoInvalidaException;
import ivens.versao.casa.munchkin.partida.exception.PartidaNaoEncontradaException;
import ivens.versao.casa.munchkin.partida.model.JogadorDaPartida;
import ivens.versao.casa.munchkin.partida.model.Partida;
import ivens.versao.casa.munchkin.partida.service.PartidaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
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

        mockMvc.perform(get(urlDaMesa))
                .andExpect(status().isOk())
                .andExpect(view().name("jogar_com_maquina/jogar_com_maquina"))
                .andExpect(model().attribute("partidaId", partida.getId()))
                .andExpect(model().attribute("rodada", rodadaDto))
                .andExpect(model().attribute("vitoria", classicaDto))
                .andExpect(model().attribute("jogador", jogadorDto))
                .andExpect(model().attribute("mao", maoDoJogadorDto))
                .andExpect(model().attribute("oponentes", List.of(new OponenteDto(botDto, maoDoBotDto))))
                .andExpect(model().attributeDoesNotExist("distribuindo"));
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

    private static MunchkinDto munchkinDto(String nome) {
        return new MunchkinDto(nome, "Humano", 0, 0,
                "/img/moes/moes_humanas_esquerda.png", "/img/moes/moes_humanas_direita.png",
                "/img/moes/moes_costas_humano_esquerda.png", "/img/moes/moes_costas_humano_direita.png");
    }
}
