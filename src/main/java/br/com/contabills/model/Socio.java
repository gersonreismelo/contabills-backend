package br.com.contabills.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.com.contabills.controller.EmpresaController;
import br.com.contabills.controller.SocioController;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa um sócio de uma {@link Empresa}, contendo dados pessoais e
 * vínculos com empresas através da entidade {@link EmpresaSocio}.
 *
 * Inclui dados como CPF, RG, endereço, estado civil e profissão.
 *
 * @author Gerson
 * @version 1.0
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_C_SOCIO")
public class Socio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotNull(message = "A data de nascimento é obrigatória")
    private LocalDate dataNascimento;

    @NotBlank(message = "O CPF é obrigatório")
    @Pattern(regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$", message = "CPF inválido")
    @Column(unique = true)
    private String cpf;

    @NotBlank(message = "O RG é obrigatório")
    private String rg;

    @NotNull(message = "A data de emissão do RG é obrigatória")
    private LocalDate dataDeEmissaoRg;

    private String cnh;

    private LocalDate dataDeEmissaoCnh;

    @Future(message = "A data de validade da CNH deve ser no futuro.")
    private LocalDate dataDeValidadeCnh;

    @NotBlank(message = "O nome da mãe é obrigatório")
    private String nomeDaMae;

    @NotBlank(message = "O nome do pai é obrigatório")
    private String nomeDoPai;

    @NotBlank(message = "A nacionalidade é obrigatória")
    private String nacionalidade;

    @NotBlank(message = "O estado civil é obrigatório")
    private String estadoCivil;

    private String tipoDeComunhao;

    @NotBlank(message = "A profissão é obrigatória")
    private String profissao;

    @Embedded
    @NotNull(message = "O endereço do sócio é obrigatório")
    private Endereco enderecoSocio;

    @OneToMany(mappedBy = "socio", cascade = CascadeType.PERSIST)
    @JsonIgnoreProperties("socio")
    private List<EmpresaSocio> empresaSocios;

    /**
     * Construtor que inicializa um {@link Socio} apenas com o CPF.
     * 
     * Útil para buscas ou operações onde apenas o CPF é necessário.
     * 
     * @param cpf CPF do sócio
     */
    public Socio(String cpf) {
        this.cpf = cpf;
    }

    /**
     * Constrói um modelo HATEOAS da entidade socio, incluindo os links
     * relacionados.
     *
     * @return um {@link EntityModel} com links para este sócio, lista de sócios,
     *         exclusão e a primeira empresa relacionada (caso exista).
     */
    public EntityModel<Socio> toEntityModel() {
        EntityModel<Socio> entityModel = EntityModel.of(
                this,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(SocioController.class).show(id)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(SocioController.class).delete(id))
                        .withRel("delete"),
                WebMvcLinkBuilder
                        .linkTo(WebMvcLinkBuilder.methodOn(SocioController.class).index(null, Pageable.unpaged()))
                        .withRel("all"));

        if (this.empresaSocios != null && !this.empresaSocios.isEmpty()) {
            EmpresaSocio primeiraAssociacao = this.empresaSocios.get(0);
            if (primeiraAssociacao != null && primeiraAssociacao.getEmpresa() != null) {
                entityModel.add(WebMvcLinkBuilder
                        .linkTo(WebMvcLinkBuilder.methodOn(EmpresaController.class)
                                .get(primeiraAssociacao.getEmpresa().getApelidoId()))
                        .withRel("empresa"));
            }
        }

        return entityModel;
    }
}
