import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.*;

public class LogAnalyzer {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Entrez la Request ID à analyser : ");
        String requestId = scanner.nextLine().trim();

        String operation = null;
        String poStatus = null;
        String woStatus = null;
        String soStatus = null;
        String processingTimeSO = null;

        Long startTimeSO = null;
        Long endTimeSO = null;

        try (BufferedReader reader = new BufferedReader(new FileReader("kpsaOrder.log"))) {
            String line;

            while ((line = reader.readLine()) != null) {

                // Opérations 
                if (line.contains("ServiceOrderData SWAPPED IN") && line.contains(requestId)) {
                    Pattern pattern = Pattern.compile("\\|([^|]+)\\|ServiceOrderData SWAPPED IN");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        operation = "GSM:ContractActivation"; // ou extraire depuis le log si c'est variable
                    }

                    // Extraire timestamp pour début SO
                    startTimeSO = extractTimestampMillis(line);
                }

                // PO
                if (line.contains("Product Order Execution ENDED") && line.contains(requestId)) {
                    Pattern pattern = Pattern.compile("STATUS : ([A-Z]+)");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        poStatus = matcher.group(1);
                    }
                }

                // WO
                if (line.contains("Cartridge WO Execution ENDED") && line.contains(requestId)) {
                    Pattern pattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}).*INSTANCE NAME : ([^:]+) : STATUS : ([A-Z]+)");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        String timestamp = matcher.group(1);
                        String instance = matcher.group(2);
                        String status = matcher.group(3);
                        woStatus = status + " (" + instance + ") à " + timestamp;
                    }
                }

                // SO
                if (line.contains("ServiceOrderData") && line.contains(requestId)) {
                    if (line.contains("DELETED")) {
                        soStatus = "DELETED";
                        endTimeSO = extractTimestampMillis(line);
                    } else if (line.contains("PROCESSED")) {
                        soStatus = "PROCESSED";
                    } else if (line.contains("FAILED")) {
                        soStatus = "FAILED";
                    }
                }
            }

            // Calcul temps SO
            if (startTimeSO != null && endTimeSO != null) {
                long diffMillis = endTimeSO - startTimeSO;
                processingTimeSO = String.format("%.2f secondes", diffMillis / 1000.0);
            } else {
                processingTimeSO = "Non disponible";
            }

            // Résultat final
            System.out.println("\nRésultat pour la Request ID " + requestId + ":");
            System.out.println("- Opération       : " + (operation != null ? operation : "Non trouvée"));
            System.out.println("- Statut PO       : " + (poStatus != null ? poStatus : "Non trouvé"));
            System.out.println("- Statut WO       : " + (woStatus != null ? woStatus : "Non trouvé"));
            System.out.println("- Statut SO       : " + (soStatus != null ? soStatus : "Non trouvé"));
            System.out.println("- Temps SO        : " + (processingTimeSO != null ? processingTimeSO : "Non trouvé"));

        } catch (IOException e) {
            System.err.println("Erreur de lecture du fichier : " + e.getMessage());
        }
    }

    private static Long extractTimestampMillis(String line) {
        Pattern pattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String timestampStr = matcher.group(1);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = sdf.parse(timestampStr);
                return date.getTime();
            } catch (ParseException e) {
                return null;
            }
        }
        return null;
    }
}
