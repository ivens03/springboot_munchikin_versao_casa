package ivens.versao.casa.munchkin.carta.service.nivel.regras;

import ivens.versao.casa.munchkin.carta.model.OpcaoDeNivel;
import ivens.versao.casa.munchkin.carta.service.nivel.EfeitoDeNivel;
import ivens.versao.casa.munchkin.carta.service.nivel.RegraDeSubaDeNivel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Contemplar seu Umbigo: "Suba 1 Nível normalmente ou descarte toda sua mão para subir 2 Níveis."
 * Os 2 níveis também não podem dar o nível da vitória.
 */
@Component
public class RegraContemplarSeuUmbigo implements RegraDeSubaDeNivel {

    @Override
    public Set<String> cartas() {
        return Set.of("contemplar_seu_umbigo");
    }

    @Override
    public List<OpcaoDeNivel> opcoes() {
        return List.of(OpcaoDeNivel.SUBIR_1_NIVEL, OpcaoDeNivel.DESCARTAR_A_MAO_E_SUBIR_2_NIVEIS);
    }

    @Override
    public EfeitoDeNivel efeito(OpcaoDeNivel opcao) {
        return opcao == OpcaoDeNivel.DESCARTAR_A_MAO_E_SUBIR_2_NIVEIS
                ? new EfeitoDeNivel(2, 0, true)
                : EfeitoDeNivel.SUBIR_1;
    }
}
