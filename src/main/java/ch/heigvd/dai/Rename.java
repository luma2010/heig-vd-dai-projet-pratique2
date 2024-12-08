package ch.heigvd.dai;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Rename {
    public static int renameVar(String fileName, String varName, String newVarname) {

        try (InputStream is = new FileInputStream(fileName);
             Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(reader);
        ) {
            boolean varExists = false;
            boolean newVarExists = false;
            List<String> lines = new ArrayList<>();
            String line;

            // Première passe pour vérifier l'existence de varName et newVarname
            while ((line = br.readLine()) != null) {
                String[] part = line.split("=");
                if (Objects.equals(part[0], varName)) {
                    varExists = true;
                }
                if (Objects.equals(part[0], newVarname)) {
                    newVarExists = true;
                }
                lines.add(line + "\n");
            }

            // Si varName existe, et que newVarname n'existe pas, on procède au renommage
            if (varExists && !newVarExists) {

                try (OutputStream os = new FileOutputStream(fileName);
                     Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);
                     BufferedWriter bw = new BufferedWriter(writer)) {

                    for (int i = 0; i < lines.size(); i++) {
                        String[] part = lines.get(i).split("=");
                        if (Objects.equals(part[0], varName)) {
                            part[0] = newVarname;
                            String newVar = part[0] + "=" + part[1];
                            lines.set(i, newVar);
                        }
                        bw.write(lines.get(i));
                    }
                    bw.flush();
                } catch (IOException e) {
                    System.out.println("Exception " + e);
                }
                System.out.println("Variable " + varName + " is now " + newVarname);

            } else if(!varExists) {
                System.out.println("Variable " + varName + " not found, no changes made.");
                return 1;
            } else {
                System.out.println("Variable " + varName + " not changed because " + newVarname + " already exists.");
                return 2;
            }

        } catch (IOException e) {
            System.out.println("Exception " + e);
            return 4;
        }
        return 0;
    }
}
