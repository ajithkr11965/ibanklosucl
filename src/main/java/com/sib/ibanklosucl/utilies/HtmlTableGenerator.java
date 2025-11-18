package com.sib.ibanklosucl.utilies;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class HtmlTableGenerator {
    public String generateTable(Map<String, List<String>> categorizedWarnings) {
        StringBuilder html = new StringBuilder();
        html.append("<table class='table table-bordered'>")
                .append("<thead class='thead-dark'>")
                .append("<tr>")
                .append("<th scope='col' colspan='2'>Warnings</th>")
                .append("</tr>")
                .append("</thead>")
                .append("<tbody>");

        addSeverityRows(html, categorizedWarnings, "High", "table-danger");
        addSeverityRows(html, categorizedWarnings, "Medium", "table-warning");
        addSeverityRows(html, categorizedWarnings, "Low", "table-light");

        html.append("</tbody>")
                .append("</table>");
        html.append("<div class='alert mt-3 alert-info'>\n" +
                "  <div class='small text-muted'>\n" +
                "    <strong>To submit this work item:</strong>\n" +
                "    <ul>\n" +
                "      <li>Resolve all warnings with <strong>High</strong> criticality.</li>\n" +
                "      <li>Warnings with <strong>Medium</strong> criticality must be resolved before reaching the CRT scrutiny queue, or they will cause a block.</li>\n" +
                "      <li><strong>Low</strong> criticality warnings will not block submission.</li>\n" +
                "    </ul>\n" +
                "  </div>\n" +
                "</div>");

        return html.toString();
    }

    private void addSeverityRows(StringBuilder html, Map<String, List<String>> categorizedWarnings, String severity, String bootstrapClass) {
        List<String> warnings = categorizedWarnings.getOrDefault(severity, Collections.emptyList());
        if (!warnings.isEmpty()) {
            html.append("<tr class='").append(bootstrapClass).append("'>")
                    .append("<td class='text-small' colspan='2'>").append("Criticality : "+(severity.toUpperCase())).append("</td>")
                    .append("</tr>");
            int i=1;
            for (String warning : warnings) {
                html.append("<tr class='").append(bootstrapClass).append("'>")
                        .append("<td class='text-small' colspan='1'>").append(i++).append("</td>")
                        .append("<td class='text-small' colspan='1'>").append(warning).append("</td>")
                        .append("</tr>");
            }
        }
    }



}
