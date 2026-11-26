package ivens.versao.casa.munchkin.munchkin.service.raca.regrasRacas;

import ivens.versao.casa.munchkin.munchkin.model.Raca;
import ivens.versao.casa.munchkin.munchkin.service.raca.RegraDeRaca;
import org.springframework.stereotype.Component;

/**
 * A Sangue Frio: Itens de Uso Único jogados para ajudar os monstros podem ser usados duas vezes.
 * Deixar o Rabo: +1 para Fugir de monstros de Nível 10 a 15 e +2 para monstros de Nível 16 ou mais.
 */
@Component
public class RegraHomemLagarto implements RegraDeRaca {

    @Override
    public Raca raca() {
        return Raca.HOMEM_LAGARTO;
    }

    @Override
    public int usosDeItemDeUsoUnicoParaAjudarMonstro() {
        return 2;
    }

    @Override
    public int bonusParaFugir(int nivelDoMonstro) {
        if (nivelDoMonstro >= 16) {
            return 2;
        }
        return nivelDoMonstro >= 10 ? 1 : 0;
    }
}
