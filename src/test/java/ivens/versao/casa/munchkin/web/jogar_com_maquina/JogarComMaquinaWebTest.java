package ivens.versao.casa.munchkin.web.jogar_com_maquina;

import ivens.versao.casa.munchkin.carta.dto.CartaDto;
import ivens.versao.casa.munchkin.carta.dto.CartaDtoMapper;
import ivens.versao.casa.munchkin.carta.dto.CartaFechadaDto;
import ivens.versao.casa.munchkin.carta.dto.MaoFechadaDto;
import ivens.versao.casa.munchkin.carta.model.Carta;
import ivens.versao.casa.munchkin.carta.model.TipoDeCarta;
import ivens.versao.casa.munchkin.carta.service.BaralhoService;
import ivens.versao.casa.munchkin.jogo.dto.VitoriaDto;
import ivens.versao.casa.munchkin.jogo.dto.VitoriaDtoMapper;
import ivens.versao.casa.munchkin.jogo.exception.NivelDeVitoriaInvalidoException;
import ivens.versao.casa.munchkin.jogo.model.TipoDeVitoria;
import ivens.versao.casa.munchkin.jogo.model.Vitoria;
import ivens.versao.casa.munchkin.jogo.service.VitoriaService;
import ivens.versao.casa.munchkin.munchkin.dto.MunchkinDto;
import ivens.versao.casa.munchkin.munchkin.dto.MunchkinDtoMapper;
import ivens.versao.casa.munchkin.munchkin.dto.OponenteDto;
import ivens.versao.casa.munchkin.munchkin.model.Munchkin;
import ivens.versao.casa.munchkin.munchkin.service.MunchkinService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(JogarComMaquinaWeb.class)
class JogarComMaquinaWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MunchkinService munchkinService;

    @MockitoBean
    private MunchkinDtoMapper munchkinDtoMapper;

    @MockitoBean
    private BaralhoService baralhoService;

    @MockitoBean
    private CartaDtoMapper cartaDtoMapper;

    @MockitoBean
    private VitoriaService vitoriaService;

    @MockitoBean
    private VitoriaDtoMapper vitoriaDtoMapper;

    private final Vitoria classica = new Vitoria(TipoDeVitoria.CLASSICA, 10);
    private final VitoriaDto classicaDto = new VitoriaDto("Clássica", 10);

    private final MunchkinDto jogadorDto = new MunchkinDto("Jogador", "Humano", 0, 0,
            "/img/moes/moes_humanas_esquerda.png", "/img/moes/moes_humanas_direita.png",
            "/img/moes/moes_costas_humano_esquerda.png", "/img/moes/moes_costas_humano_direita.png");
    private final MunchkinDto botDto = new MunchkinDto("Bot 1", "Humano", 0, 0,
            "/img/moes/moes_humanas_esquerda.png", "/img/moes/moes_humanas_direita.png",
            "/img/moes/moes_costas_humano_esquerda.png", "/img/moes/moes_costas_humano_direita.png");
    private final List<CartaDto> maoDoJogadorDto = List.of(new CartaDto("tesouro", "Espada",
            "/img/cartas/tesouro/1_itens/espada_tesouro.jpg", "/img/cartas/traseira/verso_tesouro.jpg"));
    private final MaoFechadaDto maoDoBotDto = new MaoFechadaDto(
            List.of(new CartaFechadaDto("masmorra", "/img/cartas/traseira/verso_masmorra.jpg")), 1, 0);

    /** Mesa com o jogador e 1 bot, cada um com uma carta, e vitória clássica (a que vale sem escolher). */
    @BeforeEach
    void mesaComUmBot() {
        Munchkin jogador = new Munchkin("Jogador");
        Munchkin bot = new Munchkin("Bot 1");
        when(munchkinService.novoJogador()).thenReturn(jogador);
        when(munchkinService.novosBots(1)).thenReturn(List.of(bot));
        when(munchkinService.forcaDeCombate(jogador)).thenReturn(0);
        when(munchkinService.forcaDeCombate(bot)).thenReturn(0);
        when(munchkinDtoMapper.paraDto(jogador, 0)).thenReturn(jogadorDto);
        when(munchkinDtoMapper.paraDto(bot, 0)).thenReturn(botDto);

        List<Carta> maoDoJogador = List.of(new Carta(TipoDeCarta.TESOURO, "tesouro/1_itens/espada_tesouro.jpg"));
        List<Carta> maoDoBot = List.of(new Carta(TipoDeCarta.MASMORRA, "masmorra/5_racas/elfo_masmorra.jpg"));
        when(baralhoService.distribuirMaosIniciais(2)).thenReturn(List.of(maoDoJogador, maoDoBot));
        when(cartaDtoMapper.paraDto(maoDoJogador)).thenReturn(maoDoJogadorDto);
        when(cartaDtoMapper.paraMaoFechada(maoDoBot)).thenReturn(maoDoBotDto);

        when(vitoriaService.definir(TipoDeVitoria.CLASSICA, null)).thenReturn(classica);
        when(vitoriaDtoMapper.paraDto(classica)).thenReturn(classicaDto);
    }

    @Test
    void primeiraMaoEDoJogadorEAsOutrasDosBotsFechadas() throws Exception {
        mockMvc.perform(get("/jogar-com-maquina").param("bots", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("jogar_com_maquina/jogar_com_maquina"))
                .andExpect(model().attribute("jogador", jogadorDto))
                .andExpect(model().attribute("maoInicial", maoDoJogadorDto))
                .andExpect(model().attribute("oponentes", List.of(new OponenteDto(botDto, maoDoBotDto))))
                .andExpect(model().attribute("vitoria", classicaDto));
    }

    @Test
    void vitoriaPersonalizadaUsaONivelEscolhido() throws Exception {
        Vitoria personalizada = new Vitoria(TipoDeVitoria.PERSONALIZADA, 37);
        VitoriaDto personalizadaDto = new VitoriaDto("Personalizada", 37);
        when(vitoriaService.definir(TipoDeVitoria.PERSONALIZADA, 37)).thenReturn(personalizada);
        when(vitoriaDtoMapper.paraDto(personalizada)).thenReturn(personalizadaDto);

        mockMvc.perform(get("/jogar-com-maquina").param("bots", "1")
                        .param("vitoria", "PERSONALIZADA").param("nivel", "37"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("vitoria", personalizadaDto));
    }

    @Test
    void nivelDeVitoriaInvalidoEPedidoInvalido() throws Exception {
        when(vitoriaService.definir(TipoDeVitoria.PERSONALIZADA, 101))
                .thenThrow(new NivelDeVitoriaInvalidoException(101));

        mockMvc.perform(get("/jogar-com-maquina").param("bots", "1")
                        .param("vitoria", "PERSONALIZADA").param("nivel", "101"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(baralhoService);
    }

    @Test
    void tipoDeVitoriaDesconhecidoEPedidoInvalido() throws Exception {
        mockMvc.perform(get("/jogar-com-maquina").param("bots", "1").param("vitoria", "RAPIDA"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(baralhoService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "11", "-1", "dois"})
    void quantidadeDeBotsForaDoLimiteEPedidoInvalido(String bots) throws Exception {
        mockMvc.perform(get("/jogar-com-maquina").param("bots", bots))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(baralhoService);
    }

    @Test
    void semQuantidadeDeBotsEPedidoInvalido() throws Exception {
        mockMvc.perform(get("/jogar-com-maquina"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(baralhoService);
    }
}
