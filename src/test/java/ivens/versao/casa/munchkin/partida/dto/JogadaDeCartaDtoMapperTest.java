package ivens.versao.casa.munchkin.partida.dto;

import ivens.versao.casa.munchkin.carta.dto.CartaDtoMapper;
import ivens.versao.casa.munchkin.carta.model.Carta;
import ivens.versao.casa.munchkin.carta.model.TipoDeCarta;
import ivens.versao.casa.munchkin.munchkin.model.Munchkin;
import ivens.versao.casa.munchkin.partida.model.EfeitoDaJogada.MaoDescartada;
import ivens.versao.casa.munchkin.partida.model.EfeitoDaJogada.MudancaDeNivel;
import ivens.versao.casa.munchkin.partida.model.JogadaDeCarta;
import ivens.versao.casa.munchkin.partida.model.JogadorDaPartida;
import ivens.versao.casa.munchkin.partida.dto.ParticipanteDto.SeloDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JogadaDeCartaDtoMapperTest {

    private final JogadaDeCartaDtoMapper mapper = new JogadaDeCartaDtoMapper(new CartaDtoMapper());

    private final JogadorDaPartida voce = new JogadorDaPartida(new Munchkin("Jogador"), List.of(), false);
    private final JogadorDaPartida bot1 = new JogadorDaPartida(new Munchkin("Bot 1"), List.of(), true);
    private final JogadorDaPartida bot2 = new JogadorDaPartida(new Munchkin("Bot 2"), List.of(), true);

    private final JogadaDeCarta voceSubiu = new JogadaDeCarta(0, voce, carta("pagar_a_pizza"), null,
            List.of(new MudancaDeNivel(voce, 0, 1)));

    @Test
    void voceSubiu() {
        assertThat(mapper.aviso(voceSubiu)).isEqualTo("Você usou Pagar a pizza: subiu do nível 0 para o 1.");
    }

    @Test
    void botDescartouAMaoESubiuDois() {
        JogadaDeCarta jogada = new JogadaDeCarta(0, bot1, carta("contemplar_seu_umbigo"), null,
                List.of(new MaoDescartada(bot1, 9), new MudancaDeNivel(bot1, 0, 2)));

        assertThat(mapper.aviso(jogada))
                .isEqualTo("Bot 1 usou Contemplar seu umbigo: descartou a mão (9 cartas) e subiu do nível 0 para o 2.");
    }

    @Test
    void descarteDeUmaCartaNoSingular() {
        JogadaDeCarta jogada = new JogadaDeCarta(0, voce, carta("contemplar_seu_umbigo"), null,
                List.of(new MaoDescartada(voce, 1), new MudancaDeNivel(voce, 3, 5)));

        assertThat(mapper.aviso(jogada))
                .isEqualTo("Você usou Contemplar seu umbigo: descartou a mão (1 carta) e subiu do nível 3 para o 5.");
    }

    @Test
    void botRoubouDeVoce() {
        JogadaDeCarta jogada = new JogadaDeCarta(0, bot1, carta("roubar_1_nivel"), voce,
                List.of(new MudancaDeNivel(bot1, 0, 1), new MudancaDeNivel(voce, 0, -1)));

        assertThat(mapper.aviso(jogada)).isEqualTo(
                "Bot 1 usou Roubar 1 nivel em você: subiu do nível 0 para o 1 e você desceu do nível 0 para o -1.");
    }

    @Test
    void voceRoubouDeUmBot() {
        JogadaDeCarta jogada = new JogadaDeCarta(1, voce, carta("roubar_1_nivel"), bot2,
                List.of(new MudancaDeNivel(voce, -2, -1), new MudancaDeNivel(bot2, 4, 3)));

        assertThat(mapper.aviso(jogada)).isEqualTo(
                "Você usou Roubar 1 nivel em Bot 2: subiu do nível -2 para o -1 e Bot 2 desceu do nível 4 para o 3.");
    }

    @Test
    void historicoComAUltimaJogadaPrimeiro() {
        JogadaDeCarta botSubiu = new JogadaDeCarta(1, bot2, carta("pagar_a_pizza"), null,
                List.of(new MudancaDeNivel(bot2, 2, 3)));

        assertThat(mapper.historico(List.of(voceSubiu, botSubiu))).containsExactly(
                new JogadaDto(1, "Bot 2", "Bot 2 usou Pagar a pizza: subiu do nível 2 para o 3.", "Pagar a pizza",
                        "/img/cartas/tesouro/3_suba_de_nivel/pagar_a_pizza_tesouro.jpg",
                        new ParticipanteDto("Bot 2", null, List.of(new SeloDto("sobe", "Nível 2 → 3"))), List.of()),
                new JogadaDto(0, "Você", "Você usou Pagar a pizza: subiu do nível 0 para o 1.", "Pagar a pizza",
                        "/img/cartas/tesouro/3_suba_de_nivel/pagar_a_pizza_tesouro.jpg",
                        new ParticipanteDto("Você", null, List.of(new SeloDto("sobe", "Nível 0 → 1"))), List.of()));
    }

    @Test
    void historicoComoNoHearthstoneQuemJogouACartaEQuemFoiAfetadoComRetratoESelos() {
        voce.setRetrato(new Carta(TipoDeCarta.MASMORRA, "masmorra/5_racas/elfo_masmorra.jpg"));
        bot1.setRetrato(new Carta(TipoDeCarta.MASMORRA, "masmorra/6_classes/mago_masmorra.jpg"));
        JogadaDeCarta jogada = new JogadaDeCarta(0, bot1, carta("roubar_1_nivel"), voce,
                List.of(new MudancaDeNivel(bot1, 0, 1), new MudancaDeNivel(voce, 0, -1)));

        JogadaDto historico = mapper.historico(List.of(jogada)).getFirst();

        assertThat(historico.autor()).isEqualTo(new ParticipanteDto("Bot 1",
                "/img/cartas/masmorra/6_classes/mago_masmorra.jpg", List.of(new SeloDto("sobe", "Nível 0 → 1"))));
        assertThat(historico.afetados()).containsExactly(new ParticipanteDto("Você",
                "/img/cartas/masmorra/5_racas/elfo_masmorra.jpg", List.of(new SeloDto("desce", "Nível 0 → -1"))));
    }

    @Test
    void contemplarNoHistoricoTemOsDoisSelosEmQuemJogouENinguemAfetado() {
        JogadaDeCarta jogada = new JogadaDeCarta(0, voce, carta("contemplar_seu_umbigo"), null,
                List.of(new MaoDescartada(voce, 9), new MudancaDeNivel(voce, 0, 2)));

        JogadaDto historico = mapper.historico(List.of(jogada)).getFirst();

        assertThat(historico.autor().selos()).containsExactly(new SeloDto("descarte", "Descartou 9 cartas"),
                new SeloDto("sobe", "Nível 0 → 2"));
        assertThat(historico.afetados()).isEmpty();
    }

    @Test
    void historicoVazio() {
        assertThat(mapper.historico(List.of())).isEmpty();
    }

    @Test
    void naMesaOBotRoubouDeVoceComOsSelosEmCimaDeCadaUm() {
        JogadaDeCarta jogada = new JogadaDeCarta(0, bot2, carta("roubar_1_nivel"), voce,
                List.of(new MudancaDeNivel(bot2, 3, 4), new MudancaDeNivel(voce, 0, -1)));

        JogadaNaMesaDto naMesa = mapper.naMesa(jogada, List.of(bot1, bot2));

        assertThat(naMesa.oponente()).isEqualTo(1);
        assertThat(naMesa.pilha()).isEqualTo("tesouro");
        assertThat(naMesa.carta()).isEqualTo("Roubar 1 nivel");
        assertThat(naMesa.imagem()).isEqualTo("/img/cartas/tesouro/3_suba_de_nivel/roubar_1_nivel_tesouro.jpg");
        assertThat(naMesa.texto()).isEqualTo(mapper.aviso(jogada));
        assertThat(naMesa.efeitos()).containsExactly(
                new JogadaNaMesaDto.EfeitoNaMesaDto(1, "sobe", "Nível 3 → 4"),
                new JogadaNaMesaDto.EfeitoNaMesaDto(null, "desce", "Nível 0 → -1"));
    }

    @Test
    void naMesaVoceDescartouAMaoESubiu() {
        JogadaDeCarta jogada = new JogadaDeCarta(0, voce, carta("contemplar_seu_umbigo"), null,
                List.of(new MaoDescartada(voce, 1), new MudancaDeNivel(voce, 0, 2)));

        JogadaNaMesaDto naMesa = mapper.naMesa(jogada, List.of(bot1, bot2));

        assertThat(naMesa.oponente()).isNull();
        assertThat(naMesa.efeitos()).containsExactly(
                new JogadaNaMesaDto.EfeitoNaMesaDto(null, "descarte", "Descartou 1 carta"),
                new JogadaNaMesaDto.EfeitoNaMesaDto(null, "sobe", "Nível 0 → 2"));
    }

    private static Carta carta(String chave) {
        return new Carta(TipoDeCarta.TESOURO, "tesouro/3_suba_de_nivel/" + chave + "_tesouro.jpg");
    }
}
