package br.com.contabills.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.com.contabills.controller.EmpresaController;
import br.com.contabills.controller.ParcelamentoController;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa um parcelamento de valores relacionado a uma {@link Empresa}.
 *
 * Contém o tipo de parcelamento, o registro e as parcelas vinculadas.
 *
 * @author Gerson
 * @version 1.0
 */
@Data
@Entity
@Builder
@AllArgsConstructor
@Table(name = "T_C_PARCELAMENTO")
public class Parcelamento {
        
        /**
         * Construtor padrão da classe Parcelamento.
         */
        public Parcelamento() {
        }

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotNull(message = "O registro do parcelamento é obrigatório")
        private String registroDoParcelamento;

        @NotBlank(message = "O tipo de parcelamento é obrigatório")
        private String tipoParcelamento;

        @ManyToOne
        @JsonIgnoreProperties("parcelamentos")
        private Empresa empresa;

        @OneToMany(mappedBy = "parcelamento", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Parcela> parcelas = new ArrayList<>();

        /**
         * Cria um modelo HATEOAS da entidade parcelamento com os links relacionados.
         *
         * @return EntityModel com os links HATEOAS.
         */
        public EntityModel<Parcelamento> toEntityModel() {
                return EntityModel.of(
                                this,
                                WebMvcLinkBuilder
                                                .linkTo(WebMvcLinkBuilder.methodOn(ParcelamentoController.class)
                                                                .get(id))
                                                .withSelfRel(),
                                WebMvcLinkBuilder
                                                .linkTo(WebMvcLinkBuilder.methodOn(ParcelamentoController.class)
                                                                .destroy(id))
                                                .withRel("delete"),
                                WebMvcLinkBuilder
                                                .linkTo(WebMvcLinkBuilder.methodOn(ParcelamentoController.class)
                                                                .index(Pageable.unpaged()))
                                                .withRel("all"),
                                WebMvcLinkBuilder.linkTo(
                                                WebMvcLinkBuilder.methodOn(EmpresaController.class)
                                                                .get(this.getEmpresa().getApelidoId()))
                                                .withRel("empresa"));
        }
}
