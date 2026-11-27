package ivens.versao.casa.munchkin.carta.service.nivel;

import ivens.versao.casa.munchkin.carta.exception.UsoDeCartaNaoPermitidoException;
import ivens.versao.casa.munchkin.carta.model.Carta;
import ivens.versao.casa.munchkin.carta.model.ImpedimentoDeUso;
import ivens.versao.casa.munchkin.carta.model.OpcaoDeNivel;
import ivens.versao.casa.munchkin.carta.model.TipoDeCarta;
import ivens.versao.casa.munchkin.carta.repository.CartaRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Cartas de subir de nível (tesouro/3_suba_de_nivel): escolhe a RegraDeSubaDeNivel (strategy) da carta
 * e confere o que vale para todas: a carta não dá o nível da vitória.
 * Quando pode usar (regra original): a qualquer momento, inclusive na preparação, respeitando o texto da carta.
 * A aplicação não sobe se alguma carta do grupo ficar sem regra.
 */
@Service
public class SubaDeNivelService {

    static final String GRUPO = "3_suba_de_nivel";

    private final Map<String, RegraDeSubaDeNivel> regras = new HashMap<>();

    public SubaDeNivelService(List<RegraDeSubaDeNivel> regras, CartaRepository cartaRepository) {
        for (RegraDeSubaDeNivel regra : regras) {
            for (String carta : regra.cartas()) {
                if (this.regras.put(carta, regra) != null) {
                    throw new IllegalStateException("Carta de nível com mais de uma regra: " + carta);
                }
            }
        }
        cartaRepository.listar(TipoDeCarta.TESOURO).stream()
                .filter(this::ehCartaDeNivel)
                .filter(carta -> !this.regras.containsKey(carta.chave()))
                .findFirst()
                .ifPresent(carta -> {
                    throw new IllegalStateException("Carta de nível sem regra: " + carta.chave());
                });
    }

    public boolean ehCartaDeNivel(Carta carta) {
        return carta.tipo() == TipoDeCarta.TESOURO && GRUPO.equals(carta.grupo());
    }

    public Optional<RegraDeSubaDeNivel> regraDe(Carta carta) {
        return ehCartaDeNivel(carta) ? Optional.ofNullable(regras.get(carta.chave())) : Optional.empty();
    }

    /** Por que não pode usar a carta com essa opção agora; vazio quando pode. */
    public Optional<ImpedimentoDeUso> impedimento(Carta carta, OpcaoDeNivel opcao, SituacaoDoJogador situacao) {
        Optional<RegraDeSubaDeNivel> regra = regraDe(carta);
        if (regra.isEmpty()) {
            return Optional.of(ImpedimentoDeUso.CARTA_SEM_USO);
        }
        if (!regra.get().opcoes().contains(opcao)) {
            return Optional.of(ImpedimentoDeUso.OPCAO_QUE_A_CARTA_NAO_TEM);
        }
        Optional<ImpedimentoDeUso> daCarta = regra.get().impedimentoDaCarta(situacao);
        if (daCarta.isPresent()) {
            return daCarta;
        }
        if (situacao.nivel() + regra.get().efeito(opcao).niveisDoJogador() >= situacao.nivelDeVitoria()) {
            return Optional.of(ImpedimentoDeUso.NIVEL_DA_VITORIA);
        }
        return Optional.empty();
    }

    /** As opções liberadas agora; sem nenhuma, diz o impedimento da primeira opção da carta. */
    public UsoDeCarta uso(Carta carta, SituacaoDoJogador situacao) {
        Optional<RegraDeSubaDeNivel> regra = regraDe(carta);
        if (regra.isEmpty()) {
            return UsoDeCarta.impedida(ImpedimentoDeUso.CARTA_SEM_USO);
        }
        List<OpcaoDeNivel> liberadas = regra.get().opcoes().stream()
                .filter(opcao -> impedimento(carta, opcao, situacao).isEmpty())
                .toList();
        if (liberadas.isEmpty()) {
            return UsoDeCarta.impedida(impedimento(carta, regra.get().opcoes().getFirst(), situacao).orElseThrow());
        }
        return new UsoDeCarta(liberadas, regra.get().precisaDeAlvo(), null);
    }

    /** Confere se pode e diz o que a carta faz; quem aplica na partida é o PartidaService. */
    public EfeitoDeNivel usar(Carta carta, OpcaoDeNivel opcao, SituacaoDoJogador situacao) {
        impedimento(carta, opcao, situacao).ifPresent(impedimento -> {
            throw new UsoDeCartaNaoPermitidoException(carta.chave(), impedimento);
        });
        return regraDe(carta).orElseThrow().efeito(opcao);
    }

    public boolean precisaDeAlvo(Carta carta) {
        return regraDe(carta).map(RegraDeSubaDeNivel::precisaDeAlvo).orElse(false);
    }
}
