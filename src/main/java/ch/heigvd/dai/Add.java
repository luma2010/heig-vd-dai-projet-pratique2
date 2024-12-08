package ch.heigvd.dai;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Add {

    /**
     * Méthode pour ajouter une variable dans un fichier.
     * @param fileName Le nom du fichier où ajouter la variable.
     * @param varName Le nom de la variable.
     * @param varValue La valeur de la variable.
     */
    public static int addVariableToFile(String fileName, String varName, String varValue) {
        try (InputStream is = new FileInputStream(fileName);
             Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(reader);
             OutputStream os = new FileOutputStream(fileName);
             Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(writer);){
            boolean alreadyExist = false;

            // Lire le fichier
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line + "\n");
            }

            // Écriture dans le fichier
            for (String currentLine : lines) {
                String[] part = currentLine.split("=");
                if (Objects.equals(part[0], varName)) {
                    alreadyExist = true;
                    System.out.println("Warning, variable " + varName + " already exists");
                }
                bw.write(currentLine);
            }

            if (!alreadyExist) {
                String variable = varName + "=" + varValue + "\n";
                bw.write(variable);
                System.out.println("Wrote new variable " + varName + " with value " + varValue);
            }

            bw.flush();
            if(alreadyExist){
                return 1;
            }

        } catch (IOException e) {
            System.out.println("Exception " + e);
            return 3;
        }
        return 0;
    }
}
