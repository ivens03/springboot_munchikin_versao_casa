# Munchkin da Casa — regras e aprendizados

Caderno das regras da casa e das decisões combinadas nas conversas. Consultar antes de perguntar de novo
e registrar aqui cada regra ou decisão nova.

- Texto das cartas (habilidades de raça etc.) fica no Javadoc da classe da regra, não aqui.
- Números das regras ficam em `jogo/service/RegrasBasicas`; aqui fica o porquê e o combinado.
- Regra que não está na carta: perguntar antes de assumir, e anotar aqui a resposta.

## Regras da casa

### Nível
- O **nível pode ficar negativo, sem limite** (-1, -5, -20...). O poder de combate acompanha.
- **Orc**: a carta diz que ignora Maldição perdendo 1 nível "a não ser que já esteja no Nível 1". Na casa,
  como o nível pode ser negativo, ele **sempre pode pagar 1 nível** (do 0 vai para -1).

### Começo da partida
- Todo Munchkin começa no **nível 0** e com **400 de ouro**.
- Mão inicial: **5 cartas de Tesouro + 5 de Masmorra**, compradas em rodadas (uma para cada, começando pelo jogador).
- Com a máquina: **1 a 10 bots**. Com a galera: bots opcionais, **0 a 10**.

### Vitória
- **Clássica**: nível 10. **Super Munchkin**: nível 20. **Personalizada**: o grupo escolhe, de 1 a 100.

### Poder de combate
- **Poder de combate = 0 + nível** (no nível 1, é 0 + 1 = 1; no nível -2, é -2).
- Os bônus (itens equipados etc.) vão somar aqui quando existirem.

### Rodada 0 — Preparação
- É onde as cartas são distribuídas e cada um organiza a mão com calma.
- Só dá para **olhar e mudar a ordem** das cartas; nada entra em jogo.
- **Não valem** as regras de limite de cartas na mão, descarte e afins.
- Termina só quando **todos dão OK** (os bots dão na hora).
- Depois do OK aparece **"Que vença o melhor Munchkin!"**, que some em ~10 s. O jogo em si ainda não começa.
- Mudar a ordem da mão vale em qualquer rodada (só organiza, não muda regra).

### Ouro
- **1000 de ouro valem 1 nível**; com mais ouro, vende de novo (1 nível a cada 1000).
- **Não pode** comprar o nível da vitória (ex.: do 9 para o 10 na clássica): esse só vem matando monstro.
- Quando: **na preparação e na vez do jogador**.

### Cartas de subir de nível (tesouro/3_suba_de_nivel)
- Strategy em `carta/service/nivel/`: uma regra por texto de carta, a comum ("Suba +1 Nível") é o padrão.
- **Quando**: a qualquer momento (regra original), **inclusive na rodada 0**, respeitando o texto da carta.
- **Não dá o nível da vitória** (regra original), nem com o +2 do Contemplar nem roubando.
- **Choramingar com o Mestre** e **Trocar a Ficha**: quem tem o maior nível não usa, **mesmo empatado** — inclusive
  todos iguais (na rodada 0 ninguém usa). Decidido seguir a carta.
- Carta que não pode ser usada **explica o motivo específico** na janela (ex.: "Todos estão no mesmo nível, então
  você também tem o maior nível da mesa..."), nunca um genérico "o texto da carta não deixa".
- **Contemplar seu Umbigo**: +1, ou descarta o resto da mão e sobe 2.
- **Roubar 1 Nível**: escolhe um oponente; ele perde 1 (pode ficar negativo) e você ganha 1.
- **Só o +1 por enquanto**: Dar Dor de Cabeça / Dar uma Olhada na Aventura / Matar a Ânsia por Dados Novos /
  Reencarnar sem Morrer (procurar Raça/Classe/Poder/Mojo nos descartes) e Gesto de Carinho (pegar Item).
- **Ainda não usáveis** (dependem do que não existe): Há Males que Vêm para o Bem (Escudeiro/Montaria),
  Levar a Fama e Mutilar os Cadáveres (combate), Matar o Escudeiro (Escudeiro).
- Carta usada vai para o descarte da pilha dela (na partida, em memória).

### Bots usando cartas de nível (decidido com o usuário)
- **Quando**: aos poucos na rodada 0. A cada 5–12 s a mesa pergunta ao servidor; um bot sorteado, entre os que têm
  carta liberada, pode usar **uma**. Depois da preparação ainda não jogam (falta o turno).
- **Guarda algumas**: no Dado, 1 ou 2 = guarda para depois (1 vez em 3). Como a preparação dura até o OK, com o
  tempo ele acaba usando.
- **Contemplar seu Umbigo**: fica por último; descarta a mão e sobe 2 só quando o resto da mão não tem outra carta
  de nível liberada agora (mão de 10 cartas sem outra de nível vai toda para o descarte); senão sobe 1.
- **Roubar 1 Nível**: de quem tem o maior nível (inclusive você); empate sorteado pelo Embaralhador.
- Decisão em `bot/service/BotService` (só decide); quem aplica é o `PartidaService.jogadaDeBot`.
- Escolhas da IA (o que está valendo e as perguntas em aberto): `escolhas_da_ia.md`.

### Já existente antes deste caderno
- Limite de cartas na mão: 5 (Anão: 6), no fim do turno.
- Fugir: tirar 5 ou 6 no dado (bônus de fuga somam).
- Habilidades de raça: ver o Javadoc de cada `Regra<Raça>` em `munchkin/service/raca/regrasRacas/`.

## Telas (o que já foi combinado)
- **Pilhas** no lado esquerdo: Dungeon em pé; Masmorra em cima e Tesouro embaixo.
- **Placar dos inimigos** no lado direito: nome e nível de cada um, sempre à vista.
- **Infos do jogador** embaixo das próprias cartas, só com o mouse no leque (sobem com a mão):
  `Nome · Raça · Nível atual · Poder de combate · Ouro` e, quando puder, o botão de vender 1000 de ouro.
- **Inimigo**: mouse no assento mostra nome, nível, ouro e quantas cartas (de cada pilha) ele tem.
- **Aviso da vitória** ("Vitória no nível X", sem o nome do tipo): aparece quando termina a distribuição e some.
- **Painel da rodada 0** no centro da mesa, com o OK.
- **Usar carta**: arrastar a carta do leque para cima, até a mesa (aparece "Solte aqui para usar a carta").
  Soltando lá: carta com **uma opção só é jogada direto**, sem confirmação. A janela só abre quando há escolha
  (Contemplar: +1 ou +2; Roubar: de quem) ou para **explicar por que não pode**. Depois de usar, um aviso diz
  o que a carta fez e some em ~6 s.
- **Histórico das cartas jogadas**: coluna **vertical no canto de baixo, à esquerda** (ao lado da mão esquerda do
  jogador; é o único canto livre com 1 a 10 bots), com 11 quadrados. Do 1º ao 10º, as últimas 10 jogadas, a última
  em cima (miniatura da carta + quem jogou; vaga sem jogada fica tracejada); embaixo, o "Tudo".
  Mouse num quadrado mostra, ao lado, a rodada e o que fez ("Bot 1 usou Roubar 1 nivel em você: subiu do nível 0 para o 1 e
  você desceu do nível 0 para o -1."). O 11º ("Tudo") abre o histórico completo quase em tela cheia quando o mouse
  **fica parado** em cima (~0,4 s; passar rápido não abre) e fecha quando o mouse sai do painel.
  O aviso depois da jogada usa o mesmo texto. Com o histórico completo aberto, a mesa não recarrega.
  Na partida: `Partida.getHistorico()` (`JogadaDeCarta`); cada efeito é um record de `EfeitoDaJogada`
  (`MudancaDeNivel`, `MaoDescartada`), guardado com o valor da hora. O combate vai somar os efeitos dele ali.
- **Bot jogando**: o bot puxa a carta para o centro, a mesa recarrega e o aviso diz o que ele fez ("Bot 2 usou...",
  "...e você desceu para o nível -1"). Não pergunta nem recarrega enquanto o jogador arrasta carta, está com a
  janela de uso ou o painel de teste aberto (`static/js/jogar_com_maquina/bots_jogam.js`).
- **"Olha se eu quiser"**: o inimigo puxa uma carta (de costas) em direção ao centro e recolhe, sem mostrar qual.
  Peça da mesa: `mesaProvocar(oponente, carta)` (`static/js/mesa/provocar.js`). Com a máquina, os bots fazem isso
  sozinhos a cada 5–12 s (`static/js/jogar_com_maquina/bots_provocam.js`, sorteio visual com Math.random, não é
  regra); no multiplayer, vai disparar quando outro jogador arrastar carta para a mesa. Sem animação, não provoca.
- **Partida em memória** (`PartidaRepository`, um mapa), **não no banco**: ainda não se sabe se o jogo vai usar
  banco. Reiniciar a aplicação apaga as partidas.
- `/jogar-com-maquina?bots=N` cria a partida e redireciona para `/jogar-com-maquina/{id}` (F5 não redistribui).
- A animação da distribuição e a mensagem de início só aparecem logo depois de criar/dar OK (flash attribute).

## Ferramenta temporária de teste
- **Pegar carta (teste)**: botão no canto superior esquerdo da mesa; abre o catálogo por pilha e pasta e põe a carta
  clicada no fim da mão (vem do catálogo, não do baralho: pode repetir). Reabre no mesmo grupo.
- Liga com `jogo.teste.cartas=true` (no `application.properties` enquanto for temporário). Nos testes fica desligado
  por `src/test/resources/config/application.properties`; o teste do painel liga com `@TestPropertySource`.
- Para remover: pacote `web/cartas_de_teste/`, fragmento `cartasDeTeste` da mesa, CSS `.teste`, a propriedade e
  `PartidaService.darCartaDeTesteAoJogador`.

## Pendências
- **Vez do jogador** ainda não existe: por enquanto o ouro só se vende na preparação. Revisar quando entrar o turno.
- **Bônus no poder de combate**: entram quando existirem itens equipados.
- Javadoc do Gnomo fala em "Força de Combate" (texto da carta): trocar para "Poder de Combate"?
- Com `prefers-reduced-motion`, o aviso da vitória não aparece (estado final = escondido). Revisar se incomodar.

- Cartas de nível com parte pendente: procurar nos descartes (4 cartas), pegar Item (Gesto de Carinho),
  e as que esperam combate/Escudeiro/Montaria.
- Combate: registrar no histórico as cartas jogadas no combate (bônus, monstro a mais, maldição) como novos
  `EfeitoDaJogada`.
- Bots na vez deles (quando existir turno) e com as outras cartas.
- Multiplayer: mandar para os outros quando um jogador arrasta carta para a mesa (chamar `mesaProvocar`).
- Nome da carta vem do arquivo, sem acento ("Roubar 1 nivel", "Pocao..."): melhorar quando houver catálogo com nome.

## Aprendizados de trabalho
- Respostas curtas, em português; perguntar quando a regra for ambígua e dizer qual leitura foi usada.
- Tela nova ou alterada: validar no Playwright em **1280×720** (a janela às vezes volta para 1920) e só depois
  atualizar a imagem base (`./mvnw test -Dtelas.atualizar=true`).
- A app costuma rodar pelo IntelliJ na porta 8484, com devtools: mudança em `static/` ou `templates/` só chega
  depois de `./mvnw process-resources`.
- Thymeleaf: `a or b` dentro de `${...}` é SpEL e quebra com nulo; usar `${a} or ${b}`.
- Commits só quando pedido, agrupados por assunto, no estilo `feat - ...`, na `develop`.
- A pasta `.playwright-mcp/` não vai para o commit.
- Texto de carta: transcrever das imagens antes de codar a regra e perguntar o que for ambíguo.
- CSS: `:has()` dentro de outro `:has()` é inválido e o navegador descarta a regra INTEIRA (inclusive os outros
  seletores da lista). Para estado da tela (ex.: mão vazia), o servidor põe uma classe (`mao--vazia`).
- Infos do jogador: com a mão vazia ficam sempre à mostra (não há leque para passar o mouse).
- Comentário `<!-- -->` do template vai para o HTML (e pode bater em teste com `containsString`); para nota
  interna usar o do Thymeleaf `<!--/* ... */-->`.
- CSS: `transform` num ancestral prende o `position: fixed` dos filhos nele (o histórico completo sumia). Para
  centralizar quem tem filho fixed, usar `calc` no `top`, não `translate`.
- Playwright arrastando carta: passar o mouse na carta antes (o leque sobe no hover e muda a posição).
