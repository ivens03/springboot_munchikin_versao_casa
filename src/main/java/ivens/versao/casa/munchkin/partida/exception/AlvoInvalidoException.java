package ivens.versao.casa.munchkin.partida.exception;

/** A carta pede um jogador (ex.: Roubar 1 Nível) e o escolhido não é um dos oponentes. */
public class AlvoInvalidoException extends RuntimeException {

    public AlvoInvalidoException(Integer alvo, int oponentes) {
        super("Alvo inválido (" + alvo + "): escolha um dos " + oponentes + " oponentes");
    }
}
