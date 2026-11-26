package ivens.versao.casa.munchkin.tela.jogarComMaquinaTela;

import ivens.versao.casa.munchkin.tela.ComparadorDeTela;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/** Semente fixa: as mãos iniciais saem sempre com as mesmas cartas. Mesa com o mínimo, um meio e o máximo de bots. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "jogo.semente=42")
class JogarComMaquinaTelaTest {

    @LocalServerPort
    private int porta;

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 10})
    void telaJogarComMaquinaIgualAImagemBase(int bots) throws Exception {
        ComparadorDeTela.comparar("jogar_com_maquina_" + bots + "_bots",
                "http://localhost:" + porta + "/jogar-com-maquina?bots=" + bots);
    }
}
