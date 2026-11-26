package ivens.versao.casa.munchkin.jogo.dto;

/**
 * Opção da janela de escolha da vitória: valor enviado no formulário, nome e nível.
 * Na personalizada o nível é nulo (o jogador digita).
 */
public record OpcaoDeVitoriaDto(String valor, String nome, Integer nivel) {
}
