package com.raizesdonordeste.api;

import com.raizesdonordeste.api.enums.PerfilUsuario;
import com.raizesdonordeste.api.model.EstoqueUnidade;
import com.raizesdonordeste.api.model.Produto;
import com.raizesdonordeste.api.model.Unidade;
import com.raizesdonordeste.api.model.Usuario;
import com.raizesdonordeste.api.repository.EstoqueUnidadeRepository;
import com.raizesdonordeste.api.repository.ProdutoRepository;
import com.raizesdonordeste.api.repository.UnidadeRepository;
import com.raizesdonordeste.api.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.raizesdonordeste.api.repository.AuditoriaRepository;
import com.raizesdonordeste.api.repository.ItemPedidoRepository;
import com.raizesdonordeste.api.repository.PagamentoRepository;
import com.raizesdonordeste.api.repository.PedidoRepository;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PedidoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UnidadeRepository unidadeRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private EstoqueUnidadeRepository estoqueUnidadeRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    private Usuario cliente;
    private Unidade unidade;
    private Produto produto;
    private String token;

    @BeforeEach
    void prepararBanco() throws Exception {

        auditoriaRepository.deleteAll();
        pagamentoRepository.deleteAll();
        itemPedidoRepository.deleteAll();
        pedidoRepository.deleteAll();
        estoqueUnidadeRepository.deleteAll();
        produtoRepository.deleteAll();
        unidadeRepository.deleteAll();
        usuarioRepository.deleteAll();

        cliente = new Usuario();
        cliente.setNome("Cliente Pedido");
        cliente.setEmail("cliente.pedido@raizes.com");
        cliente.setSenha(passwordEncoder.encode("Cliente@123"));
        cliente.setPerfil(PerfilUsuario.CLIENTE);
        cliente = usuarioRepository.save(cliente);

        unidade = new Unidade();
        unidade.setNome("Unidade Teste");
        unidade.setCidade("Recife");
        unidade.setEstado("PE");
        unidade = unidadeRepository.save(unidade);

        produto = new Produto();
        produto.setNome("Baião de Dois");
        produto.setDescricao("Produto utilizado nos testes");
        produto.setPreco(new BigDecimal("29.90"));
        produto = produtoRepository.save(produto);

        EstoqueUnidade estoque = new EstoqueUnidade();
        estoque.setUnidade(unidade);
        estoque.setProduto(produto);
        estoque.setQuantidade(10);
        estoqueUnidadeRepository.save(estoque);

        token = realizarLogin();
    }

    private String realizarLogin() throws Exception {

        String loginJson = """
                {
                  "email": "cliente.pedido@raizes.com",
                  "senha": "Cliente@123"
                }
                """;

        String resposta = mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginJson)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return resposta
                .split("\"token\":\"")[1]
                .split("\"")[0];
    }

    @Test
    void deveCriarPedidoValido() throws Exception {

        String json = """
                {
                  "clienteId": %d,
                  "unidadeId": %d,
                  "canalPedido": "APP",
                  "itens": [
                    {
                      "produtoId": %d,
                      "quantidade": 2
                    }
                  ]
                }
                """.formatted(
                cliente.getId(),
                unidade.getId(),
                produto.getId()
        );

        mockMvc.perform(
                        post("/pedidos")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status")
                        .value("AGUARDANDO_PAGAMENTO"))
                .andExpect(jsonPath("$.valorTotal")
                        .value(59.80));
    }

    @Test
    void deveRetornar400QuandoQuantidadeForInvalida() throws Exception {

        String json = """
                {
                  "clienteId": %d,
                  "unidadeId": %d,
                  "canalPedido": "APP",
                  "itens": [
                    {
                      "produtoId": %d,
                      "quantidade": 0
                    }
                  ]
                }
                """.formatted(
                cliente.getId(),
                unidade.getId(),
                produto.getId()
        );

        mockMvc.perform(
                        post("/pedidos")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400QuandoCampoObrigatorioEstiverAusente()
            throws Exception {

        String json = """
                {
                  "clienteId": %d,
                  "unidadeId": %d,
                  "itens": [
                    {
                      "produtoId": %d,
                      "quantidade": 1
                    }
                  ]
                }
                """.formatted(
                cliente.getId(),
                unidade.getId(),
                produto.getId()
        );

        mockMvc.perform(
                        post("/pedidos")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar404QuandoProdutoNaoExistir() throws Exception {

        long produtoInexistente = 999999L;

        String json = """
                {
                  "clienteId": %d,
                  "unidadeId": %d,
                  "canalPedido": "APP",
                  "itens": [
                    {
                      "produtoId": %d,
                      "quantidade": 1
                    }
                  ]
                }
                """.formatted(
                cliente.getId(),
                unidade.getId(),
                produtoInexistente
        );

        mockMvc.perform(
                        post("/pedidos")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveRetornar409QuandoEstoqueForInsuficiente() throws Exception {

        String json = """
                {
                  "clienteId": %d,
                  "unidadeId": %d,
                  "canalPedido": "APP",
                  "itens": [
                    {
                      "produtoId": %d,
                      "quantidade": 50
                    }
                  ]
                }
                """.formatted(
                cliente.getId(),
                unidade.getId(),
                produto.getId()
        );

        mockMvc.perform(
                        post("/pedidos")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void deveFiltrarPedidosPorCanal() throws Exception {

        String json = """
                {
                  "clienteId": %d,
                  "unidadeId": %d,
                  "canalPedido": "APP",
                  "itens": [
                    {
                      "produtoId": %d,
                      "quantidade": 1
                    }
                  ]
                }
                """.formatted(
                cliente.getId(),
                unidade.getId(),
                produto.getId()
        );

        mockMvc.perform(
                        post("/pedidos")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isCreated());

        mockMvc.perform(
                        get("/pedidos")
                                .param("canalPedido", "APP")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].canalPedido").value("APP"));
    }
}