package ivens.versao.casa.munchkin.web.cartas_de_teste;

import ivens.versao.casa.munchkin.carta.dto.CartaDtoMapper;
import ivens.versao.casa.munchkin.carta.model.Carta;
import ivens.versao.casa.munchkin.partida.service.PartidaService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

/**
 * TEMPORÁRIO, só para testar cenários: pega qualquer carta do catálogo para a mão do jogador.
 * Só existe com jogo.teste.cartas=true. Para tirar: apagar este pacote e o fragmento cartasDeTeste da mesa.
 */
@Controller
@RequiredArgsConstructor
@ConditionalOnProperty(name = "jogo.teste.cartas", havingValue = "true")
public class CartasDeTesteWeb {

    private final PartidaService partidaService;
    private final CartaDtoMapper cartaDtoMapper;

    /** "grupoDeTeste" só vai neste redirecionamento: o painel reabre no grupo da carta, para pegar outra. */
    @PostMapping("/jogar-com-maquina/{id}/teste/pegar-carta")
    public String pegarCarta(@PathVariable UUID id, @RequestParam String caminho, RedirectAttributes redirecionamento) {
        Carta carta = partidaService.darCartaDeTesteAoJogador(id, caminho);
        redirecionamento.addFlashAttribute("grupoDeTeste", cartaDtoMapper.nomeDoGrupo(carta));
        return "redirect:/jogar-com-maquina/" + id;
    }
}
