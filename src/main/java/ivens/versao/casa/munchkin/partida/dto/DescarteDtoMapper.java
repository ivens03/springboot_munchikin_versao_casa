package ivens.versao.casa.munchkin.partida.dto;

import ivens.versao.casa.munchkin.carta.dto.CartaDtoMapper;
import ivens.versao.casa.munchkin.carta.model.TipoDeCarta;
import ivens.versao.casa.munchkin.partida.model.Partida;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/** As pilhas de descarte da partida, uma por tipo de carta (Dungeon, Masmorra e Tesouro), mesmo vazias. */
@Component
@RequiredArgsConstructor
public class DescarteDtoMapper {

    private final CartaDtoMapper cartaDtoMapper;

    public List<DescarteDto> paraDto(Partida partida) {
        return Arrays.stream(TipoDeCarta.values())
                .map(tipo -> new DescarteDto(tipo.getPasta(), tipo.getNome(),
                        cartaDtoMapper.paraDto(partida.getDescarte(tipo).reversed())))
                .toList();
    }
}
