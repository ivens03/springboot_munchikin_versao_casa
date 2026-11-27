package ivens.versao.casa.munchkin.partida.dto;

import ivens.versao.casa.munchkin.carta.dto.CartaDtoMapper;
import ivens.versao.casa.munchkin.partida.model.EfeitoDaJogada;
import ivens.versao.casa.munchkin.partida.model.EfeitoDaJogada.MaoDescartada;
import ivens.versao.casa.munchkin.partida.model.EfeitoDaJogada.MudancaDeNivel;
import ivens.versao.casa.munchkin.partida.model.JogadaDeCarta;
import ivens.versao.casa.munchkin.partida.model.JogadorDaPartida;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Cartas jogadas em texto, do ponto de vista do jogador da tela ("Você usou...", "...em você").
 * O mesmo texto vai no aviso que aparece logo depois da jogada e no histórico da mesa.
 * Ex.: "Bot 1 usou Roubar 1 nivel em você: subiu do nível 0 para o 1 e você desceu do nível 0 para o -1."
 */
@Component
@RequiredArgsConstructor
public class JogadaDeCartaDtoMapper {

    private final CartaDtoMapper cartaDtoMapper;

    public String aviso(JogadaDeCarta jogada) {
        String alvo = jogada.alvo() == null ? "" : " em " + quem(jogada.alvo(), "você");
        return quem(jogada.quem(), "Você") + " usou " + cartaDtoMapper.nome(jogada.carta()) + alvo + ": "
                + juntar(jogada.efeitos().stream().map(efeito -> efeito(jogada, efeito)).toList()) + ".";
    }

    /** O histórico com a última jogada primeiro. */
    public List<JogadaDto> historico(List<JogadaDeCarta> jogadas) {
        return jogadas.reversed().stream()
                .map(jogada -> new JogadaDto(jogada.rodada(), quem(jogada.quem(), "Você"), aviso(jogada),
                        cartaDtoMapper.nome(jogada.carta()),
                        cartaDtoMapper.paraDto(jogada.carta()).imagem()))
                .toList();
    }

    /** O efeito de quem jogou vai sem sujeito ("subiu..."); o dos outros, com ("Bot 2 desceu..."). */
    private static String efeito(JogadaDeCarta jogada, EfeitoDaJogada efeito) {
        return switch (efeito) {
            case MudancaDeNivel(JogadorDaPartida jogador, int antes, int depois) ->
                    sujeito(jogada, jogador) + (depois >= antes ? "subiu" : "desceu")
                            + " do nível " + antes + " para o " + depois;
            case MaoDescartada(JogadorDaPartida jogador, int cartas) ->
                    sujeito(jogada, jogador) + "descartou a mão (" + cartas + (cartas == 1 ? " carta)" : " cartas)");
        };
    }

    private static String sujeito(JogadaDeCarta jogada, JogadorDaPartida jogador) {
        return jogador == jogada.quem() ? "" : quem(jogador, "você") + " ";
    }

    private static String quem(JogadorDaPartida jogador, String voce) {
        return jogador.isBot() ? jogador.getMunchkin().getNome() : voce;
    }

    /** "a", "a e b", "a, b e c". */
    private static String juntar(List<String> partes) {
        if (partes.size() <= 1) {
            return String.join("", partes);
        }
        return String.join(", ", partes.subList(0, partes.size() - 1)) + " e " + partes.getLast();
    }
}
