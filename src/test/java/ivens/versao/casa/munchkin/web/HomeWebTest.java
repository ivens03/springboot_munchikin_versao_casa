package ivens.versao.casa.munchkin.web;

import ivens.versao.casa.munchkin.jogo.dto.OpcaoDeVitoriaDto;
import ivens.versao.casa.munchkin.jogo.dto.VitoriaDtoMapper;
import ivens.versao.casa.munchkin.web.home.HomeWeb;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(HomeWeb.class)
@Import(VitoriaDtoMapper.class)
class HomeWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void raizDevolveViewHome() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home/home"));
    }

    @Test
    void homeOfereceDeUmADezBots() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(model().attribute("minimoDeBots", 1))
                .andExpect(model().attribute("maximoDeBots", 10));
    }

    @Test
    void comAGaleraOsBotsVaoDeZeroADez() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(model().attribute("minimoDeBotsComAGalera", 0))
                .andExpect(model().attribute("maximoDeBots", 10));
    }

    @Test
    void homeOfereceAsVitoriasEOsLimitesDaPersonalizada() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(model().attribute("opcoesDeVitoria", List.of(
                        new OpcaoDeVitoriaDto("CLASSICA", "Clássica", 10),
                        new OpcaoDeVitoriaDto("SUPER_MUNCHKIN", "Super Munchkin", 20),
                        new OpcaoDeVitoriaDto("PERSONALIZADA", "Personalizada", null))))
                .andExpect(model().attribute("minimoNivelDeVitoria", 1))
                .andExpect(model().attribute("maximoNivelDeVitoria", 100))
                .andExpect(model().attribute("nivelDeVitoriaSugerido", 10));
    }
}
