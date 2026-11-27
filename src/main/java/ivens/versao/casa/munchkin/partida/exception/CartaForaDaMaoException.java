package ivens.versao.casa.munchkin.partida.exception;

/** Posição de carta que não existe na mão do jogador. */
public class CartaForaDaMaoException extends RuntimeException {

    public CartaForaDaMaoException(int posicao, int cartasNaMao) {
        super("Não há carta na posição " + posicao + ": a mão tem " + cartasNaMao + " cartas");
    }
}
