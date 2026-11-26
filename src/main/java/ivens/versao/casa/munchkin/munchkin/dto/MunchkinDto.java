package ivens.versao.casa.munchkin.munchkin.dto;

/**
 * Munchkin como aparece na mesa. As mãos de palma são as do jogador (ele vê a própria mão por trás das cartas);
 * as de costas são as dos oponentes (quem está do outro lado da mesa vê o dorso da mão por cima dos versos).
 * Nível e força de combate do jogador aparecem embaixo das cartas dele quando a mão sobe (mouse no leque);
 * o nível dos oponentes, no placar e ao passar o mouse no assento.
 * A força de combate vem calculada do MunchkinService (regra de jogo não fica no mapper).
 */
public record MunchkinDto(String nome, String raca, int nivel, int forcaDeCombate, String imagemDaMaoEsquerda, String imagemDaMaoDireita,
                          String imagemDasCostasDaMaoEsquerda, String imagemDasCostasDaMaoDireita) {
}
