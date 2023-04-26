import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Calendar;
import java.util.Date;

public class Inovacao {

  public static void main(String[] args) {
    // Diretório a ser limpo
    String diretorio = "";

    // Data limite para manter os arquivos (30 dias atrás)
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_MONTH, -30);
    Date dataLimite = calendar.getTime();

    // Lista de arquivos no diretório
    File[] arquivos = new File(diretorio).listFiles();
    
      // Verifica se o array de arquivos não é nulo antes de iterar sobre ele
    if (arquivos != null) {
      // Percorre a lista de arquivos e exclui os que atendem aos critérios
      for (File arquivo : arquivos) {
      try {
        BasicFileAttributes attr = Files.readAttributes(arquivo.toPath(), BasicFileAttributes.class);
        Date dataModificacao = new Date(attr.lastModifiedTime().toMillis());
        
        if (dataModificacao.before(dataLimite)) {
            System.out.println(arquivo);
            arquivo.delete();
        }
      } catch (IOException e) {
        System.out.println("Erro ao ler os atributos do arquivo " + arquivo.getName());
      }
    }
    } else {
      System.out.println("Não há arquivos no diretório " + diretorio);
    }
  }
}
