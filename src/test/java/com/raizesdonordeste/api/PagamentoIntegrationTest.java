package com.raizesdonordeste.api;

import com.raizesdonordeste.api.enums.CanalPedido;
import com.raizesdonordeste.api.enums.PerfilUsuario;
import com.raizesdonordeste.api.enums.StatusPedido;
import com.raizesdonordeste.api.model.Pedido;
import com.raizesdonordeste.api.model.Usuario;
import com.raizesdonordeste.api.repository.AuditoriaRepository;
import com.raizesdonordeste.api.repository.ItemPedidoRepository;
import com.raizesdonordeste.api.repository.PagamentoRepository;
import com.raizesdonordeste.api.repository.PedidoRepository;
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

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PagamentoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private Usuario cliente;
    private String token;

    @BeforeEach
    void prepararBanco() throws Exception {

        // A ordem é importante por causa das chaves estrangeiras.
        auditoriaRepository.deleteAll();
        pagamentoRepository.deleteAll();
        itemPedidoRepository.deleteAll();
        pedidoRepository.deleteAll();
        usuarioRepository.deleteAll();

        cliente = new Usuario();
        cliente.setNome("Cliente Pagamento");
        cliente.setEmail("cliente.pagamento@raizes.com");
        cliente.setSenha(passwordEncoder.encode("Cliente@123"));
        cliente.setPerfil(PerfilUsuario.CLIENTE);

        cliente = usuarioRepository.save(cliente);

        token = realizarLogin();
    }

    private String realizarLogin() throws Exception {

        String loginJson = """
                {
                  "email": "cliente.pagamento@raizes.com",
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

    private Pedido criarPedidoAguardandoPagamento() {

        Pedido pedido = new Pedido();

        pedido.setClienteId(cliente.getId());
        pedido.setUnidadeId(1L);
        pedido.setCanalPedido(CanalPedido.APP);
        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
        pedido.setValorTotal(new BigDecimal("59.80"));

        return pedidoRepository.save(pedido);
    }

    @Test
    void deveAprovarPagamentoEAtualizarPedidoParaPago() throws Exception {

        Pedido pedido = criarPedidoAguardandoPagamento();

        mockMvc.perform(
                        post("/pagamentos/pedido/" + pedido.getId())
                                .param("resultado", "APROVADO")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("APROVADO"))
                .andExpect(jsonPath("$.valor").value(59.80));

        Pedido pedidoAtualizado = pedidoRepository
                .findById(pedido.getId())
                .orElseThrow();

        assertEquals(
                StatusPedido.PAGO,
                pedidoAtualizado.getStatus()
        );
    }

    @Test
    void deveRecusarPagamentoEAtualizarPedido() throws Exception {

        Pedido pedido = criarPedidoAguardandoPagamento();

        mockMvc.perform(
                        post("/pagamentos/pedido/" + pedido.getId())
                                .param("resultado", "RECUSADO")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("RECUSADO"));

        Pedido pedidoAtualizado = pedidoRepository
                .findById(pedido.getId())
                .orElseThrow();

        assertEquals(
                StatusPedido.PAGAMENTO_RECUSADO,
                pedidoAtualizado.getStatus()
        );
    }

    @Test
    void deveRegistrarAuditoriaAoAprovarPagamento() throws Exception {

        Pedido pedido = criarPedidoAguardandoPagamento();

        long quantidadeAntes = auditoriaRepository.count();

        mockMvc.perform(
                        post("/pagamentos/pedido/" + pedido.getId())
                                .param("resultado", "APROVADO")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isCreated());

        long quantidadeDepois = auditoriaRepository.count();

        assertTrue(
                quantidadeDepois > quantidadeAntes,
                "O pagamento aprovado deveria gerar um registro de auditoria."
        );
    }
}