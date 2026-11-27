package ivens.versao.casa.munchkin.munchkin.service;

import ivens.versao.casa.munchkin.munchkin.exception.MunchkinNaoEncontradoException;
import ivens.versao.casa.munchkin.munchkin.model.Munchkin;
import ivens.versao.casa.munchkin.munchkin.model.Raca;
import ivens.versao.casa.munchkin.munchkin.repository.MunchkinRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MunchkinServiceTest {

    @Mock
    private MunchkinRepository munchkinRepository;

    @InjectMocks
    private MunchkinService munchkinService;

    @Test
    void munchkinNovoComecaHumano() {
        when(munchkinRepository.save(any(Munchkin.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        Munchkin munchkin = munchkinService.criar("Ivens");

        assertThat(munchkin.getNome()).isEqualTo("Ivens");
        assertThat(munchkin.getRaca()).isEqualTo(Raca.HUMANO);
        assertThat(munchkin.getNivel()).isZero();
    }

    @Test
    void jogadorNovoComecaHumanoENaoESalvo() {
        Munchkin jogador = munchkinService.novoJogador();

        assertThat(jogador.getNome()).isEqualTo(MunchkinService.NOME_DO_JOGADOR);
        assertThat(jogador.getRaca()).isEqualTo(Raca.HUMANO);
        assertThat(jogador.getNivel()).isZero();
        verifyNoInteractions(munchkinRepository);
    }

    @Test
    void jogadorNovoComecaComPoderDeCombateZero() {
        assertThat(munchkinService.poderDeCombate(munchkinService.novoJogador())).isZero();
    }

    /** Poder de Combate = 0 (inicial) + nível: no nível 1 é 0 + 1 = 1; nível negativo deixa o poder negativo. */
    @ParameterizedTest
    @CsvSource({"-7,-7", "-1,-1", "0,0", "1,1", "9,9", "10,10"})
    void oNivelSomaNoPoderDeCombate(int nivel, int poderEsperado) {
        Munchkin munchkin = mock(Munchkin.class);
        when(munchkin.getNivel()).thenReturn(nivel);

        assertThat(munchkinService.poderDeCombate(munchkin)).isEqualTo(poderEsperado);
    }

    /** Vitória clássica (nível 10): vender não pode dar o nível 10. */
    @ParameterizedTest
    @CsvSource({
            "999,0,false",   // falta ouro
            "1000,0,true",
            "2500,0,true",
            "1000,8,true",   // 8 → 9
            "1000,9,false",  // 9 → 10 seria o nível da vitória
            "5000,9,false",
            "1000,-5,true"   // nível negativo também sobe vendendo
    })
    @Test
    void botsSaoNumeradosComecamHumanosENaoSaoSalvos() {
        List<Munchkin> bots = munchkinService.novosBots(3);

        assertThat(bots).extracting(Munchkin::getNome).containsExactly("Bot 1", "Bot 2", "Bot 3");
        assertThat(bots).extracting(Munchkin::getRaca).containsOnly(Raca.HUMANO);
        assertThat(bots).extracting(Munchkin::getNivel).containsOnly(0);
        verifyNoInteractions(munchkinRepository);
    }

    @Test
    void buscarDevolveOMunchkin() {
        Munchkin munchkin = new Munchkin("Ivens");
        when(munchkinRepository.findById(1L)).thenReturn(Optional.of(munchkin));

        assertThat(munchkinService.buscar(1L)).isSameAs(munchkin);
    }

    @Test
    void buscarMunchkinInexistenteLancaExcecao() {
        when(munchkinRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> munchkinService.buscar(99L))
                .isInstanceOf(MunchkinNaoEncontradoException.class)
                .hasMessage("Munchkin não encontrado: 99");
    }
}
