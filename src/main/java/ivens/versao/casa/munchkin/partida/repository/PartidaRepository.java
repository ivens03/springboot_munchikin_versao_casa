package ivens.versao.casa.munchkin.partida.repository;

import ivens.versao.casa.munchkin.partida.model.Partida;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Partidas em memória: ainda não está decidido se o jogo vai usar o banco.
 * Reiniciar a aplicação apaga as partidas. Se for para o banco, só esta classe muda.
 */
@Repository
public class PartidaRepository {

    private final Map<UUID, Partida> partidas = new ConcurrentHashMap<>();

    public Partida salvar(Partida partida) {
        partidas.put(partida.getId(), partida);
        return partida;
    }

    public Optional<Partida> buscar(UUID id) {
        return Optional.ofNullable(partidas.get(id));
    }
}
