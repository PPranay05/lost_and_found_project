package com.example.lostfound.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class QrGeneratorUtil {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 300;

    public static String generateQrCodeBase64(String text) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Could not generate QR code image", e);
        }
    }

    public static byte[] generateQrCodeBytes(String text) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, WIDTH, HEIGHT);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Could not generate QR code bytes", e);
        }
    }

    public static String decodeQrCodeFromBase64(String base64Image) {
        try {
            String cleanBase64 = base64Image;
            if (base64Image.contains(",")) {
                cleanBase64 = base64Image.split(",")[1];
            }
            byte[] imageBytes = Base64.getDecoder().decode(cleanBase64);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
            BufferedImage bufferedImage = ImageIO.read(inputStream);

            if (bufferedImage == null) {
                throw new IllegalArgumentException("Invalid image data");
            }

            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            throw new IllegalArgumentException("No QR code found in the image", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode QR code", e);
        }
    }
}
