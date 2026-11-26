package ivens.versao.casa.munchkin.munchkin.service;

import ivens.versao.casa.munchkin.munchkin.exception.MunchkinNaoEncontradoException;
import ivens.versao.casa.munchkin.munchkin.model.Munchkin;
import ivens.versao.casa.munchkin.munchkin.model.Raca;
import ivens.versao.casa.munchkin.munchkin.repository.MunchkinRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
    void semItemEquipadoAForcaDeCombateEOProprioNivel() {
        Munchkin jogador = munchkinService.novoJogador();

        assertThat(munchkinService.forcaDeCombate(jogador)).isEqualTo(jogador.getNivel()).isZero();
    }

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
