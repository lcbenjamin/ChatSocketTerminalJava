package serversocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Cliente extends Thread
{

   private static int PORTA_PADRAO = 12345;
   private Socket conexao;

   public Cliente(Socket conexao)
   {
      this.conexao = conexao;
   }

   public static void main(String[] args)
   {

      try
      {
         Socket socket = new Socket("127.0.0.1", PORTA_PADRAO);

         PrintStream saida = new PrintStream(socket.getOutputStream());
         BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
         System.out.print("Digite seu nome: ");

         String meuNome = teclado.readLine();

         // envia o nome digitado para o servidor

         saida.println(meuNome.toUpperCase());
         // instancia a thread para ip e porta conectados e depois inicia ela

         Thread thread = new Cliente(socket);

         thread.start();

         // Cria a variavel msg responsavel por enviar a mensagem para o servidor
         String msg;
         while (true)
         {
            // cria linha para digitação da mensagem e a armazena na variavel msg
            System.out.print("Mensagem > ");
            msg = teclado.readLine();
            // envia a mensagem para o servidor
            saida.println(msg);
         }

      }
      catch (UnknownHostException e)
      {

      }
      catch (IOException e)
      {

      }

   }

   @Override
   public void run()
   {
      try
      {
         // recebe mensagens de outro cliente através do servidor
         BufferedReader entrada =
            new BufferedReader(new InputStreamReader(this.conexao.getInputStream()));
         // cria variavel de mensagem
         String msg;
         while (true)
         {
            // pega o que o servidor enviou
            msg = entrada.readLine();
            // se a mensagem contiver dados, passa pelo if,
            // caso contrario cai no break e encerra a conexao
            if (msg == null)
            {
               System.out.println("Conexão encerrada!");
               System.exit(0);
            }
            System.out.println();
            // imprime a mensagem recebida
            System.out.println(msg);
            // cria uma linha visual para resposta
            System.out.print("Responder > ");
         }
      }
      catch (IOException e)
      {
         // caso ocorra alguma exceção de E/S, mostra qual foi.
         System.out.println("Ocorreu uma Falha... .. ." +
            " IOException: " + e);
      }
   }
}

