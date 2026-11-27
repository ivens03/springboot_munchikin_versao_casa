package ivens.versao.casa.munchkin.carta.dto;

import ivens.versao.casa.munchkin.carta.model.ImpedimentoDeUso;
import ivens.versao.casa.munchkin.carta.model.OpcaoDeNivel;
import ivens.versao.casa.munchkin.carta.service.nivel.UsoDeCarta;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UsoDeCartaDtoMapperTest {

    private final UsoDeCartaDtoMapper mapper = new UsoDeCartaDtoMapper();

    @Test
    void cartaLiberadaLevaAsOpcoesEOAlvo() {
        UsoDeCarta uso = new UsoDeCarta(List.of(OpcaoDeNivel.SUBIR_1_NIVEL, OpcaoDeNivel.DESCARTAR_A_MAO_E_SUBIR_2_NIVEIS),
                false, null);

        assertThat(mapper.paraDto(List.of(uso, new UsoDeCarta(List.of(OpcaoDeNivel.ROUBAR_1_NIVEL), true, null))))
                .containsExactly(
                        new UsoDeCartaDto(List.of("SUBIR_1_NIVEL", "DESCARTAR_A_MAO_E_SUBIR_2_NIVEIS"), false, null),
                        new UsoDeCartaDto(List.of("ROUBAR_1_NIVEL"), true, null));
    }

    @Test
    void cartaImpedidaLevaOMotivo() {
        assertThat(mapper.paraDto(UsoDeCarta.impedida(ImpedimentoDeUso.NIVEL_DA_VITORIA)))
                .isEqualTo(new UsoDeCartaDto(List.of(), false, "Carta de nível não dá o nível da vitória."));
    }

    @Test
    void choramingarNaRodadaZeroExplicaQueTodosEstaoNoMesmoNivel() {
        assertThat(mapper.paraDto(UsoDeCarta.impedida(ImpedimentoDeUso.TODOS_NO_MESMO_NIVEL)).impedimento())
                .isEqualTo("Todos estão no mesmo nível, então você também tem o maior nível da mesa. Quem tem o maior "
                        + "nível não pode usar esta carta, mesmo empatado.");
    }

    @Test
    void umBotaoParaCadaOpcao() {
        assertThat(mapper.todasAsOpcoes()).containsExactly(
                new OpcaoDto("SUBIR_1_NIVEL", "Subir 1 nível"),
                new OpcaoDto("DESCARTAR_A_MAO_E_SUBIR_2_NIVEIS", "Descartar a mão e subir 2 níveis"),
                new OpcaoDto("ROUBAR_1_NIVEL", "Roubar 1 nível"));
    }
}
