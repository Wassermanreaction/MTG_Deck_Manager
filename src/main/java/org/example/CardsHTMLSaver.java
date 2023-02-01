package org.example;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CardsHTMLSaver {

    /**
     * Defines the required edition
     */
    private String edition;
    private String iniPageURL;
    private String iniPage;

    /**
     * Defines the URL of the first page of the edition search quarry
     */
//    String iniPageURL = "http://www.mtg.ru/cards/search.phtml?Grp=" + edition;
//    String iniPageURL = "http://www.mtg.ru/cards/search.phtml?Type=Land&Grp=M21";

    /**
     * Contains the HTML code of the first page of the edition search quarry
     */
//    String iniPage = WebRequest.getThePage(iniPageURL);
    private final int pagesQty;

    /**
     * Contains IDs of the 10 cards on the page
     */
    private ArrayList<String> divIDs = new ArrayList<>();

    public CardsHTMLSaver() {
        pagesQty = countPagesQty();
    }

    public CardsHTMLSaver(int pagesQty) {
        this.pagesQty = pagesQty;
    }

    public CardsHTMLSaver(String edition) {
        this.edition = edition;
        iniPageURL = "http://www.mtg.ru/cards/search.phtml?Grp=" + edition;
        iniPage = WebRequest.getThePage(iniPageURL);
        pagesQty = countPagesQty();
    }

    public int getPagesQty() {
        return pagesQty;
    }

    public ArrayList<String> getDivIDs() {
        return divIDs;
    }

    public String getIniPage() {
        return iniPage;
    }

    /**
     * Counts the number of pages with 10 cards on each
     */
    private int countPagesQty() {
        int cardsQty = 0;
        Pattern p = Pattern.compile(">Найдено карт: \\d+<");
        Matcher m = p.matcher(iniPage);
        while (m.find()) cardsQty = Integer.parseInt(iniPage.substring(m.start() + 15, m.end() - 1)) - 1;
        return (cardsQty / 10) + 1;
    }

    private void saveUniqueDivsContent() {
        StringBuilder page = new StringBuilder(iniPageURL + "&page=");
        System.out.println("SB page = " + page);
        String response = "";
        for (int i = 1; i <= pagesQty; i++) {
//        for (int i = 1; i <= 2; i++) {
//            System.out.println("step " + i);
            page.append(i);
            System.out.println("SB page = " + page);
            response = WebRequest.getThePage(page.toString());
//            System.out.println(response);
            searchForUniqueDivIDs(response);
            saveTenCardDivContents(response);
            page = new StringBuilder(iniPageURL + "&page=");
            divIDs.clear();
        }
    }

    /**
     * Finds the unique div IDs that indicates divs with cards information
     */
    private void searchForUniqueDivIDs(String source) {
        Pattern p = Pattern.compile("<div id=\".{32}\">");
        Matcher m = p.matcher(source);
        while (m.find()) {
            divIDs.add(source.substring(m.start() + 9, m.end() - 2));
        }
        System.out.println(Arrays.toString(divIDs.toArray()));
    }

    /**
     * Extracts the content of the div with card information
     */
    private String receiveCardDivContent(String source, String divID) {
        Document doc = Jsoup.parse(source, "mtg.ru");
        Elements div = doc.select("#" + divID);
        return div.toString();
    }

    public void run() {
        System.out.println(edition);
        saveUniqueDivsContent();
    }

    /**
     * Saves 10 div content to files with names representing the card name and edition using the
     * writeToFile method
     */
    private void saveTenCardDivContents(String source) {
        for (int i = 0; i < divIDs.size(); i++) {
            String divContent = receiveCardDivContent(source, divIDs.get(i));
            File file = new File("src/main/resources/PageDivContents/" + extractEngNameForNaming(findEngRusName(divContent)) + "_" + edition + ".html");
//            String divContent = receiveCardDivContent(iniPage, divIDs.get(i));
            writeToFile(file, divContent);
        }
    }

    private void writeToFile(File whereToWrite, @NotNull String whatToWrite) {
        try {
            FileOutputStream fos = new FileOutputStream(whereToWrite);
            fos.write(whatToWrite.getBytes());
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    public String findEngRusName() {
//        File file = new File("src/main/resources/PageDivContents/0.html");
//        try {
//            Document doc = Jsoup.parse(file, "windows-1251");
//            Elements elements = doc.select("h2");
//            return elements.text();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    private String findEngRusName(String st) {
        Document doc = Jsoup.parse(st);
        Elements elements = doc.select("h2");
        return elements.text();
    }

    private String extractEngNameForNaming (String s) {
        StringBuilder sb = new StringBuilder(s);
        if (s.contains(" // ")) {
            Pattern p = Pattern.compile(".+ //");
            Matcher m = p.matcher(s);
            if (m.find()) {
                String st = s.substring(0, m.end() - 3);
                sb = new StringBuilder(st);
            }
        }
        while (!(sb.indexOf(" ") < 0)) {
            sb.replace(sb.indexOf(" "), sb.indexOf(" ") + 1, "_");
        }
        while (!(sb.indexOf("/") < 0)) {
            sb.replace(sb.indexOf("/"), sb.indexOf("/") + 1, "_");
        }
        while (!(sb.indexOf("?") < 0)) {
            sb.replace(sb.indexOf("?"), sb.indexOf("?") + 1, "_");
        }
        return sb.toString();
    }

}
