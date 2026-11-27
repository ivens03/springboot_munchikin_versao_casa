package ivens.versao.casa.munchkin.partida.repository;

import ivens.versao.casa.munchkin.jogo.model.TipoDeVitoria;
import ivens.versao.casa.munchkin.jogo.model.Vitoria;
import ivens.versao.casa.munchkin.munchkin.model.Munchkin;
import ivens.versao.casa.munchkin.partida.model.JogadorDaPartida;
import ivens.versao.casa.munchkin.partida.model.Partida;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PartidaRepositoryTest {

    private final PartidaRepository partidaRepository = new PartidaRepository();

    @Test
    void partidaSalvaEEncontradaPeloId() {
        Partida partida = new Partida(UUID.randomUUID(), new Vitoria(TipoDeVitoria.CLASSICA, 10),
                List.of(new JogadorDaPartida(new Munchkin("Jogador"), List.of(), false)));

        partidaRepository.salvar(partida);

        assertThat(partidaRepository.buscar(partida.getId())).containsSame(partida);
    }

    @Test
    void idDesconhecidoNaoTemPartida() {
        assertThat(partidaRepository.buscar(UUID.randomUUID())).isEmpty();
    }
}
