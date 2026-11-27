package ivens.versao.casa.munchkin.partida.dto;

import ivens.versao.casa.munchkin.partida.model.Partida;
import org.springframework.stereotype.Component;

@Component
public class RodadaDtoMapper {

    /** Se é preparação vem do PartidaService (regra de jogo não fica no mapper). */
    public RodadaDto paraDto(Partida partida, boolean preparacao) {
        return new RodadaDto(partida.getRodada(), preparacao, partida.getJogador().isPronto());
    }
}
