package ivens.versao.casa.munchkin.jogo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Embaralha os baralhos. Com a propriedade jogo.semente definida (ex.: no teste de tela),
 * a ordem sai sempre a mesma.
 */
@Component
public class Embaralhador {

    private final Random random;

    public Embaralhador(@Value("${jogo.semente:#{null}}") Long semente) {
        this.random = semente == null ? new Random() : new Random(semente);
    }

    public <T> List<T> embaralhar(List<T> itens) {
        List<T> embaralhados = new ArrayList<>(itens);
        Collections.shuffle(embaralhados, random);
        return embaralhados;
    }
}
