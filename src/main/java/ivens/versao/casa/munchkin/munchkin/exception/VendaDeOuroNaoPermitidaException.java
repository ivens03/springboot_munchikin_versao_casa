package ivens.versao.casa.munchkin.munchkin.exception;

/** Vender 1000 de ouro por 1 nível sem ter o ouro, ou quando o nível ganho seria o da vitória. */
public class VendaDeOuroNaoPermitidaException extends RuntimeException {

    public VendaDeOuroNaoPermitidaException(String nome, int ouro, int nivel, int nivelDeVitoria) {
        super(nome + " não pode vender ouro por nível: tem " + ouro + " de ouro, está no nível " + nivel
                + " e a vitória é no nível " + nivelDeVitoria);
    }
}
