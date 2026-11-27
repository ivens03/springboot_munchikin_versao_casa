/*
 * Organizar a mão: arrastando uma carta para o lado (mouse ou dedo), ela troca de lugar no leque.
 * Ao soltar, a ordem nova vai para o servidor (data-ordenar da .mao): as posições antigas das cartas
 * (data-posicao), na ordem em que ficaram. Se o servidor recusar, a página recarrega com a ordem dele.
 * Arrastando para cima, até a mesa, a carta volta para o lugar dela no leque e, solta lá, fica na mesa
 * (carta--solta) e dispara o evento "carta-na-mesa": o usar_carta.js joga a carta ou abre a janela de uso.
 * Um clique sem arrastar não mexe em nada.
 */
(() => {
    // quanto o mouse anda antes de virar arrasto (um clique com a mão tremendo não muda a ordem)
    const DISTANCIA_PARA_ARRASTAR = 6;

    const mao = document.querySelector('.mao[data-ordenar]');
    if (!mao) {
        return;
    }
    const mesa = mao.closest('.mesa');

    let arrasto = null;
    // um envio de cada vez, na ordem: cada ordem nova parte da que o servidor já tem
    let envios = Promise.resolve();

    const cartas = () => [...mao.querySelectorAll(':scope > .carta')];

    function maisPerto(lugares, x) {
        return lugares.reduce((melhor, lugar, indice) =>
            Math.abs(lugar - x) < Math.abs(lugares[melhor] - x) ? indice : melhor, 0);
    }

    function moverPara(carta, lugar) {
        if (lugar === cartas().indexOf(carta)) {
            return;
        }
        const outras = cartas().filter(outra => outra !== carta);
        mao.insertBefore(carta, outras[lugar] ?? null);
        cartas().forEach((outra, indice) => outra.style.setProperty('--i', indice));
    }

    mao.addEventListener('pointerdown', evento => {
        const carta = evento.target.closest('.carta');
        if (!carta || evento.button !== 0 || mesa.classList.contains('mesa--comprando')) {
            return;
        }
        arrasto = {carta, inicioX: evento.clientX, inicioY: evento.clientY, ativo: false, naMesa: false};
        carta.setPointerCapture(evento.pointerId);
    });

    mao.addEventListener('pointermove', evento => {
        if (!arrasto) {
            return;
        }
        const {carta} = arrasto;
        if (!arrasto.ativo) {
            if (Math.abs(evento.clientX - arrasto.inicioX) < DISTANCIA_PARA_ARRASTAR
                && Math.abs(evento.clientY - arrasto.inicioY) < DISTANCIA_PARA_ARRASTAR) {
                return;
            }
            arrasto.ativo = true;
            const caixas = cartas().map(outra => outra.getBoundingClientRect());
            // centro de cada lugar do leque quando o arrasto começa: a carta vai para o lugar mais perto do mouse
            arrasto.lugares = caixas.map(caixa => caixa.left + caixa.width / 2);
            // acima do topo das outras cartas é a mesa (a arrastada está levantada, não conta)
            const outras = caixas.filter((caixa, indice) => cartas()[indice] !== carta);
            arrasto.topoDoLeque = Math.min(...(outras.length ? outras : caixas).map(caixa => caixa.top));
            arrasto.lugarInicial = cartas().indexOf(carta);
            carta.classList.add('carta--arrastando');
        }
        carta.style.setProperty('--puxada', `${Math.min(0, evento.clientY - arrasto.inicioY)}px`);
        arrasto.naMesa = evento.clientY < arrasto.topoDoLeque;
        carta.style.setProperty('--puxada-x', `${arrasto.naMesa ? evento.clientX - arrasto.inicioX : 0}px`);
        mesa.classList.toggle('mesa--soltando-carta', arrasto.naMesa);
        // na mesa a carta não muda de lugar no leque: ela vai ser usada
        moverPara(carta, arrasto.naMesa ? arrasto.lugarInicial : maisPerto(arrasto.lugares, evento.clientX));
    });

    function soltar() {
        if (!arrasto) {
            return;
        }
        const {carta, ativo, naMesa} = arrasto;
        arrasto = null;
        carta.classList.remove('carta--arrastando');
        mesa.classList.remove('mesa--soltando-carta');
        if (naMesa) {
            // fica onde caiu (mesma posição puxada) até ser jogada; o usar_carta.js devolve se não for
            carta.classList.add('carta--solta');
        } else {
            carta.style.removeProperty('--puxada');
            carta.style.removeProperty('--puxada-x');
        }
        if (!ativo) {
            return;
        }
        if (naMesa) {
            // "envios": a janela de uso espera as ordens que ainda estão indo (a posição da carta é a da ordem nova)
            carta.dispatchEvent(new CustomEvent('carta-na-mesa', {bubbles: true, detail: {carta, envios}}));
            return;
        }
        const ordem = cartas().map(outra => Number(outra.dataset.posicao));
        if (ordem.every((posicao, indice) => posicao === indice)) {
            return;
        }
        // a partir de agora a posição de cada carta é a da ordem nova
        cartas().forEach((outra, indice) => outra.dataset.posicao = indice);
        envios = envios.then(() => enviar(ordem));
    }

    async function enviar(ordem) {
        const corpo = new URLSearchParams();
        ordem.forEach(posicao => corpo.append('ordem', posicao));
        const resposta = await fetch(mao.dataset.ordenar, {method: 'POST', body: corpo}).catch(() => null);
        if (!resposta?.ok) {
            location.reload();
        }
    }

    mao.addEventListener('pointerup', soltar);
    mao.addEventListener('pointercancel', soltar);
})();
