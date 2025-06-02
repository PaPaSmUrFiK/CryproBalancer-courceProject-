package cryptoBalancer.Utility;

import cryptoBalancer.Models.Entities.Investment;
import cryptoBalancer.Models.Entities.Portfolio;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.util.Units;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.data.general.DefaultPieDataset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ReportGenerator {

    public void generateReport(Portfolio portfolio) throws Exception {
        if (portfolio.getAnalytic() == null) {
            throw new IllegalStateException("Аналитика портфеля отсутствует.");
        }

        BigDecimal expectedReturn = portfolio.getAnalytic().getExpectedReturn();
        BigDecimal risk = portfolio.getAnalytic().getRisk();
//        if (expectedReturn.compareTo(BigDecimal.ZERO) == 0 || risk.compareTo(BigDecimal.ZERO) == 0) {
//            throw new IllegalStateException("Риск или доходность не могут быть нулевыми.");
//        }

        BigDecimal totalAmount = portfolio.getInvestments().stream()
                .map(Investment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Сравнение с допуском для избежания проблем с округлением
        if (totalAmount.subtract(BigDecimal.ONE).abs().compareTo(new BigDecimal("0.001")) > 0) {
            throw new IllegalStateException("Сумма долей активов не равна 100%. Фактическая сумма: "
                    + totalAmount.multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP) + "%");
        }

        createWordReport(portfolio);
    }

    private void createWordReport(Portfolio portfolio) throws Exception {
        XWPFDocument document = new XWPFDocument();
        String chartFileName = null;

        try {
            // Заголовок документа
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText("Отчет о портфеле");
            titleRun.setBold(true);
            titleRun.setFontSize(16);

            addEmptyLine(document, 1);

            // Информация о портфеле
            addKeyValueParagraph(document, "Название портфеля:", portfolio.getPortfolioName());
            addKeyValueParagraph(document, "Дата создания:", portfolio.getCreatedAt().toString());
            addKeyValueParagraph(document, "Ожидаемая доходность:", portfolio.getAnalytic().getExpectedReturn() + "%");
            addKeyValueParagraph(document, "Риск:", portfolio.getAnalytic().getRisk() + "%");

            addEmptyLine(document, 1);

            // Генерация и вставка диаграммы
            chartFileName = generatePieChart(portfolio);
            addImageToDocument(document, chartFileName);

            // Сохранение документа
            try (FileOutputStream out = new FileOutputStream("portfolio_report.docx")) {
                document.write(out);
            }
        } finally {
            // Закрытие ресурсов и удаление временного файла
            document.close();
            if (chartFileName != null) {
                new File(chartFileName).delete();
            }
        }
    }

    private void addKeyValueParagraph(XWPFDocument document, String key, String value) {
        XWPFParagraph para = document.createParagraph();
        para.setIndentationLeft(200); // Отступ слева
        XWPFRun run = para.createRun();
        run.setText(key + " " + value);
        run.setFontSize(12);
    }

    private void addEmptyLine(XWPFDocument doc, int lines) {
        for (int i = 0; i < lines; i++) {
            doc.createParagraph().createRun().addBreak();
        }
    }

    private String generatePieChart(Portfolio portfolio) throws IOException {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        for (Investment investment : portfolio.getInvestments()) {
            String cryptoName = investment.getCrypto().getName();
            double percentage = investment.getAmount().multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
            dataset.setValue(cryptoName, percentage);
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Распределение инвестиций",
                dataset,
                true,
                true,
                false
        );

        String fileName = "investment_chart.png";
        ChartUtils.saveChartAsPNG(new File(fileName), chart, 500, 300);
        return fileName;
    }

    private void addImageToDocument(XWPFDocument document, String imagePath) throws Exception {
        XWPFParagraph imgPara = document.createParagraph();
        imgPara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = imgPara.createRun();

        try (FileInputStream fis = new FileInputStream(imagePath)) {
            run.addPicture(
                    fis,
                    XWPFDocument.PICTURE_TYPE_PNG,
                    imagePath,
                    Units.toEMU(500),
                    Units.toEMU(300)
            );
        }
    }
}