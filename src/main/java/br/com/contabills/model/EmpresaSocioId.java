package br.com.contabills.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Classe de chave composta para a entidade {@link EmpresaSocio}.
 *
 * Representa a associação entre uma empresa e um sócio. Utilizada como chave
 * primária composta na entidade que relaciona os dois.
 *
 * @author Gerson
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@Embeddable
public class EmpresaSocioId implements Serializable {

    /**
     * Construtor padrão da classe EmpresaSocioId.
     */
    public EmpresaSocioId() {
    }

    /**
     * Identificador da empresa na associação.
     */
    private Long empresaId;

    /**
     * Identificador do sócio na associação.
     */
    private Long socioId;

    /**
     * Compara se dois objetos {@link EmpresaSocioId} são iguais.
     *
     * @param o objeto a ser comparado
     * @return true se os dois objetos forem iguais, false caso contrário
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EmpresaSocioId that = (EmpresaSocioId) o;
        return Objects.equals(empresaId, that.empresaId) &&
                Objects.equals(socioId, that.socioId);
    }

    /**
     * Retorna o hash code baseado nos campos {@code empresaId} e {@code socioId}.
     *
     * @return código hash do objeto
     */
    @Override
    public int hashCode() {
        return Objects.hash(empresaId, socioId);
    }
}