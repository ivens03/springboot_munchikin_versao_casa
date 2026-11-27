/*
 * Pilhas de descarte: clicar numa abre a janela com todas as cartas dela (a do topo primeiro).
 * Clicar numa carta da janela põe ela em foco, grande, para ler; clicar de novo (ou Esc) volta para a janela.
 * A janela fecha no botão, no Esc ou clicando fora. Pilha vazia não tem janela (o botão fica desligado).
 */
(() => {
    document.querySelectorAll('.descarte').forEach(descarte => {
        const janela = descarte.querySelector('.descarte__janela');
        if (!janela) {
            return;
        }
        const foco = janela.querySelector('.descarte__foco');
        const cartaEmFoco = foco.querySelector('img');
        let voltarPara = null;

        function focar(botao) {
            const imagem = botao.querySelector('img');
            cartaEmFoco.src = imagem.src;
            cartaEmFoco.alt = imagem.alt;
            voltarPara = botao;
            foco.hidden = false;
        }

        function desfocar() {
            foco.hidden = true;
            voltarPara?.focus();
        }

        descarte.querySelector('.descarte__botao').addEventListener('click', () => janela.showModal());
        janela.querySelector('.descarte__fechar').addEventListener('click', () => janela.close());
        janela.querySelectorAll('.descarte__carta').forEach(botao => botao.addEventListener('click', () => focar(botao)));
        foco.addEventListener('click', desfocar);
        // com uma carta em foco, o Esc só tira o foco (a janela continua aberta)
        janela.addEventListener('cancel', evento => {
            if (!foco.hidden) {
                evento.preventDefault();
                desfocar();
            }
        });
        janela.addEventListener('click', evento => {
            if (evento.target === janela) {
                janela.close();
            }
        });
        janela.addEventListener('close', () => {
            foco.hidden = true;
        });
    });
})();
