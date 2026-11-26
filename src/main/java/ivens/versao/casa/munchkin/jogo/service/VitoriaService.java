package ivens.versao.casa.munchkin.jogo.service;

import ivens.versao.casa.munchkin.jogo.exception.NivelDeVitoriaInvalidoException;
import ivens.versao.casa.munchkin.jogo.model.TipoDeVitoria;
import ivens.versao.casa.munchkin.jogo.model.Vitoria;
import org.springframework.stereotype.Service;

import static ivens.versao.casa.munchkin.jogo.service.RegrasBasicas.MAXIMO_NIVEL_DE_VITORIA_PERSONALIZADA;
import static ivens.versao.casa.munchkin.jogo.service.RegrasBasicas.MINIMO_NIVEL_DE_VITORIA_PERSONALIZADA;

/** Define em que nível a partida é vencida (vale para jogar com a máquina e com a galera). */
@Service
public class VitoriaService {

    /**
     * Clássica vence no nível 10 e Super Munchkin no 20 (o nível escolhido é ignorado);
     * na personalizada vale o nível escolhido, que tem que estar entre 1 e 100.
     */
    public Vitoria definir(TipoDeVitoria tipo, Integer nivelEscolhido) {
        if (tipo != TipoDeVitoria.PERSONALIZADA) {
            return new Vitoria(tipo, tipo.getNivelFixo());
        }
        if (nivelEscolhido == null
                || nivelEscolhido < MINIMO_NIVEL_DE_VITORIA_PERSONALIZADA
                || nivelEscolhido > MAXIMO_NIVEL_DE_VITORIA_PERSONALIZADA) {
            throw new NivelDeVitoriaInvalidoException(nivelEscolhido);
        }
        return new Vitoria(tipo, nivelEscolhido);
    }
}
