package ivens.versao.casa.munchkin.carta.service.nivel.regras;

import ivens.versao.casa.munchkin.carta.model.ImpedimentoDeUso;
import ivens.versao.casa.munchkin.carta.service.nivel.SituacaoDoJogador;
import ivens.versao.casa.munchkin.carta.service.nivel.RegraDeSubaDeNivel;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

/**
 * Trocar a Ficha dos Personagens: "Você não pode usar se for o jogador com maior Nível, mesmo se estiver empatado
 * com outro jogador." Inclusive quando todos estão no mesmo nível (decidido com o grupo).
 */
@Component
public class RegraTrocarAFichaDosPersonagens implements RegraDeSubaDeNivel {

    @Override
    public Set<String> cartas() {
        return Set.of("trocar_a_ficha_dos_personagens");
    }

    @Override
    public Optional<ImpedimentoDeUso> impedimentoDaCarta(SituacaoDoJogador situacao) {
        return situacao.impedimentoPorTerOMaiorNivel();
    }
}
