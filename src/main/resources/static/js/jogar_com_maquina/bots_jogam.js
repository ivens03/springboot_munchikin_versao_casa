/*
 * Com a máquina, na preparação: de tempos em tempos pergunta ao servidor se um bot usa uma carta de nível.
 * Quem decide se joga, qual carta e de quem rouba é o servidor (BotService, com o Dado); daqui só sai o ritmo
 * (Math.random no intervalo, como no bots_provocam.js). Quando um bot joga, ele puxa uma carta para o centro
 * (mesaProvocar) e a mesa recarrega com o aviso e os níveis novos.
 * Espera o jogador: não pergunta nem recarrega enquanto ele arrasta carta, está com a janela de uso ou o painel
 * de teste aberto, ou a ordem da mão ainda está indo para o servidor. Sem data-bots-jogam (fora da preparação),
 * não faz nada.
 */
(() => {
    const MINIMO_MS = 5000;
    const VARIACAO_MS = 7000;
    const PUXADA_MS = 1300; // metade da animação "provocar": a carta já está no centro
    const ESPERA_MS = 500;

    const mesa = document.querySelector('.mesa');
    const acao = mesa?.dataset.botsJogam;
    if (!acao) {
        return;
    }
    const sortear = quantos => Math.floor(Math.random() * quantos);
    const esperar = ms => new Promise(pronto => setTimeout(pronto, ms));
    const ocupado = () => document.hidden || mesa.classList.contains('mesa--comprando')
        || document.querySelector('.carta--arrastando, .carta--solta, .mao--enviando, dialog[open], details.teste[open]');

    async function quandoLivre() {
        while (ocupado()) {
            await esperar(ESPERA_MS);
        }
    }

    async function puxar(oponente) {
        const cartas = document.querySelectorAll('.oponente')[oponente]
            ?.querySelectorAll('.oponente__cartas .carta').length ?? 0;
        if (cartas && window.mesaProvocar?.(oponente, sortear(cartas))) {
            await esperar(PUXADA_MS);
        }
    }

    async function vez() {
        await quandoLivre();
        const resposta = await fetch(acao, {method: 'POST'}).catch(() => null);
        if (resposta?.status === 200) {
            await puxar((await resposta.json()).oponente);
            await quandoLivre();
            location.reload();
            return;
        }
        setTimeout(vez, MINIMO_MS + sortear(VARIACAO_MS));
    }

    setTimeout(vez, MINIMO_MS + sortear(VARIACAO_MS));
})();
