package ivens.versao.casa.munchkin.carta.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoDeCarta {

    /** Ainda sem cartas (só o verso): a pilha e o descarte já ficam na mesa. */
    DUNGEON("Dungeon", "dungeon"),
    MASMORRA("Masmorra", "masmorra"),
    TESOURO("Tesouro", "tesouro");

    private final String nome;
    /** Pasta das imagens do baralho em static/img/cartas/. */
    private final String pasta;
}
