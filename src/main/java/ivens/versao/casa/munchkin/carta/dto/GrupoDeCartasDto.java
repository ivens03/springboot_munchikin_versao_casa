package ivens.versao.casa.munchkin.carta.dto;

import java.util.List;

/** Cartas de uma pasta do catálogo (ex.: "Tesouro · Suba de nivel"). */
public record GrupoDeCartasDto(String nome, List<CartaDoCatalogoDto> cartas) {
}
