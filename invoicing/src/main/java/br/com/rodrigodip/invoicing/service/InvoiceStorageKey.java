package br.com.rodrigodip.invoicing.service;

public final class InvoiceStorageKey {

    private InvoiceStorageKey() {
    }

    public static String forOrder(Long orderId) {
        return "invoices/" + orderId + ".pdf";
    }

    public static String downloadPath(Long orderId) {
        return "/invoices/" + orderId + "/download";
    }
}
