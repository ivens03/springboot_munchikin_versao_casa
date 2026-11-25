# Munchkin da Casa

Versão caseira do jogo de cartas Munchkin, para jogar com os amigos. As regras são nossas (adaptadas do Munchkin original).

## Stack

- Java 25, Spring Boot 4.2 (Web MVC, Data JPA, Validation), Lombok
- Front-end: Thymeleaf (renderizado no servidor, sem SPA)
- Banco: PostgreSQL 18 via `compose.yaml`
- Build: Maven (`./mvnw`)

## Arquitetura

Monólito em camadas. Pacote base: `ivens.versao.casa.munchkin`.

```
model       → entidades JPA e enums
dto         → DTOs de entrada/saída das telas e os DtoMappers (model ↔ dto)
repository  → acesso a dados (Spring Data JPA)
service     → regras do jogo e de negócio (toda lógica fica aqui)
controller  → recebe a requisição, valida a entrada, devolve a view Thymeleaf
exception/
  GlobalExceptionHandler  → @ControllerAdvice, trata todas as exceções
  exceptions/             → exceções do jogo (ex.: JogadorNaoEncontradoException)
```

Regras:
- Fluxo: controller → service → repository. Nunca controller → repository.
- Controller não tem regra de jogo. Model não vai direto para a view: converter com o DtoMapper.
- Transação (`@Transactional`) fica no service.
- Service lança exceção de `exception/exceptions`; quem trata é o `GlobalExceptionHandler`. Nada de try/catch no controller.
- Templates em `src/main/resources/templates/`, CSS/JS/imagens em `src/main/resources/static/`.

## Comandos

```bash
docker compose up -d       # sobe o Postgres
./mvnw spring-boot:run     # roda a aplicação
./mvnw test                # testes
```

## Testes

Dois tipos, os dois automatizados e rodando no `./mvnw test`:

- **Código** (JUnit 5 + Mockito): teste unitário de todo service e DtoMapper; `@WebMvcTest` nos controllers.
- **Tela** (comparação de pixels): sobe a app (`@SpringBootTest(webEnvironment = RANDOM_PORT)`), tira print de cada tela com o Chrome headless (`google-chrome --headless --screenshot`) e compara pixel a pixel com a imagem base. Não valida campos no código.

Teste de tela:
- Imagens base em `src/test/resources/telas/<tela>.png`; o diff gerado em caso de falha vai para `target/telas/`.
- Tamanho de janela fixo e dados fixos (seed de teste), para o print não variar.
- Tolerância pequena de pixels diferentes, por causa do antialiasing.
- Imagem base só é atualizada de propósito: `./mvnw test -Dtelas.atualizar=true`.

Playwright (MCP) não faz parte da suíte: só é usado quando o usuário pedir criação ou alteração de tela, para ver e validar a tela no navegador. Depois de validada, atualizar a imagem base.

Regras:
- Toda regra de jogo nova tem teste de código.
- Toda tela nova ou alterada tem imagem base e teste de tela.
- Testes de tela em `src/test/java/.../tela/`; os de código espelham o pacote da classe testada.

## Convenções

- Código, nomes de classes e mensagens em português (ex.: `Jogador`, `Carta`, `PartidaService`).
- Injeção por construtor (`@RequiredArgsConstructor`).
- Antes de criar classe/método novo, verificar se já existe algo para reusar.
- Não commitar sem o usuário pedir.
