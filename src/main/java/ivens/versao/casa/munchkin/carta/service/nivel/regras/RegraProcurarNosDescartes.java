package ivens.versao.casa.munchkin.carta.service.nivel.regras;

import ivens.versao.casa.munchkin.carta.service.nivel.RegraDeSubaDeNivel;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Dar Dor de Cabeça para o Mestre, Dar uma Olhada na Aventura, Matar a Ânsia por Dados Novos do Mestre e
 * Reencarnar sem Morrer (mesmo texto): "Em vez de subir de Nível, você pode usar esta carta a qualquer momento,
 * desde que não esteja envolvido em um combate. Descarte-a para procurar nos descartes uma carta de Raça, Classe,
 * Poder ou Mojo e coloque-a na sua mão."
 * <p>
 * Por enquanto só sobe 1 nível: procurar nos descartes entra quando houver como ver o descarte e Classe/Poder/Mojo.
 */
@Component
public class RegraProcurarNosDescartes implements RegraDeSubaDeNivel {

    @Override
    public Set<String> cartas() {
        return Set.of("dar_dor_de_cabeca_para_o_mestre", "dar_uma_olhada_na_aventura", "matar_a_ansia_por_dados_novos_do_mestre", "reencarnar_sem_morrer");
    }
}
