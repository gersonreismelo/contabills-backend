package br.com.contabills.model;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.contabills.controller.UsuarioController;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Entidade que representa um usuário do sistema.
 * Implementa a interface {@link UserDetails} para integração com o Spring
 * Security.
 *
 * Contém dados de autenticação e informações pessoais do usuário.
 * Também fornece links HATEOAS para os endpoints da API RESTful.
 * 
 * @author Gerson
 * @version 1.0
 */
@Data
@AllArgsConstructor
@Builder
@Entity
@Table(name = "T_C_USUARIO")
public class Usuario implements UserDetails {

    /**
     * Construtor padrão
     */
    public Usuario() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cd_usuario")
    /** Identificador único do usuário. */
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 40)
    @Column(name = "nm_usuario")
    /** Nome completo do usuário. */
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email
    @Size(max = 100)
    @Column(name = "ds_email", unique = true)
    /** Endereço de e-mail do usuário, utilizado para login. */
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8)
    @Column(name = "ds_senha")
    /** Senha do usuário (armazenada de forma segura). */
    private String senha;

    @Size(min = 8)
    @Transient
    /** Campo utilizado apenas para confirmação da senha no cadastro. */
    private String confirmarSenha;

    @NotNull
    @Column(name = "dt_nascimento")
    /** Data de nascimento do usuário. */
    private LocalDate data;

    @NotBlank(message = "O telefone é obrigatório")
    @Pattern(regexp = "^\\(?\\d{2}\\)?[\\s-]?\\d{4,5}-\\d{4}$")
    @Column(name = "nr_telefone")
    /** Número de telefone para contato. */
    private String telefone;

    @Column(name = "ds_foto", columnDefinition = "LONGBLOB")
    @Lob
    /** Foto do usuário armazenada em formato binário. */
    private byte[] foto;

    /**
     * Cria um modelo HATEOAS da entidade usuario com os links relacionados.
     *
     * @return EntityModel com os links HATEOAS.
     */
    public EntityModel<Usuario> toEntityModel() {
        return EntityModel.of(
                this,
                linkTo(methodOn(UsuarioController.class).show(id)).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).delete(id)).withRel("delete"),
                linkTo(methodOn(UsuarioController.class).index(null, Pageable.unpaged())).withRel("all"));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USUARIO"));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
