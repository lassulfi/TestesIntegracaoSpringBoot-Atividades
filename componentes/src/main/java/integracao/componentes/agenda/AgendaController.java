package integracao.componentes.agenda;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import integracao.componentes.contatos.Contato;
import integracao.componentes.contatos.ContatoException;
import integracao.componentes.contatos.ContatoService;

@Controller
public class AgendaController {

	@Autowired
	private ContatoService contatoService;

	public void inserirRegistro(String nome, String ddd, String telefone) throws ContatoException {
		Contato contato = new Contato(nome, ddd, telefone);
		contatoService.inserir(contato);
	}

	public void removerRegistro(Long id) {
		contatoService.remover(id);
	}

}
