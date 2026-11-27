package ivens.versao.casa.munchkin.carta.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** O que o jogador escolhe ao usar uma carta de subir de nível (a maioria só tem SUBIR_1_NIVEL). */
@Getter
@RequiredArgsConstructor
public enum OpcaoDeNivel {

    SUBIR_1_NIVEL("Subir 1 nível"),
    DESCARTAR_A_MAO_E_SUBIR_2_NIVEIS("Descartar a mão e subir 2 níveis"),
    ROUBAR_1_NIVEL("Roubar 1 nível");

    private final String texto;
}
