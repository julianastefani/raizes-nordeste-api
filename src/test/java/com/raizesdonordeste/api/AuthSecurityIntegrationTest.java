package com.raizesdonordeste.api;


import com.raizesdonordeste.api.enums.PerfilUsuario;
import com.raizesdonordeste.api.model.Usuario;
import com.raizesdonordeste.api.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void prepararBanco() {

        usuarioRepository.deleteAll();

        Usuario cliente = new Usuario();
        cliente.setNome("Cliente Teste");
        cliente.setEmail("cliente.teste@raizes.com");
        cliente.setSenha(passwordEncoder.encode("Cliente@123"));
        cliente.setPerfil(PerfilUsuario.CLIENTE);

        usuarioRepository.save(cliente);
    }

    @Test
    void deveRealizarLoginComSucesso() throws Exception {

        String json = """
                {
                  "email": "cliente.teste@raizes.com",
                  "senha": "Cliente@123"
                }
                """;

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.usuario.email")
                        .value("cliente.teste@raizes.com"))
                .andExpect(jsonPath("$.usuario.perfil")
                        .value("CLIENTE"));
    }

    @Test
    void deveRetornar401AoAcessarRecursoSemToken() throws Exception {

        mockMvc.perform(get("/produtos"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.erro").value("Unauthorized"));
    }

    @Test
    void deveRetornar403QuandoClienteTentarCriarProduto() throws Exception {

        String loginJson = """
                {
                  "email": "cliente.teste@raizes.com",
                  "senha": "Cliente@123"
                }
                """;

        String respostaLogin = mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginJson)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = respostaLogin
                .split("\"token\":\"")[1]
                .split("\"")[0];

        String produtoJson = """
                {
                  "nome": "Produto Teste",
                  "preco": 25.90
                }
                """;

        mockMvc.perform(
                        post("/produtos")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(produtoJson)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.erro").value("Forbidden"));
    }
}