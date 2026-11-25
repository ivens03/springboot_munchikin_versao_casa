package ivens.versao.casa.munchkin.munchkin.service.raca;

import ivens.versao.casa.munchkin.munchkin.model.Raca;
import ivens.versao.casa.munchkin.jogo.service.RegrasBasicas;

/**
 * Strategy: cada raça implementa as próprias regras.
 * Os métodos default são a regra padrão (Humano, que não tem habilidade);
 * cada raça sobrescreve só o que a carta dela muda.
 */
public interface RegraDeRaca {

    int ILIMITADO = Integer.MAX_VALUE;

    Raca raca();

    /** Raça que os monstros enxergam (ex.: "+3 contra Halflings"). */
    default Raca racaParaOsMonstros() {
        return raca();
    }

    // ---------- Fuga ----------

    default int bonusParaFugir(int nivelDoMonstro) {
        return 0;
    }

    /** Depois de falhar na primeira tentativa de fuga, pode descartar uma carta e tentar de novo. */
    default boolean podeTentarFugirDeNovoDescartandoCarta() {
        return false;
    }

    /** Foge sem rolar o dado se não conseguir derrotar o monstro. */
    default boolean fogeAutomaticamente(String nomeDoMonstro) {
        return false;
    }

    // ---------- Combate ----------

    default int niveisPorAjudarAMatar(int monstrosMortos) {
        return 0;
    }

    /** Níveis além do normal ao vencer um combate; diferencaDeForca = força do munchkin - força do monstro. */
    default int niveisExtrasAoVencer(boolean venceuSozinho, int diferencaDeForca) {
        return 0;
    }

    /** Pode jogar um monstro da mão como Ilusão de uso único, somando o nível dele à Força de Combate. */
    default boolean podeUsarMonstroComoIlusao(boolean combateComAjuda) {
        return false;
    }

    default int bonusDoItem(String nomeDoItem, boolean itemDeUsoUnico) {
        return 0;
    }

    /** Quantas vezes um item de uso único jogado para ajudar um monstro pode ser usado. */
    default int usosDeItemDeUsoUnicoParaAjudarMonstro() {
        return 1;
    }

    // ---------- Maldição ----------

    default boolean podeIgnorarMaldicao() {
        return false;
    }

    default int niveisPerdidosAoIgnorarMaldicao(int nivelAtual) {
        return 0;
    }

    // ---------- Itens e venda ----------

    /** Uma vez por turno, vende um item pelo dobro do preço. */
    default boolean vendeUmItemPorTurnoPeloDobro() {
        return false;
    }

    default int limiteDeItensGrandes() {
        return 1;
    }

    default int limiteDeCalcados() {
        return 1;
    }

    default int limiteDeMontarias() {
        return 1;
    }

    /** Cartas que pode manter na mão no final do turno. */
    default int limiteDeCartasNaMao() {
        return RegrasBasicas.LIMITE_DE_CARTAS_NA_MAO;
    }
}
