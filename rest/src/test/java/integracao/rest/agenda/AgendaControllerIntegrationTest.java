package integracao.rest.agenda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import integracao.rest.contatos.Contato;
import integracao.rest.contatos.ContatoRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AgendaControllerIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;
	
	@Autowired
	private ContatoRepository contatoRepository;
	
	private Contato contato;
	
	private static final String NOME = "Nome Usuario 1";
	private static final String DDD = "019";
	private static final String TELEFONE = "665894458";
	
	@Before
	public void setup() {
		contato = new Contato(NOME, DDD, TELEFONE);
		
		contatoRepository.save(contato);
	}
	
	@After
	public void tearDown() {
		contatoRepository.deleteAll();
	}
	
	@Test
	public void deveRecuperarContatoCadastrado() {
		ResponseEntity<Contato> resposta = restTemplate.getForEntity("/agenda/contato/{id}", Contato.class, contato.getId());
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertTrue(resposta.getHeaders().getContentType()
				.equals(MediaType.parseMediaType("application/json;charset=UTF-8")));
		assertEquals(contato, resposta.getBody());
	}
	
	@Test
	public void deveRetornarMensagemDeErroSeContatoNaoEncontrado() {
		ResponseEntity<Contato> resposta = restTemplate.getForEntity("/agenda/contato/{id}", Contato.class, 100);
		
		assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
		assertNull(resposta.getBody());
	}
	
	@Test
	public void salvarContatoComParametrosInvalidosDeveRetornarErro() {
		Contato contato = new Contato(NOME, null, null);
		HttpEntity<Contato> entity = new HttpEntity<>(contato);
		ResponseEntity<List<String>> response = restTemplate.exchange("/agenda/inserir", 
				HttpMethod.POST, 
				entity,
				new ParameterizedTypeReference<List<String>>(){});
	
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().contains("O DDD deve ser preenchido"));
		assertTrue(response.getBody().contains("O Telefone deve ser preenchido"));
	}
	
	@Test
	public void deveSalvarContato() {
		HttpEntity<Contato> entity = new HttpEntity<>(contato);
		ResponseEntity<Contato> response = restTemplate.exchange("/agenda/inserir", 
				HttpMethod.POST, 
				entity,
				Contato.class);
		
		Contato result = response.getBody();
		
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNotNull(result.getId());
		assertEquals(contato.getNome(), result.getNome());
		assertEquals(contato.getDdd(), result.getDdd());
		assertEquals(contato.getTelefone(), result.getTelefone());
	}
	
	@Test
	public void deveAtualizarContato() {
		final String NOVO_TELEFONE = "445626645";
		contato.setTelefone(NOVO_TELEFONE);
		
		restTemplate.put("/agenda/alterar/{id}", contato, contato.getId());
		
		Contato result = contatoRepository.findById(contato.getId()).get();
		
		assertEquals(DDD, result.getDdd());
		assertEquals(NOME, result.getNome());
		assertEquals(NOVO_TELEFONE, result.getTelefone());
	}
	
	@Test
	public void naoDeveAtualizarContatoInvalido() {
		contato.setTelefone(null);
		contato.setDdd(null);
		
		HttpEntity<Contato> entity = new HttpEntity<>(contato);
		ResponseEntity<List<String>> response = restTemplate.exchange("/agenda/alterar/{id}", 
				HttpMethod.PUT, 
				entity,
				new ParameterizedTypeReference<List<String>>(){}, 
				contato.getId());
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().contains("O DDD deve ser preenchido"));
		assertTrue(response.getBody().contains("O Telefone deve ser preenchido"));
	}
	
	@Test
	public void deveEcluirContato() {
		ResponseEntity<Contato> response = restTemplate.exchange("/agenda/remover/{id}", 
				HttpMethod.DELETE, 
				null, 
				Contato.class, 
				contato.getId());
		
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		assertNull(response.getBody());
	}
}
