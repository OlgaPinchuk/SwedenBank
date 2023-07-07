package project.ui;

import java.util.List;

public class Table {
    private final List<Integer> columnWidth;
    private final List<String> header;
    private final List<List<String>> body;
    private final String format;
    private final String border;
    public Table(List<Integer> columnWidth, List<String> header, List<List<String>> body) {
        this.columnWidth = columnWidth;
        this.header = header;
        this.body = body;
        this.format = createFormat();
        this.border = createBorder();

        showData();
    }
    public void showData()  {
        generateBorder();
        generateHeader();
        generateBorder();
        generateBody();
        generateBorder();
    }

    private String createFormat() {
        StringBuilder result = new StringBuilder();
        String prefix = "| %-";
        String postfix = "s ";
        String endingCharacter = "|%n";

        for (int item: columnWidth) {
            result.append(prefix).append(item).append(postfix);
        }
        result.append(endingCharacter);

        return result.toString();
    }

    private String createBorder() {
        StringBuilder result = new StringBuilder();
        String prefix = "+";
        String endingCharacter = "+";

        for (int item: columnWidth) {
            result.append(prefix).append(createBorderCell(item));
        }
        result.append(endingCharacter);

        return result.toString();
    }

    private String createBorderCell(int width) {
        int blankSpacesBetweenCells = 2;

        return "-".repeat(Math.max(0, width + blankSpacesBetweenCells));
    }

    private void generateHeader() {
        System.out.format(format, header.toArray());
    }

    private void generateBody() {
        for (List<String> item : body) {
            System.out.format(format, item.toArray());
        }
    }

    private void generateBorder() {
        System.out.println(border);
    }

}
