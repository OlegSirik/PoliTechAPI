package ru.pt.hz;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.util.Matrix;

public class PDFWatermarkUtils {
    
    public enum WatermarkType {
        TEXT, IMAGE, PATTERN, SMART, LOGO_TEXT
    }
    
    
    public static void addTextWatermark(String inputPath, String outputPath, 
    String watermarkText) throws IOException {

try (PDDocument document = Loader.loadPDF(new File(inputPath))) {

for (PDPage page : document.getPages()) {
PDRectangle pageSize = page.getMediaBox();

try (PDPageContentStream contentStream = new PDPageContentStream(
document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {

// Настройка прозрачности
PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
graphicsState.setNonStrokingAlphaConstant(0.3f);
graphicsState.setStrokingAlphaConstant(0.3f);
graphicsState.setAlphaSourceFlag(false);
contentStream.setGraphicsStateParameters(graphicsState);

// Настройка шрифта и цвета
//contentStream.setFont(PDType1Font.HELVETICA_BOLD, 48);
contentStream.setNonStrokingColor(200, 200, 200); // Серый цвет

// Поворот текста на 45 градусов
contentStream.beginText();
contentStream.setTextMatrix(Matrix.getRotateInstance(
Math.toRadians(45), pageSize.getWidth() / 3, pageSize.getHeight() / 3));

contentStream.showText(watermarkText);
contentStream.endText();
}
}

document.save(outputPath);
}
}

public static void addImageWatermark(String inputPath, String outputPath,
                                       String imagePath, float opacity) throws IOException {
        
        try (PDDocument document = Loader.loadPDF(new File(inputPath))) {
            
            // Загрузка изображения
            PDImageXObject watermarkImage = PDImageXObject.createFromFile(imagePath, document);
            
            for (PDPage page : document.getPages()) {
                PDRectangle pageSize = page.getMediaBox();
                
                try (PDPageContentStream contentStream = new PDPageContentStream(
                    document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                    
                    // Настройка прозрачности
                    PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
                    graphicsState.setNonStrokingAlphaConstant(opacity);
                    graphicsState.setStrokingAlphaConstant(opacity);
                    contentStream.setGraphicsStateParameters(graphicsState);
                    
                    // Позиционирование изображения по центру
                    float x = (pageSize.getWidth() - watermarkImage.getWidth() / 2f) / 2f;
                    float y = (pageSize.getHeight() - watermarkImage.getHeight() / 2f) / 2f;
                    
                    contentStream.drawImage(watermarkImage, x, y, 
                        watermarkImage.getWidth() / 2f, watermarkImage.getHeight() / 2f);
                }
            }
            
            document.save(outputPath);
        }
    }

    





}