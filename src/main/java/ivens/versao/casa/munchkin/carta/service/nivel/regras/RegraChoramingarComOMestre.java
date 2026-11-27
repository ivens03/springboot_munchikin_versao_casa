package ivens.versao.casa.munchkin.carta.service.nivel.regras;

import ivens.versao.casa.munchkin.carta.model.ImpedimentoDeUso;
import ivens.versao.casa.munchkin.carta.service.nivel.SituacaoDoJogador;
import ivens.versao.casa.munchkin.carta.service.nivel.RegraDeSubaDeNivel;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

/**
 * Choramingar com o Mestre: "Você não pode usar esta carta se for um dos jogadores com o maior Nível."
 * Empatado no maior nível também não usa, inclusive quando todos estão no mesmo nível (decidido com o grupo).
 */
@Component
public class RegraChoramingarComOMestre implements RegraDeSubaDeNivel {

    @Override
    public Set<String> cartas() {
        return Set.of("choramingar_com_o_mestre");
    }

    @Override
    public Optional<ImpedimentoDeUso> impedimentoDaCarta(SituacaoDoJogador situacao) {
        return situacao.impedimentoPorTerOMaiorNivel();
    }
}
