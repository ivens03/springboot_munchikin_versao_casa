package ivens.versao.casa.munchkin.partida.dto;

/**
 * Rodada como aparece na mesa.
 *
 * @param preparacao    rodada 0: organizar a mão e dar OK
 * @param jogadorPronto o jogador já deu OK e espera os outros
 */
public record RodadaDto(int numero, boolean preparacao, boolean jogadorPronto) {
}
