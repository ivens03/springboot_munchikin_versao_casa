package ivens.versao.casa.munchkin.carta.service.nivel;

import ivens.versao.casa.munchkin.carta.model.ImpedimentoDeUso;

import java.util.List;
import java.util.Optional;

/**
 * O que uma carta de nível precisa saber da mesa para decidir se pode ser usada.
 *
 * @param nivel           nível de quem usa a carta
 * @param niveisDosOutros níveis dos outros jogadores
 * @param nivelDeVitoria  nível que ganha a partida
 */
public record SituacaoDoJogador(int nivel, List<Integer> niveisDosOutros, int nivelDeVitoria) {

    public SituacaoDoJogador {
        niveisDosOutros = List.copyOf(niveisDosOutros);
    }

    /** É (um dos) jogador(es) com o maior nível: empatado também conta. */
    public boolean temOMaiorNivel() {
        return niveisDosOutros.stream().allMatch(outro -> nivel >= outro);
    }

    /**
     * Para as cartas que "não pode usar quem tem o maior Nível, mesmo empatado": qual é o caso, para explicar.
     * Vazio quando tem alguém acima.
     */
    public Optional<ImpedimentoDeUso> impedimentoPorTerOMaiorNivel() {
        if (!temOMaiorNivel()) {
            return Optional.empty();
        }
        if (niveisDosOutros.stream().allMatch(outro -> outro == nivel)) {
            return Optional.of(ImpedimentoDeUso.TODOS_NO_MESMO_NIVEL);
        }
        return Optional.of(niveisDosOutros.contains(nivel)
                ? ImpedimentoDeUso.EMPATADO_NO_MAIOR_NIVEL
                : ImpedimentoDeUso.MAIOR_NIVEL);
    }
}
