package ivens.versao.casa.munchkin.carta.dto;

import java.util.List;

/**
 * Mão de um oponente como os outros veem: os versos e quantas cartas de cada pilha ele tem.
 */
public record MaoFechadaDto(List<CartaFechadaDto> cartas, int cartasDeMasmorra, int cartasDeTesouro) {

    public int total() {
        return cartas.size();
    }
}
