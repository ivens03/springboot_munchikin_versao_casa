package ivens.versao.casa.munchkin.jogo.service;

/**
 * Regras básicas do Munchkin, valem para todos antes das habilidades de raça.
 */
public final class RegrasBasicas {

    public static final int LIMITE_DE_CARTAS_NA_MAO = 5;

    /** Regra da casa: todo Munchkin começa no nível 0. O nível pode ficar negativo, sem limite. */
    public static final int NIVEL_INICIAL = 0;

    /**
     * Regra da casa: a rodada 0 é a preparação. As cartas são distribuídas e cada um organiza a mão;
     * não se joga e não vale limite de cartas nem descarte. Termina quando todos dão OK.
     */
    public static final int RODADA_DE_PREPARACAO = 0;

    /** Regra da casa: todo Munchkin começa a partida com 400 de ouro. */
    public static final int OURO_INICIAL = 400;

    /** Regra da casa: 1000 de ouro podem ser vendidos em troca de 1 nível (nunca o nível da vitória). */
    public static final int OURO_POR_NIVEL = 1000;

    /** Regra da casa: o Poder de Combate começa em 0 e o nível do Munchkin soma nele. */
    public static final int PODER_DE_COMBATE_INICIAL = 0;

    /** Vitória clássica: ganha quem chegar ao nível 10. */
    public static final int NIVEL_DE_VITORIA_CLASSICA = 10;

    /** Vitória Super Munchkin: ganha quem chegar ao nível 20. */
    public static final int NIVEL_DE_VITORIA_SUPER_MUNCHKIN = 20;

    /** Vitória personalizada: os jogadores escolhem o nível, de 1 a 100. */
    public static final int MINIMO_NIVEL_DE_VITORIA_PERSONALIZADA = 1;
    public static final int MAXIMO_NIVEL_DE_VITORIA_PERSONALIZADA = 100;

    /** No começo da partida cada jogador compra 5 cartas de Tesouro e 5 de Masmorra. */
    public static final int CARTAS_INICIAIS_DE_TESOURO = 5;
    public static final int CARTAS_INICIAIS_DE_MASMORRA = 5;

    /** Quantos bots o jogador pode chamar para a mesa (regra da casa). Com a máquina tem que ter pelo menos 1. */
    public static final int MINIMO_DE_BOTS = 1;
    public static final int MAXIMO_DE_BOTS = 10;

    /** Com a galera os bots são opcionais: completam a mesa de 0 até o máximo. */
    public static final int MINIMO_DE_BOTS_COM_A_GALERA = 0;

    public static final int FACES_DO_DADO = 6;

    /** Para fugir tem que tirar 5 ou 6 no dado (os bônus de fuga somam ao resultado). */
    public static final int MINIMO_NO_DADO_PARA_FUGIR = 5;

    private RegrasBasicas() {
    }
}
