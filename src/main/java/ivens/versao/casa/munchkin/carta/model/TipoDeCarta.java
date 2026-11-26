package ivens.versao.casa.munchkin.carta.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoDeCarta {

    MASMORRA("Masmorra", "masmorra"),
    TESOURO("Tesouro", "tesouro");

    private final String nome;
    /** Pasta das imagens do baralho em static/img/cartas/. */
    private final String pasta;
}
