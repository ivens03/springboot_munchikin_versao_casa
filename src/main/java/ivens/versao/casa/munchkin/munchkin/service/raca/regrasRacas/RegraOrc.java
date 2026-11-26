package ivens.versao.casa.munchkin.munchkin.service.raca.regrasRacas;

import ivens.versao.casa.munchkin.munchkin.model.Raca;
import ivens.versao.casa.munchkin.munchkin.service.raca.RegraDeRaca;
import org.springframework.stereotype.Component;

/**
 * Alvo de uma Maldição pode ignorá-la perdendo 1 Nível (a não ser que já esteja no Nível 1).
 * Vencendo um combate sozinho por mais de 10, sobe 1 Nível adicional.
 */
@Component
public class RegraOrc implements RegraDeRaca {

    private static final int NIVEL_MINIMO = 1;
    private static final int VANTAGEM_PARA_NIVEL_EXTRA = 10;

    @Override
    public Raca raca() {
        return Raca.ORC;
    }

    @Override
    public boolean podeIgnorarMaldicao() {
        return true;
    }

    @Override
    public int niveisPerdidosAoIgnorarMaldicao(int nivelAtual) {
        return nivelAtual > NIVEL_MINIMO ? 1 : 0;
    }

    @Override
    public int niveisExtrasAoVencer(boolean venceuSozinho, int diferencaDeForca) {
        return venceuSozinho && diferencaDeForca > VANTAGEM_PARA_NIVEL_EXTRA ? 1 : 0;
    }
}
