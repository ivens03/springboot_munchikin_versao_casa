package ivens.versao.casa.munchkin.carta.service.nivel.regras;

import ivens.versao.casa.munchkin.carta.model.OpcaoDeNivel;
import ivens.versao.casa.munchkin.carta.service.nivel.EfeitoDeNivel;
import ivens.versao.casa.munchkin.carta.service.nivel.SituacaoDoJogador;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RegraRoubarUmNivelTest {

    private final RegraRoubarUmNivel regra = new RegraRoubarUmNivel();

    @Test
    void escolheUmJogadorQuePerdeUmEVoceGanhaUm() {
        assertThat(regra.cartas()).containsExactly("roubar_1_nivel");
        assertThat(regra.opcoes()).containsExactly(OpcaoDeNivel.ROUBAR_1_NIVEL);
        assertThat(regra.precisaDeAlvo()).isTrue();
        assertThat(regra.efeito(OpcaoDeNivel.ROUBAR_1_NIVEL)).isEqualTo(new EfeitoDeNivel(1, 1, false));
        // o nível pode ficar negativo: dá para roubar de quem está no 0 ou abaixo
        assertThat(regra.impedimentoDaCarta(new SituacaoDoJogador(0, List.of(0, -3), 10))).isEmpty();
    }
}
