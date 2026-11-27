package ivens.versao.casa.munchkin.carta.service.nivel;

import ivens.versao.casa.munchkin.carta.model.ImpedimentoDeUso;
import ivens.versao.casa.munchkin.carta.model.OpcaoDeNivel;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Strategy: regra de cada carta de subir de nível (tesouro/3_suba_de_nivel).
 * Os métodos default são a carta comum, sem texto: "Suba +1 Nível", a qualquer momento.
 * Cada carta com texto sobrescreve só o que o texto muda; o texto vai no Javadoc da classe.
 * <p>
 * Vale para todas (regra original, mantida na casa): a carta não dá o nível da vitória.
 * Quem confere isso é o SubaDeNivelService, não a regra.
 */
public interface RegraDeSubaDeNivel {

    /** Cartas desta regra: o nome do arquivo sem "_tesouro" e sem o número da cópia (Carta.chave()). */
    Set<String> cartas();

    /** Opções que a carta oferece. */
    default List<OpcaoDeNivel> opcoes() {
        return List.of(OpcaoDeNivel.SUBIR_1_NIVEL);
    }

    /** O jogador escolhe outro jogador ao usar a carta. */
    default boolean precisaDeAlvo() {
        return false;
    }

    /**
     * O que o texto da carta não deixa agora, com o motivo para explicar ao jogador; vazio quando deixa
     * (a do nível da vitória vale para todas e quem confere é o SubaDeNivelService).
     */
    default Optional<ImpedimentoDeUso> impedimentoDaCarta(SituacaoDoJogador situacao) {
        return Optional.empty();
    }

    /** O que a opção faz. Só é chamado com uma opção de opcoes(). */
    default EfeitoDeNivel efeito(OpcaoDeNivel opcao) {
        return EfeitoDeNivel.SUBIR_1;
    }
}
