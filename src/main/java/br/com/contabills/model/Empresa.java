package br.com.contabills.model;

import java.util.ArrayList;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Classe que representa uma Empresa dentro do sistema Contabills.
 * 
 * Esta entidade possui dados como razão social, CNPJ, endereço, sócios e
 * parcelamentos.
 * Ela também é usada em conjunto com Spring HATEOAS para expor links RESTful.
 * 
 * @author Gerson
 * @version 1.0
 */
@Data
@Entity
@Builder
@AllArgsConstructor
@Table(name = "T_C_EMPRESA")
public class Empresa {

        /**
         * Construtor padrão necessário para frameworks e serialização.
         */
        public Empresa() {
        }

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

        @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonIgnoreProperties("empresa")
        private List<EmpresaSocio> empresaSocios;

        @OneToMany(mappedBy = "empresa", cascade = CascadeType.PERSIST)
        @JsonIgnoreProperties("empresa")
        private List<Parcelamento> parcelamentos;

        /**
         * Adiciona um vínculo entre empresa e sócio. Também atualiza a referência
         * inversa.
         * 
         * @param empresaSocio o vínculo entre {@link Empresa} e
         *                     {@link Socio}
         */
        public void adicionarEmpresaSocio(EmpresaSocio empresaSocio) {
                if (this.empresaSocios == null) {
                        this.empresaSocios = new ArrayList<>();
                }
                this.empresaSocios.add(empresaSocio);
                empresaSocio.setEmpresa(this);
        }

        /**
         * Cria um modelo HATEOAS da entidade empresa com os links relacionados.
         *
         * @return EntityModel com os links HATEOAS.
         */
        public EntityModel<Empresa> toEntityModel() {
                return EntityModel.of(
                                this,
                                WebMvcLinkBuilder
                                                .linkTo(WebMvcLinkBuilder.methodOn(EmpresaController.class)
                                                                .get(apelidoId))
                                                .withSelfRel(),
                                WebMvcLinkBuilder
                                                .linkTo(WebMvcLinkBuilder.methodOn(EmpresaController.class)
                                                                .destroy(apelidoId))
                                                .withRel("delete"),
                                WebMvcLinkBuilder
                                                .linkTo(WebMvcLinkBuilder.methodOn(EmpresaController.class)
                                                                .index(Pageable.unpaged()))
                                                .withRel("all"),
                                WebMvcLinkBuilder
                                                .linkTo(WebMvcLinkBuilder.methodOn(SocioController.class).index(null,
                                                                Pageable.unpaged()))
                                                .withRel("socios"),
                                WebMvcLinkBuilder
                                                .linkTo(WebMvcLinkBuilder.methodOn(ParcelamentoController.class)
                                                                .index(Pageable.unpaged()))
                                                .withRel("parcelamentos"));
        }
}
