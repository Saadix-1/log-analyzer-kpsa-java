
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

        List<String> poInstances = new ArrayList<>();
        List<String> woInstances = new ArrayList<>();
        List<String> soInstances = new ArrayList<>();
        String accountCaller = null;

        Long swappedInTime = null;
        Long deletedTime = null;

        try (BufferedReader reader = new BufferedReader(new FileReader("kpsaOrder.log"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // GET ACCOUNT line
                if (line.contains("GET ACCOUNT") && line.contains(requestId)) {
                    Pattern pattern = Pattern.compile("ACCOUNT : ([^|]+)");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        accountCaller = matcher.group(1).trim();
                    }
                }

                // PO - Product Order
                if (line.contains("Product Order Execution ENDED") && line.contains(requestId)) {
                    Pattern pattern = Pattern.compile("INSTANCE NAME : ([^|]+).*STATUS : ([A-Z]+)");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        String instance = matcher.group(1).trim();
                        String status = matcher.group(2).trim();
                        poInstances.add(instance + " = " + status);
                    }
                }

                // WO - Work Order
                if (line.contains("Cartridge WO Execution ENDED") && line.contains(requestId)) {
                    Pattern pattern = Pattern.compile("INSTANCE NAME : ([^|]+).*STATUS : ([A-Z]+)");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        String instance = matcher.group(1).trim();
                        String status = matcher.group(2).trim();
                        woInstances.add(instance + " = " + status);
                    }
                }

                // SO - Service Order
                if (line.contains("ServiceOrderData SWAPPED IN") && line.contains(requestId)) {
                    swappedInTime = extractTimestampMillis(line);
                }
                if (line.contains("ServiceOrderData DELETED") && line.contains(requestId)) {
                    deletedTime = extractTimestampMillis(line);
                    Pattern pattern = Pattern.compile("INSTANCE NAME : ([^|]+).*STATUS : ([A-Z]+)");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        String instance = matcher.group(1).trim();
                        String status = matcher.group(2).trim();
                        soInstances.add(instance + " = " + status);
                    }
                }
            }

            // Résultat
            System.out.println("\n--- Résultat pour la Request ID " + requestId + " ---");
            System.out.println("- Compte appelant : " + (accountCaller != null ? accountCaller : "Non trouvé"));

            System.out.println("\n- Product Order (PO):");
            if (!poInstances.isEmpty()) {
                for (String po : poInstances) System.out.println("   ➤ " + po);
            } else {
                System.out.println("   Aucun PO trouvé.");
            }

            System.out.println("\n- Work Order (WO):");
            if (!woInstances.isEmpty()) {
                for (String wo : woInstances) System.out.println("   ➤ " + wo);
            } else {
                System.out.println("   Aucun WO trouvé.");
            }

            System.out.println("\n- Service Order (SO):");
            if (!soInstances.isEmpty()) {
                for (String so : soInstances) System.out.println("   ➤ " + so);
            } else {
                System.out.println("   Aucun SO trouvé.");
            }

            if (swappedInTime != null && deletedTime != null) {
                long durationMillis = deletedTime - swappedInTime;
                System.out.println("\n- Temps de traitement SO : " + durationMillis + " millisecondes");
            } else {
                System.out.println("\n- Temps de traitement SO : Données incomplètes");
            }

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