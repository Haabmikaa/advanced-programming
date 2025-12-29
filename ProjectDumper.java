import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

public class ProjectDumper {
    public static void main(String[] args) throws Exception {
        PrintWriter writer = new PrintWriter(new FileWriter("project_context.txt"));
        dump(new File("."), "", writer);
        writer.close();
        System.out.println("Done! Copy from project_context.txt");
    }

    private static void dump(File folder, String path, PrintWriter writer) {
        for (File file : folder.listFiles()) {
            String name = file.getName();
            // Skip large/useless folders
            if (name.equals(".git") || name.equals("bin") || name.equals("target") || name.equals(".metadata")) continue;
            
            if (file.isDirectory()) {
                dump(file, path + name + "/", writer);
            } else {
                // Only read code/web files
                if (name.endsWith(".java") || name.endsWith(".jsp") || name.endsWith(".html") || name.endsWith(".css") || name.endsWith(".xml") || name.endsWith(".sql")) {
                    writer.println("--- FILE: " + path + name + " ---");
                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = br.readLine()) != null) writer.println(line);
                    } catch (Exception e) {}
                    writer.println("\n");
                }
            }
        }
    }
}