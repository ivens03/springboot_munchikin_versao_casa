/*
 * Com a máquina: de tempos em tempos um bot faz o "olha se eu quiser" (mesaProvocar, do provocar.js):
 * puxa uma carta para o centro e recolhe. É só para dar vida à mesa: não é jogada nem regra,
 * por isso o sorteio é daqui (Math.random) e não do Dado do servidor.
 * Não provoca durante a distribuição nem com a aba escondida.
 */
(() => {
    const MINIMO_MS = 5000;
    const VARIACAO_MS = 7000;

    const mesa = document.querySelector('.mesa');
    if (!mesa || !window.mesaProvocar || matchMedia('(prefers-reduced-motion: reduce)').matches) {
        return;
    }
    const sortear = quantos => Math.floor(Math.random() * quantos);

    function provocar() {
        const oponentes = [...document.querySelectorAll('.oponente')];
        if (oponentes.length && !document.hidden && !mesa.classList.contains('mesa--comprando')) {
            const oponente = sortear(oponentes.length);
            const cartas = oponentes[oponente].querySelectorAll('.oponente__cartas .carta').length;
            if (cartas) {
                mesaProvocar(oponente, sortear(cartas));
            }
        }
        setTimeout(provocar, MINIMO_MS + sortear(VARIACAO_MS));
    }

    setTimeout(provocar, MINIMO_MS + sortear(VARIACAO_MS));
})();
