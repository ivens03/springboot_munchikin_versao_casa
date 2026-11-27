package ivens.versao.casa.munchkin.munchkin.service;

import ivens.versao.casa.munchkin.munchkin.exception.MunchkinNaoEncontradoException;
import ivens.versao.casa.munchkin.munchkin.exception.VendaDeOuroNaoPermitidaException;
import ivens.versao.casa.munchkin.munchkin.model.Munchkin;
import ivens.versao.casa.munchkin.munchkin.repository.MunchkinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

import static ivens.versao.casa.munchkin.jogo.service.RegrasBasicas.OURO_POR_NIVEL;
import static ivens.versao.casa.munchkin.jogo.service.RegrasBasicas.PODER_DE_COMBATE_INICIAL;

@Service
@RequiredArgsConstructor
public class MunchkinService {

    public static final String NOME_DO_JOGADOR = "Jogador";
    public static final String PREFIXO_DO_BOT = "Bot ";

    private final MunchkinRepository munchkinRepository;

    /** Munchkin do jogador para uma partida nova; ainda não é salvo (a partida não é persistida). */
    public Munchkin novoJogador() {
        return new Munchkin(NOME_DO_JOGADOR);
    }

    /** Bots do "jogar com a máquina" ("Bot 1", "Bot 2"...); começam humanos como todo Munchkin e não são salvos. */
    public List<Munchkin> novosBots(int quantidade) {
        return IntStream.rangeClosed(1, quantidade)
                .mapToObj(numero -> new Munchkin(PREFIXO_DO_BOT + numero))
                .toList();
    }

    /**
     * Poder de Combate (regra da casa): começa em 0 e soma o nível do Munchkin.
     * Ex.: no nível 1 o poder é 0 + 1 = 1; com nível negativo o poder também fica negativo (nível -2 → -2).
     * Os bônus (itens equipados...) entram aqui quando existirem.
     */
    public int poderDeCombate(Munchkin munchkin) {
        return PODER_DE_COMBATE_INICIAL + munchkin.getNivel();
    }

    /**
     * Vender ouro por nível (regra da casa): 1000 de ouro valem 1 nível, quantas vezes o ouro der.
     * Não vale para o nível da vitória: esse só vem matando monstro (como no Munchkin original).
     */
    public boolean podeVenderOuroPorNivel(Munchkin munchkin, int nivelDeVitoria) {
        return munchkin.getOuro() >= OURO_POR_NIVEL && munchkin.getNivel() + 1 < nivelDeVitoria;
    }

    /** Troca 1000 de ouro por 1 nível. */
    public Munchkin venderOuroPorNivel(Munchkin munchkin, int nivelDeVitoria) {
        if (!podeVenderOuroPorNivel(munchkin, nivelDeVitoria)) {
            throw new VendaDeOuroNaoPermitidaException(munchkin.getNome(), munchkin.getOuro(), munchkin.getNivel(),
                    nivelDeVitoria);
        }
        munchkin.setOuro(munchkin.getOuro() - OURO_POR_NIVEL);
        return mudarNivel(munchkin, 1);
    }

    /** Sobe (niveis positivo) ou desce (negativo). O nível pode ficar negativo, sem limite (regra da casa). */
    public Munchkin mudarNivel(Munchkin munchkin, int niveis) {
        munchkin.setNivel(munchkin.getNivel() + niveis);
        return munchkin;
    }

    @Transactional
    public Munchkin criar(String nome) {
        return munchkinRepository.save(new Munchkin(nome));
    }

    @Transactional(readOnly = true)
    public Munchkin buscar(Long id) {
        return munchkinRepository.findById(id)
                .orElseThrow(() -> new MunchkinNaoEncontradoException(id));
    }
}
