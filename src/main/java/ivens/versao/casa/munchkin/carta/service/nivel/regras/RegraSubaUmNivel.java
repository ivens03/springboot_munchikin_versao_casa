package ivens.versao.casa.munchkin.carta.service.nivel.regras;

import ivens.versao.casa.munchkin.carta.service.nivel.RegraDeSubaDeNivel;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Cartas sem texto de regra, só "SUBA +1 NÍVEL": a regra padrão da interface.
 * 1000 Peças de Ouro, Ameaçar Acabar com o Jogo, Assistir Filmes de Monstros Japoneses, Chegar Cedo para a Partida,
 * Comprar Novas Miniaturas para o Mestre, Desenhar seu Personagem, Empalamento em Série, Encontrar Alguém para te
 * Levar de Volta para Casa ao Fim da Partida, Erro de Soma Conveniente, Invocar Regras Obscuras, Lavar o Carro do
 * Mestre, Lembrar o Aniversário do Mestre, Massacre de Formigas com Água Fervendo, Olhe este Dado... Agora Dorme,
 * Dorme, Dorme..., Pagar a Pizza, Passar a Limpo sua Ficha de Personagem Imunda, Poção de Virar Macho, Prometa ao
 * Mestre que Vai Parar de Falar sobre seu Personagem, Subornar o Mestre com Comida, Trair o Restante do Grupo e
 * Violência Desnecessária.
 */
@Component
public class RegraSubaUmNivel implements RegraDeSubaDeNivel {

    @Override
    public Set<String> cartas() {
        return Set.of("1000_pecas_de_ouro", "ameacar_acabar_com_o_jogo", "assistir_filmes_de_monstros_japoneses", "chegar_cedo_para_a_partida", "comprar_novas_miniaturas_para_o_mestre", "desenhar_seu_personagem", "empalamento_em_serie", "encontrar_alguem_para_te_levar_de_volta_para_casa_ao_fim_da_partida", "erro_de_soma_conveniente", "invocar_regras_obscuras", "lavar_o_carro_do_mestre", "lembrar_o_aniversario_do_mestre", "massacre_de_formigas_com_agua_fervendo", "olhe_este_dado_agora_dorme_dorme_dorme", "pagar_a_pizza", "passar_a_limpo_sua_ficha_de_personagem_imunda", "pocao_de_virar_macho", "prometa_ao_mestre_que_vai_parar_de_falar_sobre_seu_personagem", "subornar_o_mestre_com_comida", "trair_o_restante_do_grupo", "violencia_desnecessaria");
    }
}
