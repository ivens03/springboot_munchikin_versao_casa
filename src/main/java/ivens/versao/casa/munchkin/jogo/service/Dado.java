package ivens.versao.casa.munchkin.jogo.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

import static ivens.versao.casa.munchkin.jogo.service.RegrasBasicas.FACES_DO_DADO;

@Component
public class Dado {

    public int rolar() {
        return ThreadLocalRandom.current().nextInt(1, FACES_DO_DADO + 1);
    }
}
