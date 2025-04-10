package br.com.contabills.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa um endereço utilizado por uma {@link Empresa} ou {@link Socio}.
 *
 * Essa classe é embutida em outras entidades (via {@code @Embedded}) e contém
 * informações completas sobre localização física.
 *
 * @author Gerson
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@Embeddable
public class Endereco {

    /**
     * Construtor padrão da classe Endereco.
     */
    public Endereco() {
    }

    @NotBlank(message = "O logradouro é obrigatório")
    private String logradouro;

    @NotNull(message = "O número é obrigatório")
    private int numero;

    private String complemento;

    @NotBlank(message = "O bairro é obrigatório")
    private String bairro;

    @NotBlank(message = "A cidade é obrigatória")
    private String cidade;

    @NotBlank(message = "A UF é obrigatória")
    @Pattern(regexp = "^[A-Z]{2}$", message = "UF inválida")
    private String uf;

    @NotBlank(message = "O CEP é obrigatório")
    @Pattern(regexp = "^\\d{5}-\\d{3}$", message = "CEP inválido")
    private String cep;
}
