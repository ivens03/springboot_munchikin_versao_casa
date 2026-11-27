package ivens.versao.casa.munchkin.partida.dto;

import ivens.versao.casa.munchkin.carta.dto.CartaDto;
import ivens.versao.casa.munchkin.carta.dto.CartaDtoMapper;
import ivens.versao.casa.munchkin.carta.model.Carta;
import ivens.versao.casa.munchkin.carta.model.TipoDeCarta;
import ivens.versao.casa.munchkin.jogo.model.TipoDeVitoria;
import ivens.versao.casa.munchkin.jogo.model.Vitoria;
import ivens.versao.casa.munchkin.munchkin.model.Munchkin;
import ivens.versao.casa.munchkin.partida.model.JogadorDaPartida;
import ivens.versao.casa.munchkin.partida.model.Partida;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DescarteDtoMapperTest {

    private final DescarteDtoMapper mapper = new DescarteDtoMapper(new CartaDtoMapper());

    private final Partida partida = new Partida(UUID.randomUUID(), new Vitoria(TipoDeVitoria.CLASSICA, 10),
            List.of(new JogadorDaPartida(new Munchkin("Jogador"), List.of(), false)));

    @Test
    void semDescarteAsTresPilhasAparecemVaziasNaOrdemDaMesa() {
        assertThat(mapper.paraDto(partida)).containsExactly(
                new DescarteDto("dungeon", "Dungeon", List.of()),
                new DescarteDto("masmorra", "Masmorra", List.of()),
                new DescarteDto("tesouro", "Tesouro", List.of()));
    }

    @Test
    void cadaCartaNaPilhaDelaComADoTopoPrimeiro() {
        partida.descartar(new Carta(TipoDeCarta.TESOURO, "tesouro/3_suba_de_nivel/pagar_a_pizza_tesouro.jpg"));
        partida.descartar(new Carta(TipoDeCarta.MASMORRA, "masmorra/5_racas/elfo_masmorra.jpg"));
        partida.descartar(new Carta(TipoDeCarta.TESOURO, "tesouro/1_itens/espada_tesouro.jpg"));

        List<DescarteDto> descartes = mapper.paraDto(partida);

        assertThat(descartes.get(0).cartas()).isEmpty();
        assertThat(descartes.get(1).cartas()).extracting(CartaDto::nome).containsExactly("Elfo");
        assertThat(descartes.get(2).cartas()).extracting(CartaDto::nome).containsExactly("Espada", "Pagar a pizza");
        assertThat(descartes.get(2).cartas().getFirst().imagem()).isEqualTo("/img/cartas/tesouro/1_itens/espada_tesouro.jpg");
    }
}
