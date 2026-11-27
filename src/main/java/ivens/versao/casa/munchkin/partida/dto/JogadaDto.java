package ivens.versao.casa.munchkin.partida.dto;

/**
 * Uma jogada do histórico da mesa.
 *
 * @param rodada rodada em que a carta foi jogada
 * @param quem   quem jogou ("Você", "Bot 2"), para a etiqueta do quadrado
 * @param texto  quem jogou, em quem e o que fez (ex.: "Bot 1 usou Roubar 1 nivel em você: ...")
 * @param carta  nome da carta
 * @param imagem frente da carta
 */
public record JogadaDto(int rodada, String quem, String texto, String carta, String imagem) {
}
