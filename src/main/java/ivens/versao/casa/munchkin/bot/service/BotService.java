package ivens.versao.casa.munchkin.bot.service;

import ivens.versao.casa.munchkin.carta.model.Carta;
import ivens.versao.casa.munchkin.carta.model.OpcaoDeNivel;
import ivens.versao.casa.munchkin.carta.service.nivel.SituacaoDoJogador;
import ivens.versao.casa.munchkin.carta.service.nivel.SubaDeNivelService;
import ivens.versao.casa.munchkin.carta.service.nivel.UsoDeCarta;
import ivens.versao.casa.munchkin.jogo.service.Dado;
import ivens.versao.casa.munchkin.jogo.service.Embaralhador;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Decisões da máquina (o que o bot faz). Só decide: quem aplica na partida é o PartidaService.
 * Cartas de subir de nível (combinado com o usuário):
 * <ul>
 *   <li>usa as cartas que o texto deixa, mas às vezes guarda para depois (Dado: 1 ou 2, guarda);</li>
 *   <li>Contemplar seu Umbigo: descarta a mão e sobe 2 só quando o resto da mão não tem outra carta de nível
 *       que dê para usar agora; senão sobe 1 (e as outras cartas vão antes dela);</li>
 *   <li>Roubar 1 Nível: de quem tem o maior nível; no empate, sorteado (Embaralhador).</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class BotService {

    /** No dado, até este número o bot guarda a carta para depois (1 ou 2: uma vez em cada três). */
    static final int GUARDA_A_CARTA_ATE = 2;

    private final SubaDeNivelService subaDeNivelService;
    private final Dado dado;
    private final Embaralhador embaralhador;

    /** Tem alguma carta de nível que o texto deixa usar agora. */
    public boolean temCartaDeNivelParaUsar(List<Carta> mao, SituacaoDoJogador situacao) {
        return mao.stream().anyMatch(carta -> subaDeNivelService.uso(carta, situacao).podeUsar());
    }

    /** A carta e a opção que o bot usa agora; vazio quando não tem o que usar ou resolve guardar. */
    public Optional<JogadaDeNivel> escolherCartaDeNivel(List<Carta> mao, SituacaoDoJogador situacao) {
        List<UsoDeCarta> usos = mao.stream().map(carta -> subaDeNivelService.uso(carta, situacao)).toList();
        List<Integer> usaveis = IntStream.range(0, usos.size()).filter(posicao -> usos.get(posicao).podeUsar())
                .boxed().toList();
        if (usaveis.isEmpty() || dado.rolar() <= GUARDA_A_CARTA_ATE) {
            return Optional.empty();
        }
        // a que descarta a mão fica por último: as outras sobem sem perder nada
        int posicao = usaveis.stream().filter(usavel -> !descartaAMao(usos.get(usavel))).findFirst()
                .orElse(usaveis.getFirst());
        UsoDeCarta uso = usos.get(posicao);
        boolean maoFraca = usaveis.size() == 1;
        OpcaoDeNivel opcao = maoFraca && descartaAMao(uso)
                ? OpcaoDeNivel.DESCARTAR_A_MAO_E_SUBIR_2_NIVEIS
                : uso.opcoes().stream().filter(liberada -> liberada != OpcaoDeNivel.DESCARTAR_A_MAO_E_SUBIR_2_NIVEIS)
                        .findFirst().orElseThrow();
        return Optional.of(new JogadaDeNivel(posicao, opcao));
    }

    /**
     * De quem roubar: quem tem o maior nível; no empate, sorteado.
     *
     * @param niveisDosAlvos níveis dos outros jogadores, na ordem da mesa
     * @return a posição do escolhido nessa lista
     */
    public int escolherAlvo(List<Integer> niveisDosAlvos) {
        int maior = niveisDosAlvos.stream().mapToInt(Integer::intValue).max().orElseThrow();
        List<Integer> empatados = IntStream.range(0, niveisDosAlvos.size())
                .filter(posicao -> niveisDosAlvos.get(posicao) == maior).boxed().toList();
        return embaralhador.embaralhar(empatados).getFirst();
    }

    private static boolean descartaAMao(UsoDeCarta uso) {
        return uso.opcoes().contains(OpcaoDeNivel.DESCARTAR_A_MAO_E_SUBIR_2_NIVEIS);
    }
}
