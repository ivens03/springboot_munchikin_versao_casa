package ivens.versao.casa.munchkin.munchkin.service.fuga;

import ivens.versao.casa.munchkin.munchkin.model.Munchkin;
import ivens.versao.casa.munchkin.jogo.service.Dado;
import ivens.versao.casa.munchkin.munchkin.service.MunchkinService;
import ivens.versao.casa.munchkin.munchkin.service.raca.RacaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static ivens.versao.casa.munchkin.jogo.service.RegrasBasicas.MINIMO_NO_DADO_PARA_FUGIR;

@Service
@RequiredArgsConstructor
public class FugaService {

    private final MunchkinService munchkinService;
    private final RacaService racaService;
    private final Dado dado;

    @Transactional(readOnly = true)
    public ResultadoDaFuga tentarFugir(Long munchkinId, int nivelDoMonstro) {
        Munchkin munchkin = munchkinService.buscar(munchkinId);
        int bonus = racaService.regraDe(munchkin.getRaca()).bonusParaFugir(nivelDoMonstro);
        int resultado = dado.rolar();
        return new ResultadoDaFuga(resultado, bonus, resultado + bonus >= MINIMO_NO_DADO_PARA_FUGIR);
    }
}
