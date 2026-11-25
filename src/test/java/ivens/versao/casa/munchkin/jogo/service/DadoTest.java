package ivens.versao.casa.munchkin.jogo.service;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DadoTest {

    private final Dado dado = new Dado();

    @Test
    void dadoSoTiraDeUmASeis() {
        Set<Integer> resultados = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            resultados.add(dado.rolar());
        }

        assertThat(resultados).containsExactlyInAnyOrder(1, 2, 3, 4, 5, 6);
    }
}
