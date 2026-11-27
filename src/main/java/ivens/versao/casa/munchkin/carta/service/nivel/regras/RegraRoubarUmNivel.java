package ivens.versao.casa.munchkin.carta.service.nivel.regras;

import ivens.versao.casa.munchkin.carta.model.OpcaoDeNivel;
import ivens.versao.casa.munchkin.carta.service.nivel.EfeitoDeNivel;
import ivens.versao.casa.munchkin.carta.service.nivel.RegraDeSubaDeNivel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Roubar 1 Nível: "Escolha um jogador para você roubar 1 Nível. Você ganha um e ele perde um."
 * O nível pode ficar negativo (regra da casa), então dá para roubar de qualquer um.
 */
@Component
public class RegraRoubarUmNivel implements RegraDeSubaDeNivel {

    @Override
    public Set<String> cartas() {
        return Set.of("roubar_1_nivel");
    }

    @Override
    public List<OpcaoDeNivel> opcoes() {
        return List.of(OpcaoDeNivel.ROUBAR_1_NIVEL);
    }

    @Override
    public boolean precisaDeAlvo() {
        return true;
    }

    @Override
    public EfeitoDeNivel efeito(OpcaoDeNivel opcao) {
        return new EfeitoDeNivel(1, 1, false);
    }
}
