package ivens.versao.casa.munchkin.partida.dto;

import ivens.versao.casa.munchkin.carta.dto.CartaDto;

import java.util.List;

/**
 * Uma pilha de descarte na mesa, à vista de todos.
 *
 * @param pilha  "dungeon", "masmorra" ou "tesouro" (o mesmo data-pilha das pilhas de compra)
 * @param nome   nome da pilha ("Tesouro")
 * @param cartas as cartas descartadas, a do topo (a última) primeiro
 */
public record DescarteDto(String pilha, String nome, List<CartaDto> cartas) {
}
