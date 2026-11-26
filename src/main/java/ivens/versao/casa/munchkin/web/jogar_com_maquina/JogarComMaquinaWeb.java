package ivens.versao.casa.munchkin.web.jogar_com_maquina;

import ivens.versao.casa.munchkin.carta.dto.CartaDtoMapper;
import ivens.versao.casa.munchkin.carta.model.Carta;
import ivens.versao.casa.munchkin.carta.service.BaralhoService;
import ivens.versao.casa.munchkin.jogo.dto.VitoriaDtoMapper;
import ivens.versao.casa.munchkin.jogo.model.TipoDeVitoria;
import ivens.versao.casa.munchkin.jogo.service.VitoriaService;
import ivens.versao.casa.munchkin.munchkin.dto.MunchkinDto;
import ivens.versao.casa.munchkin.munchkin.dto.MunchkinDtoMapper;
import ivens.versao.casa.munchkin.munchkin.dto.OponenteDto;
import ivens.versao.casa.munchkin.munchkin.model.Munchkin;
import ivens.versao.casa.munchkin.munchkin.service.MunchkinService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.IntStream;

import static ivens.versao.casa.munchkin.jogo.service.RegrasBasicas.MAXIMO_DE_BOTS;
import static ivens.versao.casa.munchkin.jogo.service.RegrasBasicas.MINIMO_DE_BOTS;

@Controller
@RequiredArgsConstructor
public class JogarComMaquinaWeb {

    private final MunchkinService munchkinService;
    private final MunchkinDtoMapper munchkinDtoMapper;
    private final BaralhoService baralhoService;
    private final CartaDtoMapper cartaDtoMapper;
    private final VitoriaService vitoriaService;
    private final VitoriaDtoMapper vitoriaDtoMapper;

    /**
     * Mesa com o jogador e os bots escolhidos na home; a mão 0 é do jogador e as seguintes, dos bots.
     * A vitória vem da mesma janela: clássica se não vier; o nível só conta na personalizada.
     */
    @GetMapping("/jogar-com-maquina")
    public String jogarComMaquina(@RequestParam @Min(MINIMO_DE_BOTS) @Max(MAXIMO_DE_BOTS) int bots,
                                  @RequestParam(defaultValue = "CLASSICA") TipoDeVitoria vitoria,
                                  @RequestParam(required = false) Integer nivel,
                                  Model model) {
        model.addAttribute("vitoria", vitoriaDtoMapper.paraDto(vitoriaService.definir(vitoria, nivel)));
        List<Munchkin> oponentes = munchkinService.novosBots(bots);
        List<List<Carta>> maos = baralhoService.distribuirMaosIniciais(bots + 1);

        model.addAttribute("jogador", paraDto(munchkinService.novoJogador()));
        model.addAttribute("maoInicial", cartaDtoMapper.paraDto(maos.getFirst()));
        model.addAttribute("oponentes", IntStream.range(0, bots)
                .mapToObj(bot -> new OponenteDto(paraDto(oponentes.get(bot)),
                        cartaDtoMapper.paraMaoFechada(maos.get(bot + 1))))
                .toList());
        return "jogar_com_maquina/jogar_com_maquina";
    }

    private MunchkinDto paraDto(Munchkin munchkin) {
        return munchkinDtoMapper.paraDto(munchkin, munchkinService.forcaDeCombate(munchkin));
    }
}
