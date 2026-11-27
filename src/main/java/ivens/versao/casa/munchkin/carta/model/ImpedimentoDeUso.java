package ivens.versao.casa.munchkin.carta.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** Por que uma carta (ou uma opção dela) não pode ser usada agora. */
@Getter
@RequiredArgsConstructor
public enum ImpedimentoDeUso {

    /** A carta ainda não tem regra de uso no jogo (só as de subir de nível têm, por enquanto). */
    CARTA_SEM_USO("Esta carta ainda não pode ser usada no jogo."),
    /** A opção não é desta carta (ex.: roubar nível com uma carta que só sobe 1). */
    OPCAO_QUE_A_CARTA_NAO_TEM("Esta carta não tem essa opção."),
    // ---------- texto da carta: "não pode usar quem tem o maior Nível" (Choramingar, Trocar a Ficha) ----------
    MAIOR_NIVEL("Você tem o maior nível da mesa, e quem tem o maior nível não pode usar esta carta."),
    EMPATADO_NO_MAIOR_NIVEL("Você está empatado com o maior nível da mesa, e quem tem o maior nível não pode usar "
            + "esta carta, mesmo empatado."),
    TODOS_NO_MESMO_NIVEL("Todos estão no mesmo nível, então você também tem o maior nível da mesa. Quem tem o maior "
            + "nível não pode usar esta carta, mesmo empatado."),

    // ---------- texto da carta: depende do que o jogo ainda não tem ----------
    SO_DEPOIS_DE_PERDER_ESCUDEIRO_OU_MONTARIA("Só pode ser usada logo depois de perder um Escudeiro ou uma Montaria, "
            + "e isso ainda não existe no jogo."),
    SO_QUANDO_UM_ADVERSARIO_VENCE_COMBATE("Só pode ser usada quando um adversário vence um combate e sobe de nível, "
            + "e ainda não existe combate no jogo."),
    SO_COM_ESCUDEIRO_EM_JOGO("Só pode ser usada com um Escudeiro em jogo, e Escudeiro ainda não existe no jogo."),
    SO_DEPOIS_DE_UM_COMBATE("Só pode ser usada depois de um combate, e ainda não existe combate no jogo."),

    /** Carta de nível não dá o nível da vitória (regra original, mantida na casa). */
    NIVEL_DA_VITORIA("Carta de nível não dá o nível da vitória.");

    private final String texto;
}
