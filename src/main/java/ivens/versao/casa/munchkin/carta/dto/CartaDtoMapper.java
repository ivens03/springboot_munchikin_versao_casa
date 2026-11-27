package ivens.versao.casa.munchkin.carta.dto;

import ivens.versao.casa.munchkin.carta.model.Carta;
import ivens.versao.casa.munchkin.carta.model.TipoDeCarta;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * Catálogo separado por pilha e pasta, na ordem das pastas (1_, 2_... 10_); a cópia (_2, _3) aparece uma vez só.
     */
    public List<GrupoDeCartasDto> paraGrupos(List<Carta> cartas) {
        Map<String, List<CartaDoCatalogoDto>> grupos = new LinkedHashMap<>();
        cartas.stream()
                .collect(Collectors.toMap(carta -> carta.tipo() + "/" + carta.grupo() + "/" + carta.chave(),
                        carta -> carta, (primeira, copia) -> primeira, LinkedHashMap::new))
                .values().stream()
                .sorted(Comparator.comparing(Carta::tipo)
                        .thenComparingInt(carta -> ordemDoGrupo(carta.grupo()))
                        .thenComparing(this::nome))
                .forEach(carta -> grupos.computeIfAbsent(nomeDoGrupo(carta), grupo -> new ArrayList<>())
                        .add(new CartaDoCatalogoDto(carta.imagem(), nome(carta), PASTA_DAS_CARTAS + carta.imagem())));
        return grupos.entrySet().stream().map(grupo -> new GrupoDeCartasDto(grupo.getKey(), grupo.getValue())).toList();
    }

    /** "tesouro/3_suba_de_nivel/x_tesouro.jpg" → "Tesouro · Suba de nivel"; sem pasta → "Tesouro · Outras". */
    public String nomeDoGrupo(Carta carta) {
        String pasta = carta.grupo().replaceFirst("^\\d+_", "").replace('_', ' ');
        String grupo = pasta.isEmpty() ? "Outras" : Character.toUpperCase(pasta.charAt(0)) + pasta.substring(1);
        return carta.tipo().getNome() + " · " + grupo;
    }

    private static int ordemDoGrupo(String grupo) {
        String numero = grupo.replaceFirst("_.*$", "");
        return numero.matches("\\d+") ? Integer.parseInt(numero) : Integer.MAX_VALUE;
    }

    /** "masmorra/5_racas/homem_lagarto_2_masmorra.jpg" → "Homem lagarto" */
    public String nome(Carta carta) {
        String nome = carta.chave().replace('_', ' ');
        return Character.toUpperCase(nome.charAt(0)) + nome.substring(1);
    }
}
