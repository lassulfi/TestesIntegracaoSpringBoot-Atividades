package integracao.componentes.controller;

import javax.validation.ConstraintViolationException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import integracao.componentes.agenda.AgendaController;
import integracao.componentes.contatos.Contato;
import integracao.componentes.contatos.ContatoException;
import integracao.componentes.contatos.ContatoRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AgendaControllerIntegrationTest {

	@MockBean
	private ContatoRepository contatoRepository;
	
	@Autowired
	private AgendaController agendaController;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	private static final String NOME = "Nome do contato 1";
	private static final String DDD = "016";
	private static final String TELEFONE = "995714567";
	
	@Test
	public void inserirRegistroComDddNullDeveLancarException() throws ContatoException {
		expectedException.expect(ContatoException.class);
		expectedException.expectMessage("O DDD deve ser preenchido");
		
		Mockito.when(contatoRepository.save((Contato) Mockito.any()))
			.thenThrow(new ConstraintViolationException("O DDD deve ser preenchido", null));
		
		agendaController.inserirRegistro(NOME, null, TELEFONE);
	}
	
	@Test
	public void inserirRegistroComTelefoneNullDeveLancarException() throws ContatoException {
		expectedException.expect(ContatoException.class);
		expectedException.expectMessage("O Telefone deve ser preenchido");
		
		Mockito.when(contatoRepository.save((Contato) Mockito.any()))
			.thenThrow(new ConstraintViolationException("O Telefone deve ser preenchido", null));
		
		agendaController.inserirRegistro(NOME, DDD, null);
	}
	
	@Test
	public void inserirRegistroComNomeNullDeveLancarException() throws ContatoException {
		expectedException.expect(ContatoException.class);
		expectedException.expectMessage("O Nome deve ser preenchido");
		
		Mockito.when(contatoRepository.save((Contato) Mockito.any()))
			.thenThrow(new ConstraintViolationException("O Nome deve ser preenchido", null));
		
		agendaController.inserirRegistro(null, DDD, TELEFONE);
	}
	
	@Test
	public void inserirRegistroDeveSalvarContato() throws ContatoException {
		agendaController.inserirRegistro(NOME, DDD, TELEFONE);
		Mockito.verify(contatoRepository, Mockito.times(1)).save(new Contato(NOME, DDD, TELEFONE));
	}
	
	@Test
	public void removerRegistroDeveRemoverContato() {
		agendaController.removerRegistro(1L);
		Mockito.verify(contatoRepository, Mockito.times(1)).deleteById(1L);
	}
}
