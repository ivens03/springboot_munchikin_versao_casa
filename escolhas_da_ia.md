# Escolhas da IA (bots)

Como os bots do "jogar com a máquina" decidem o que fazer. Primeiro o que já está no código, depois as perguntas
em aberto para pensar com calma. Decisão tomada aqui também vai para o `learning.md`.

- Quem decide: `bot/service/BotService` (só escolhe). Quem aplica na partida: `PartidaService.jogadaDeBot`.
- Ritmo na tela: `static/js/jogar_com_maquina/bots_jogam.js`.
- Sorte sempre pelo `Dado` ou pelo `Embaralhador` (regra do AGENTS.md); `Math.random` só para ritmo e animação.

## O que já está valendo

### Cartas de subir de nível (rodada 0)

| Escolha | Como está | Onde mexer |
|---|---|---|
| Quando joga | Aos poucos na preparação: a cada 5–12 s, a mesa pergunta ao servidor | `bots_jogam.js` (`MINIMO_MS`, `VARIACAO_MS`) |
| Quem joga | Um bot sorteado (Embaralhador) entre os que têm carta liberada; um por vez | `PartidaService.jogadaDeBot` |
| Guardar carta | Dado 1 ou 2 = guarda desta vez (1 em 3). Só atrasa: com o tempo, usa todas | `BotService.GUARDA_A_CARTA_ATE` |
| Qual carta | A primeira liberada da mão; Contemplar seu Umbigo fica por último | `BotService.escolherCartaDeNivel` |
| Contemplar seu Umbigo | +2 (descartando a mão) só quando não tem outra carta de nível liberada; senão +1 | `BotService.escolherCartaDeNivel` |
| Roubar 1 Nível | De quem tem o maior nível, você incluído; empate sorteado (Embaralhador) | `BotService.escolherAlvo` |
| Carta impedida | Não usa (Choramingar e Trocar a Ficha com o maior nível, as que esperam combate etc.) | `SubaDeNivelService` |
| Depois da preparação | Não joga ainda (falta o turno) | `PartidaService.jogadaDeBot` |
| Esperar o jogador | Não joga nem recarrega com o jogador arrastando carta ou com janela ou painel aberto | `bots_jogam.js` (`ocupado`) |

### Outras

- **OK da preparação**: o bot dá na hora (não precisa organizar a mão).
- **"Olha se eu quiser"**: a cada 5–12 s um bot puxa uma carta para o centro e recolhe. É só visual, sem regra.

## Para pensar depois

### Cartas de nível

1. **Contemplar com a mão cheia.** Hoje o bot pode jogar fora 10 cartas para subir 2. Pôr um limite (ex.: só com
   até N cartas na mão)? Ou nunca descartar quando a mão tem carta boa (item, monstro)?
2. **Guardar de verdade.** Hoje "guardar" só atrasa. Ele deveria segurar alguma para depois da preparação (ex.: para
   usar quando alguém passar à frente)?
3. **Chance de guardar.** 1 em 3 está bom? Mudar por bot (um mais apressado, outro mais calculista)?
4. **Prioridade entre as cartas.** Usar o Roubar antes das comuns, já que ele sobe 1 e ainda derruba um adversário?
5. **De quem roubar.** O maior nível está bom? Ou o bot deveria preferir você, ou evitar os outros bots
   ("aliança das máquinas")?
6. **Ritmo.** 5–12 s e um bot por vez está bom com 10 bots? Com muitos bots, jogar mais rápido?

### Ouro

7. **Bot vende ouro por nível?** Eles começam com 400, então ainda não dá. Quando tiverem 1000 (tesouro, itens
   vendidos), vendem na hora ou guardam?

### Jeito de cada bot

8. **Dificuldade.** Fácil, normal ou difícil na home (quanto guarda, quanto mira em você, se erra de propósito)?
9. **Personalidade por bot.** Ex.: agressivo (rouba sempre o líder), econômico (guarda ouro), caótico (sorteia
   tudo). Seria um Strategy por personalidade em `bot/service/`.

### Quando existir turno e combate

10. Na vez dele: chutar a porta, procurar encrenca (monstro da própria mão) ou saquear a sala?
11. Lutar ou fugir: quando o bot acha que ganha (poder de combate contra o monstro, com margem)?
12. Pedir ajuda: a quem, e quanto tesouro oferece?
13. Ajudar os outros: ajuda quem está atrás e atrapalha quem está perto de vencer?
14. Botar monstro, maldição ou bônus no combate dos outros: em quem (o líder? você?) e quando?
15. Equipar itens, trocar raça ou classe e respeitar o limite da mão (o que dá para a caridade e para quem).

### Mesa e tela

16. Mostrar no aviso o motivo da escolha do bot (ex.: "roubou de você porque você está na frente")?
17. Guardar um histórico das jogadas dos bots na mesa (hoje só o último aviso aparece)?
