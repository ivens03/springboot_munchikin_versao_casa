package ivens.versao.casa.munchkin.carta.service.nivel.regras;

import ivens.versao.casa.munchkin.carta.model.ImpedimentoDeUso;
import ivens.versao.casa.munchkin.carta.service.nivel.SituacaoDoJogador;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RegraTrocarAFichaDosPersonagensTest {

    private final RegraTrocarAFichaDosPersonagens regra = new RegraTrocarAFichaDosPersonagens();

    /** niveis: os dos outros jogadores, separados por espaço; motivo vazio = pode usar. */
    @ParameterizedTest
    @CsvSource({
            "0,0 0,TODOS_NO_MESMO_NIVEL",        // rodada 0: todos no 0
            "3,3 1,EMPATADO_NO_MAIOR_NIVEL",
            "5,3 1,MAIOR_NIVEL",
            "-1,-1 -5,EMPATADO_NO_MAIOR_NIVEL",
            "2,3 1,",                             // tem alguém acima: pode usar
            "-2,-1 -5,"
    })
    void naoUsaQuemTemOMaiorNivelEDizPorQue(int nivel, String niveis, ImpedimentoDeUso motivo) {
        List<Integer> outros = Arrays.stream(niveis.split(" ")).map(Integer::valueOf).toList();

        assertThat(regra.impedimentoDaCarta(new SituacaoDoJogador(nivel, outros, 10))).isEqualTo(Optional.ofNullable(motivo));
        assertThat(regra.cartas()).containsExactly("trocar_a_ficha_dos_personagens");
    }
}
