package ivens.versao.casa.munchkin.jogo.exception;

import static ivens.versao.casa.munchkin.jogo.service.RegrasBasicas.MAXIMO_NIVEL_DE_VITORIA_PERSONALIZADA;
import static ivens.versao.casa.munchkin.jogo.service.RegrasBasicas.MINIMO_NIVEL_DE_VITORIA_PERSONALIZADA;

public class NivelDeVitoriaInvalidoException extends RuntimeException {

    public NivelDeVitoriaInvalidoException(Integer nivel) {
        super("Na vitória personalizada o nível tem que ser de " + MINIMO_NIVEL_DE_VITORIA_PERSONALIZADA
                + " a " + MAXIMO_NIVEL_DE_VITORIA_PERSONALIZADA + ", mas veio: " + nivel);
    }
}
