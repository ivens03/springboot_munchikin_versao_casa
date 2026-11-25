package ivens.versao.casa.munchkin.munchkin.service.raca;

import ivens.versao.casa.munchkin.munchkin.model.Raca;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Em combate sem ajuda, pode jogar um monstro da mão como Ilusão de uso único (soma o Nível dele à Força).
 * +1 nos Itens que não sejam de uso único e que comecem com G ou N.
 * Os monstros o tratam como Halfling. Monstros com "nariz" no nome não o perseguem:
 * se não puder derrotá-los, foge automaticamente.
 */
@Component
public class RegraGnomo implements RegraDeRaca {

    @Override
    public Raca raca() {
        return Raca.GNOMO;
    }

    @Override
    public Raca racaParaOsMonstros() {
        return Raca.HALFLING;
    }

    @Override
    public boolean podeUsarMonstroComoIlusao(boolean combateComAjuda) {
        return !combateComAjuda;
    }

    @Override
    public int bonusDoItem(String nomeDoItem, boolean itemDeUsoUnico) {
        if (itemDeUsoUnico || nomeDoItem == null || nomeDoItem.isBlank()) {
            return 0;
        }
        char inicial = Character.toUpperCase(nomeDoItem.strip().charAt(0));
        return inicial == 'G' || inicial == 'N' ? 1 : 0;
    }

    @Override
    public boolean fogeAutomaticamente(String nomeDoMonstro) {
        return nomeDoMonstro != null && nomeDoMonstro.toLowerCase(Locale.ROOT).contains("nariz");
    }
}
