package ivens.versao.casa.munchkin.carta.model;

/**
 * Uma carta do baralho. Por enquanto não é entidade: o baralho vem das imagens das cartas.
 *
 * @param imagem caminho da imagem a partir de /img/cartas/ (ex.: masmorra/5_racas/elfo_masmorra.jpg)
 */
public record Carta(TipoDeCarta tipo, String imagem) {

    /**
     * Qual carta é, sem a pilha e sem o número da cópia (as cópias têm a mesma regra):
     * "masmorra/5_racas/homem_lagarto_2_masmorra.jpg" → "homem_lagarto".
     */
    public String chave() {
        String arquivo = imagem.substring(imagem.lastIndexOf('/') + 1);
        return arquivo
                .replaceFirst("\\.jpg$", "")
                .replaceFirst("_" + tipo.getPasta() + "$", "")
                .replaceFirst("_\\d+$", "");
    }

    /** Pasta da carta dentro da pilha, que diz o grupo dela: "tesouro/3_suba_de_nivel/x_tesouro.jpg" → "3_suba_de_nivel". */
    public String grupo() {
        String[] partes = imagem.split("/");
        return partes.length > 2 ? partes[1] : "";
    }
}
