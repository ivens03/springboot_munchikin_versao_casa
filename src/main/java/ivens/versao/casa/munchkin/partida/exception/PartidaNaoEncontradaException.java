package ivens.versao.casa.munchkin.partida.exception;

import java.util.UUID;

public class PartidaNaoEncontradaException extends RuntimeException {

    public PartidaNaoEncontradaException(UUID id) {
        super("Partida não encontrada: " + id);
    }
}
