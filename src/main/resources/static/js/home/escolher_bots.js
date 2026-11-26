/*
 * Janela de montagem da mesa: mexer no nível da vitória personalizada já escolhe a personalizada,
 * para o nível digitado não ser ignorado.
 */
document.querySelectorAll('.escolher-bots__nivel-campo').forEach(campo => {
    const personalizada = campo.closest('.escolher-bots__vitoria-opcao').querySelector('input[type="radio"]');
    campo.addEventListener('focus', () => {
        personalizada.checked = true;
    });
});
