package server.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe Servidor
 * 
 * @author Lucas Benjamin
 * @since <setembro>/<2018>
 */
public class Servidor extends Thread {

	private static int PORTA_PADRAO = 10588;
	private static Map<String, PrintStream> USUARIOS_CONECTADOS = new HashMap<>();
	private Socket conexao;
	private String nomeUsuario;
	private boolean usuarioLogado;

	/**
	 * Construtor que recebe do cliente o objeto socket dele
	 * 
	 * @param socket Objeto Socket
	 */
	public Servidor(Socket socket) {
		this.conexao = socket;
	}

	public static void main(String[] args) throws IOException {

		try {

			ServerSocket server = new ServerSocket(PORTA_PADRAO);
			System.out.println("O sistema esta escutando na porta " + server.getLocalPort());

			// Fica esperando novas conexões, aceita e cria nova thread com conexão
			while (true) {
				Socket conexao = server.accept();
				Thread threadConexao = new Servidor(conexao);
				threadConexao.start();
			}

		} catch (IOException e) {
			System.out.println("[LOG] IOException: " + e);
		}

	}

	/**
	 * Método responsável pelo pela execução da Thread que vai
	 * 
	 * @param mensagem String com a mensagem a ser enviada para os clientes
	 * @param saida    Objeto PrintStream com a conexão de saida do cliente
	 * @return void
	 */
	@Override
	public void run() {

		try {

			PrintStream saida = new PrintStream(this.conexao.getOutputStream());
			BufferedReader entrada = new BufferedReader(new InputStreamReader(this.conexao.getInputStream()));
			String mensagem = null;

			this.nomeUsuario = entrada.readLine();

			if (validaUsuario(this.nomeUsuario)) {

				logarUsuario(this.nomeUsuario, saida);

				System.out.println("O usuário [" + this.nomeUsuario + "] se conectou ao chat...");
				saida.println("[SERV] : Usuarios conectados " + USUARIOS_CONECTADOS.keySet().toString());

				// Envia ao sender as mensagens digitadas
				mensagem = entrada.readLine();
				while (mensagem != null && !mensagem.equalsIgnoreCase("sairdochat")) {
					enviar(saida, mensagem);
					mensagem = entrada.readLine();
				}

			} else {
				saida.println("Seu nome de usuário é invalido. Tente novamente");
			}

			// Fecha conexão e exclui usuario caso a mensagem seja nula ou vazia
			deslogarUsuario(this.nomeUsuario);
			this.conexao.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param mensagem String com a mensagem a ser enviada para os clientes
	 * @param saida    Objeto PrintStream com a conexão de saida do cliente
	 * @return void
	 */
	protected void enviar(PrintStream saida, String mensagem) {

		for (Map.Entry<String, PrintStream> usuario : USUARIOS_CONECTADOS.entrySet()) {
			PrintStream chat = usuario.getValue();
			chat.println(this.nomeUsuario + " : " + mensagem);
		}
	}

	/**
	 * @param loginUsuario String com o login do usuário
	 * @param saida        Objeto PrintStream com a conexão de saida do cliente
	 * @return void
	 */
	private void logarUsuario(String loginUsuario, PrintStream saida) {

		USUARIOS_CONECTADOS.put(loginUsuario, saida);
		this.usuarioLogado = true;

	}

	/**
	 * @param loginUsuario String com o login do usuário
	 * @return void
	 */
	private void deslogarUsuario(String loginUsuario) {

		USUARIOS_CONECTADOS.remove(loginUsuario);
		this.usuarioLogado = false;

	}

	/**
	 * @param loginUsuario String com o login do usuário
	 * @return boolean caso o usuário passe em todos as validações retorna true
	 */
	protected boolean validaUsuario(String loginUsuario) {

		for (Map.Entry<String, PrintStream> usuario : USUARIOS_CONECTADOS.entrySet()) {

			// não pode haver usuario logado com o mesmo nome
			if (usuario.getKey().equalsIgnoreCase(loginUsuario)) {
				System.out.println("Usuário já logado no sistema");
				return false;
			}
			// login não pode ser vazio
			if (loginUsuario.trim().equals("")) {
				System.out.println("O nome do usuario não pode ser nulo");
				return false;
			}
		}
		return true;
	}

}