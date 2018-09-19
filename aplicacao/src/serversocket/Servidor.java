
package serversocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Servidor extends Thread
{

   private Socket conexao;
   private static int PORTA_PADRAO = 12345;
   private static Map<String, PrintStream> USUARIOS_CONECTADOS;
   private static List<String> LISTA_DE_NOMES = new ArrayList<String>();
   private String nomeUsuario;


   public Servidor(Socket socket)
   {
      this.conexao = socket;
   }

   public static void main(String[] args)
   {
      USUARIOS_CONECTADOS = new HashMap<String, PrintStream>();
      
      try
      {
         ServerSocket server = new ServerSocket(PORTA_PADRAO);
         while (true)
         {
            Socket conexao = server.accept();
            Thread thread = new Servidor(conexao);
            thread.start();
         }
      }
      catch (IOException e)
      {

      }
   }

   @Override
   public void run()
   {

      String msg = null;

      try
      {
         BufferedReader entrada = new BufferedReader(new InputStreamReader(this.conexao.getInputStream()));
         PrintStream saida = new PrintStream(this.conexao.getOutputStream());
         
         String login = entrada.readLine();
         if (validaUsuario(login))
         {
            this.nomeUsuario = login;
            LISTA_DE_NOMES.add(this.nomeUsuario);
            USUARIOS_CONECTADOS.put(this.nomeUsuario, saida);

            System.out.println("O usuario " + this.nomeUsuario + " Conectou ao chat!");
            saida.println("Usuarios Conectados:" + LISTA_DE_NOMES.toString());
         }
         else
            return;

         while (true)
         {
            msg = entrada.readLine();
            System.out.println("[LOG] Mensagem de " + this.nomeUsuario + " : " + msg);
            send(saida, "Escreveu", msg);
         }

      }
      catch (IOException e)
      {

      }
   }

   private boolean validaUsuario(String nomeUsuario)
   {

      if (nomeUsuario.isEmpty())
      {
         System.out.println("Nome do usuário não pode ser vazio");
         return false;
      }
      for (int i = 0; i < LISTA_DE_NOMES.size(); i++)
      {
         if (LISTA_DE_NOMES.get(i).equalsIgnoreCase(nomeUsuario))
         {
            System.out.println("Nome do usuário já existe no Chat");
            return false;
         }
      }

      return true;
   }

   private void send(PrintStream saida, String acao,String mensagem)
   {

      for (Map.Entry<String, PrintStream> cliente : USUARIOS_CONECTADOS.entrySet())
      {
         if (cliente.getValue() != saida)
         {
            PrintStream chat = cliente.getValue();
            chat.println(cliente.getKey() + "  : " + mensagem);
         }
      }

   }
}
