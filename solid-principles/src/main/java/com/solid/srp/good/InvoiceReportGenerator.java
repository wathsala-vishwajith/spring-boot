package com.solid.srp.good;

/**
 * Separate class responsible ONLY for generating reports
 */
public class InvoiceReportGenerator {

    public String generateTextReport(Invoice invoice) {
        StringBuilder report = new StringBuilder();
        report.append("Invoice: ").append(invoice.getInvoiceNumber()).append("\n");
        report.append("Customer: ").append(invoice.getCustomerEmail()).append("\n");
        report.append("Items:\n");
        for (LineItem item : invoice.getItems()) {
            report.append("  - ").append(item.getName())
                  .append(": $").append(item.getPrice())
                  .append(" x ").append(item.getQuantity())
                  .append("\n");
        }
        report.append("Total: $").append(invoice.calculateTotal());
        return report.toString();
    }

    public String generateHtmlReport(Invoice invoice) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<h1>Invoice: ").append(invoice.getInvoiceNumber()).append("</h1>");
        html.append("<p>Customer: ").append(invoice.getCustomerEmail()).append("</p>");
        html.append("<table>");
        for (LineItem item : invoice.getItems()) {
            html.append("<tr>");
            html.append("<td>").append(item.getName()).append("</td>");
            html.append("<td>$").append(item.getPrice()).append("</td>");
            html.append("<td>").append(item.getQuantity()).append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        html.append("<p><strong>Total: $").append(invoice.calculateTotal()).append("</strong></p>");
        html.append("</body></html>");
        return html.toString();
    }
}
