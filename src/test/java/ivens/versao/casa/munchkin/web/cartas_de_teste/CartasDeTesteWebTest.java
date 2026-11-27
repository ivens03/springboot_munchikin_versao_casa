package ivens.versao.casa.munchkin.web.cartas_de_teste;

import ivens.versao.casa.munchkin.carta.dto.CartaDtoMapper;
import ivens.versao.casa.munchkin.carta.exception.CartaNaoEncontradaException;
import ivens.versao.casa.munchkin.carta.model.Carta;
import ivens.versao.casa.munchkin.carta.model.TipoDeCarta;
import ivens.versao.casa.munchkin.carta.service.BaralhoService;
import ivens.versao.casa.munchkin.partida.service.PartidaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartasDeTesteWeb.class)
@TestPropertySource(properties = "jogo.teste.cartas=true")
class CartasDeTesteWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PartidaService partidaService;

    @MockitoBean
    private CartaDtoMapper cartaDtoMapper;

    @MockitoBean
    private BaralhoService baralhoService;

    private final UUID id = UUID.fromString("7f000000-0000-0000-0000-000000000001");
    private final String urlDaMesa = "/jogar-com-maquina/" + id;
    private final Carta pizza = new Carta(TipoDeCarta.TESOURO, "tesouro/3_suba_de_nivel/pagar_a_pizza_tesouro.jpg");

    @Test
    void pegarCartaVoltaParaAMesaComOPainelNoGrupoDaCarta() throws Exception {
        when(partidaService.darCartaDeTesteAoJogador(id, pizza.imagem())).thenReturn(pizza);
        when(cartaDtoMapper.nomeDoGrupo(pizza)).thenReturn("Tesouro · Suba de nivel");

        mockMvc.perform(post(urlDaMesa + "/teste/pegar-carta").param("caminho", pizza.imagem()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(urlDaMesa))
                .andExpect(flash().attribute("grupoDeTeste", "Tesouro · Suba de nivel"));
    }

    @Test
    void cartaForaDoCatalogoNaoEEncontrada() throws Exception {
        when(partidaService.darCartaDeTesteAoJogador(id, "tesouro/nao_existe.jpg"))
                .thenThrow(new CartaNaoEncontradaException("tesouro/nao_existe.jpg"));

        mockMvc.perform(post(urlDaMesa + "/teste/pegar-carta").param("caminho", "tesouro/nao_existe.jpg"))
                .andExpect(status().isNotFound());
    }
}
