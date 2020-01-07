package integracao.componentes.contatos;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ContatoServiceIntegrationTest {

	@Autowired
	private ContatoService contatoService;
	
	@Autowired
	private ContatoRepository contatoRepository;
	
	private Contato contato;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void setup() {
		contato = new Contato("Nome do contato 1", "016", "3333552");
	}
	
	@After
	public void tearDown() {
		contatoRepository.deleteAll();
	}
	
	@Test
	public void inserirContatoComDddNuloLancaException() throws ContatoException {
		expectedException.expect(ContatoException.class);
		expectedException.expectMessage("O DDD deve ser preenchido");
		
		contato.setDdd(null);
		contatoService.inserir(contato);
	}
	
	@Test
	public void inserirContatoComTelefoneNuloLancaException() throws ContatoException {
		expectedException.expect(ContatoException.class);
		expectedException.expectMessage("O Telefone deve ser preenchido");
		
		contato.setTelefone(null);
		contatoService.inserir(contato);
	}
	
	@Test
	public void inserirContatoComNomeNuloLancaException() throws ContatoException {
		expectedException.expect(ContatoException.class);
		expectedException.expectMessage("O Nome deve ser preenchido");
		
		contato.setNome(null);
		contatoService.inserir(contato);
	}
	
	@Test
	public void inserirDeveSalvarContato() throws ContatoException {
		contatoService.inserir(contato);
		
		List<Contato> contatos = contatoRepository.findAll();
		Assert.assertEquals(1, contatos.size());
	}
	
	@Test
	public void removerDeveExcluirContato() throws ContatoException {
		contatoService.inserir(contato);
		List<Contato> contatos = contatoRepository.findAll();
		Assert.assertEquals(1, contatos.size());
		
		contatoService.remover(contato.getId());
		List<Contato> resultado = contatoRepository.findAll();
		Assert.assertEquals(0, resultado.size());
	}
}
