package ivens.versao.casa.munchkin.web.jogar_com_maquina;

import ivens.versao.casa.munchkin.carta.dto.CartaDtoMapper;
import ivens.versao.casa.munchkin.carta.dto.UsoDeCartaDtoMapper;
import ivens.versao.casa.munchkin.carta.model.OpcaoDeNivel;
import ivens.versao.casa.munchkin.jogo.dto.VitoriaDtoMapper;
import ivens.versao.casa.munchkin.jogo.model.TipoDeVitoria;
import ivens.versao.casa.munchkin.munchkin.dto.MunchkinDto;
import ivens.versao.casa.munchkin.munchkin.dto.MunchkinDtoMapper;
import ivens.versao.casa.munchkin.munchkin.dto.OponenteDto;
import ivens.versao.casa.munchkin.munchkin.model.Munchkin;
import ivens.versao.casa.munchkin.munchkin.service.MunchkinService;
import ivens.versao.casa.munchkin.partida.dto.JogadaDeCartaDtoMapper;
import ivens.versao.casa.munchkin.partida.dto.JogadaDoBotDto;
import ivens.versao.casa.munchkin.partida.dto.RodadaDtoMapper;
import ivens.versao.casa.munchkin.partida.model.Partida;
import ivens.versao.casa.munchkin.partida.service.PartidaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.List;
import java.util.UUID;

import static ivens.versao.casa.munchkin.jogo.service.RegrasBasicas.MAXIMO_DE_BOTS;
import static ivens.versao.casa.munchkin.jogo.service.RegrasBasicas.MINIMO_DE_BOTS;

@Controller
@RequiredArgsConstructor
@RequestMapping("/jogar-com-maquina")
public class JogarComMaquinaWeb {

    private static final String MESA = "redirect:/jogar-com-maquina/";

    private final PartidaService partidaService;
    private final MunchkinService munchkinService;
    private final MunchkinDtoMapper munchkinDtoMapper;
    private final CartaDtoMapper cartaDtoMapper;
    private final VitoriaDtoMapper vitoriaDtoMapper;
    private final RodadaDtoMapper rodadaDtoMapper;
    private final UsoDeCartaDtoMapper usoDeCartaDtoMapper;
    private final JogadaDeCartaDtoMapper jogadaDeCartaDtoMapper;

    /**
     * Nova partida com o jogador e os bots escolhidos na home, e vai para a mesa dela (um F5 lá não distribui de novo).
     * A vitória vem da mesma janela: clássica se não vier; o nível só conta na personalizada.
     * "distribuindo" só vai neste redirecionamento: a animação da distribuição roda uma vez.
     */
    @GetMapping
    public String novaPartida(@RequestParam @Min(MINIMO_DE_BOTS) @Max(MAXIMO_DE_BOTS) int bots,
                              @RequestParam(defaultValue = "CLASSICA") TipoDeVitoria vitoria,
                              @RequestParam(required = false) Integer nivel,
                              RedirectAttributes redirecionamento) {
        Partida partida = partidaService.criarComMaquina(bots, vitoria, nivel);
        redirecionamento.addFlashAttribute("distribuindo", true);
        return MESA + partida.getId();
    }

    @GetMapping("/{id}")
    public String mesa(@PathVariable UUID id, Model model) {
        Partida partida = partidaService.buscar(id);
        model.addAttribute("partidaId", id);
        model.addAttribute("rodada", rodadaDtoMapper.paraDto(partida, partidaService.emPreparacao(partida)));
        model.addAttribute("vitoria", vitoriaDtoMapper.paraDto(partida.getVitoria()));
        model.addAttribute("jogador", paraDto(partida.getJogador().getMunchkin()));
        model.addAttribute("podeVenderOuro", partidaService.jogadorPodeVenderOuro(partida));
        model.addAttribute("mao", cartaDtoMapper.paraDto(partida.getJogador().getMao()));
        model.addAttribute("usos", usoDeCartaDtoMapper.paraDto(partidaService.usosDaMaoDoJogador(partida)));
        model.addAttribute("opcoesDeUso", usoDeCartaDtoMapper.todasAsOpcoes());
        model.addAttribute("oponentes", partida.getOponentes().stream()
                .map(oponente -> new OponenteDto(paraDto(oponente.getMunchkin()),
                        cartaDtoMapper.paraMaoFechada(oponente.getMao())))
                .toList());
        return "jogar_com_maquina/jogar_com_maquina";
    }

    /**
     * OK da preparação. Se com ele todos ficaram prontos, a partida começa:
     * "comecou" só vai neste redirecionamento, para a mensagem de início aparecer uma vez.
     */
    @PostMapping("/{id}/pronto")
    public String pronto(@PathVariable UUID id, RedirectAttributes redirecionamento) {
        Partida partida = partidaService.confirmarPronto(id);
        if (!partidaService.emPreparacao(partida)) {
            redirecionamento.addFlashAttribute("comecou", true);
        }
        return MESA + id;
    }

    /** Vende 1000 de ouro por 1 nível. */
    @PostMapping("/{id}/vender-ouro")
    public String venderOuro(@PathVariable UUID id) {
        partidaService.venderOuroPorNivel(id);
        return MESA + id;
    }

    /**
     * Usa uma carta da mão (arrastada para a mesa e confirmada na janela de uso).
     * "aviso" só vai neste redirecionamento: diz o que a carta fez e some.
     */
    @PostMapping("/{id}/usar-carta")
    public String usarCarta(@PathVariable UUID id, @RequestParam int posicao, @RequestParam OpcaoDeNivel opcao,
                            @RequestParam(required = false) Integer alvo, RedirectAttributes redirecionamento) {
        redirecionamento.addFlashAttribute("aviso",
                jogadaDeCartaDtoMapper.aviso(partidaService.usarCartaDeNivel(id, posicao, opcao, alvo)));
        return MESA + id;
    }

    /**
     * Com a máquina, na preparação: o bots_jogam.js chama de tempos em tempos e um bot pode usar uma carta.
     * Sem jogada, 204. Com jogada, o aviso fica guardado (flash) para a mesa, que o JS recarrega depois de mostrar
     * o bot puxando a carta; a resposta diz qual oponente jogou.
     */
    @PostMapping("/{id}/bots/jogar")
    public ResponseEntity<JogadaDoBotDto> botsJogam(@PathVariable UUID id, HttpServletRequest requisicao,
                                                    HttpServletResponse resposta) {
        return partidaService.jogadaDeBot(id)
                .map(jogada -> {
                    RequestContextUtils.getOutputFlashMap(requisicao)
                            .put("aviso", jogadaDeCartaDtoMapper.aviso(jogada.jogada()));
                    RequestContextUtils.saveOutputFlashMap(
                            requisicao.getContextPath() + "/jogar-com-maquina/" + id, requisicao, resposta);
                    return ResponseEntity.ok(new JogadaDoBotDto(jogada.oponente()));
                })
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    /** Nova ordem da mão, mandada pelo organizar_mao.js quando o jogador solta uma carta arrastada. */
    @PostMapping("/{id}/mao")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reordenarMao(@PathVariable UUID id, @RequestParam List<Integer> ordem) {
        partidaService.reordenarMaoDoJogador(id, ordem);
    }

    private MunchkinDto paraDto(Munchkin munchkin) {
        return munchkinDtoMapper.paraDto(munchkin, munchkinService.poderDeCombate(munchkin));
    }
}
