# E-Commerce System Enhancement TODO

## Phase 1: Product Enhancements ✅ COMPLETED
- [x] Update Product.java to add buyingPrice, sellingPrice, expiryDate fields
- [x] Update ProductTable.sql to add new columns (buying_price, selling_price, expiry_date)
- [x] Update AddProductController.java to handle new fields
- [x] Update AddProduct.fxml to include new input fields
- [x] Update CreateSaleController.java to use selling price for sales

## Phase 2: Memento Pattern for Draft Sales
- [ ] Create SaleMemento.java to store sale state
- [ ] Create SaleOriginator.java to create/manage mementos
- [ ] Create SaleCaretaker.java to manage memento storage (in-memory)
- [ ] Modify CreateSaleController.java to save drafts when customer leaves
- [ ] Add warning dialog when drafts are being cleared

## Phase 3: Observer Pattern for Alerts
- [ ] Create AlertObserver.java interface
- [ ] Create StockAlertObserver.java and ExpiryAlertObserver.java implementations
- [ ] Create AlertSubject.java to manage observers
- [ ] Create AlertManager.java singleton to coordinate alerts
- [ ] Update DashboardController.java to display alerts
- [ ] Add background service to check for low stock (< 10) and expired products

## Phase 4: Bridge Pattern for Sales Reports
- [ ] Create Report.java interface (abstraction)
- [ ] Create SalesReport.java implementation
- [ ] Create ReportGenerator.java interface (implementor)
- [ ] Create PDFReportGenerator.java, PrintReportGenerator.java, EmailReportGenerator.java
- [ ] Create ReportBridge.java to connect abstraction and implementor

## Phase 5: Strategy Pattern for Report Handling
- [ ] Create ReportStrategy.java interface
- [ ] Create DownloadStrategy.java, PrintStrategy.java, EmailStrategy.java implementations
- [ ] Create ReportContext.java to execute strategies
- [ ] Add methods to show success messages for each strategy

## Phase 6: Database Integration
- [ ] Update all SQL queries to work with new product schema
- [ ] Ensure DatabaseManager singleton is used throughout
- [ ] Add methods to check for low stock and expired products

## Phase 7: UI Updates
- [ ] Update both Admin and Employee dashboards to show alerts
- [ ] Add alert notifications to dashboard FXML files
- [ ] Update product display to show both buying and selling prices

## Testing
- [ ] Test product addition with new fields
- [ ] Test draft saving functionality
- [ ] Test alert system for low stock and expiry
- [ ] Test sales report generation with different strategies
- [ ] Integration testing of all components
