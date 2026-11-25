package ivens.versao.casa.munchkin.tela.homeTela;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HomeTelaTest {

    @LocalServerPort
    private int porta;

    @Test
    void telaHomeIgualAImagemBase() throws Exception {
        ComparadorDeTela.comparar("home", "http://localhost:" + porta + "/");
    }
}
