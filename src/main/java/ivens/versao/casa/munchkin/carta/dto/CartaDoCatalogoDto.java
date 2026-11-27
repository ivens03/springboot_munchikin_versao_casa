package ivens.versao.casa.munchkin.carta.dto;

/**
 * Uma carta do catálogo, para escolher (painel de cartas de teste).
 *
 * @param caminho o que vai no formulário: a imagem a partir de /img/cartas/
 * @param nome    nome da carta tirado do arquivo
 * @param imagem  URL da imagem da frente
 */
public record CartaDoCatalogoDto(String caminho, String nome, String imagem) {
}
