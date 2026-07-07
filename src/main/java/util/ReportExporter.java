package util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DeflaterOutputStream;

public final class ReportExporter {

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ReportExporter() {
    }

    public static void exportCsv(
            File file, String[] headers, List<String[]> rows) throws IOException {
        try (OutputStream out = new FileOutputStream(file)) {
            out.write(0xEF);
            out.write(0xBB);
            out.write(0xBF);
            out.write(joinCsv(headers).getBytes(StandardCharsets.UTF_8));
            out.write('\n');
            for (String[] row : rows) {
                out.write(joinCsv(row).getBytes(StandardCharsets.UTF_8));
                out.write('\n');
            }
        }
    }

    public static void exportTxt(
            File file, String title, String[] headers, List<String[]> rows)
            throws IOException {
        StringBuilder text = new StringBuilder();
        text.append(title).append(System.lineSeparator());
        text.append("匯出時間：")
                .append(TIME_FORMAT.format(LocalDateTime.now()))
                .append(System.lineSeparator());
        text.append("資料筆數：").append(rows.size())
                .append(System.lineSeparator());
        text.append(repeat('=', 88)).append(System.lineSeparator());
        text.append(joinText(headers)).append(System.lineSeparator());
        text.append(repeat('-', 88)).append(System.lineSeparator());
        for (String[] row : rows) {
            text.append(joinText(row)).append(System.lineSeparator());
        }
        Files.write(file.toPath(), text.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static void exportPdf(
            File file, String title, String[] headers, List<String[]> rows)
            throws IOException {
        List<BufferedImage> pages = renderPages(title, headers, rows);
        writeImagePdf(file, pages);
    }

    private static String joinCsv(String[] values) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) line.append(',');
            String value = safe(values[i]).replace("\"", "\"\"");
            line.append('"').append(value).append('"');
        }
        return line.toString();
    }

    private static String joinText(String[] values) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) line.append("\t");
            line.append(safe(values[i]).replace('\t', ' ')
                    .replace('\r', ' ').replace('\n', ' '));
        }
        return line.toString();
    }

    private static List<BufferedImage> renderPages(
            String title, String[] headers, List<String[]> rows) {
        final int width = 1684;
        final int height = 1190;
        final int margin = 54;
        final int titleHeight = 110;
        final int headerHeight = 46;
        final int rowHeight = 42;
        final int footerHeight = 42;
        final int rowsPerPage = Math.max(1,
                (height - margin * 2 - titleHeight - headerHeight - footerHeight)
                / rowHeight);

        int pageCount = Math.max(1,
                (int) Math.ceil(rows.size() / (double) rowsPerPage));
        double[] weights = calculateColumnWeights(headers, rows);
        List<BufferedImage> pages = new ArrayList<>();

        for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
            BufferedImage image = new BufferedImage(
                    width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);

            g.setColor(new Color(30, 34, 44));
            g.fillRect(0, 0, width, 92);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
            g.setColor(new Color(197, 160, 89));
            g.drawString(safe(title), margin, 52);
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
            g.setColor(new Color(225, 225, 225));
            g.drawString("匯出時間：" + TIME_FORMAT.format(LocalDateTime.now())
                    + "　資料筆數：" + rows.size(), margin, 80);

            int tableX = margin;
            int tableY = margin + titleHeight;
            int tableWidth = width - margin * 2;
            int[] columnWidths = toPixelWidths(weights, tableWidth);

            g.setColor(new Color(58, 62, 74));
            g.fillRect(tableX, tableY, tableWidth, headerHeight);
            g.setColor(Color.WHITE);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
            drawRow(g, headers, tableX, tableY, headerHeight,
                    columnWidths, true);

            int start = pageIndex * rowsPerPage;
            int end = Math.min(rows.size(), start + rowsPerPage);
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
            int y = tableY + headerHeight;
            for (int i = start; i < end; i++) {
                if ((i - start) % 2 == 1) {
                    g.setColor(new Color(246, 246, 248));
                    g.fillRect(tableX, y, tableWidth, rowHeight);
                }
                g.setColor(new Color(40, 40, 44));
                drawRow(g, rows.get(i), tableX, y, rowHeight,
                        columnWidths, false);
                y += rowHeight;
            }

            g.setColor(new Color(135, 135, 140));
            g.setStroke(new BasicStroke(1f));
            g.drawRect(tableX, tableY, tableWidth,
                    headerHeight + (end - start) * rowHeight);

            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
            g.setColor(new Color(90, 90, 95));
            String footer = "PuzzleGamePlatform Phase 2　　第 "
                    + (pageIndex + 1) + " / " + pageCount + " 頁";
            g.drawString(footer, margin, height - 30);
            g.dispose();
            pages.add(image);
        }
        return pages;
    }

    private static void drawRow(
            Graphics2D g,
            String[] values,
            int x,
            int y,
            int height,
            int[] widths,
            boolean header) {
        FontMetrics metrics = g.getFontMetrics();
        int currentX = x;
        for (int i = 0; i < widths.length; i++) {
            int width = widths[i];
            String value = i < values.length ? safe(values[i]) : "";
            value = value.replace('\r', ' ').replace('\n', ' ');
            value = ellipsize(value, metrics, Math.max(8, width - 12));
            int textY = y + (height - metrics.getHeight()) / 2
                    + metrics.getAscent();
            g.drawString(value, currentX + 6, textY);
            g.setColor(header ? new Color(120, 122, 130)
                    : new Color(205, 205, 210));
            g.drawLine(currentX + width, y,
                    currentX + width, y + height);
            g.drawLine(currentX, y + height,
                    currentX + width, y + height);
            g.setColor(header ? Color.WHITE : new Color(40, 40, 44));
            currentX += width;
        }
    }

    private static double[] calculateColumnWeights(
            String[] headers, List<String[]> rows) {
        double[] weights = new double[headers.length];
        for (int i = 0; i < headers.length; i++) {
            weights[i] = Math.max(4, Math.min(18, visualLength(headers[i]) + 2));
        }
        int sampleCount = Math.min(rows.size(), 120);
        for (int r = 0; r < sampleCount; r++) {
            String[] row = rows.get(r);
            for (int i = 0; i < headers.length && i < row.length; i++) {
                double value = Math.max(4,
                        Math.min(22, visualLength(row[i]) + 1));
                weights[i] = Math.max(weights[i], value);
            }
        }
        return weights;
    }

    private static int[] toPixelWidths(double[] weights, int totalWidth) {
        double sum = 0;
        for (double weight : weights) sum += weight;
        int[] widths = new int[weights.length];
        int used = 0;
        for (int i = 0; i < weights.length; i++) {
            widths[i] = i == weights.length - 1
                    ? totalWidth - used
                    : Math.max(40, (int) Math.round(totalWidth * weights[i] / sum));
            used += widths[i];
        }
        if (used != totalWidth && widths.length > 0) {
            widths[widths.length - 1] += totalWidth - used;
        }
        return widths;
    }

    private static int visualLength(String text) {
        String value = safe(text);
        int length = 0;
        for (int i = 0; i < value.length(); i++) {
            length += value.charAt(i) <= 255 ? 1 : 2;
        }
        return length;
    }

    private static String ellipsize(
            String value, FontMetrics metrics, int maxWidth) {
        if (metrics.stringWidth(value) <= maxWidth) return value;
        String suffix = "…";
        int end = value.length();
        while (end > 0
                && metrics.stringWidth(value.substring(0, end) + suffix)
                > maxWidth) {
            end--;
        }
        return value.substring(0, Math.max(0, end)) + suffix;
    }

    private static void writeImagePdf(
            File file, List<BufferedImage> pages) throws IOException {
        List<byte[]> objects = new ArrayList<>();
        objects.add(ascii("<< /Type /Catalog /Pages 2 0 R >>"));

        StringBuilder kids = new StringBuilder("[");
        for (int i = 0; i < pages.size(); i++) {
            int pageObjectNo = 3 + i * 3;
            kids.append(pageObjectNo).append(" 0 R ");
        }
        kids.append(']');
        objects.add(ascii("<< /Type /Pages /Kids " + kids
                + " /Count " + pages.size() + " >>"));

        for (int i = 0; i < pages.size(); i++) {
            BufferedImage image = pages.get(i);
            int pageObjectNo = 3 + i * 3;
            int imageObjectNo = pageObjectNo + 1;
            int contentObjectNo = pageObjectNo + 2;
            String imageName = "Im" + (i + 1);

            String pageObject = "<< /Type /Page /Parent 2 0 R "
                    + "/MediaBox [0 0 842 595] "
                    + "/Resources << /XObject << /" + imageName + " "
                    + imageObjectNo + " 0 R >> >> "
                    + "/Contents " + contentObjectNo + " 0 R >>";
            objects.add(ascii(pageObject));

            byte[] compressedImage = compressRgb(image);
            ByteArrayOutputStream imageObject = new ByteArrayOutputStream();
            imageObject.write(ascii("<< /Type /XObject /Subtype /Image /Width "
                    + image.getWidth() + " /Height " + image.getHeight()
                    + " /ColorSpace /DeviceRGB /BitsPerComponent 8 "
                    + "/Filter /FlateDecode /Length "
                    + compressedImage.length + " >>\nstream\n"));
            imageObject.write(compressedImage);
            imageObject.write(ascii("\nendstream"));
            objects.add(imageObject.toByteArray());

            byte[] content = ascii("q 842 0 0 595 0 0 cm /"
                    + imageName + " Do Q");
            ByteArrayOutputStream contentObject = new ByteArrayOutputStream();
            contentObject.write(ascii("<< /Length " + content.length
                    + " >>\nstream\n"));
            contentObject.write(content);
            contentObject.write(ascii("\nendstream"));
            objects.add(contentObject.toByteArray());
        }

        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(ascii("%PDF-1.4\n%âãÏÓ\n"));
            List<Long> offsets = new ArrayList<>();
            offsets.add(0L);
            for (int i = 0; i < objects.size(); i++) {
                offsets.add(out.getChannel().position());
                out.write(ascii((i + 1) + " 0 obj\n"));
                out.write(objects.get(i));
                out.write(ascii("\nendobj\n"));
            }
            long xrefOffset = out.getChannel().position();
            out.write(ascii("xref\n0 " + (objects.size() + 1) + "\n"));
            out.write(ascii("0000000000 65535 f \n"));
            for (int i = 1; i < offsets.size(); i++) {
                out.write(ascii(String.format("%010d 00000 n \n", offsets.get(i))));
            }
            out.write(ascii("trailer\n<< /Size " + (objects.size() + 1)
                    + " /Root 1 0 R >>\nstartxref\n" + xrefOffset
                    + "\n%%EOF\n"));
        }
    }

    private static byte[] compressRgb(BufferedImage image) throws IOException {
        ByteArrayOutputStream raw = new ByteArrayOutputStream(
                image.getWidth() * image.getHeight() * 3);
        try (DeflaterOutputStream deflater = new DeflaterOutputStream(raw)) {
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int rgb = image.getRGB(x, y);
                    deflater.write((rgb >> 16) & 0xFF);
                    deflater.write((rgb >> 8) & 0xFF);
                    deflater.write(rgb & 0xFF);
                }
            }
        }
        return raw.toByteArray();
    }

    private static byte[] ascii(String value) {
        return value.getBytes(StandardCharsets.ISO_8859_1);
    }

    private static String repeat(char value, int count) {
        StringBuilder text = new StringBuilder(count);
        for (int i = 0; i < count; i++) text.append(value);
        return text.toString();
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
