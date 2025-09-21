package com.shwandashop.reports;

public class SalesReport implements Report {
    private ReportGenerator reportGenerator;

    public SalesReport(ReportGenerator reportGenerator) {
        this.reportGenerator = reportGenerator;
    }

    @Override
    public void generateReport() {
        reportGenerator.generateReport();
    }
}
