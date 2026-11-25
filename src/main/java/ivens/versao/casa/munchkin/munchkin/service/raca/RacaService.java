package ivens.versao.casa.munchkin.munchkin.service.raca;

import ivens.versao.casa.munchkin.munchkin.model.Munchkin;
import ivens.versao.casa.munchkin.munchkin.model.Raca;
import ivens.versao.casa.munchkin.munchkin.service.MunchkinService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Responsável pelas regras de raça. Escolhe a RegraDeRaca (strategy) da raça do Munchkin.
 */
@Service
public class RacaService {

    private final MunchkinService munchkinService;
    private final Map<Raca, RegraDeRaca> regras = new EnumMap<>(Raca.class);

    public RacaService(MunchkinService munchkinService, List<RegraDeRaca> regras) {
        this.munchkinService = munchkinService;
        for (RegraDeRaca regra : regras) {
            if (this.regras.put(regra.raca(), regra) != null) {
                throw new IllegalStateException("Raça com mais de uma regra: " + regra.raca());
            }
        }
        for (Raca raca : Raca.values()) {
            if (!this.regras.containsKey(raca)) {
                throw new IllegalStateException("Raça sem regra: " + raca);
            }
        }
    }

    public RegraDeRaca regraDe(Raca raca) {
        return regras.get(raca);
    }

    @Transactional
    public Munchkin trocarRaca(Long munchkinId, Raca raca) {
        Munchkin munchkin = munchkinService.buscar(munchkinId);
        munchkin.setRaca(raca);
        return munchkin;
    }

    @Transactional
    public Munchkin perderRaca(Long munchkinId) {
        return trocarRaca(munchkinId, Raca.HUMANO);
    }
}
