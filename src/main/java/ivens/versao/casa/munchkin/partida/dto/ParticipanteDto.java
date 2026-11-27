package ivens.versao.casa.munchkin.partida.dto;

import java.util.List;

/**
 * Alguém numa jogada do histórico (quem jogou ou quem foi afetado), como no histórico do Hearthstone:
 * o retrato, o nome e o que mudou nele.
 *
 * @param nome    "Você", "Bot 2"
 * @param retrato desenho da carta de personagem sorteada para ele; null se não tiver
 * @param selos   o que a jogada mudou nele (ex.: "Nível 0 → 1")
 */
public record ParticipanteDto(String nome, String retrato, List<SeloDto> selos) {

    /**
     * @param tipo  "sobe", "desce" ou "descarte" (a cor do selo)
     * @param texto ex.: "Nível 0 → 1", "Descartou 9 cartas"
     */
    public record SeloDto(String tipo, String texto) {
    }
}
