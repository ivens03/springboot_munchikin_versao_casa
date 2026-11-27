/*
 * Carta solta na mesa (evento "carta-na-mesa" do organizar_mao.js):
 *  - com uma opção só e sem alvo, é jogada direto (sem janela);
 *  - com escolha (mais de uma opção ou um oponente a escolher), a janela mostra só as escolhas;
 *  - se não pode ser usada agora, a janela explica o motivo (data-impedimento) e a carta volta para a mão.
 * Jogar manda o formulário (POST): o servidor aplica a carta e volta para a mesa.
 */
(() => {
    const janela = document.querySelector('dialog.uso');
    if (!janela) {
        return;
    }
    const formulario = janela.querySelector('form');
    const imagem = janela.querySelector('.uso__carta');
    const titulo = janela.querySelector('.uso__titulo');
    const impedimento = janela.querySelector('.uso__impedimento');
    const alvo = janela.querySelector('.uso__alvo');
    const opcoes = [...janela.querySelectorAll('.uso__opcao')];
    let ordensPendentes = Promise.resolve();

    function devolver(carta) {
        carta.classList.remove('carta--solta');
        carta.style.removeProperty('--puxada');
        carta.style.removeProperty('--puxada-x');
    }

    async function jogar(opcao) {
        // a ordem da mão que ainda está indo para o servidor chega antes (a posição da carta é a da ordem nova)
        await ordensPendentes;
        // submit() não leva o botão clicado: a opção vai num campo escondido
        const campo = formulario.querySelector('input[name="opcao"]') ?? Object.assign(document.createElement('input'),
            {type: 'hidden', name: 'opcao'});
        campo.value = opcao;
        formulario.append(campo);
        opcoes.forEach(botao => botao.disabled = true);
        formulario.submit();
    }

    document.addEventListener('carta-na-mesa', evento => {
        const {carta, envios} = evento.detail;
        ordensPendentes = envios;
        formulario.elements.posicao.value = carta.dataset.posicao;
        const liberadas = (carta.dataset.opcoes ?? '').split(' ').filter(Boolean);
        const comAlvo = liberadas.length > 0 && carta.dataset.alvo === 'true';
        alvo.querySelector('select').disabled = !comAlvo;

        if (liberadas.length === 1 && !comAlvo) {
            jogar(liberadas[0]);
            return;
        }

        devolver(carta);
        const frente = carta.querySelector('.carta__frente');
        imagem.src = frente.src;
        imagem.alt = frente.alt;
        titulo.textContent = frente.alt;
        opcoes.forEach(botao => botao.hidden = !liberadas.includes(botao.value));
        alvo.hidden = !comAlvo;
        impedimento.hidden = liberadas.length > 0;
        impedimento.textContent = carta.dataset.impedimento ?? '';
        janela.showModal();
        (opcoes.find(botao => !botao.hidden) ?? janela.querySelector('.uso__cancelar')).focus();
    });

    formulario.addEventListener('submit', evento => {
        evento.preventDefault();
        jogar(evento.submitter.value);
    });

    janela.querySelector('.uso__cancelar').addEventListener('click', () => janela.close());
    // clique fora da janela (no fundo escuro) também cancela
    janela.addEventListener('click', evento => {
        if (evento.target === janela) {
            janela.close();
        }
    });
})();
