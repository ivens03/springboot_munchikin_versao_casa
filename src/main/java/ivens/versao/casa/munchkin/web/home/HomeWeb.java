package ivens.versao.casa.munchkin.web.home;

import ivens.versao.casa.munchkin.jogo.dto.VitoriaDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static ivens.versao.casa.munchkin.jogo.service.RegrasBasicas.MAXIMO_DE_BOTS;
import static ivens.versao.casa.munchkin.jogo.service.RegrasBasicas.MAXIMO_NIVEL_DE_VITORIA_PERSONALIZADA;
import static ivens.versao.casa.munchkin.jogo.service.RegrasBasicas.MINIMO_DE_BOTS;
import static ivens.versao.casa.munchkin.jogo.service.RegrasBasicas.MINIMO_DE_BOTS_COM_A_GALERA;
import static ivens.versao.casa.munchkin.jogo.service.RegrasBasicas.MINIMO_NIVEL_DE_VITORIA_PERSONALIZADA;
import static ivens.versao.casa.munchkin.jogo.service.RegrasBasicas.NIVEL_DE_VITORIA_CLASSICA;

@Controller
@RequiredArgsConstructor
public class HomeWeb {

    private final VitoriaDtoMapper vitoriaDtoMapper;

    @GetMapping({"/"})
    public String home(Model model) {
        model.addAttribute("minimoDeBots", MINIMO_DE_BOTS);
        model.addAttribute("minimoDeBotsComAGalera", MINIMO_DE_BOTS_COM_A_GALERA);
        model.addAttribute("maximoDeBots", MAXIMO_DE_BOTS);
        model.addAttribute("opcoesDeVitoria", vitoriaDtoMapper.opcoes());
        model.addAttribute("minimoNivelDeVitoria", MINIMO_NIVEL_DE_VITORIA_PERSONALIZADA);
        model.addAttribute("maximoNivelDeVitoria", MAXIMO_NIVEL_DE_VITORIA_PERSONALIZADA);
        // o campo da personalizada já vem com o nível da clássica, para o jogador só ajustar
        model.addAttribute("nivelDeVitoriaSugerido", NIVEL_DE_VITORIA_CLASSICA);
        return "home/home";
    }
}
