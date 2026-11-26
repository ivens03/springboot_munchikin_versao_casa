package ivens.versao.casa.munchkin.carta.model;

/**
 * Uma carta do baralho. Por enquanto não é entidade: o baralho vem das imagens das cartas.
 *
 * @param imagem caminho da imagem a partir de /img/cartas/ (ex.: masmorra/5_racas/elfo_masmorra.jpg)
 */
public record Carta(TipoDeCarta tipo, String imagem) {
}
