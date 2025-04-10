package br.com.contabills.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.com.contabills.controller.ParcelaController;
import br.com.contabills.controller.ParcelamentoController;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Representa uma parcela financeira vinculada a um {@link Parcelamento}.
 *
 * Contém informações como número, valor e status de envio da parcela.
 *
 * @author Gerson
 * @version 1.0
 */
@Data
@Entity
@Builder
@AllArgsConstructor
@Table(name = "T_C_PARCELA")
public class Parcela {

    /**
     * Construtor padrão da classe Parcela.
     */
    public Parcela() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O numero da parcela é obrigatório")
    private Integer numero;

    @NotNull(message = "O valor da parcela é obrigatório")
    private double valor;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private boolean enviadoMesAtual;

    @ManyToOne
    @JoinColumn(name = "parcelamento_id")
    @JsonIgnoreProperties("parcelas")
    private Parcelamento parcelamento;

    /**
     * Cria um modelo HATEOAS da entidade parcela com os links relacionados.
     *
     * @return EntityModel com os links HATEOAS.
     */
    public EntityModel<Parcela> toEntityModel() {
        return EntityModel.of(
            this,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ParcelaController.class).get(id)).withSelfRel(),
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ParcelaController.class).delete(id)).withRel("delete"),
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ParcelaController.class).index(Pageable.unpaged())).withRel("all"),
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ParcelamentoController.class).get(parcelamento.getId())).withRel("parcelamento")
        );
    }
}
