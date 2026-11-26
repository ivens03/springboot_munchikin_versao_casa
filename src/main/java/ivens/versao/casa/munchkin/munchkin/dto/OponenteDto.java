package ivens.versao.casa.munchkin.munchkin.dto;

import ivens.versao.casa.munchkin.carta.dto.MaoFechadaDto;

/** Outro jogador da mesa (bot ou amigo): o Munchkin dele e a mão fechada. */
public record OponenteDto(MunchkinDto munchkin, MaoFechadaDto mao) {
}
