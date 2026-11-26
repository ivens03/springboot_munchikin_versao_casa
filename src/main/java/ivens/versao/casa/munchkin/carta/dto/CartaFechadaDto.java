package ivens.versao.casa.munchkin.carta.dto;

/**
 * Carta virada para baixo (mão do oponente): só o verso. A frente nem vai para a tela,
 * para ninguém ver a carta do outro pelo código da página.
 *
 * @param tipo  "masmorra" ou "tesouro" (de qual pilha a carta sai na tela)
 * @param verso URL da imagem do verso
 */
public record CartaFechadaDto(String tipo, String verso) {
}
