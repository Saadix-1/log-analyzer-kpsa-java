# KPSA Log Analyzer (Java)

This is a Java-based tool developed as part of an internship at **Atos**.  
It reads production log files from the **KPSA provisioning system** used in the telecom domain and automatically extracts key operational statuses related to a specific request ID (PO / WO / SO).

---

##  Features

- Read `.log` files line by line
- Extract and identify the Request ID
- Detect and display the operation type (e.g., GSM:ContractActivation)
- Parse and summarize:
  - PO (Product Order) status
  - WO (Work Order) status
  - SO (Service Order) status
- Convert SO processing time (microseconds ➝ seconds)
- Outputs a clear summary in the console

---

##  Technologies

- Java (JDK 17+)
- Regular Expressions (`java.util.regex`)
- File I/O with `BufferedReader`
- Console interaction with `Scanner`

---

##  Example

**Input file:** `kpsaOrder.log`

