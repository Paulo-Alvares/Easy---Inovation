package com.mycompany.inovacao;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.Instant;

public class CleanerLocal {
    public static void main(String[] args) {
        String diretorio = "/home/diegovieira/Downloads/Teste_arquivos"; // Substitua pelo diretório desejado

        File[] arquivos = new File(diretorio).listFiles();
        if (arquivos != null) {
            for (File arquivo : arquivos) {
                //if (isInativo(arquivo.toPath())) {
                    if (fazerBackup(arquivo)) {
                        if (arquivo.delete()) {
                            System.out.println("Arquivo " + arquivo.getName() + " excluído.");
                        } else {
                            System.out.println("Erro ao excluir arquivo " + arquivo.getName());
                        }
                    } else {
                        System.out.println("Erro ao fazer backup do arquivo " + arquivo.getName());
                    }
                //} else {
                //    System.out.println(arquivo.getName() + " é recente.");
                //}
            }
        }
    }

    private static boolean isInativo(Path path) {
        try {
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            Instant lastModifiedTime = attributes.lastModifiedTime().toInstant();
            Instant now = Instant.now();
            Duration duration = Duration.between(lastModifiedTime, now);
            long diasInativos = duration.toDays();
            return diasInativos >= 30; // Defina o número de dias para considerar um arquivo como inativo
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean fazerBackup(File arquivo) {
        String diretorioBackup = System.getProperty("user.home") + "/Desktop/Backup"; // Pasta de backup no desktop

        File pastaBackup = new File(diretorioBackup);
        if (!pastaBackup.exists()) {
            if (!pastaBackup.mkdir()) {
                System.out.println("Erro ao criar pasta de backup.");
                return false;
            }
        }

        String nomeArquivo = arquivo.getName();
        File destinoBackup = new File(diretorioBackup, nomeArquivo);

        try {
            Files.move(arquivo.toPath(), destinoBackup.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Arquivo " + arquivo.getName() + " movido para o backup.");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
