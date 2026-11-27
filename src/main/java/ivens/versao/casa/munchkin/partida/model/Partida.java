package ivens.versao.casa.munchkin.partida.model;

import ivens.versao.casa.munchkin.carta.model.Carta;
import ivens.versao.casa.munchkin.carta.model.TipoDeCarta;
import ivens.versao.casa.munchkin.jogo.model.Vitoria;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ivens.versao.casa.munchkin.jogo.service.RegrasBasicas.RODADA_DE_PREPARACAO;

/**
 * Uma partida em andamento. Fica em memória (PartidaRepository), não no banco.
 * O primeiro jogador é quem está na frente da tela; os outros são os oponentes, na ordem em que sentam.
 */
@Getter
public class Partida {

    private final UUID id;
    private final Vitoria vitoria;
    private final List<JogadorDaPartida> jogadores;

    @Setter
    private int rodada;

    public Partida(UUID id, Vitoria vitoria, List<JogadorDaPartida> jogadores) {
        this.id = id;
        this.vitoria = vitoria;
        this.jogadores = List.copyOf(jogadores);
        this.rodada = RODADA_DE_PREPARACAO;
    }

    /** Quem está na frente da tela. */
    public JogadorDaPartida getJogador() {
        return jogadores.getFirst();
    }

    public List<JogadorDaPartida> getOponentes() {
        return jogadores.subList(1, jogadores.size());
    }
}
