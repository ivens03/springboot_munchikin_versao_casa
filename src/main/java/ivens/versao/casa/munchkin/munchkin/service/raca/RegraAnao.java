package ivens.versao.casa.munchkin.munchkin.service.raca;

import ivens.versao.casa.munchkin.munchkin.model.Raca;
import org.springframework.stereotype.Component;

/** Carrega qualquer número de Itens Grandes. Fica com 6 cartas na mão no final do turno. */
@Component
public class RegraAnao implements RegraDeRaca {

    @Override
    public Raca raca() {
        return Raca.ANAO;
    }

    @Override
    public int limiteDeItensGrandes() {
        return ILIMITADO;
    }

    @Override
    public int limiteDeCartasNaMao() {
        return 6;
    }
}
