package ivens.versao.casa.munchkin.munchkin.service.raca;

import ivens.versao.casa.munchkin.munchkin.model.Raca;
import org.springframework.stereotype.Component;

/**
 * Dois Pés Esquerdos: usa dois Calçados ao mesmo tempo.
 * O Líder da Manada: tem em jogo qualquer número de Montarias ao mesmo tempo.
 */
@Component
public class RegraCentauro implements RegraDeRaca {

    @Override
    public Raca raca() {
        return Raca.CENTAURO;
    }

    @Override
    public int limiteDeCalcados() {
        return 2;
    }

    @Override
    public int limiteDeMontarias() {
        return ILIMITADO;
    }
}
