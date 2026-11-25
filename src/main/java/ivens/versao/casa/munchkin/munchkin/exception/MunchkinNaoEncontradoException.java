package ivens.versao.casa.munchkin.munchkin.exception;

public class MunchkinNaoEncontradoException extends RuntimeException {

    public MunchkinNaoEncontradoException(Long id) {
        super("Munchkin não encontrado: " + id);
    }
}
