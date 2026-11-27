package ivens.versao.casa.munchkin.carta.service.nivel;

/**
 * O que acontece quando a carta é usada.
 *
 * @param niveisDoJogador     quantos níveis quem usou a carta sobe
 * @param niveisQueOAlvoPerde quantos níveis o jogador escolhido perde (0 quando a carta não tem alvo)
 * @param descartaAMao        quem usou descarta o resto da mão
 */
public record EfeitoDeNivel(int niveisDoJogador, int niveisQueOAlvoPerde, boolean descartaAMao) {

    public static final EfeitoDeNivel SUBIR_1 = new EfeitoDeNivel(1, 0, false);
}
