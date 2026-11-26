package ivens.versao.casa.munchkin.munchkin.service.raca.regrasRacas;

import ivens.versao.casa.munchkin.munchkin.model.Raca;
import ivens.versao.casa.munchkin.munchkin.service.raca.RegraDeRaca;
import org.springframework.stereotype.Component;

/**
 * Uma vez por turno, vende um Item pelo dobro do preço (os outros continuam com o preço normal).
 * Se falhar na primeira tentativa de Fuga, pode descartar uma carta para tentar Fugir novamente.
 */
@Component
public class RegraHalfling implements RegraDeRaca {

    @Override
    public Raca raca() {
        return Raca.HALFLING;
    }

    @Override
    public boolean vendeUmItemPorTurnoPeloDobro() {
        return true;
    }

    @Override
    public boolean podeTentarFugirDeNovoDescartandoCarta() {
        return true;
    }
}
