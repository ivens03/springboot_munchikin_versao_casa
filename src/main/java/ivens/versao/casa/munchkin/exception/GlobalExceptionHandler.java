package ivens.versao.casa.munchkin.exception;

import ivens.versao.casa.munchkin.jogo.exception.NivelDeVitoriaInvalidoException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

/** Trata as exceções de todas as funcionalidades; os controllers não têm try/catch. */
@ControllerAdvice
public class GlobalExceptionHandler {

    /** Escolha inválida na montagem da mesa: pedido inválido (400), como os outros parâmetros fora do limite. */
    @ExceptionHandler(NivelDeVitoriaInvalidoException.class)
    public void pedidoInvalido(RuntimeException excecao, HttpServletResponse resposta) throws IOException {
        resposta.sendError(HttpStatus.BAD_REQUEST.value(), excecao.getMessage());
    }
}
