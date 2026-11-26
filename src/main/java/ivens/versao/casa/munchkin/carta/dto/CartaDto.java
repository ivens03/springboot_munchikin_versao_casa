package ivens.versao.casa.munchkin.carta.dto;

/**
 * @param tipo   "masmorra" ou "tesouro" (de qual pilha a carta sai na tela)
 * @param nome   nome da carta tirado do arquivo, para o texto alternativo
 * @param imagem URL da imagem da frente
 * @param verso  URL da imagem do verso
 */
public record CartaDto(String tipo, String nome, String imagem, String verso) {
}
