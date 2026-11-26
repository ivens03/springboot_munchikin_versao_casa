# Munchkin da Casa

Versão caseira do jogo de cartas Munchkin, para jogar com os amigos. As regras são nossas (adaptadas do Munchkin original).

## Stack

- Java 25, Spring Boot 4.2 (Web MVC, Data JPA, Validation), Lombok
- Front-end: Thymeleaf (renderizado no servidor, sem SPA)
- Banco: PostgreSQL 18 via `compose.yaml`
- Build: Maven (`./mvnw`)

## Arquitetura

Monólito em camadas, organizado em pastas por funcionalidade. Pacote base: `ivens.versao.casa.munchkin`.

```
<funcionalidade>/   → ex.: munchkin/ (depois carta/, monstro/, combate/...)
  model/            → entidades JPA e enums
  dto/              → DTOs de entrada/saída das telas e os DtoMappers (model ↔ dto)
  repository/       → acesso a dados (Spring Data JPA)
  service/          → regras do jogo e de negócio (toda lógica fica aqui); subpasta quando houver Strategy (ex.: service/raca/)
  web/              → controllers (endpoints): recebem a requisição, validam a entrada, devolvem a view Thymeleaf
  exception/        → exceções da funcionalidade (ex.: MunchkinNaoEncontradoException)
jogo/               → regras gerais do jogo, que não são de uma funcionalidade (RegrasBasicas, Dado)
web/                → telas gerais, que não são de uma funcionalidade (ex.: HomeWeb)
config/             → configuração de infraestrutura (segurança, log e afins)
exception/GlobalExceptionHandler → @ControllerAdvice único, trata as exceções de todas as funcionalidades
```

Camadas (cada uma só fala com a de baixo):

```
apresentação → web/ + dto/ + templates/ + static/   (o que o usuário acessa)
negócio      → service/
dados        → repository/
model/       → entidades usadas pelas camadas
```

A camada de apresentação não é só o pacote `web`: os templates (views) e os estáticos ficam em `resources/` por não serem Java, mas fazem parte dela. Template só é acessado via controller; o que está em `static/` é servido direto pela URL.

Regras:
- Fluxo: web (controller) → service → repository. Nunca controller → repository.
- Uma funcionalidade usa outra pelo service (ex.: `FugaService` → `RacaService`), nunca pelo repository dela.
- Controller não tem regra de jogo. Model não vai direto para a view: converter com o DtoMapper.
- Transação (`@Transactional`) fica no service.
- Service lança exceção do `exception/` da funcionalidade; quem trata é o `GlobalExceptionHandler`. Nada de try/catch no controller.
- Templates em `src/main/resources/templates/`, CSS/JS/imagens em `src/main/resources/static/`.
- Mesa compartilhada: o que é igual em todo modo de jogo (com a máquina e com a galera) fica em `templates/mesa/mesa.html` (fragmentos), `static/css/mesa/` e `static/js/mesa/`. A tela do modo só monta a página com `th:replace` e guarda o que é só dela. O modo com a máquina serve para achar os bugs antes do multiplayer: mecânica nova nasce na mesa, não na tela do modo.

## Regras do jogo

- Fonte: o texto das cartas em `static/img/cartas/`. O que não está na carta é regra geral: perguntar antes de assumir.
- `static/img/` está no `.gitignore`: o texto da habilidade vai no Javadoc da classe da regra.
- Regra que varia por tipo (raça, depois classe) usa Strategy: interface com a regra padrão nos métodos `default`, um `@Component` por tipo sobrescrevendo só o que muda, e um service que escolhe. A aplicação não sobe se faltar regra para algum tipo.
- Números das regras básicas (limite da mão, mínimo para fugir...) ficam em `jogo/service/RegrasBasicas`, nunca soltos no código.
- Sorte sempre pelo `Dado` ou pelo `Embaralhador` injetados (`jogo/service/`), nunca `Random` direto (o teste fixa o valor; a propriedade `jogo.semente` fixa o embaralhamento).

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
- Tamanho de janela fixo e dados fixos (`jogo.semente` no `@SpringBootTest` da tela), para o print não variar.
- O Chrome roda com `--force-prefers-reduced-motion`: tela com animação tem que respeitar `prefers-reduced-motion` e já mostrar o estado final.
- Tolerância pequena de pixels diferentes, por causa do antialiasing.
- Imagem base só é atualizada de propósito: `./mvnw test -Dtelas.atualizar=true`.

Playwright (MCP) não faz parte da suíte: só é usado quando o usuário pedir criação ou alteração de tela, para ver e validar a tela no navegador. Depois de validada, atualizar a imagem base.

Regras:
- Toda regra de jogo nova tem teste de código.
- Toda regra de tipo (raça, classe) tem seu próprio teste, com os casos de limite (ex.: nível 9/10/15/16).
- Toda tela nova ou alterada tem imagem base e teste de tela.
- Testes de tela em `src/test/java/.../tela/`; os de código espelham o pacote da classe testada.

## Convenções

- Código, nomes de classes e mensagens em português (ex.: `Jogador`, `Carta`, `PartidaService`).
- Injeção por construtor (`@RequiredArgsConstructor`).
- Antes de criar classe/método novo, verificar se já existe algo para reusar.
- Não commitar sem o usuário pedir.
