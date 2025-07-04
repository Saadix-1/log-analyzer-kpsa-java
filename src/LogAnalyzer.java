import java.io.*;
        import java.util.*;
        import java.util.regex.*;

public class    LogAnalyzer {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 1. Demander à l'utilisateur la Request ID
        System.out.print("Entrez la Request ID à analyser : ");
        String requestId = scanner.nextLine().trim();

        // 2. Initialiser les variables pour stocker les résultats
        String operation = null;
        String poStatus = null;
        String woStatus = null;
        String soStatus = null;
        String processingTimeSO = null;

        // 3. Lire le fichier log
        try (BufferedReader reader = new BufferedReader(new FileReader("kpsaOrder.log"))) {
            String line;

            while ((line = reader.readLine()) != null) {

                // a. Détecter l'opération (ligne SWAPPED IN)
                if (line.contains("ServiceOrderData SWAPPED IN") && line.contains(requestId)) {
                    Pattern pattern = Pattern.compile("\\|([^|]+)\\|ServiceOrderData SWAPPED IN");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        operation = matcher.group(1); 
                    }
                }

                // b. Statut PO (Product Order Execution ENDED)
                if (line.contains("Product Order Execution ENDED") && line.contains(requestId)) {
                    Pattern pattern = Pattern.compile("STATUS : ([A-Z]+)");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        poStatus = matcher.group(1);
                    }
                }

                // c. Statut WO (Cartridge WO Execution ENDED)
                if (line.contains("Cartridge WO Execution ENDED") && line.contains(requestId)) {
                    Pattern pattern = Pattern.compile("INSTANCE NAME : ([^:]+) : STATUS : ([A-Z]+)");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        String instance = matcher.group(1);
                        String status = matcher.group(2);
                        woStatus = status + " (" + instance + ")";
                    }
                }

                // d. Statut SO (ServiceOrderData DELETED)
                if (line.contains("ServiceOrderData DELETED") && line.contains(requestId)) {
                    Pattern pattern = Pattern.compile("\\|([^|]+)\\|ServiceOrderData SWAPPED IN");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        soStatus = matcher.group(1);
                        long micros = Long.parseLong(matcher.group(2));
                        processingTimeSO = String.format("%.2f secondes", micros / 1_000_000.0);
                    }
                }
            }

            // 4. Affichage du résultat
            System.out.println("\n Résultat pour la Request ID " + requestId + ":");
            System.out.println("- Opération       : " + (operation != null ? operation : "Non trouvée"));
            System.out.println("- Statut PO       : " + (poStatus != null ? poStatus : "Non trouvé"));
            System.out.println("- Statut WO       : " + (woStatus != null ? woStatus : "Non trouvé"));
            System.out.println("- Statut SO       : " + (soStatus != null ? soStatus : "Non trouvé"));
            System.out.println("- Temps SO        : " + (processingTimeSO != null ? processingTimeSO : "Non trouvé"));

        } catch (IOException e) {
            System.err.println("Erreur de lecture du fichier : " + e.getMessage());
        }
    }
}
