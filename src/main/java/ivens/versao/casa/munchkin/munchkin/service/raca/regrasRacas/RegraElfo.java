package ivens.versao.casa.munchkin.munchkin.service.raca.regrasRacas;

import ivens.versao.casa.munchkin.munchkin.model.Raca;
import ivens.versao.casa.munchkin.munchkin.service.raca.RegraDeRaca;
import org.springframework.stereotype.Component;

/** +1 para Fugir. Ganha 1 Nível para cada monstro que ajudar alguém a matar. */
@Component
public class RegraElfo implements RegraDeRaca {

    @Override
    public Raca raca() {
        return Raca.ELFO;
    }

    @Override
    public int bonusParaFugir(int nivelDoMonstro) {
        return 1;
    }

    @Override
    public int niveisPorAjudarAMatar(int monstrosMortos) {
        return monstrosMortos;
    }
}
