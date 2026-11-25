package ivens.versao.casa.munchkin.munchkin.service.raca;

import ivens.versao.casa.munchkin.munchkin.model.Raca;
import org.springframework.stereotype.Component;

/** Raça padrão de quem não tem carta de raça: sem habilidade, usa só a regra padrão. */
@Component
public class RegraHumano implements RegraDeRaca {

    @Override
    public Raca raca() {
        return Raca.HUMANO;
    }
}
