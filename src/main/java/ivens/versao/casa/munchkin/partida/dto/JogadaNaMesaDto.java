package ivens.versao.casa.munchkin.partida.dto;

import java.util.List;

/**
 * A carta que acabou de ser jogada, para a mesa mostrar: sai de quem jogou, fica no centro com o efeito e vai
 * para a pilha de descarte dela (jogada_na_mesa.js).
 *
 * @param oponente posição de quem jogou entre os oponentes; null quando foi o jogador da tela
 * @param pilha    pilha de descarte para onde a carta vai ("masmorra", "tesouro"...)
 * @param carta    nome da carta
 * @param imagem   frente da carta
 * @param texto    quem jogou, em quem e o que fez (o mesmo texto do histórico)
 * @param efeitos  o que mudou em cada um, para aparecer em cima de quem mudou
 */
public record JogadaNaMesaDto(Integer oponente, String pilha, String carta, String imagem, String texto,
                              List<EfeitoNaMesaDto> efeitos) {

    /**
     * @param oponente posição de quem mudou entre os oponentes; null quando foi o jogador da tela
     * @param tipo     "sobe", "desce" ou "descarte" (a cor do selo)
     * @param texto    ex.: "Nível 0 → 1", "Descartou 9 cartas"
     */
    public record EfeitoNaMesaDto(Integer oponente, String tipo, String texto) {
    }
}
