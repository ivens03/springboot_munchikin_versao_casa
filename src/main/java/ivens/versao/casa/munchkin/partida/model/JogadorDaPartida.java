package ivens.versao.casa.munchkin.partida.model;

import ivens.versao.casa.munchkin.carta.model.Carta;
import ivens.versao.casa.munchkin.munchkin.model.Munchkin;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** Um lugar na mesa: o Munchkin, as cartas na mão e se já deu OK para sair da preparação. */
@Getter
public class JogadorDaPartida {

    private final Munchkin munchkin;
    private final boolean bot;

    @Setter
    private List<Carta> mao;

    @Setter
    private boolean pronto;

    public JogadorDaPartida(Munchkin munchkin, List<Carta> mao, boolean bot) {
        this.munchkin = munchkin;
        this.mao = new ArrayList<>(mao);
        this.bot = bot;
    }
}
