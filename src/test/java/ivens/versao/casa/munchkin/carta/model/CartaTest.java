package ivens.versao.casa.munchkin.carta.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class CartaTest {

    @ParameterizedTest
    @CsvSource({
            "MASMORRA,masmorra/5_racas/homem_lagarto_2_masmorra.jpg,homem_lagarto,5_racas",
            "MASMORRA,masmorra/5_racas/elfo_masmorra.jpg,elfo,5_racas",
            "TESOURO,tesouro/3_suba_de_nivel/roubar_1_nivel_tesouro.jpg,roubar_1_nivel,3_suba_de_nivel",
            "TESOURO,tesouro/3_suba_de_nivel/1000_pecas_de_ouro_tesouro.jpg,1000_pecas_de_ouro,3_suba_de_nivel",
            "TESOURO,tesouro/espada_tesouro.jpg,espada,''"
    })
    void chaveSemPilhaESemCopiaEGrupoPelaPasta(TipoDeCarta tipo, String imagem, String chave, String grupo) {
        Carta carta = new Carta(tipo, imagem);

        assertThat(carta.chave()).isEqualTo(chave);
        assertThat(carta.grupo()).isEqualTo(grupo);
    }
}
