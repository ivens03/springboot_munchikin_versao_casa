package ivens.versao.casa.munchkin.partida.dto;

/**
 * Resposta do "bots jogam" para o bots_jogam.js: qual oponente jogou, para ele puxar a carta na tela
 * antes de a mesa recarregar com o aviso.
 */
public record JogadaDoBotDto(int oponente) {
}
