package com.shwandashop.reports;

public class ReportBridge {
    private Report report;
    private ReportGenerator reportGenerator;

    public ReportBridge(Report report, ReportGenerator reportGenerator) {
        this.report = report;
        this.reportGenerator = reportGenerator;
    }

    public void generate() {
        report.generateReport();
        reportGenerator.generateReport();
    }
}
