//package com.mycompany.inovacao;
//
//import com.microsoft.graph.auth.publicClient.UsernamePasswordProvider;
//import com.microsoft.graph.models.extensions.IGraphServiceClient;
//import com.microsoft.graph.requests.extensions.GraphServiceClient;
//import com.microsoft.graph.requests.extensions.IOnedriveRootFolderRequest;
//import com.microsoft.graph.requests.extensions.IOnedriveRootItemRequest;
//import com.microsoft.graph.requests.extensions.IOnedriveRootItemRequestBuilder;
//import com.microsoft.graph.requests.extensions.OnedriveRootFolderRequestBuilder;
//import com.microsoft.graph.requests.extensions.OnedriveRootItemRequestBuilder;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.attribute.BasicFileAttributes;
//import java.time.Duration;
//import java.time.Instant;
//import java.util.Properties;
//
//public class CleanerOneDrive {
//    private static final String CLIENT_ID = "YOUR_CLIENT_ID";
//    private static final String CLIENT_SECRET = "YOUR_CLIENT_SECRET";
//    private static final String USERNAME = "YOUR_USERNAME";
//    private static final String PASSWORD = "YOUR_PASSWORD";
//    private static final String DRIVE_ROOT_ID = "YOUR_DRIVE_ROOT_ID";
//
//    public static void main(String[] args) {
//        String diretorio = "/home/diegovieira/Downloads/Teste_arquivos"; // Substitua pelo diretório desejado
//
//        File[] arquivos = new File(diretorio).listFiles();
//        if (arquivos != null) {
//            for (File arquivo : arquivos) {
//                if (isInativo(arquivo.toPath())) {
//                    if (enviarParaOneDrive(arquivo)) {
//                        if (arquivo.delete()) {
//                            System.out.println("Arquivo " + arquivo.getName() + " excluído.");
//                        } else {
//                            System.out.println("Erro ao excluir arquivo " + arquivo.getName());
//                        }
//                    } else {
//                        System.out.println("Erro ao enviar arquivo " + arquivo.getName() + " para o OneDrive");
//                    }
//                } else {
//                    System.out.println(arquivo.getName() + " é recente.");
//                }
//            }
//        }
//    }
//
//    private static boolean isInativo(Path path) {
//        try {
//            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
//            Instant lastModifiedTime = attributes.lastModifiedTime().toInstant();
//            Instant now = Instant.now();
//            Duration duration = Duration.between(lastModifiedTime, now);
//            long diasInativos = duration.toDays();
//            return diasInativos >= 30; // Defina o número de dias para considerar um arquivo como inativo
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    private static boolean enviarParaOneDrive(File arquivo) {
//        try {
//            Properties properties = new Properties();
//            properties.load(new FileInputStream("config.properties"));
//
//            UsernamePasswordProvider authProvider = new UsernamePasswordProvider(CLIENT_ID, CLIENT_SECRET,
//                    USERNAME, PASSWORD, properties.getProperty("authorityUrl"));
//
//            IGraphServiceClient graphClient = GraphServiceClient.builder()
//                    .authenticationProvider(authProvider)
//                    .buildClient();
//
//            IOnedriveRootItemRequestBuilder driveRootItemRequestBuilder = graphClient
//                    .me().drive(DRIVE_ROOT_ID).root().itemWithPath(arquivo.getName());
//
//            IOnedriveRootItemRequestBuilder uploadRequestBuilder = driveRootItemRequestBuilder.content().buildRequest();
//
//            uploadRequestBuilder.put(new FileInputStream(arquivo));
//
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//}
