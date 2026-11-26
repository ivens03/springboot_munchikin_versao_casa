package ivens.versao.casa.munchkin.carta.service;

import ivens.versao.casa.munchkin.carta.exception.CartasInsuficientesException;
import ivens.versao.casa.munchkin.carta.model.Carta;
import ivens.versao.casa.munchkin.carta.model.TipoDeCarta;
import ivens.versao.casa.munchkin.carta.repository.CartaRepository;
import ivens.versao.casa.munchkin.jogo.service.Embaralhador;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static ivens.versao.casa.munchkin.jogo.service.RegrasBasicas.CARTAS_INICIAIS_DE_MASMORRA;
import static ivens.versao.casa.munchkin.jogo.service.RegrasBasicas.CARTAS_INICIAIS_DE_TESOURO;

@Service
@RequiredArgsConstructor
public class BaralhoService {

    private final CartaRepository cartaRepository;
    private final Embaralhador embaralhador;

    /**
     * Começo da partida: cada jogador compra 5 cartas de Tesouro e depois 5 de Masmorra, nessa ordem.
     * Todos compram do mesmo baralho embaralhado, então nenhuma carta se repete entre as mãos.
     *
     * @return uma mão por jogador, na ordem dos jogadores
     */
    public List<List<Carta>> distribuirMaosIniciais(int jogadores) {
        List<Carta> tesouro = comprar(TipoDeCarta.TESOURO, CARTAS_INICIAIS_DE_TESOURO * jogadores);
        List<Carta> masmorra = comprar(TipoDeCarta.MASMORRA, CARTAS_INICIAIS_DE_MASMORRA * jogadores);

        List<List<Carta>> maos = new ArrayList<>();
        for (int jogador = 0; jogador < jogadores; jogador++) {
            List<Carta> mao = new ArrayList<>();
            mao.addAll(fatia(tesouro, jogador, CARTAS_INICIAIS_DE_TESOURO));
            mao.addAll(fatia(masmorra, jogador, CARTAS_INICIAIS_DE_MASMORRA));
            maos.add(mao);
        }
        return maos;
    }

    private List<Carta> comprar(TipoDeCarta tipo, int quantidade) {
        List<Carta> baralho = cartaRepository.listar(tipo);
        if (baralho.size() < quantidade) {
            throw new CartasInsuficientesException(tipo, quantidade, baralho.size());
        }
        return embaralhador.embaralhar(baralho).subList(0, quantidade);
    }

    private static List<Carta> fatia(List<Carta> compradas, int jogador, int porJogador) {
        return compradas.subList(jogador * porJogador, (jogador + 1) * porJogador);
    }
}
