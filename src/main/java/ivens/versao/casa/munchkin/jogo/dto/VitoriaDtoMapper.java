package ivens.versao.casa.munchkin.jogo.dto;

import ivens.versao.casa.munchkin.jogo.model.TipoDeVitoria;
import ivens.versao.casa.munchkin.jogo.model.Vitoria;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class VitoriaDtoMapper {

    public VitoriaDto paraDto(Vitoria vitoria) {
        return new VitoriaDto(vitoria.tipo().getNome(), vitoria.nivel());
    }

    /** Todos os tipos de vitória, na ordem do enum, para a janela de escolha. */
    public List<OpcaoDeVitoriaDto> opcoes() {
        return Arrays.stream(TipoDeVitoria.values())
                .map(tipo -> new OpcaoDeVitoriaDto(tipo.name(), tipo.getNome(), tipo.getNivelFixo()))
                .toList();
    }
}
