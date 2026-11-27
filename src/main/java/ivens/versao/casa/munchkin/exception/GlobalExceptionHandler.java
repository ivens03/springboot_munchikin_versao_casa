package ivens.versao.casa.munchkin.exception;

import ivens.versao.casa.munchkin.carta.exception.CartaNaoEncontradaException;
import ivens.versao.casa.munchkin.carta.exception.UsoDeCartaNaoPermitidoException;
import ivens.versao.casa.munchkin.jogo.exception.NivelDeVitoriaInvalidoException;
import ivens.versao.casa.munchkin.munchkin.exception.VendaDeOuroNaoPermitidaException;
import ivens.versao.casa.munchkin.partida.exception.AlvoInvalidoException;
import ivens.versao.casa.munchkin.partida.exception.CartaForaDaMaoException;
import ivens.versao.casa.munchkin.partida.exception.ForaDaHoraDeVenderOuroException;
import ivens.versao.casa.munchkin.partida.exception.OrdemDaMaoInvalidaException;
import ivens.versao.casa.munchkin.partida.exception.PartidaNaoEncontradaException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

/** Trata as exceções de todas as funcionalidades; os controllers não têm try/catch. */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Escolha inválida (nível de vitória, ordem da mão, venda de ouro, uso de carta): pedido inválido (400),
     * como os outros parâmetros fora do limite.
     */
    @ExceptionHandler({NivelDeVitoriaInvalidoException.class, OrdemDaMaoInvalidaException.class,
            VendaDeOuroNaoPermitidaException.class, ForaDaHoraDeVenderOuroException.class,
            UsoDeCartaNaoPermitidoException.class, CartaForaDaMaoException.class, AlvoInvalidoException.class})
    public void pedidoInvalido(RuntimeException excecao, HttpServletResponse resposta) throws IOException {
        resposta.sendError(HttpStatus.BAD_REQUEST.value(), excecao.getMessage());
    }

    /** Link de uma partida que não existe (ou que sumiu porque a aplicação reiniciou), ou carta fora do catálogo. */
    @ExceptionHandler({PartidaNaoEncontradaException.class, CartaNaoEncontradaException.class})
    public void naoEncontrado(RuntimeException excecao, HttpServletResponse resposta) throws IOException {
        resposta.sendError(HttpStatus.NOT_FOUND.value(), excecao.getMessage());
    }
}
