package ivens.versao.casa.munchkin.partida.exception;

import java.util.List;

/** A nova ordem tem que trazer cada posição da mão uma vez só (0 até o tamanho da mão - 1). */
public class OrdemDaMaoInvalidaException extends RuntimeException {

    public OrdemDaMaoInvalidaException(List<Integer> ordem, int cartasNaMao) {
        super("Ordem da mão inválida para " + cartasNaMao + " cartas: " + ordem);
    }
}
