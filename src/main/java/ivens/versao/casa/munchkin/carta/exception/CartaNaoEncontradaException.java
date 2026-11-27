package ivens.versao.casa.munchkin.carta.exception;

/** Carta que não existe no catálogo (static/img/cartas). */
public class CartaNaoEncontradaException extends RuntimeException {

    public CartaNaoEncontradaException(String caminho) {
        super("Carta não encontrada: " + caminho);
    }
}
