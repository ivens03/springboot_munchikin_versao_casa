package ivens.versao.casa.munchkin.partida.service;

import ivens.versao.casa.munchkin.partida.model.JogadaDeCarta;

/**
 * Um bot usou uma carta.
 *
 * @param oponente posição do bot entre os oponentes (o lugar dele na tela)
 */
public record JogadaDoBot(int oponente, JogadaDeCarta jogada) {
}
