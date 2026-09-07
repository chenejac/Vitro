package edu.cornell.mannlib.vitro.webapp.reporting;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class XlsxTemplateSanitizer {

    private static final Log log =
        LogFactory.getLog(XlsxTemplateSanitizer.class);

    private static final String WORKBOOK_XML = "xl/workbook.xml";

    /*
     * M365 revision metadata not supported correctly by the
     * xlsx4j version used by YARG.
     */
    private static final Pattern REVISION_PTR =
        Pattern.compile("<xr:revisionPtr\\b[^>]*/>", Pattern.DOTALL);

    /*
     * Example:
     * mc:Ignorable="x15 xr xr6 xr10 xr2"
     */
    private static final Pattern MC_IGNORABLE =
        Pattern.compile("mc:Ignorable=\"([^\"]*)\"");

    private static final Pattern XR_PREFIX =
        Pattern.compile("xr\\d*");

    private XlsxTemplateSanitizer() {
        // Utility class
    }

    public static byte[] sanitize(byte[] template) {
        if (template == null || template.length == 0) {
            return template;
        }

        try {
            return sanitizeInternal(template);
        } catch (IOException e) {
            throw new IllegalStateException(
                "Could not sanitize XLSX template", e);
        }
    }

    private static byte[] sanitizeInternal(byte[] template)
        throws IOException {

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        boolean changed = false;

        try (ZipInputStream zis =
                 new ZipInputStream(new ByteArrayInputStream(template));
             ZipOutputStream zos =
                 new ZipOutputStream(result)) {

            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                byte[] content = readEntry(zis);

                if (WORKBOOK_XML.equals(entry.getName())) {
                    String xml =
                        new String(content, StandardCharsets.UTF_8);

                    String sanitized = sanitizeWorkbookXml(xml);

                    if (!sanitized.equals(xml)) {
                        changed = true;
                        content =
                            sanitized.getBytes(StandardCharsets.UTF_8);
                    }
                }

                ZipEntry outputEntry = new ZipEntry(entry.getName());

                if (entry.getTime() >= 0) {
                    outputEntry.setTime(entry.getTime());
                }

                zos.putNextEntry(outputEntry);
                zos.write(content);
                zos.closeEntry();
            }
        }

        return changed ? result.toByteArray() : template;
    }

    private static String sanitizeWorkbookXml(String xml) {
        String sanitized =
            REVISION_PTR.matcher(xml).replaceAll("");

        sanitized = removeRevisionPrefixes(sanitized);

        if (!sanitized.equals(xml)) {
            log.info("Sanitized unsupported M365 revision metadata "
                         + "from XLSX template.");
        }

        return sanitized;
    }

    private static String removeRevisionPrefixes(String xml) {
        Matcher matcher = MC_IGNORABLE.matcher(xml);

        if (!matcher.find()) {
            return xml;
        }

        String originalPrefixes = matcher.group(1);

        String cleanedPrefixes =
            Arrays.stream(originalPrefixes.trim().split("\\s+"))
                  .filter(prefix ->
                              !XR_PREFIX.matcher(prefix).matches())
                  .collect(Collectors.joining(" "));

        if (cleanedPrefixes.equals(originalPrefixes)) {
            return xml;
        }

        return matcher.replaceFirst(
            "mc:Ignorable=\""
                + Matcher.quoteReplacement(cleanedPrefixes)
                + "\"");
    }

    private static byte[] readEntry(ZipInputStream zis)
        throws IOException {

        ByteArrayOutputStream buffer =
            new ByteArrayOutputStream();

        byte[] data = new byte[8192];
        int count;

        while ((count = zis.read(data)) != -1) {
            buffer.write(data, 0, count);
        }

        return buffer.toByteArray();
    }
}