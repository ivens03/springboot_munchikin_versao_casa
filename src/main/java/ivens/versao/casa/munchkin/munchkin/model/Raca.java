package ivens.versao.casa.munchkin.munchkin.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Raca {

    HUMANO("Humano"),
    HALFLING("Halfling"),
    ELFO("Elfo"),
    ORC("Orc"),
    HOMEM_LAGARTO("Homem-Lagarto"),
    GNOMO("Gnomo"),
    CENTAURO("Centauro"),
    ANAO("Anão");

    private final String nome;
}
