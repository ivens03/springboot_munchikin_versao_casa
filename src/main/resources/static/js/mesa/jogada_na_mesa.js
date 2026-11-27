/*
 * Carta que acabou de ser jogada (fragmento jogadaNaMesa, só logo depois da jogada), para todo mundo entender o que
 * rolou:
 *  1. a carta sai de quem jogou (assento do oponente, ou a mão do jogador da tela) e vai para o centro da mesa;
 *  2. parada no centro, aparece o texto (quem usou, em quem, o que fez) e, em cima de cada um que mudou, o selo do
 *     efeito (ex.: "Nível 0 → 1"); a linha do placar dele pisca, e se mudou o jogador da tela, as infos dele aparecem;
 *  3. a carta voa para a pilha de descarte dela, que até ali mostrava a carta de baixo e a contagem de antes.
 * Enquanto a jogada está na mesa (.jogada existe), os bots esperam para jogar (bots_jogam.js).
 * Sem animação (prefers-reduced-motion): a carta, o texto e os selos ficam parados uns segundos e somem.
 */
(() => {
    const CHEGADA_MS = 700;
    const ATE_O_EFEITO_MS = 400;
    const PARADA_MS = 3400;
    const VOO_MS = 750;
    const SAIDA_MS = 500;
    const PARADA_SEM_ANIMACAO_MS = 6000;

    const jogada = document.querySelector('.jogada');
    if (!jogada) {
        return;
    }
    const mesa = document.querySelector('.mesa');
    const carta = jogada.querySelector('.jogada__carta');
    const efeitos = [...jogada.querySelectorAll('.jogada__efeito')];
    const descarte = document.querySelector(`.descarte[data-pilha="${jogada.dataset.pilha}"]`);
    const total = descarte?.querySelector('.descarte__total');
    const semAnimacao = matchMedia('(prefers-reduced-motion: reduce)').matches;
    const esperar = ms => new Promise(pronto => setTimeout(pronto, ms));
    const oponente = posicao => document.querySelectorAll('.oponente')[posicao];

    function centro(elemento) {
        const caixa = elemento.getBoundingClientRect();
        return {x: caixa.left + caixa.width / 2, y: caixa.top + caixa.height / 2, largura: caixa.width, giro: 0};
    }

    // a vaga do descarte deitado (Masmorra, Tesouro) é larga: a carta chega girada, com a largura dela na altura da vaga
    function vagaDoDescarte() {
        const vaga = centro(descarte.querySelector('.descarte__vaga'));
        if (descarte.classList.contains('descarte--deitado')) {
            vaga.largura = descarte.querySelector('.descarte__vaga').getBoundingClientRect().height;
            vaga.giro = 90;
        }
        return vaga;
    }

    // de onde a carta sai: o assento de quem jogou, ou o meio da mão do jogador da tela
    function origem() {
        const posicao = jogada.dataset.oponente;
        const lugar = posicao === undefined ? document.querySelector('.mao')
            : oponente(posicao)?.querySelector('.oponente__assento');
        return lugar ? centro(lugar) : null;
    }

    // ida (ou volta) entre o centro da mesa e um lugar, com a carta no tamanho do lugar
    function ate(lugar) {
        const aqui = centro(carta);
        return `translate(${lugar.x - aqui.x}px, ${lugar.y - aqui.y}px) rotate(${lugar.giro}deg) `
            + `scale(${lugar.largura / aqui.largura})`;
    }

    // cada selo em cima de quem mudou: o assento do oponente, ou as infos do jogador da tela
    function posicionarEfeitos() {
        efeitos.forEach(efeito => {
            const posicao = efeito.dataset.oponente;
            const lugar = posicao === undefined ? document.querySelector('.jogador')
                : oponente(posicao)?.querySelector('.oponente__assento');
            if (!lugar) {
                return;
            }
            const caixa = lugar.getBoundingClientRect();
            efeito.style.left = `${caixa.left + caixa.width / 2}px`;
            efeito.style.top = `${posicao === undefined ? caixa.top : caixa.top + caixa.height / 2}px`;
            if (posicao !== undefined) {
                document.querySelectorAll('.placar__linha')[posicao]?.classList.add('placar__linha--mudou');
            }
        });
    }

    // as infos do jogador da tela aparecem logo, para o selo achar o lugar delas
    function subirInfosSeMudouOJogador() {
        if (efeitos.some(efeito => efeito.dataset.oponente === undefined)) {
            mesa.classList.add('mesa--efeito-no-jogador');
        }
    }

    function mostrarEfeitos() {
        posicionarEfeitos();
        jogada.classList.add('jogada--efeito');
    }

    function acabar() {
        jogada.remove();
        mesa.classList.remove('mesa--efeito-no-jogador');
        document.querySelectorAll('.placar__linha--mudou').forEach(linha => linha.classList.remove('placar__linha--mudou'));
    }

    async function semMovimento() {
        jogada.classList.add('jogada--parada');
        subirInfosSeMudouOJogador();
        mostrarEfeitos();
        await esperar(PARADA_SEM_ANIMACAO_MS);
        acabar();
    }

    async function animar() {
        // o descarte mostra o de antes até a carta chegar lá
        descarte?.classList.add('descarte--esperando');
        total && (total.textContent = Number(total.textContent) - 1);

        const saida = origem();
        subirInfosSeMudouOJogador();
        jogada.classList.add('jogada--parada');
        if (saida) {
            await carta.animate([{transform: ate(saida), opacity: 0.4}, {transform: 'none', opacity: 1}],
                {duration: CHEGADA_MS, easing: 'cubic-bezier(.2, .7, .3, 1)'}).finished;
        }
        await esperar(ATE_O_EFEITO_MS);
        mostrarEfeitos();
        await esperar(PARADA_MS);

        jogada.classList.remove('jogada--parada');
        if (descarte) {
            await carta.animate([{transform: 'none'}, {transform: ate(vagaDoDescarte())}],
                {duration: VOO_MS, easing: 'cubic-bezier(.5, 0, .3, 1)', fill: 'forwards'}).finished;
            carta.hidden = true;
            descarte.classList.remove('descarte--esperando');
            descarte.classList.add('descarte--chegou');
            total && (total.textContent = Number(total.textContent) + 1);
        }
        jogada.classList.remove('jogada--efeito');
        await esperar(SAIDA_MS);
        acabar();
    }

    (semAnimacao ? semMovimento : animar)();
})();
