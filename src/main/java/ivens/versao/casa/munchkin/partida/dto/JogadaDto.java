package ivens.versao.casa.munchkin.partida.dto;

import java.util.List;

/**
 * Uma jogada do histórico da mesa, para ver em imagens: quem jogou → a carta → quem foi afetado, com os selos.
 *
 * @param rodada    rodada em que a carta foi jogada
 * @param quem      quem jogou ("Você", "Bot 2"), para a etiqueta do quadrado
 * @param texto     quem jogou, em quem e o que fez (ex.: "Bot 1 usou Roubar 1 nivel em você: ..."), para leitor de tela
 * @param carta     nome da carta
 * @param imagem    frente da carta
 * @param autor     quem jogou, com o que mudou nele
 * @param afetados  os outros em quem a carta mexeu (o alvo e quem mais mudou), na ordem dos efeitos
 */
public record JogadaDto(int rodada, String quem, String texto, String carta, String imagem, ParticipanteDto autor,
                        List<ParticipanteDto> afetados) {
}
