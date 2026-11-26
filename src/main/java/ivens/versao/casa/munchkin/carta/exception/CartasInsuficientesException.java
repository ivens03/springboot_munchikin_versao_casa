package ivens.versao.casa.munchkin.carta.exception;

import ivens.versao.casa.munchkin.carta.model.TipoDeCarta;

public class CartasInsuficientesException extends RuntimeException {

    public CartasInsuficientesException(TipoDeCarta tipo, int pedidas, int existentes) {
        super("Baralho de " + tipo.getNome() + " tem " + existentes + " cartas, mas precisa comprar " + pedidas);
    }
}
