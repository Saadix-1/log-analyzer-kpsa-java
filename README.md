# KPSA Log Analyzer (Java + Spring Boot)

This is a Java & Spring Boot-based tool developed as part of an internship at **Atos**.  
It reads production log files from the **KPSA provisioning system** used in the telecom domain and automatically extracts key operational statuses related to a specific request ID (PO / WO / SO).

---

## 🔍 Features

- Web-based interface using Spring Boot (localhost:8080)
- Upload or analyze a log file from the `resources` folder
- Read `.log` files line by line
- Extract and identify the Request ID
- Detect and display the operation type (e.g., GSM:ContractActivation)
- Parse and summarize:
  - ✅ PO (Product Order) status
  - ✅ WO (Work Order) status
  - ✅ SO (Service Order) status
- Convert SO processing time (microseconds ➝ seconds)
- Output results clearly on a webpage

---

## 🧰 Technologies

- Java (JDK 17+)
- Spring Boot (v3.5.3)
- Maven
- HTML + Thymeleaf (for minimal front-end)
- Regular Expressions (`java.util.regex`)
- File I/O with `BufferedReader`
- MVC pattern (Controller → Service → Model)

---

## 🏁 Getting Started

### 1. Prerequisites
- Java 17+
- Maven 3.9+
- (Optional) IDE like VS Code or IntelliJ

### 2. Run the Application

```bash
./mvnw spring-boot:run
````

Then open your browser and go to:
[http://localhost:8080](http://localhost:8080)

### 3. Analyze a Request ID

* Make sure the `kpsaOrder.log` file is present in `src/main/resources/`
* Enter a valid Request ID in the form
* The result will show the operation, statuses (PO, WO, SO), and processing time

---

## 📁 Project Structure

```
loganalyzer/
├── controller/
│   └── LogAnalyzerController.java
├── service/
│   └── LogAnalyzerService.java
├── model/
│   └── LogResult.java
├── resources/
│   ├── templates/index.html
│   └── kpsaOrder.log
└── LogAnalyzerApplication.java
```

---

## 📌 Example Output

```text
Résultat pour Request ID 2147578658 :
- Opération       : 2147578658
- Statut PO       : OK
- Statut WO       : OK (BSCSWS_01)
- Statut SO       : 2147578658
- Temps SO        : 13.34 secondes
```

---

## 🧑‍💻 Author

**Saad Mehamdi**
Software Engineering Intern @ Atos
University of Ottawa – 1st year

