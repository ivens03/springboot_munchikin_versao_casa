package ivens.versao.casa.munchkin.carta.exception;

import ivens.versao.casa.munchkin.carta.model.ImpedimentoDeUso;
import lombok.Getter;

/** Usar uma carta (ou uma opção dela) que não pode ser usada agora. */
@Getter
public class UsoDeCartaNaoPermitidoException extends RuntimeException {

    private final ImpedimentoDeUso impedimento;

    public UsoDeCartaNaoPermitidoException(String carta, ImpedimentoDeUso impedimento) {
        super("Não pode usar a carta " + carta + ": " + impedimento.getTexto());
        this.impedimento = impedimento;
    }
}
