package ivens.versao.casa.munchkin.carta.service.nivel;

import ivens.versao.casa.munchkin.carta.model.ImpedimentoDeUso;
import ivens.versao.casa.munchkin.carta.model.OpcaoDeNivel;

import java.util.List;

/**
 * Como uma carta da mão pode ser usada agora.
 *
 * @param opcoes        opções liberadas agora (vazia: não pode usar)
 * @param precisaDeAlvo o jogador escolhe outro jogador (ex.: Roubar 1 Nível)
 * @param impedimento   por que não pode usar, quando nenhuma opção está liberada; null quando pode
 */
public record UsoDeCarta(List<OpcaoDeNivel> opcoes, boolean precisaDeAlvo, ImpedimentoDeUso impedimento) {

    public UsoDeCarta {
        opcoes = List.copyOf(opcoes);
    }

    public static UsoDeCarta impedida(ImpedimentoDeUso impedimento) {
        return new UsoDeCarta(List.of(), false, impedimento);
    }

    public boolean podeUsar() {
        return !opcoes.isEmpty();
    }
}
