package ivens.versao.casa.munchkin.carta.service.nivel.regras;

import ivens.versao.casa.munchkin.carta.model.OpcaoDeNivel;
import ivens.versao.casa.munchkin.carta.service.nivel.EfeitoDeNivel;
import ivens.versao.casa.munchkin.carta.service.nivel.SituacaoDoJogador;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RegraContemplarSeuUmbigoTest {

    private final RegraContemplarSeuUmbigo regra = new RegraContemplarSeuUmbigo();

    @Test
    void subirUmOuDescartarAMaoESubirDois() {
        assertThat(regra.cartas()).containsExactly("contemplar_seu_umbigo");
        assertThat(regra.opcoes())
                .containsExactly(OpcaoDeNivel.SUBIR_1_NIVEL, OpcaoDeNivel.DESCARTAR_A_MAO_E_SUBIR_2_NIVEIS);
        assertThat(regra.efeito(OpcaoDeNivel.SUBIR_1_NIVEL)).isEqualTo(EfeitoDeNivel.SUBIR_1);
        assertThat(regra.efeito(OpcaoDeNivel.DESCARTAR_A_MAO_E_SUBIR_2_NIVEIS)).isEqualTo(new EfeitoDeNivel(2, 0, true));
        assertThat(regra.precisaDeAlvo()).isFalse();
        assertThat(regra.impedimentoDaCarta(new SituacaoDoJogador(0, List.of(0), 10))).isEmpty();
    }
}
