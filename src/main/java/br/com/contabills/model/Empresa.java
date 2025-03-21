package br.com.contabills.model;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.com.contabills.controller.EmpresaController;
import br.com.contabills.controller.ParcelamentoController;
import br.com.contabills.controller.SocioController;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_C_EMPRESA")
public class Empresa {

    @Id
    @NotNull(message = "O apelido da empresa é obrigatório")
    private Long apelidoId;

    @NotBlank(message = "A razão social é obrigatória")
    private String razaoSocial;

    @NotBlank(message = "O tipo de empresa é obrigatório")
    private String tipoEmpresa;

    @NotBlank(message = "O CNPJ é obrigatório")
    @Pattern(regexp = "^[0-9]{2}\\.([0-9]{3})\\.([0-9]{3})\\/([0-9]{4})-([0-9]{2})$", message = "CNPJ inválido")
    private String cnpj;

    @NotBlank(message = "O IPTU é obrigatório")
    private String iptu;

    @NotBlank(message = "O e-mail é obrigatório")
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "E-mail inválido")
    private String email;

    @NotBlank(message = "O telefone é obrigatório")
    @Pattern(regexp = "^\\(?\\d{2}\\)?[\\s-]?\\d{4,5}-\\d{4}$", message = "Telefone inválido")
    private String telefone;

    @NotNull(message = "O capital social é obrigatório")
    private double capitalSocialEmpresa;

    @NotNull(message = "A informação de procuração é obrigatória")
    private boolean possuiProcuracao;

    @NotNull(message = "A informação de certificado é obrigatória")
    private boolean possuiCertificado;

    @Embedded
    @NotNull(message = "O endereço da empresa é obrigatório")
    private Endereco enderecoEmpresa;

    @ManyToMany(cascade = { CascadeType.PERSIST })
    @JoinTable(name = "T_C_EMPRESA_SOCIO", joinColumns = @JoinColumn(name = "empresa_id"), inverseJoinColumns = @JoinColumn(name = "socio_id"))
    @JsonIgnoreProperties("empresas")
    private List<Socio> socios;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.PERSIST)
    @JsonIgnoreProperties("empresa")
    private List<Parcelamento> parcelamentos;

    public EntityModel<Empresa> toEntityModel() {
        return EntityModel.of(
                this,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaController.class).get(apelidoId))
                        .withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaController.class).destroy(apelidoId))
                        .withRel("delete"),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaController.class).index(Pageable.unpaged()))
                        .withRel("all"),
                WebMvcLinkBuilder
                        .linkTo(WebMvcLinkBuilder.methodOn(SocioController.class).index(null, Pageable.unpaged()))
                        .withRel("socios"),
                WebMvcLinkBuilder
                        .linkTo(WebMvcLinkBuilder.methodOn(ParcelamentoController.class).index(Pageable.unpaged()))
                        .withRel("parcelamentos"));
    }
}
