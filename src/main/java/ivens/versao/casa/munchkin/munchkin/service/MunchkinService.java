package ivens.versao.casa.munchkin.munchkin.service;

import ivens.versao.casa.munchkin.munchkin.exception.MunchkinNaoEncontradoException;
import ivens.versao.casa.munchkin.munchkin.model.Munchkin;
import ivens.versao.casa.munchkin.munchkin.repository.MunchkinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

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
     * Força de Combate: o Nível mais os bônus dos itens equipados (regra geral do Munchkin).
     * Ainda não existe item equipado no jogo, então por enquanto é só o Nível.
     */
    public int forcaDeCombate(Munchkin munchkin) {
        return munchkin.getNivel();
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
