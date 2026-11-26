package ivens.versao.casa.munchkin.jogo.service;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class EmbaralhadorTest {

    private final List<Integer> baralho = IntStream.rangeClosed(1, 50).boxed().toList();

    @Test
    void embaralhaSemPerderNemRepetirCarta() {
        List<Integer> embaralhado = new Embaralhador(null).embaralhar(baralho);

        assertThat(embaralhado).containsExactlyInAnyOrderElementsOf(baralho);
    }

    @Test
    void naoMexeNaListaOriginal() {
        new Embaralhador(7L).embaralhar(baralho);

        assertThat(baralho).isSorted();
    }

    @Test
    void mesmaSementeDaSempreAMesmaOrdem() {
        List<Integer> primeira = new Embaralhador(42L).embaralhar(baralho);
        List<Integer> segunda = new Embaralhador(42L).embaralhar(baralho);

        assertThat(primeira).isEqualTo(segunda).isNotEqualTo(baralho);
    }
}
