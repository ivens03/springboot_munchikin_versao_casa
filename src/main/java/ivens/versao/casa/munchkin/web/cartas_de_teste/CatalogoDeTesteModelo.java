package ivens.versao.casa.munchkin.web.cartas_de_teste;

import ivens.versao.casa.munchkin.carta.dto.CartaDtoMapper;
import ivens.versao.casa.munchkin.carta.dto.GrupoDeCartasDto;
import ivens.versao.casa.munchkin.carta.service.BaralhoService;
import ivens.versao.casa.munchkin.web.jogar_com_maquina.JogarComMaquinaWeb;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * TEMPORÁRIO: põe o catálogo ("cartasDeTeste") no model da mesa, para o painel de cartas de teste.
 * Desligado (sem jogo.teste.cartas=true), o atributo não existe e a mesa não mostra o painel.
 */
@ControllerAdvice(assignableTypes = JogarComMaquinaWeb.class)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "jogo.teste.cartas", havingValue = "true")
public class CatalogoDeTesteModelo {

    private final BaralhoService baralhoService;
    private final CartaDtoMapper cartaDtoMapper;

    @ModelAttribute("cartasDeTeste")
    public List<GrupoDeCartasDto> cartasDeTeste() {
        return cartaDtoMapper.paraGrupos(baralhoService.todasAsCartas());
    }
}
