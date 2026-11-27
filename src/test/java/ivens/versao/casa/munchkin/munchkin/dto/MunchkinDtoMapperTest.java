package ivens.versao.casa.munchkin.munchkin.dto;

import ivens.versao.casa.munchkin.munchkin.model.Munchkin;
import ivens.versao.casa.munchkin.munchkin.model.Raca;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class MunchkinDtoMapperTest {

    private final MunchkinDtoMapper mapper = new MunchkinDtoMapper();

    @Test
    void jogadorHumanoMostraAsMaosHumanasSeparadas() {
        MunchkinDto dto = mapper.paraDto(new Munchkin("Jogador"), 3);

        assertThat(dto.nome()).isEqualTo("Jogador");
        assertThat(dto.raca()).isEqualTo("Humano");
        assertThat(dto.nivel()).isZero();
        assertThat(dto.poderDeCombate()).isEqualTo(3);
        assertThat(dto.imagemDaMaoEsquerda()).isEqualTo("/img/moes/moes_humanas_esquerda.png");
        assertThat(dto.imagemDaMaoDireita()).isEqualTo("/img/moes/moes_humanas_direita.png");
        assertThat(dto.imagemDasCostasDaMaoEsquerda()).isEqualTo("/img/moes/moes_costas_humano_esquerda.png");
        assertThat(dto.imagemDasCostasDaMaoDireita()).isEqualTo("/img/moes/moes_costas_humano_direita.png");
    }

    @ParameterizedTest
    @CsvSource({
            "HUMANO,moes_humanas",
            "HALFLING,moes_halfling",
            "ELFO,moes_elfos",
            "ORC,moes_orcs",
            "HOMEM_LAGARTO,moes_homens_lagartos",
            "CENTAURO,moes_centauros",
            "ANAO,moes_anoes",
            "GNOMO,moes_humanas"
    })
    void cadaRacaTemSuaImagemDeMaos(Raca raca, String arquivo) {
        assertThat(mapper.imagemDasMaos(raca)).isEqualTo(arquivo);
    }

    @ParameterizedTest
    @EnumSource(Raca.class)
    void enquantoSoExisteACostasDoHumanoTodaRacaUsaEla(Raca raca) {
        assertThat(mapper.imagemDasCostasDasMaos(raca)).isEqualTo("moes_costas_humano");
    }
}
