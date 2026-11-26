package ivens.versao.casa.munchkin.jogo.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static ivens.versao.casa.munchkin.jogo.service.RegrasBasicas.NIVEL_DE_VITORIA_CLASSICA;
import static ivens.versao.casa.munchkin.jogo.service.RegrasBasicas.NIVEL_DE_VITORIA_SUPER_MUNCHKIN;

/** Como se ganha a partida: o nível que o Munchkin tem que alcançar (regras da casa). */
@Getter
@RequiredArgsConstructor
public enum TipoDeVitoria {
    CLASSICA("Clássica", NIVEL_DE_VITORIA_CLASSICA),
    SUPER_MUNCHKIN("Super Munchkin", NIVEL_DE_VITORIA_SUPER_MUNCHKIN),
    /** O nível é escolhido pelos jogadores ao montar a mesa. */
    PERSONALIZADA("Personalizada", null);

    private final String nome;

    /** Nível de vitória já definido pelo tipo; nulo na personalizada. */
    private final Integer nivelFixo;
}
