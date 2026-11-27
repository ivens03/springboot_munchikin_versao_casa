package ivens.versao.casa.munchkin.bot.service;

import ivens.versao.casa.munchkin.carta.model.Carta;
import ivens.versao.casa.munchkin.carta.model.ImpedimentoDeUso;
import ivens.versao.casa.munchkin.carta.model.OpcaoDeNivel;
import ivens.versao.casa.munchkin.carta.model.TipoDeCarta;
import ivens.versao.casa.munchkin.carta.service.nivel.SituacaoDoJogador;
import ivens.versao.casa.munchkin.carta.service.nivel.SubaDeNivelService;
import ivens.versao.casa.munchkin.carta.service.nivel.UsoDeCarta;
import ivens.versao.casa.munchkin.jogo.service.Dado;
import ivens.versao.casa.munchkin.jogo.service.Embaralhador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Optional;

import static ivens.versao.casa.munchkin.carta.model.OpcaoDeNivel.DESCARTAR_A_MAO_E_SUBIR_2_NIVEIS;
import static ivens.versao.casa.munchkin.carta.model.OpcaoDeNivel.ROUBAR_1_NIVEL;
import static ivens.versao.casa.munchkin.carta.model.OpcaoDeNivel.SUBIR_1_NIVEL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BotServiceTest {

    private final SubaDeNivelService subaDeNivelService = mock(SubaDeNivelService.class);
    private final Dado dado = mock(Dado.class);
    private final Embaralhador embaralhador = mock(Embaralhador.class);
    private final BotService botService = new BotService(subaDeNivelService, dado, embaralhador);

    private final SituacaoDoJogador situacao = new SituacaoDoJogador(0, List.of(0, 0), 10);
    private final Carta espada = carta("1_itens/espada");
    private final Carta pizza = carta("3_suba_de_nivel/pagar_a_pizza");
    private final Carta umbigo = carta("3_suba_de_nivel/contemplar_seu_umbigo");
    private final Carta roubar = carta("3_suba_de_nivel/roubar_1_nivel");

    private static final UsoDeCarta SEM_USO = UsoDeCarta.impedida(ImpedimentoDeUso.CARTA_SEM_USO);
    private static final UsoDeCarta SOBE_1 = new UsoDeCarta(List.of(SUBIR_1_NIVEL), false, null);
    private static final UsoDeCarta CONTEMPLAR = new UsoDeCarta(List.of(SUBIR_1_NIVEL, DESCARTAR_A_MAO_E_SUBIR_2_NIVEIS), false, null);

    @BeforeEach
    void cartas() {
        when(subaDeNivelService.uso(espada, situacao)).thenReturn(SEM_USO);
        when(subaDeNivelService.uso(pizza, situacao)).thenReturn(SOBE_1);
        when(subaDeNivelService.uso(umbigo, situacao)).thenReturn(CONTEMPLAR);
        when(subaDeNivelService.uso(roubar, situacao)).thenReturn(new UsoDeCarta(List.of(ROUBAR_1_NIVEL), true, null));
        when(dado.rolar()).thenReturn(GUARDA_NAO);
    }

    private static final int GUARDA_NAO = BotService.GUARDA_A_CARTA_ATE + 1;

    @Test
    void semCartaDeNivelUsavelNaoJogaNemRolaODado() {
        assertThat(botService.temCartaDeNivelParaUsar(List.of(espada), situacao)).isFalse();
        assertThat(botService.escolherCartaDeNivel(List.of(espada), situacao)).isEmpty();
        verify(dado, org.mockito.Mockito.never()).rolar();
    }

    @Test
    void usaAPrimeiraCartaQueDa() {
        assertThat(botService.temCartaDeNivelParaUsar(List.of(espada, pizza), situacao)).isTrue();
        assertThat(botService.escolherCartaDeNivel(List.of(espada, pizza, roubar), situacao))
                .contains(new JogadaDeNivel(1, SUBIR_1_NIVEL));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void noUmOuNoDoisDoDadoGuardaACarta(int face) {
        when(dado.rolar()).thenReturn(face);

        assertThat(botService.escolherCartaDeNivel(List.of(pizza), situacao)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 4, 5, 6})
    void doTresParaCimaUsa(int face) {
        when(dado.rolar()).thenReturn(face);

        assertThat(botService.escolherCartaDeNivel(List.of(pizza), situacao)).isPresent();
    }

    @Test
    void contemplarFicaParaDepoisDasOutras() {
        assertThat(botService.escolherCartaDeNivel(List.of(umbigo, espada, pizza), situacao))
                .contains(new JogadaDeNivel(2, SUBIR_1_NIVEL));
    }

    @Test
    void contemplarComMaoFracaDescartaASobeDois() {
        assertThat(botService.escolherCartaDeNivel(List.of(espada, umbigo, espada), situacao))
                .contains(new JogadaDeNivel(1, DESCARTAR_A_MAO_E_SUBIR_2_NIVEIS));
    }

    @Test
    void doisContemplarOPrimeiroSobeUm() {
        assertThat(botService.escolherCartaDeNivel(List.of(umbigo, umbigo), situacao))
                .contains(new JogadaDeNivel(0, SUBIR_1_NIVEL));
    }

    @Test
    void contemplarSemOMaisDoisLiberadoSobeUm() {
        // perto da vitória o +2 daria o nível dela: só o +1 fica liberado
        when(subaDeNivelService.uso(umbigo, situacao)).thenReturn(SOBE_1);

        assertThat(botService.escolherCartaDeNivel(List.of(umbigo), situacao))
                .contains(new JogadaDeNivel(0, SUBIR_1_NIVEL));
    }

    @Test
    void cartaImpedidaNaoEUsada() {
        Carta choramingar = carta("3_suba_de_nivel/choramingar_com_o_mestre");
        when(subaDeNivelService.uso(choramingar, situacao)).thenReturn(UsoDeCarta.impedida(ImpedimentoDeUso.TODOS_NO_MESMO_NIVEL));

        assertThat(botService.escolherCartaDeNivel(List.of(choramingar), situacao)).isEqualTo(Optional.empty());
    }

    @Test
    void roubaDeQuemTemOMaiorNivel() {
        when(embaralhador.embaralhar(List.of(1))).thenReturn(List.of(1));

        assertThat(botService.escolherAlvo(List.of(2, 5, -3))).isEqualTo(1);
    }

    @Test
    void empateNoMaiorNivelESorteado() {
        when(embaralhador.embaralhar(List.of(0, 2))).thenReturn(List.of(2, 0));

        assertThat(botService.escolherAlvo(List.of(4, 1, 4))).isEqualTo(2);
        verify(embaralhador).embaralhar(anyList());
    }

    private static Carta carta(String caminho) {
        return new Carta(TipoDeCarta.TESOURO, "tesouro/" + caminho + "_tesouro.jpg");
    }
}
