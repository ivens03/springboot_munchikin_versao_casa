package ivens.versao.casa.munchkin.bot.service;

import ivens.versao.casa.munchkin.carta.model.OpcaoDeNivel;

/**
 * Carta de nível que o bot resolveu usar.
 *
 * @param posicao posição da carta na mão do bot
 * @param opcao   opção escolhida (uma das liberadas agora)
 */
public record JogadaDeNivel(int posicao, OpcaoDeNivel opcao) {
}
