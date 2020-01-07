package integracao.componentes.contatos;

public interface ContatoService {

	void inserir(Contato contato) throws ContatoException;

	void remover(Long id);

}
