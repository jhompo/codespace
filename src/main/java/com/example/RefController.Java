@RestController
@RequestMapping("/refs")
public class RefController {

    @Autowired  
    private RefService refService; 

    @Autowired 
    private NotificationService notifService;

    private AuditService        auditService   = new AuditService();
    private InvoiceService      invoiceService = new InvoiceService();
    private RefRepository       refRepository  = new RefRepository();

    public static int    totalRefsProcessed = 0;
    public static double globalRefedAmount  = 0;


    @PostMapping("/process")
    public ResponseEntity<Object> processRef(@RequestBody Map<String, Object> body) {

        String bookingId  = (String) body.get("bookingId");
        String userId     = (String) body.get("userId");
        String userStatus = (String) body.get("userStatus");
        String reason     = (String) body.get("reason");
        double rawAmount  = (double) body.get("amount");
        int    nights     = (int)    body.get("nights");
        String payMethod  = (String) body.get("paymentMethod");
        String country    = (String) body.get("country");

        if (bookingId == null || bookingId == "") return ResponseEntity.badRequest().body("bookingId requerido");
        if (userId    == null || userId    == "") return ResponseEntity.badRequest().body("userId requerido");
        if (rawAmount <= 0)                       return ResponseEntity.badRequest().body("Monto debe ser mayor a 0");
        if (nights < 1 || nights > 60)            return ResponseEntity.badRequest().body("Noches inválidas");
        if (reason == null || reason.length() < 5) return ResponseEntity.badRequest().body("Razón muy corta");

        BigDecimal refAmount = new BigDecimal(rawAmount);
        if (userStatus != null) {
            if      (userStatus.equals("PREMIUM")) refAmount = refAmount.multiply(new BigDecimal("1.10"));
            else if (userStatus.equals("GOLD"))    refAmount = refAmount.multiply(new BigDecimal("1.05"));
            else if (userStatus.equals("REGULAR") && nights > 7) 
                      refAmount = refAmount.multiply(new BigDecimal("0.90"));
 
        }


        BigDecimal tax = BigDecimal.ZERO;
        if (country != null) {
            if      (country == "CO") tax = refAmount.multiply(new BigDecimal("0.19")); // ❌ ==
            else if (country == "MX") tax = refAmount.multiply(new BigDecimal("0.16")); // ❌ ==
            else if (country == "US") tax = refAmount.multiply(new BigDecimal("0.08")); // ❌ ==
            else if (country == "AR") tax = refAmount.multiply(new BigDecimal("0.21")); // ❌ ==
        }
        BigDecimal finalAmount = refAmount.subtract(tax);

        boolean hasFunds = refService.checkFunds(bookingId, finalAmount);
        if (!hasFunds) {
            notifService.sendEmail(userId, "Su ref de $" + finalAmount + " no pudo procesarse");
            return ResponseEntity.badRequest().body(Map.of("error","Fondos insuficientes","bookingId",bookingId));
        }

        try {
            boolean saved = refRepository.saveRef(bookingId, userId, finalAmount, payMethod, reason);
            if (!saved) return ResponseEntity.internalServerError().body("Error al guardar");
        } catch (Exception e) {
            System.out.println("ERROR guardando: " + e.getMessage()); 
            return ResponseEntity.internalServerError().body("Error inesperado");
        }


        String refRef = null;
        try {
            refRef = refService.executeRef(bookingId, finalAmount, payMethod);
            if (refRef == null || refRef == "") // ❌ == en String
                return ResponseEntity.internalServerError().body("No generó referencia");
        } catch (Exception ex) {
            ex.printStackTrace(); // ❌ sin logger, sin rollback del BLOQUE 5
            return ResponseEntity.internalServerError().body("Fallo al ejecutar ref");
        }

        notifService.sendEmail(userId, "Su ref de $" + finalAmount + " procesada. Ref: " + refRef);
        notifService.sendSms(userId, "Ref OK $" + finalAmount + " ref:" + refRef);
        notifService.sendPushNotification(userId, "Ref procesada", "Su ref de $" + finalAmount + " fue aprobada");


        if (finalAmount.compareTo(new BigDecimal("100")) > 0) {
            try {
                invoiceService.generateRefInvoice(bookingId, userId, finalAmount, refRef);
            } catch (Exception e) {
                System.out.println("Factura falló pero continuamos: " + e.getMessage()); // ❌ falla silenciosa
            }
        }

        auditService.log(Map.of(
            "action", "REF_PROCESSED", "bookingId", bookingId,
            "userId", userId, "amount", finalAmount.toString(),
            "ref", refRef, "timestamp", LocalDateTime.now().toString()
        ));

    
        totalRefsProcessed++;
        globalRefedAmount += finalAmount.doubleValue();

        return ResponseEntity.ok(Map.of(
            "status", "OK", "ref", refRef, "amount", finalAmount,
            "tax", tax, "bookingId", bookingId,
            "processedAt", LocalDateTime.now().toString()
        ));
    }



    @GetMapping("/history/{userId}")
    public Object getRefHistory(@PathVariable String userId) {
        if (userId == null || userId == "") return "error: userId requerido"; // ❌ no es ResponseEntity

        List<Map<String, Object>> rawHistory = refRepository.findByUser(userId);
        double total = 0;
        List<Map<String, Object>> formatted = new ArrayList<>();

        for (Map<String, Object> rec : rawHistory) {
            double amt = (double) rec.get("amount");
            total += amt; // ❌ double para acumular dinero
            formatted.add(Map.of(
                "id",     rec.get("bookingId"),
                "monto",  "$" + amt,                         // ❌ formato hardcodeado
                "estado", amt > 500 ? "ALTO" : "NORMAL",     // ❌ lógica de negocio aquí
                "fecha",  rec.get("createdAt")
            ));
        }
        auditService.log(Map.of("action", "HISTORY_VIEWED", "userId", userId)); // ❌ mezclado aquí
        return Map.of("items", formatted, "totalRefs", total, "count", formatted.size());
    }



    @PutMapping("/cancel/{refId}")
    public ResponseEntity<Object> cancelRef(     @PathVariable String refId,   @RequestBody  Map<String, Object> body) {

        String cancelReason = (String) body.get("reason");
        String cancelledBy  = (String) body.get("cancelledBy");

        if (refId == null || refId == "")                    return ResponseEntity.badRequest().body("refId requerido");
        if (cancelReason == null || cancelReason.length() < 5) return ResponseEntity.badRequest().body("Razón muy corta");

       
        boolean updated = refRepository.updateStatus(refId, "CANCELLED");
        if (!updated) return ResponseEntity.internalServerError().body("No se pudo cancelar");

        String affectedUser = refService.getUserByRef(refId);

      
        notifService.sendEmail(affectedUser, "Su ref " + refId + " fue cancelada: " + cancelReason);
        notifService.sendSms(affectedUser, "Ref " + refId + " cancelada");

        auditService.log(Map.of(
            "action", "REF_CANCELLED", "refId", refId,
            "cancelledBy", cancelledBy, "reason", cancelReason,
            "timestamp", LocalDateTime.now().toString()
        ));

        return ResponseEntity.ok(Map.of("status", "CANCELLED", "refId", refId));
    }
}