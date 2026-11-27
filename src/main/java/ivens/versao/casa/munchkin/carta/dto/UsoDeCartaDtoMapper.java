package ivens.versao.casa.munchkin.carta.dto;

import ivens.versao.casa.munchkin.carta.model.OpcaoDeNivel;
import ivens.versao.casa.munchkin.carta.service.nivel.UsoDeCarta;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class UsoDeCartaDtoMapper {

    public UsoDeCartaDto paraDto(UsoDeCarta uso) {
        return new UsoDeCartaDto(uso.opcoes().stream().map(OpcaoDeNivel::name).toList(), uso.precisaDeAlvo(),
                uso.impedimento() == null ? null : uso.impedimento().getTexto());
    }

    public List<UsoDeCartaDto> paraDto(List<UsoDeCarta> usos) {
        return usos.stream().map(this::paraDto).toList();
    }

    /** Todas as opções: a janela de uso tem um botão para cada e mostra só as da carta solta. */
    public List<OpcaoDto> todasAsOpcoes() {
        return Arrays.stream(OpcaoDeNivel.values()).map(opcao -> new OpcaoDto(opcao.name(), opcao.getTexto())).toList();
    }
}
