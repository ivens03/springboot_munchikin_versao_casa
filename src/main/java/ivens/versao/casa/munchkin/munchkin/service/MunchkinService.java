package ivens.versao.casa.munchkin.munchkin.service;

import ivens.versao.casa.munchkin.munchkin.exception.MunchkinNaoEncontradoException;
import ivens.versao.casa.munchkin.munchkin.model.Munchkin;
import ivens.versao.casa.munchkin.munchkin.repository.MunchkinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MunchkinService {

    private final MunchkinRepository munchkinRepository;

    @Transactional
    public Munchkin criar(String nome) {
        return munchkinRepository.save(new Munchkin(nome));
    }

    @Transactional(readOnly = true)
    public Munchkin buscar(Long id) {
        return munchkinRepository.findById(id)
                .orElseThrow(() -> new MunchkinNaoEncontradoException(id));
    }
}
