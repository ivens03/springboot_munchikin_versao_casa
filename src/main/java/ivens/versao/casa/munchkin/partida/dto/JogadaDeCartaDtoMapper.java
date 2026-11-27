package ivens.versao.casa.munchkin.partida.dto;

import ivens.versao.casa.munchkin.carta.dto.CartaDtoMapper;
import ivens.versao.casa.munchkin.partida.dto.JogadaNaMesaDto.EfeitoNaMesaDto;
import ivens.versao.casa.munchkin.partida.dto.ParticipanteDto.SeloDto;
import ivens.versao.casa.munchkin.partida.model.EfeitoDaJogada;
import ivens.versao.casa.munchkin.partida.model.EfeitoDaJogada.MaoDescartada;
import ivens.versao.casa.munchkin.partida.model.EfeitoDaJogada.MudancaDeNivel;
import ivens.versao.casa.munchkin.partida.model.JogadaDeCarta;
import ivens.versao.casa.munchkin.partida.model.JogadorDaPartida;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Cartas jogadas em texto, do ponto de vista do jogador da tela ("Você usou...", "...em você").
 * O mesmo texto vai na carta mostrada no centro da mesa logo depois da jogada e no histórico.
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
                        cartaDtoMapper.nome(jogada.carta()), cartaDtoMapper.paraDto(jogada.carta()).imagem(),
                        participante(jogada, jogada.quem()), afetados(jogada)))
                .toList();
    }

    /** O alvo (mesmo sem efeito) e quem mais aparece nos efeitos, sem repetir e sem quem jogou. */
    private List<ParticipanteDto> afetados(JogadaDeCarta jogada) {
        // JogadorDaPartida não tem equals: o conjunto compara pela instância (um lugar na mesa), na ordem de entrada
        Set<JogadorDaPartida> afetados = new LinkedHashSet<>();
        if (jogada.alvo() != null) {
            afetados.add(jogada.alvo());
        }
        jogada.efeitos().forEach(efeito -> afetados.add(dono(efeito)));
        return afetados.stream()
                .filter(jogador -> jogador != jogada.quem())
                .map(jogador -> participante(jogada, jogador))
                .toList();
    }

    private ParticipanteDto participante(JogadaDeCarta jogada, JogadorDaPartida jogador) {
        String retrato = jogador.getRetrato() == null ? null : cartaDtoMapper.paraDto(jogador.getRetrato()).imagem();
        return new ParticipanteDto(quem(jogador, "Você"), retrato, jogada.efeitos().stream()
                .filter(efeito -> dono(efeito) == jogador)
                .map(efeito -> {
                    EfeitoNaMesaDto naMesa = naMesa(efeito, List.of());
                    return new SeloDto(naMesa.tipo(), naMesa.texto());
                })
                .toList());
    }

    private static JogadorDaPartida dono(EfeitoDaJogada efeito) {
        return switch (efeito) {
            case MudancaDeNivel mudanca -> mudanca.jogador();
            case MaoDescartada descarte -> descarte.jogador();
        };
    }

    /**
     * A jogada para a mesa mostrar logo depois dela. Quem jogou e quem mudou vão pela posição entre os oponentes
     * (null é o jogador da tela), para a animação achar o assento.
     */
    public JogadaNaMesaDto naMesa(JogadaDeCarta jogada, List<JogadorDaPartida> oponentes) {
        return new JogadaNaMesaDto(posicao(jogada.quem(), oponentes), jogada.carta().tipo().getPasta(),
                cartaDtoMapper.nome(jogada.carta()), cartaDtoMapper.paraDto(jogada.carta()).imagem(), aviso(jogada),
                jogada.efeitos().stream().map(efeito -> naMesa(efeito, oponentes)).toList());
    }

    private static EfeitoNaMesaDto naMesa(EfeitoDaJogada efeito, List<JogadorDaPartida> oponentes) {
        return switch (efeito) {
            case MudancaDeNivel(JogadorDaPartida jogador, int antes, int depois) ->
                    new EfeitoNaMesaDto(posicao(jogador, oponentes), depois >= antes ? "sobe" : "desce",
                            "Nível " + antes + " → " + depois);
            case MaoDescartada(JogadorDaPartida jogador, int cartas) ->
                    new EfeitoNaMesaDto(posicao(jogador, oponentes), "descarte",
                            "Descartou " + cartas + (cartas == 1 ? " carta" : " cartas"));
        };
    }

    private static Integer posicao(JogadorDaPartida jogador, List<JogadorDaPartida> oponentes) {
        int posicao = oponentes.indexOf(jogador);
        return posicao < 0 ? null : posicao;
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
