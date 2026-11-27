package ivens.versao.casa.munchkin.carta.dto;

import ivens.versao.casa.munchkin.carta.model.Carta;
import ivens.versao.casa.munchkin.carta.model.TipoDeCarta;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartaDtoMapper {

    private static final String PASTA_DAS_CARTAS = "/img/cartas/";

    public CartaDto paraDto(Carta carta) {
        return new CartaDto(carta.tipo().getPasta(), nome(carta), PASTA_DAS_CARTAS + carta.imagem(), verso(carta));
    }

    public List<CartaDto> paraDto(List<Carta> cartas) {
        return cartas.stream().map(this::paraDto).toList();
    }

    /** Mão do oponente: só os versos e a contagem por pilha, sem a frente das cartas. */
    public MaoFechadaDto paraMaoFechada(List<Carta> cartas) {
        List<CartaFechadaDto> versos = cartas.stream()
                .map(carta -> new CartaFechadaDto(carta.tipo().getPasta(), verso(carta)))
                .toList();
        return new MaoFechadaDto(versos, quantas(cartas, TipoDeCarta.MASMORRA), quantas(cartas, TipoDeCarta.TESOURO));
    }

    private static String verso(Carta carta) {
        return PASTA_DAS_CARTAS + "traseira/verso_" + carta.tipo().getPasta() + ".jpg";
    }

    private static int quantas(List<Carta> cartas, TipoDeCarta tipo) {
        return (int) cartas.stream().filter(carta -> carta.tipo() == tipo).count();
    }

    /** "masmorra/5_racas/homem_lagarto_2_masmorra.jpg" → "Homem lagarto" */
    public String nome(Carta carta) {
        String nome = carta.chave().replace('_', ' ');
        return Character.toUpperCase(nome.charAt(0)) + nome.substring(1);
    }
}
