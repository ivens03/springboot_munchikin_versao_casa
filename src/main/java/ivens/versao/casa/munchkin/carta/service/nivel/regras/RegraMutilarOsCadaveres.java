package ivens.versao.casa.munchkin.carta.service.nivel.regras;

import ivens.versao.casa.munchkin.carta.model.ImpedimentoDeUso;
import ivens.versao.casa.munchkin.carta.service.nivel.SituacaoDoJogador;
import ivens.versao.casa.munchkin.carta.service.nivel.RegraDeSubaDeNivel;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

/**
 * Mutilar os Cadáveres: "Esta carta só pode ser usada depois do combate, mas não necessariamente do seu combate."
 */
@Component
public class RegraMutilarOsCadaveres implements RegraDeSubaDeNivel {

    @Override
    public Set<String> cartas() {
        return Set.of("mutilar_os_cadaveres");
    }

    /** Combate ainda não existe no jogo: por enquanto nunca dá para usar. */
    @Override
    public Optional<ImpedimentoDeUso> impedimentoDaCarta(SituacaoDoJogador situacao) {
        return Optional.of(ImpedimentoDeUso.SO_DEPOIS_DE_UM_COMBATE);
    }
}
