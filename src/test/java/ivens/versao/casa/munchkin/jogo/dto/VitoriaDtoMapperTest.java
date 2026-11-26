package ivens.versao.casa.munchkin.jogo.dto;

import ivens.versao.casa.munchkin.jogo.model.TipoDeVitoria;
import ivens.versao.casa.munchkin.jogo.model.Vitoria;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VitoriaDtoMapperTest {

    private final VitoriaDtoMapper mapper = new VitoriaDtoMapper();

    @Test
    void vitoriaMostraNomeENivel() {
        assertThat(mapper.paraDto(new Vitoria(TipoDeVitoria.SUPER_MUNCHKIN, 20)))
                .isEqualTo(new VitoriaDto("Super Munchkin", 20));
        assertThat(mapper.paraDto(new Vitoria(TipoDeVitoria.PERSONALIZADA, 37)))
                .isEqualTo(new VitoriaDto("Personalizada", 37));
    }

    @Test
    void opcoesNaOrdemComNivelSoNasFixas() {
        assertThat(mapper.opcoes()).containsExactly(
                new OpcaoDeVitoriaDto("CLASSICA", "Clássica", 10),
                new OpcaoDeVitoriaDto("SUPER_MUNCHKIN", "Super Munchkin", 20),
                new OpcaoDeVitoriaDto("PERSONALIZADA", "Personalizada", null));
    }
}
