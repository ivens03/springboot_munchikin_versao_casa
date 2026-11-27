package ivens.versao.casa.munchkin.partida.model;

import ivens.versao.casa.munchkin.carta.model.Carta;

import java.util.List;

/**
 * Uma carta jogada, para o histórico da partida: quando, quem jogou, em quem, qual carta e o que ela fez.
 *
 * @param rodada  rodada em que foi jogada (0 = preparação)
 * @param quem    quem jogou (o jogador da tela ou um bot)
 * @param alvo    em quem foi jogada (ex.: de quem roubou o nível); null quando a carta não tem alvo
 * @param efeitos o que mudou, na ordem em que aconteceu
 */
public record JogadaDeCarta(int rodada, JogadorDaPartida quem, Carta carta, JogadorDaPartida alvo,
                            List<EfeitoDaJogada> efeitos) {

    public JogadaDeCarta {
        efeitos = List.copyOf(efeitos);
    }
}
