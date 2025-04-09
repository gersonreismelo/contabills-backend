package br.com.contabills.config;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import br.com.contabills.model.Empresa;
import br.com.contabills.model.Endereco;
import br.com.contabills.model.Parcela;
import br.com.contabills.model.Parcelamento;
import br.com.contabills.model.Socio;
import br.com.contabills.model.Usuario;
import br.com.contabills.repository.EmpresaRepository;
import br.com.contabills.repository.ParcelamentoRepository;
import br.com.contabills.repository.SocioRepository;
import br.com.contabills.repository.UsuarioRepository;

@Configuration
public class DatabaseSeeder implements CommandLineRunner {

        @Autowired
        private EmpresaRepository empresaRepository;

        @Autowired
        private SocioRepository socioRepository;

        @Autowired
        private ParcelamentoRepository parcelamentoRepository;

        @Autowired
        private UsuarioRepository usuarioRepository;

        @Override
        public void run(String... args) throws Exception {

                Endereco endereco1 = Endereco.builder()
                                .logradouro("Rua das Flores")
                                .numero(123)
                                .complemento("Apto 101")
                                .bairro("Centro")
                                .cidade("São Paulo")
                                .uf("SP")
                                .cep("01000-000")
                                .build();

                Endereco endereco2 = Endereco.builder()
                                .logradouro("Avenida Brasil")
                                .numero(456)
                                .complemento("Sala 202")
                                .bairro("Jardins")
                                .cidade("Rio de Janeiro")
                                .uf("RJ")
                                .cep("22000-000")
                                .build();

                Endereco endereco3 = Endereco.builder()
                                .logradouro("Rua do Comércio")
                                .numero(789)
                                .complemento("Loja 5")
                                .bairro("Centro")
                                .cidade("Belo Horizonte")
                                .uf("MG")
                                .cep("30100-000")
                                .build();

                Endereco endereco4 = Endereco.builder()
                                .logradouro("Travessa das Palmeiras")
                                .numero(321)
                                .complemento("Bloco B")
                                .bairro("Vila Nova")
                                .cidade("Curitiba")
                                .uf("PR")
                                .cep("80500-000")
                                .build();

                Socio socio1 = Socio.builder()
                                .nome("João Silva")
                                .dataNascimento(LocalDate.of(1980, 5, 20))
                                .cpf("123.456.789-01")
                                .rg("MG1234567")
                                .dataDeEmissaoRg(LocalDate.of(2000, 5, 30))
                                .cnh("AB123456")
                                .dataDeEmissaoCnh(LocalDate.of(2015, 6, 15))
                                .dataDeValidadeCnh(LocalDate.of(2025, 6, 15))
                                .nomeDaMae("Maria Silva")
                                .nomeDoPai("José Silva")
                                .nacionalidade("Brasileira")
                                .estadoCivil("Casado")
                                .tipoDeComunhao("Comunhão Parcial")
                                .profissao("Engenheiro")
                                .enderecoSocio(endereco1)
                                .build();

                Socio socio2 = Socio.builder()
                                .nome("Ana Souza")
                                .dataNascimento(LocalDate.of(1990, 8, 10))
                                .cpf("987.654.321-00")
                                .rg("SP9876543")
                                .dataDeEmissaoRg(LocalDate.of(2010, 8, 25))
                                .cnh("CD654321")
                                .dataDeEmissaoCnh(LocalDate.of(2018, 9, 10))
                                .dataDeValidadeCnh(LocalDate.of(2028, 9, 10))
                                .nomeDaMae("Lúcia Souza")
                                .nomeDoPai("Carlos Souza")
                                .nacionalidade("Brasileira")
                                .estadoCivil("Solteira")
                                .tipoDeComunhao("Separação Total")
                                .profissao("Advogada")
                                .enderecoSocio(endereco2)
                                .build();

                socioRepository.saveAll(List.of(socio1, socio2));

                Empresa empresa1 = Empresa.builder()
                                .apelidoId(1L)
                                .razaoSocial("Tech Solutions LTDA")
                                .tipoEmpresa("TI")
                                .cnpj("12.345.678/0001-99")
                                .iptu("123456789")
                                .email("contato@techsolutions.com")
                                .telefone("(31) 99999-8888")
                                .capitalSocialEmpresa(500000.00)
                                .possuiProcuracao(true)
                                .possuiCertificado(true)
                                .enderecoEmpresa(endereco3)
                                .socios(List.of(socio2))
                                .build();

                Empresa empresa2 = Empresa.builder()
                                .apelidoId(2L)
                                .razaoSocial("Construtora Alfa S/A")
                                .tipoEmpresa("Construção Civil")
                                .cnpj("98.765.432/0001-11")
                                .iptu("987654321")
                                .email("contato@construtoraalfa.com")
                                .telefone("(41) 98888-7777")
                                .capitalSocialEmpresa(1000000.00)
                                .possuiProcuracao(false)
                                .possuiCertificado(true)
                                .enderecoEmpresa(endereco4)
                                .socios(List.of(socio1, socio2))
                                .build();

                empresaRepository.saveAll(List.of(empresa1, empresa2));

                Parcelamento parcelamento1 = Parcelamento.builder()
                                .registroDoParcelamento("PRC-001")
                                .tipoParcelamento("Imposto de Renda")
                                .empresa(empresa1)
                                .build();

                Parcelamento parcelamento2 = Parcelamento.builder()
                                .registroDoParcelamento("PRC-002")
                                .tipoParcelamento("FGTS")
                                .empresa(empresa2)
                                .build();

                Parcela parcela1 = Parcela.builder()
                                .numero(1)
                                .valor(1500.00)
                                .enviadoMesAtual(true)
                                .parcelamento(parcelamento1)
                                .build();

                Parcela parcela2 = Parcela.builder()
                                .numero(2)
                                .valor(1500.00)
                                .enviadoMesAtual(false)
                                .parcelamento(parcelamento1)
                                .build();

                Parcela parcela3 = Parcela.builder()
                                .numero(1)
                                .valor(2000.00)
                                .enviadoMesAtual(true)
                                .parcelamento(parcelamento2)
                                .build();

                parcelamento1.setParcelas(List.of(parcela1, parcela2));
                parcelamento2.setParcelas(List.of(parcela3));

                parcelamentoRepository.saveAll(List.of(parcelamento1, parcelamento2));

                Usuario usuario1 = Usuario.builder()
                                .nome("Carlos Pereira")
                                .email("carlos.pereira@email.com")
                                .senha("senha123")
                                .confirmarSenha("senha123")
                                .data(LocalDate.of(1985, 2, 15))
                                .telefone("(11) 99876-5432")
                                .build();

                Usuario usuario2 = Usuario.builder()
                                .nome("Fernanda Lima")
                                .email("fernanda.lima@email.com")
                                .senha("senha123")
                                .confirmarSenha("senha123")
                                .data(LocalDate.of(1992, 7, 25))
                                .telefone("(21) 99987-6543")
                                .build();

                usuarioRepository.saveAll(List.of(usuario1, usuario2));
        }
}
