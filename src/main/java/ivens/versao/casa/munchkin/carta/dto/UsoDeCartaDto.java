package ivens.versao.casa.munchkin.carta.dto;

import java.util.List;

/**
 * Como uma carta da mão pode ser usada, para a tela (vira data-* da carta no leque).
 *
 * @param opcoes        valores das opções liberadas agora (vazia: não pode usar)
 * @param precisaDeAlvo a janela pede para escolher um oponente
 * @param impedimento   texto de por que não pode usar; null quando pode
 */
public record UsoDeCartaDto(List<String> opcoes, boolean precisaDeAlvo, String impedimento) {
}
