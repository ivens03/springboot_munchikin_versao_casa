package ivens.versao.casa.munchkin.jogo.service;

/**
 * Regras básicas do Munchkin, valem para todos antes das habilidades de raça.
 */
public final class RegrasBasicas {

    public static final int LIMITE_DE_CARTAS_NA_MAO = 5;

    public static final int FACES_DO_DADO = 6;

    /** Para fugir tem que tirar 5 ou 6 no dado (os bônus de fuga somam ao resultado). */
    public static final int MINIMO_NO_DADO_PARA_FUGIR = 5;

    private RegrasBasicas() {
    }
}
