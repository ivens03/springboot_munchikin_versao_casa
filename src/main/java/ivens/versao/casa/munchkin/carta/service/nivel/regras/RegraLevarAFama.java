package ivens.versao.casa.munchkin.carta.service.nivel.regras;

import ivens.versao.casa.munchkin.carta.model.ImpedimentoDeUso;
import ivens.versao.casa.munchkin.carta.service.nivel.SituacaoDoJogador;
import ivens.versao.casa.munchkin.carta.service.nivel.RegraDeSubaDeNivel;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

/**
 * Levar a Fama: "Use esta carta quando um adversário ganhar um combate e subir de Nível. Você também sobe."
 */
@Component
public class RegraLevarAFama implements RegraDeSubaDeNivel {

    @Override
    public Set<String> cartas() {
        return Set.of("levar_a_fama");
    }

    /** Combate ainda não existe no jogo: por enquanto nunca dá para usar. */
    @Override
    public Optional<ImpedimentoDeUso> impedimentoDaCarta(SituacaoDoJogador situacao) {
        return Optional.of(ImpedimentoDeUso.SO_QUANDO_UM_ADVERSARIO_VENCE_COMBATE);
    }
}
