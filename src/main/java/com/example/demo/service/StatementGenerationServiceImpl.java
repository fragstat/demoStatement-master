package com.example.demo.service;

import com.example.demo.Operation;
import com.example.demo.Type;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class StatementGenerationServiceImpl {

    private Date start;
    private Date end;
    private Type type;
    private XWPFDocument statement;

    public static void generate(List<Operation> operations, Type type) {
        Date start = new Date();
        start.setTime(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(2));
        new StatementGenerationServiceImpl().generateDocument(start, new Date(), type, operations);
    }

    public void generateDocument(Date start, Date end, Type type, List<Operation> operations) {
        this.start = start;
        this.end = end;
        this.type = type;
        this.statement = new XWPFDocument();
        statement.createStyles();
        String accountNum = "12345678";
        addHeader();
        populateAccountInfo();
        populateSummaryInfo();
        populateDetails(operations, accountNum);
        addFooter();
        saveStatement();
    }

    private void addHeader() {
        InputStream streamLogo = null;
        try {
            streamLogo = new FileInputStream("/Users/sergey/Desktop/demoStatement-master/src/main/resources/logoC.png");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        XWPFTable headerTable = statement.createTable(1, 3);
        headerTable.setCellMargins(0, 100, 0, 50);
        headerTable.setTopBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "000000");
        headerTable.setLeftBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "000000");
        headerTable.setBottomBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "000000");
        headerTable.setRightBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "000000");
        headerTable.setInsideHBorder(XWPFTable.XWPFBorderType.NONE, 0,0, "000000");
        headerTable.setInsideVBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "000000");
        headerTable.setWidth("100%");
        XWPFTableCell logoCell = headerTable.getRow(0).getCell(0);
        XWPFParagraph logoPar = logoCell.getParagraphs().get(0);
        logoPar.setAlignment(ParagraphAlignment.LEFT);
        try {
            logoPar.createRun().addPicture(streamLogo, Document.PICTURE_TYPE_PNG, "logo", 2250000, 500000);
        } catch (InvalidFormatException | IOException e) {
            e.printStackTrace();
        }
        XWPFTableCell addressCell = headerTable.getRow(0).getCell(1);
        XWPFTableCell phoneCell = headerTable.getRow(0).getCell(2);
        addressCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        addressCell.getParagraphs().get(0).createRun().setText("190000, г. Санкт-Петербург, ул. Большая Морская, д.29");
        phoneCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        phoneCell.getParagraphs().get(0).createRun().setText("8 (800) 100-24-24");
        phoneCell.setWidth("20%");
        statement.createParagraph();

    }

    private void addFooter() {
        InputStream streamStamp = null;
        try {
            streamStamp = new FileInputStream("/Users/sergey/Desktop/demoStatement-master/src/main/resources/sig.png");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        statement.createParagraph();
        XWPFTable table = statement.createTable(1,2);
        table.setCellMargins(500, 50, 0 ,0);
        XWPFTableRow row = table.getRow(0);
        table.setWidth(100);
        table.setWidthType(TableWidthType.PCT);
        table.setTopBorder(XWPFTable.XWPFBorderType.NONE, 10, 25, "000000");
        table.setLeftBorder(XWPFTable.XWPFBorderType.NONE, 6, 6, "000000");
        table.setBottomBorder(XWPFTable.XWPFBorderType.NONE, 6, 0, "000000");
        table.setRightBorder(XWPFTable.XWPFBorderType.NONE, 6, 1, "000000");
        table.setInsideVBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "000000");
        XWPFTableCell cell = row.getCell(0);
        cell.setWidth("35%");

        XWPFRun nameRun = cell.getParagraphs().get(0).createRun();
        XWPFRun stampRun = row.getCell(1).getParagraphs().get(0).createRun();
        row.getCell(1).getParagraphs().get(0).setAlignment(ParagraphAlignment.RIGHT);
        nameRun.setFontSize(15);
        try {
            nameRun.setText("Валавин Сергей");
            nameRun.addBreak();
            nameRun.setText("Главный по выпискам");
            stampRun.addPicture(streamStamp, Document.PICTURE_TYPE_PNG, "signature" , 2250000, 1500000);

        } catch (InvalidFormatException | IOException e) {
            e.printStackTrace();
        }
        XWPFParagraph formDate = statement.createParagraph();
        XWPFRun formRun = formDate.createRun();
        formRun.setText("Дата формирования выписки: " +
                LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
    }

    private void populateDetails(List<Operation> operations, String accountNum) {
        XWPFParagraph detailsTitle = statement.createParagraph();
        detailsTitle.setAlignment(ParagraphAlignment.LEFT);
        detailsTitle.createRun().setText("Детализация операций");
        XWPFTable detailsTable = statement.createTable(operations.size() + 1, 4);
        detailsTable.setCellMargins(50,50,50,50);
        detailsTable.setWidth(95);
        detailsTable.setWidthType(TableWidthType.PCT);
        XWPFTableRow headerRow = detailsTable.getRow(0);
        headerRow.getCell(0).setText("Дата операции");
        headerRow.getCell(1).setText("Название операции");
        headerRow.getCell(2).setText("Описание операции");
        headerRow.getCell(3).setText(String.format("Сумма в валюте %s", type.equals(Type.ACNT) ? "счёта" : "карты"));
        for (int i = 1; i < operations.size() + 1; i++) {
            Operation operation = operations.get(i -1);
            XWPFTableRow row = detailsTable.getRow(i);
            XWPFTableCell dateTimeCell = row.getCell(0);
            dateTimeCell.getParagraphs().get(0).createRun()
                    .setText(operation.getDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)) + "   " +
                            operation.getDate().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));
            row.getCell(1).getParagraphs().get(0).createRun().setText(operation.getTitle());
            row.getCell(2).getParagraphs().get(0).createRun().setText(operation.getTitle());
            row.getCell(3).setText(operation.getSrc().getId().equals(accountNum) ?
                    "+" + operation.getSum().toPlainString() :
                    operation.getSum().toPlainString());
            row.getCell(0).setWidth("auto");
            row.getCell(1).setWidth("auto");
            row.getCell(2).setWidth("auto");
            row.getCell(3).setWidth("auto");
            row.getCell(3).getParagraphs().get(0).setAlignment(ParagraphAlignment.RIGHT);
        }
    }

    private void populateSummaryInfo() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        XWPFParagraph summaryInfo = statement.createParagraph();
        summaryInfo.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun summaryInfoRun = summaryInfo.createRun();
        summaryInfoRun.setText("Итого по операциям с " + sdf.format(start) + " по " + sdf.format(end));
        summaryInfoRun.setFontSize(14);
        summaryInfoRun.setBold(true);
    }

    private void saveStatement() {
        try {
            String file = "/Users/sergey/Desktop/demoStatement-master/src/main/resources/statements/" + System.currentTimeMillis();
            FileOutputStream out =
                    new FileOutputStream(file + ".docx");
            statement.write(out);
            InputStream doc = new FileInputStream(file + ".docx");
            XWPFDocument document = new XWPFDocument(doc);
            PdfOptions pdfOptions = PdfOptions.create();
            FileOutputStream outPdf =
                    new FileOutputStream(file + ".pdf");
            PdfConverter.getInstance().convert(document, outPdf, pdfOptions);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void populateAccountInfo() {
        XWPFParagraph statementName = statement.createParagraph();
        statementName.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun statementNameRun = statementName.createRun();
        statementNameRun.setText(String.format("Выписка по %s", type.equals(Type.ACNT) ? "счёту" : "карте"));
        statementNameRun.setFontSize(16);
        statementNameRun.setBold(true);

        XWPFTable accountInfoTable = statement.createTable(3, 3);
        accountInfoTable.setWidth(90);
        accountInfoTable.setWidthType(TableWidthType.PCT);
        accountInfoTable.setTopBorder(XWPFTable.XWPFBorderType.SINGLE, 6, 0, "4d4d4d");
        accountInfoTable.setLeftBorder(XWPFTable.XWPFBorderType.SINGLE, 6, 6, "4d4d4d");
        accountInfoTable.setBottomBorder(XWPFTable.XWPFBorderType.SINGLE, 6, 0, "4d4d4d");
        accountInfoTable.setRightBorder(XWPFTable.XWPFBorderType.SINGLE, 6, 1, "4d4d4d");
        accountInfoTable.setInsideHBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "000000");
        accountInfoTable.setInsideVBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "000000");

        XWPFTableRow nameRow = accountInfoTable.getRow(0);
        XWPFTableCell nameCell = nameRow.getCell(0);
        XWPFRun run = nameCell.getParagraphs().get(0).createRun();
        run.setText("Валавин Сергей Евгеньевич");
        run.setBold(true);
        run.setFontSize(12);
        nameRow.setHeight(22);
        XWPFTableRow titleRow = accountInfoTable.getRow(1);
        XWPFRun accNumRun = titleRow.getCell(0).getParagraphs().get(0).createRun();
        accNumRun.setText(String.format("Номер %s", type.equals(Type.ACNT) ? "счёта" : "карты"));
        titleRow.getCell(1).setText(String.format("Валюта %s", type.equals(Type.ACNT) ? "счёта" : "карты"));
        titleRow.getCell(2).setText("Баланс");
        XWPFTableRow valueRow = accountInfoTable.getRow(2);
        XWPFRun accNumValRun = valueRow.getCell(0).getParagraphs().get(0).createRun();
        accNumValRun.setText("12345678");
        accNumValRun.setBold(true);
        accNumValRun.setFontSize(10);
        XWPFRun currValRun = valueRow.getCell(1).getParagraphs().get(0).createRun();
        currValRun.setText("Рубль");
        currValRun.setBold(true);
        currValRun.setFontSize(10);
        XWPFRun balValRun = valueRow.getCell(2).getParagraphs().get(0).createRun();
        balValRun.setText("12548,55");
        balValRun.setBold(true);
        balValRun.setFontSize(10);

        statement.createParagraph();
    }

}
