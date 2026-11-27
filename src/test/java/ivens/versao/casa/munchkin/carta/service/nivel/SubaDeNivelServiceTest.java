package ivens.versao.casa.munchkin.carta.service.nivel;

import ivens.versao.casa.munchkin.carta.exception.UsoDeCartaNaoPermitidoException;
import ivens.versao.casa.munchkin.carta.model.Carta;
import ivens.versao.casa.munchkin.carta.model.ImpedimentoDeUso;
import ivens.versao.casa.munchkin.carta.model.OpcaoDeNivel;
import ivens.versao.casa.munchkin.carta.model.TipoDeCarta;
import ivens.versao.casa.munchkin.carta.repository.CartaRepository;
import ivens.versao.casa.munchkin.carta.service.nivel.regras.RegraChoramingarComOMestre;
import ivens.versao.casa.munchkin.carta.service.nivel.regras.RegraContemplarSeuUmbigo;
import ivens.versao.casa.munchkin.carta.service.nivel.regras.RegraGestoDeCarinhoInesperado;
import ivens.versao.casa.munchkin.carta.service.nivel.regras.RegraHaMalesQueVemParaOBem;
import ivens.versao.casa.munchkin.carta.service.nivel.regras.RegraLevarAFama;
import ivens.versao.casa.munchkin.carta.service.nivel.regras.RegraMatarOEscudeiro;
import ivens.versao.casa.munchkin.carta.service.nivel.regras.RegraMutilarOsCadaveres;
import ivens.versao.casa.munchkin.carta.service.nivel.regras.RegraProcurarNosDescartes;
import ivens.versao.casa.munchkin.carta.service.nivel.regras.RegraRoubarUmNivel;
import ivens.versao.casa.munchkin.carta.service.nivel.regras.RegraSubaUmNivel;
import ivens.versao.casa.munchkin.carta.service.nivel.regras.RegraTrocarAFichaDosPersonagens;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SubaDeNivelServiceTest {

    private static final List<RegraDeSubaDeNivel> TODAS_AS_REGRAS = List.of(new RegraSubaUmNivel(),
            new RegraChoramingarComOMestre(), new RegraTrocarAFichaDosPersonagens(), new RegraContemplarSeuUmbigo(),
            new RegraRoubarUmNivel(), new RegraProcurarNosDescartes(), new RegraGestoDeCarinhoInesperado(),
            new RegraHaMalesQueVemParaOBem(), new RegraLevarAFama(), new RegraMatarOEscudeiro(),
            new RegraMutilarOsCadaveres());

    /** Catálogo de verdade (as imagens em static/img/cartas): a aplicação sobe com todas as regras. */
    private final CartaRepository catalogo = new CartaRepository("classpath:static/img/cartas/");
    private final SubaDeNivelService service = new SubaDeNivelService(TODAS_AS_REGRAS, catalogo);

    private final Carta pagarAPizza = nivel("pagar_a_pizza");
    private final Carta contemplar = nivel("contemplar_seu_umbigo");
    private final Carta roubar = nivel("roubar_1_nivel");
    private final Carta choramingar = nivel("choramingar_com_o_mestre");
    private final Carta espada = new Carta(TipoDeCarta.TESOURO, "tesouro/1_itens/espada_tesouro.jpg");

    @Test
    void todaCartaDeNivelDoBaralhoTemRegra() {
        List<Carta> cartasDeNivel = catalogo.listar(TipoDeCarta.TESOURO).stream().filter(service::ehCartaDeNivel).toList();

        assertThat(cartasDeNivel).hasSize(34);
        assertThat(cartasDeNivel).allSatisfy(carta -> assertThat(service.regraDe(carta)).isPresent());
    }

    @Test
    void naoSobeComCartaDeNivelSemRegra() {
        CartaRepository repository = mock(CartaRepository.class);
        when(repository.listar(TipoDeCarta.TESOURO)).thenReturn(List.of(pagarAPizza, nivel("carta_nova")));

        assertThatThrownBy(() -> new SubaDeNivelService(List.of(new RegraSubaUmNivel()), repository))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("carta_nova");
    }

    @Test
    void naoSobeComCartaEmDuasRegras() {
        RegraDeSubaDeNivel outra = () -> Set.of("pagar_a_pizza");

        assertThatThrownBy(() -> new SubaDeNivelService(List.of(new RegraSubaUmNivel(), outra), mock(CartaRepository.class)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("pagar_a_pizza");
    }

    @Test
    void copiaDaCartaUsaAMesmaRegra() {
        assertThat(service.regraDe(new Carta(TipoDeCarta.TESOURO, "tesouro/3_suba_de_nivel/pagar_a_pizza_2_tesouro.jpg")))
                .containsInstanceOf(RegraSubaUmNivel.class);
    }

    @Test
    void cartaQueNaoEDeNivelNaoTemUso() {
        assertThat(service.ehCartaDeNivel(espada)).isFalse();
        assertThat(service.uso(espada, situacao(0))).isEqualTo(UsoDeCarta.impedida(ImpedimentoDeUso.CARTA_SEM_USO));
        assertThatThrownBy(() -> service.usar(espada, OpcaoDeNivel.SUBIR_1_NIVEL, situacao(0)))
                .isInstanceOf(UsoDeCartaNaoPermitidoException.class);
    }

    /** Vitória clássica (10): a carta não dá o nível da vitória. */
    @ParameterizedTest
    @CsvSource({"-3,true", "0,true", "8,true", "9,false", "15,false"})
    void subirUmNaoPodeDarONivelDaVitoria(int nivel, boolean pode) {
        Optional<ImpedimentoDeUso> impedimento = service.impedimento(pagarAPizza, OpcaoDeNivel.SUBIR_1_NIVEL, situacao(nivel));

        assertThat(impedimento.isEmpty()).isEqualTo(pode);
        if (!pode) {
            assertThat(impedimento).contains(ImpedimentoDeUso.NIVEL_DA_VITORIA);
        }
    }

    @Test
    void contemplarNoNivel8SoLiberaSubirUm() {
        assertThat(service.uso(contemplar, situacao(7)).opcoes())
                .containsExactly(OpcaoDeNivel.SUBIR_1_NIVEL, OpcaoDeNivel.DESCARTAR_A_MAO_E_SUBIR_2_NIVEIS);
        assertThat(service.uso(contemplar, situacao(8)).opcoes()).containsExactly(OpcaoDeNivel.SUBIR_1_NIVEL);
        assertThat(service.uso(contemplar, situacao(9)))
                .isEqualTo(UsoDeCarta.impedida(ImpedimentoDeUso.NIVEL_DA_VITORIA));
    }

    @Test
    void vitoriaNoNivel1NaoDeixaUsarDoZero() {
        assertThat(service.uso(pagarAPizza, new SituacaoDoJogador(0, List.of(0), 1)))
                .isEqualTo(UsoDeCarta.impedida(ImpedimentoDeUso.NIVEL_DA_VITORIA));
    }

    @Test
    void opcaoQueACartaNaoTemNaoPode() {
        assertThat(service.impedimento(pagarAPizza, OpcaoDeNivel.ROUBAR_1_NIVEL, situacao(0)))
                .contains(ImpedimentoDeUso.OPCAO_QUE_A_CARTA_NAO_TEM);
    }

    @Test
    void motivoDaCartaVemAntesDoDaVitoria() {
        // todos no nível 0: Choramingar não deixa (empatado no maior nível), e diz por quê
        assertThat(service.uso(choramingar, situacao(0)))
                .isEqualTo(UsoDeCarta.impedida(ImpedimentoDeUso.TODOS_NO_MESMO_NIVEL));
        // no 9 (vitória no 10) e com o maior nível: o motivo é o da carta, não o da vitória
        assertThat(service.uso(choramingar, situacao(9)))
                .isEqualTo(UsoDeCarta.impedida(ImpedimentoDeUso.MAIOR_NIVEL));
        assertThat(service.uso(choramingar, new SituacaoDoJogador(0, List.of(2), 10)).podeUsar()).isTrue();
    }

    @Test
    void roubarPedeAlvo() {
        UsoDeCarta uso = service.uso(roubar, situacao(0));

        assertThat(uso.opcoes()).containsExactly(OpcaoDeNivel.ROUBAR_1_NIVEL);
        assertThat(uso.precisaDeAlvo()).isTrue();
        assertThat(service.precisaDeAlvo(roubar)).isTrue();
        assertThat(service.precisaDeAlvo(pagarAPizza)).isFalse();
        assertThat(service.precisaDeAlvo(espada)).isFalse();
    }

    @Test
    void usarDizOEfeitoDaOpcao() {
        assertThat(service.usar(pagarAPizza, OpcaoDeNivel.SUBIR_1_NIVEL, situacao(0))).isEqualTo(EfeitoDeNivel.SUBIR_1);
        assertThat(service.usar(contemplar, OpcaoDeNivel.DESCARTAR_A_MAO_E_SUBIR_2_NIVEIS, situacao(0)))
                .isEqualTo(new EfeitoDeNivel(2, 0, true));
        assertThat(service.usar(roubar, OpcaoDeNivel.ROUBAR_1_NIVEL, situacao(0))).isEqualTo(new EfeitoDeNivel(1, 1, false));
    }

    @Test
    void usarImpedidoDizOMotivo() {
        assertThatThrownBy(() -> service.usar(pagarAPizza, OpcaoDeNivel.SUBIR_1_NIVEL, situacao(9)))
                .isInstanceOf(UsoDeCartaNaoPermitidoException.class)
                .extracting("impedimento").isEqualTo(ImpedimentoDeUso.NIVEL_DA_VITORIA);
    }

    private static SituacaoDoJogador situacao(int nivel) {
        return new SituacaoDoJogador(nivel, List.of(0, 0), 10);
    }

    private static Carta nivel(String chave) {
        return new Carta(TipoDeCarta.TESOURO, "tesouro/3_suba_de_nivel/" + chave + "_tesouro.jpg");
    }
}
