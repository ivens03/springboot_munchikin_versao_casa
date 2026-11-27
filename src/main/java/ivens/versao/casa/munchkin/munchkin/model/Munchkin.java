package ivens.versao.casa.munchkin.munchkin.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import static ivens.versao.casa.munchkin.jogo.service.RegrasBasicas.NIVEL_INICIAL;
import static ivens.versao.casa.munchkin.jogo.service.RegrasBasicas.OURO_INICIAL;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Munchkin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Raca raca;

    /**
     * Começa no nível 0 e pode ficar negativo, sem limite (regras da casa);
     * o default no banco é para as linhas que já existiam.
     */
    @Setter
    @ColumnDefault("0")
    @Column(nullable = false)
    private int nivel;

    /** Começa com 400 de ouro (regra da casa); o default no banco é para as linhas que já existiam. */
    @Setter
    @ColumnDefault("400")
    @Column(nullable = false)
    private int ouro;

    public Munchkin(String nome) {
        this.nome = nome;
        this.raca = Raca.HUMANO;
        this.nivel = NIVEL_INICIAL;
        this.ouro = OURO_INICIAL;
    }
}
