package ivens.versao.casa.munchkin.partida.dto;

import ivens.versao.casa.munchkin.jogo.model.TipoDeVitoria;
import ivens.versao.casa.munchkin.jogo.model.Vitoria;
import ivens.versao.casa.munchkin.munchkin.model.Munchkin;
import ivens.versao.casa.munchkin.partida.model.JogadorDaPartida;
import ivens.versao.casa.munchkin.partida.model.Partida;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RodadaDtoMapperTest {

    private final RodadaDtoMapper mapper = new RodadaDtoMapper();

    @Test
    void levaONumeroSeEPreparacaoESeOJogadorJaDeuOk() {
        JogadorDaPartida jogador = new JogadorDaPartida(new Munchkin("Jogador"), List.of(), false);
        Partida partida = new Partida(UUID.randomUUID(), new Vitoria(TipoDeVitoria.CLASSICA, 10), List.of(jogador));

        assertThat(mapper.paraDto(partida, true)).isEqualTo(new RodadaDto(0, true, false));

        jogador.setPronto(true);
        partida.setRodada(1);
        assertThat(mapper.paraDto(partida, false)).isEqualTo(new RodadaDto(1, false, true));
    }
}
