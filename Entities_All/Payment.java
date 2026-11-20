package Entities_All;

public class Payment {
    private double flightFare;
    private double classSurcharge;
    private double demandAdjustment;
    private double taxes;           // 15 % of fare
    private double serviceCharges;  // flat fee
    private String paymentMethod;   // Credit Card / Debit Card / Mobile Banking / Cash
    private String paymentStatus;   // Pending / Completed / Failed
    private double totalAmount;
    private String transactionId;

    public Payment() {
        this.paymentStatus = "Pending";
        this.serviceCharges = 500.0;
    }

    public Payment(double flightFare, double classSurcharge, double demandAdjustment,
                   String paymentMethod) {
        this();
        this.flightFare       = flightFare;
        this.classSurcharge   = classSurcharge;
        this.demandAdjustment = demandAdjustment;
        this.paymentMethod    = paymentMethod;
        this.taxes            = calculateTaxes();
        this.totalAmount      = calculateTotal();
    }

    // ── Getters ────────────────────────────────────────────────────────────────
    public double getFlightFare()       { return flightFare; }
    public double getClassSurcharge()   { return classSurcharge; }
    public double getDemandAdjustment() { return demandAdjustment; }
    public double getTaxes()            { return taxes; }
    public double getServiceCharges()   { return serviceCharges; }
    public String getPaymentMethod()    { return paymentMethod; }
    public String getPaymentStatus()    { return paymentStatus; }
    public double getTotalAmount()      { return totalAmount; }
    public String getTransactionId()    { return transactionId; }

    // ── Setters ────────────────────────────────────────────────────────────────
    public void setFlightFare(double f)       { flightFare = f; recalculate(); }
    public void setClassSurcharge(double c)   { classSurcharge = c; recalculate(); }
    public void setDemandAdjustment(double d) { demandAdjustment = d; recalculate(); }
    public void setPaymentMethod(String m)    { paymentMethod = m; }
    public void setPaymentStatus(String s)    { paymentStatus = s; }
    public void setServiceCharges(double sc)  { serviceCharges = sc; recalculate(); }

    // ── Business methods ───────────────────────────────────────────────────────
    private void recalculate() {
        taxes = calculateTaxes();
        totalAmount = calculateTotal();
    }

    public double calculateTaxes() {
        return Math.round((flightFare + classSurcharge + demandAdjustment) * 0.15 * 100.0) / 100.0;
    }

    public double calculateTotal() {
        return Math.round((flightFare + classSurcharge + demandAdjustment + calculateTaxes() + serviceCharges) * 100.0) / 100.0;
    }

    public boolean processPayment() {
        if (paymentMethod == null || paymentMethod.isEmpty()) return false;
        // Simulated payment — always succeeds in demo
        this.transactionId = "TXN" + System.currentTimeMillis();
        this.paymentStatus = "Completed";
        this.totalAmount   = calculateTotal();
        return true;
    }

    public String paymentSummary() {
        return String.format(
            "── Payment Summary ─────────────────\n" +
            "  Base Fare:          ৳%,10.2f\n" +
            "  Class Surcharge:    ৳%,10.2f\n" +
            "  Demand Adjustment:  ৳%,10.2f\n" +
            "  Taxes (15%%):        ৳%,10.2f\n" +
            "  Service Charges:    ৳%,10.2f\n" +
            "────────────────────────────────────\n" +
            "  TOTAL:              ৳%,10.2f\n" +
            "  Method: %s\n" +
            "  Status: %s\n" +
            (transactionId != null ? "  Txn ID: " + transactionId : ""),
            flightFare, classSurcharge, demandAdjustment,
            taxes, serviceCharges, totalAmount,
            paymentMethod, paymentStatus);
    }
}
