package ivens.versao.casa.munchkin.carta.service.nivel.regras;

import ivens.versao.casa.munchkin.carta.service.nivel.RegraDeSubaDeNivel;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Gesto de Carinho Inesperado: "Ao invés de subir 1 Nível, pode escolher pegar o Item com o maior bônus de outro
 * jogador. Se o fizer, ele sobe de Nível e não você."
 * <p>
 * Por enquanto só sobe 1 nível: pegar o Item entra quando existirem itens equipados.
 */
@Component
public class RegraGestoDeCarinhoInesperado implements RegraDeSubaDeNivel {

    @Override
    public Set<String> cartas() {
        return Set.of("gesto_de_carinho_inesperado");
    }
}
