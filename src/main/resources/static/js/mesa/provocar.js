/*
 * "Olha se eu quiser": um inimigo puxa uma carta (de costas) em direção ao centro da mesa e recolhe,
 * sem mostrar qual é. Peça da mesa: mesaProvocar(oponente, carta) anima a carta "carta" do oponente "oponente"
 * (posições na tela). Com a máquina, quem chama são os bots (jogar_com_maquina/bots_provocam.js); no multiplayer,
 * vai ser chamado quando outro jogador arrastar uma carta para a mesa.
 * Sem animação (prefers-reduced-motion) não faz nada.
 */
(() => {
    const semAnimacao = matchMedia('(prefers-reduced-motion: reduce)').matches;

    window.mesaProvocar = (oponente, carta) => {
        const alvo = document.querySelectorAll('.oponente')[oponente]
            ?.querySelectorAll('.oponente__cartas .carta')[carta];
        if (!alvo || semAnimacao || alvo.classList.contains('carta--provocando')) {
            return false;
        }
        alvo.classList.add('carta--provocando');
        alvo.addEventListener('animationend', () => alvo.classList.remove('carta--provocando'), {once: true});
        return true;
    };
})();
