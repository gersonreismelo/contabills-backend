package br.com.contabills.model;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.com.contabills.controller.EmpresaController;
import br.com.contabills.controller.ParcelamentoController;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_C_PARCELAMENTO")
public class Parcelamento {

    @Id
    @NotNull(message = "O número do parcelamento é obrigatório")
    private Long numeroParcelamento;

    @NotBlank(message = "O tipo de parcelamento é obrigatório")
    private String tipoParcelamento;

    @NotNull(message = "O valor da parcela é obrigatório")
    private double valorParcela;

    private boolean enviadoMesAtual;

    @ManyToOne
    @JsonIgnoreProperties("parcelamentos")
    private Empresa empresa;

    public EntityModel<Parcelamento> toEntityModel() {
        return EntityModel.of(
                this,
                WebMvcLinkBuilder
                        .linkTo(WebMvcLinkBuilder.methodOn(ParcelamentoController.class).get(numeroParcelamento))
                        .withSelfRel(),
                WebMvcLinkBuilder
                        .linkTo(WebMvcLinkBuilder.methodOn(ParcelamentoController.class).destroy(numeroParcelamento))
                        .withRel("delete"),
                WebMvcLinkBuilder
                        .linkTo(WebMvcLinkBuilder.methodOn(ParcelamentoController.class).index(Pageable.unpaged()))
                        .withRel("all"),
                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(EmpresaController.class).get(this.getEmpresa().getApelidoId()))
                        .withRel("empresa"));
    }
}
