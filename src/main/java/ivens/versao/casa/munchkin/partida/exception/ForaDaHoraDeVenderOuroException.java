package ivens.versao.casa.munchkin.partida.exception;

/** Ouro só se vende na preparação ou na vez do jogador. */
public class ForaDaHoraDeVenderOuroException extends RuntimeException {

    public ForaDaHoraDeVenderOuroException(int rodada) {
        super("Fora da hora de vender ouro (rodada " + rodada + "): só na preparação ou na vez do jogador");
    }
}
