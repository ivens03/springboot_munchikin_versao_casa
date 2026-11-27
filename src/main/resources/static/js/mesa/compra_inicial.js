/*
 * Animação da compra inicial: cada carta sai da pilha dela (Masmorra ou Tesouro, deitadas)
 * e voa até o seu lugar no leque. As do jogador viram no caminho e mostram a frente; as dos oponentes
 * chegam de costas. A distribuição é em rodadas, como na mesa de verdade: uma carta para cada um,
 * a começar pelo jogador e seguindo pelos oponentes, na ordem em que vieram do servidor
 * (5 de Tesouro e depois 5 de Masmorra).
 * As cartas já estão no HTML na posição final; quem voa é uma cópia, que some quando chega.
 * Quando a última carta chega, aparece o aviso do nível que vence, que some sozinho depois de uns segundos.
 * Só roda logo depois de criar a partida (a <main> vem com data-distribuindo); num F5 as cartas já estão na mão.
 * Enquanto distribui (.mesa--comprando) e enquanto o aviso aparece (.mesa--anunciando), a rodada fica escondida.
 */
(() => {
    // na imagem pilha_*.png a carta do topo começa em (margem, margem) e ocupa a largura menos as margens
    const MARGEM_DA_PILHA = 24;
    const DURACAO_DO_VOO = 700;
    const INTERVALO_ENTRE_RODADAS = 450;

    const mesa = document.querySelector('.mesa');
    const leques = [...document.querySelectorAll('.mao, .oponente__cartas')]
        .map(leque => [...leque.querySelectorAll('.carta')]);

    if (!mesa.hasAttribute('data-distribuindo') || window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
        return;
    }
    mesa.classList.add('mesa--comprando');

    function cartaDoTopo(pilha) {
        const caixa = pilha.getBoundingClientRect();
        const escala = caixa.width / pilha.naturalWidth;
        const largura = (pilha.naturalWidth - 2 * MARGEM_DA_PILHA) * escala;
        const deitada = pilha.classList.contains('pilha--deitada');
        // largura da carta em pé: na pilha deitada é a altura da carta (proporção 657 × 1019)
        const larguraEmPe = deitada ? largura * 657 / 1019 : largura;
        return {
            centroX: caixa.left + MARGEM_DA_PILHA * escala + largura / 2,
            centroY: caixa.top + MARGEM_DA_PILHA * escala + (deitada ? larguraEmPe : largura * 1019 / 657) / 2,
            larguraEmPe,
            giro: deitada ? 90 : 0
        };
    }

    // onde a carta fica na tela: centro, giro e tamanho somando o transform dela e o dos pais (leque, assento)
    function lugarNaMesa(carta) {
        const caixa = carta.getBoundingClientRect();
        let giro = 0;
        let escala = 1;
        for (let elemento = carta; elemento && elemento !== mesa; elemento = elemento.parentElement) {
            const transform = getComputedStyle(elemento).transform;
            if (transform !== 'none') {
                const matriz = new DOMMatrix(transform);
                giro += Math.atan2(matriz.b, matriz.a) * 180 / Math.PI;
                escala *= Math.hypot(matriz.a, matriz.b);
            }
        }
        return {
            centroX: caixa.left + caixa.width / 2,
            centroY: caixa.top + caixa.height / 2,
            giro,
            largura: carta.offsetWidth * escala,
            altura: carta.offsetHeight * escala
        };
    }

    // giro de saída mais perto do de chegada (sem dar volta inteira no ar)
    function giroMaisPerto(giro, alvo) {
        return giro + 360 * Math.round((alvo - giro) / 360);
    }

    function anunciarVitoria() {
        const vitoria = document.querySelector('.vitoria');
        if (!vitoria) {
            return;
        }
        mesa.classList.add('mesa--anunciando');
        vitoria.addEventListener('animationend', () => {
            vitoria.remove();
            mesa.classList.remove('mesa--anunciando');
        }, {once: true});
        vitoria.classList.add('vitoria--anunciada');
    }

    // devolve a promessa do voo, que resolve quando a carta chega no lugar dela
    function comprar(carta) {
        const topo = cartaDoTopo(document.querySelector(`.pilha[data-pilha="${carta.dataset.pilha}"]`));
        const lugar = lugarNaMesa(carta);

        const voo = document.createElement('div');
        voo.className = 'voo';
        Object.assign(voo.style, {
            left: `${lugar.centroX - lugar.largura / 2}px`,
            top: `${lugar.centroY - lugar.altura / 2}px`,
            width: `${lugar.largura}px`,
            height: `${lugar.altura}px`
        });
        const interior = carta.querySelector('.carta__interior').cloneNode(true);
        voo.append(interior);
        mesa.append(voo);

        const saida = `translate(${topo.centroX - lugar.centroX}px, ${topo.centroY - lugar.centroY}px) `
            + `rotate(${giroMaisPerto(topo.giro, lugar.giro)}deg) scale(${topo.larguraEmPe / lugar.largura})`;
        const chegada = `rotate(${lugar.giro}deg)`;

        const chegou = voo.animate([{transform: saida}, {transform: chegada}],
            {duration: DURACAO_DO_VOO, easing: 'cubic-bezier(.2, .7, .3, 1)'})
            .finished.then(() => {
                carta.classList.add('carta--comprada');
                voo.remove();
            });

        // carta do jogador: sai de costas e vira no caminho
        if (interior.querySelector('.carta__frente')) {
            interior.animate([
                {transform: 'rotateY(180deg)'},
                {transform: 'rotateY(180deg)', offset: 0.35},
                {transform: 'rotateY(0deg)'}
            ], {duration: DURACAO_DO_VOO, easing: 'ease-in-out'});
        }
        return chegou;
    }

    window.addEventListener('load', () => {
        const rodadas = Math.max(...leques.map(cartas => cartas.length));
        const intervaloNaRodada = INTERVALO_ENTRE_RODADAS / leques.length;
        const voos = [];
        for (let rodada = 0; rodada < rodadas; rodada++) {
            leques.forEach((cartas, posicao) => {
                if (cartas[rodada]) {
                    voos.push(new Promise(chegou => setTimeout(() => comprar(cartas[rodada]).then(chegou),
                        rodada * INTERVALO_ENTRE_RODADAS + posicao * intervaloNaRodada)));
                }
            });
        }
        Promise.all(voos).then(() => {
            mesa.classList.remove('mesa--comprando');
            anunciarVitoria();
        });
    });
})();
