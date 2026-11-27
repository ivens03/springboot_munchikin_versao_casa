package ivens.versao.casa.munchkin.partida.service;

import ivens.versao.casa.munchkin.carta.model.Carta;
import ivens.versao.casa.munchkin.carta.service.BaralhoService;
import ivens.versao.casa.munchkin.jogo.model.TipoDeVitoria;
import ivens.versao.casa.munchkin.jogo.model.Vitoria;
import ivens.versao.casa.munchkin.jogo.service.VitoriaService;
import ivens.versao.casa.munchkin.munchkin.model.Munchkin;
import ivens.versao.casa.munchkin.munchkin.service.MunchkinService;
import ivens.versao.casa.munchkin.partida.exception.ForaDaHoraDeVenderOuroException;
import ivens.versao.casa.munchkin.partida.exception.OrdemDaMaoInvalidaException;
import ivens.versao.casa.munchkin.partida.exception.PartidaNaoEncontradaException;
import ivens.versao.casa.munchkin.partida.model.JogadorDaPartida;
import ivens.versao.casa.munchkin.partida.model.Partida;
import ivens.versao.casa.munchkin.partida.repository.PartidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static ivens.versao.casa.munchkin.jogo.service.RegrasBasicas.RODADA_DE_PREPARACAO;

/**
 * Rodadas da partida (regra da casa).
 * Rodada 0 é a preparação: as cartas são distribuídas e cada um organiza a mão com calma; ainda não se joga
 * e as regras de limite de cartas e descarte não valem. Ela só termina quando todos dão OK (os bots dão na hora);
 * aí vem a rodada 1.
 */
@Service
@RequiredArgsConstructor
public class PartidaService {

    private final PartidaRepository partidaRepository;
    private final MunchkinService munchkinService;
    private final BaralhoService baralhoService;
    private final VitoriaService vitoriaService;

    /** Mesa com o jogador e os bots, já com as mãos distribuídas, na rodada de preparação. */
    public Partida criarComMaquina(int bots, TipoDeVitoria tipoDeVitoria, Integer nivelDeVitoria) {
        Vitoria vitoria = vitoriaService.definir(tipoDeVitoria, nivelDeVitoria);

        List<Munchkin> munchkins = new ArrayList<>();
        munchkins.add(munchkinService.novoJogador());
        munchkins.addAll(munchkinService.novosBots(bots));
        List<List<Carta>> maos = baralhoService.distribuirMaosIniciais(munchkins.size());

        List<JogadorDaPartida> jogadores = IntStream.range(0, munchkins.size())
                .mapToObj(lugar -> {
                    boolean bot = lugar > 0;
                    JogadorDaPartida jogador = new JogadorDaPartida(munchkins.get(lugar), maos.get(lugar), bot);
                    jogador.setPronto(bot); // bot não precisa se organizar: dá OK na hora
                    return jogador;
                })
                .toList();
        return partidaRepository.salvar(new Partida(UUID.randomUUID(), vitoria, jogadores));
    }

    public Partida buscar(UUID id) {
        return partidaRepository.buscar(id).orElseThrow(() -> new PartidaNaoEncontradaException(id));
    }

    /** Na preparação ainda não se joga: limite de cartas na mão e descarte não valem. */
    public boolean emPreparacao(Partida partida) {
        return partida.getRodada() == RODADA_DE_PREPARACAO;
    }

    /** O jogador deu OK; com todos prontos, a preparação acaba e começa a rodada 1. */
    public Partida confirmarPronto(UUID id) {
        Partida partida = buscar(id);
        partida.getJogador().setPronto(true);
        if (emPreparacao(partida) && partida.getJogadores().stream().allMatch(JogadorDaPartida::isPronto)) {
            partida.setRodada(RODADA_DE_PREPARACAO + 1);
        }
        return partidaRepository.salvar(partida);
    }

    /**
     * Hora de vender ouro por nível (regra da casa): na preparação e na vez do jogador.
     * Ainda não existe turno, então por enquanto só a preparação libera.
     */
    public boolean horaDeVenderOuro(Partida partida) {
        return emPreparacao(partida);
    }

    /** O botão de vender só aparece quando é a hora e o MunchkinService deixa (ouro e nível da vitória). */
    public boolean jogadorPodeVenderOuro(Partida partida) {
        return horaDeVenderOuro(partida)
                && munchkinService.podeVenderOuroPorNivel(partida.getJogador().getMunchkin(), partida.getVitoria().nivel());
    }

    /** O jogador troca 1000 de ouro por 1 nível. */
    public Partida venderOuroPorNivel(UUID id) {
        Partida partida = buscar(id);
        if (!horaDeVenderOuro(partida)) {
            throw new ForaDaHoraDeVenderOuroException(partida.getRodada());
        }
        munchkinService.venderOuroPorNivel(partida.getJogador().getMunchkin(), partida.getVitoria().nivel());
        return partidaRepository.salvar(partida);
    }

    /**
     * Muda a ordem das cartas na mão do jogador (só organiza, não muda regra nenhuma, por isso vale em qualquer rodada).
     *
     * @param ordem as posições atuais das cartas, na ordem nova (ex.: [2, 0, 1] põe a terceira carta na frente)
     */
    public Partida reordenarMaoDoJogador(UUID id, List<Integer> ordem) {
        Partida partida = buscar(id);
        List<Carta> mao = partida.getJogador().getMao();
        boolean cadaPosicaoUmaVez = ordem.size() == mao.size()
                && ordem.stream().sorted().toList().equals(IntStream.range(0, mao.size()).boxed().toList());
        if (!cadaPosicaoUmaVez) {
            throw new OrdemDaMaoInvalidaException(ordem, mao.size());
        }
        partida.getJogador().setMao(new ArrayList<>(ordem.stream().map(mao::get).toList()));
        return partidaRepository.salvar(partida);
    }
}
