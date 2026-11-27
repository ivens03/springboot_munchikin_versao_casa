package ivens.versao.casa.munchkin.munchkin.dto;

import ivens.versao.casa.munchkin.munchkin.model.Munchkin;
import ivens.versao.casa.munchkin.munchkin.model.Raca;
import org.springframework.stereotype.Component;

@Component
public class MunchkinDtoMapper {

    private static final String PASTA_DAS_MAOS = "/img/moes/";

    public MunchkinDto paraDto(Munchkin munchkin, int poderDeCombate) {
        String maos = PASTA_DAS_MAOS + imagemDasMaos(munchkin.getRaca());
        String costas = PASTA_DAS_MAOS + imagemDasCostasDasMaos(munchkin.getRaca());
        return new MunchkinDto(munchkin.getNome(), munchkin.getRaca().getNome(), munchkin.getNivel(), poderDeCombate,
                maos + "_esquerda.png", maos + "_direita.png",
                costas + "_esquerda.png", costas + "_direita.png");
    }

    /** Nome base das imagens das costas das mãos da raça (usadas nos oponentes). */
    String imagemDasCostasDasMaos(Raca raca) {
        // por enquanto só existe a do humano; as outras raças usam a mesma até ter imagem
        return "moes_costas_humano";
    }

    /** Nome base das imagens das mãos da raça (sem o lado e a extensão). */
    String imagemDasMaos(Raca raca) {
        return switch (raca) {
            case HUMANO -> "moes_humanas";
            case HALFLING -> "moes_halfling";
            case ELFO -> "moes_elfos";
            case ORC -> "moes_orcs";
            case HOMEM_LAGARTO -> "moes_homens_lagartos";
            case CENTAURO -> "moes_centauros";
            case ANAO -> "moes_anoes";
            case GNOMO -> "moes_humanas"; // ainda não existe imagem das mãos do gnomo
        };
    }
}
