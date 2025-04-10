package br.com.contabills.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade que representa o relacionamento entre {@link Empresa} e
 * {@link Socio}.
 * 
 * Define os vínculos de sócios a empresas, incluindo o capital investido
 * e o cargo exercido pelo sócio dentro da empresa.
 *
 * @author Gerson
 * @version 1.0
 */
@Data
@Entity
@Builder
@AllArgsConstructor
@Table(name = "T_C_EMPRESA_SOCIO")
public class EmpresaSocio {

    /**
     * Construtor padrão da classe EmpresaSocio.
     */
    public EmpresaSocio() {
    }

    @EmbeddedId
    private EmpresaSocioId id;

    @ManyToOne
    @MapsId("empresaId")
    @JoinColumn(name = "empresa_id")
    @JsonIgnoreProperties("empresaSocios")
    private Empresa empresa;

    @ManyToOne
    @MapsId("socioId")
    @JoinColumn(name = "socio_id")
    @JsonIgnoreProperties("empresaSocios")
    private Socio socio;

    @NotNull(message = "Capital investido pelo sócio é obrigatório")
    private double capitalInvestido;

    @NotBlank(message = "O cargo do sócio é obrigatório")
    private String cargo;
}
