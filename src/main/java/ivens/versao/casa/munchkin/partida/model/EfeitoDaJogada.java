package ivens.versao.casa.munchkin.partida.model;

/**
 * O que uma carta jogada mudou na mesa, guardado no histórico com o valor de antes e de depois
 * (o histórico não pode depender do estado atual, que muda depois). Cada tipo de efeito é um record;
 * o combate vai somar os dele (ex.: bônus no monstro).
 */
public sealed interface EfeitoDaJogada {

    /** O nível de alguém mudou (quem jogou ou o alvo). */
    record MudancaDeNivel(JogadorDaPartida jogador, int antes, int depois) implements EfeitoDaJogada {
    }

    /** Alguém descartou a mão (as cartas além da jogada). */
    record MaoDescartada(JogadorDaPartida jogador, int cartas) implements EfeitoDaJogada {
    }
}
