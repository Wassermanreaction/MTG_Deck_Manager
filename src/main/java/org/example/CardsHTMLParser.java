package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CardsHTMLParser {

//    private String engName;
//    private String rusName;
//    private String engEdition;
//    private String rusEdition;
//    private String power;
//    private String toughness;
//    private String rarity;
    private File file;
    private final String[] propertiesNames = {"Eng Name", "Rus Name", "Eng Type", "Rus Type", "Summoning Cost", "Total Summoning Cost", "Power", "Toughness", "Loyalty", "Rarity", "Eng Edition", "Rus Edition", "Eng Rule", "Rus Rule", "Eng Fictional Text", "Rus Fictional Text", "Artist", "Date"};
    private String[] properties = new String[20];
    private String aux;
    private Document doc;

    public CardsHTMLParser(File f) {
        file = f;
        try {
            doc = Jsoup.parse(file, "windows-1251");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void setEngName(String engName) {properties[0] = engName;}
    public void setRusName(String rusName) {properties[1] = rusName;}
    public void setEngType(String engType) {properties[2] = engType;}
    public void setRusType(String rusType) {properties[3] = rusType;}
    public void setSummoningCost(String summoningCost) {properties[4] = summoningCost;}
    private void setTotalSummoningCost(int totalSummoningCost) {properties[5] = String.valueOf(totalSummoningCost);}
    public void setPower(String power) {properties[6] = power;}
    public void setToughness(String toughness) {properties[7] = toughness;}
    public void setLoyalty(String loyalty) {properties[8] = loyalty;}
    public void setRarity(String rarity) {properties[9] = rarity;}
    public void setEngEdition(String engEdition) {properties[10] = engEdition;}
    public void setRusEdition(String rusEdition) {properties[11] = rusEdition;}
    public void setEngRule(String engRule) {properties[12] = engRule;}
    public void setRusRule(String engRule) {properties[13] = engRule;}
    public void setEngFictionalText(String engFictionalText) {properties[14] = engFictionalText;}
    public void setRusFictionalText(String rusFictionalText) {properties[15] = rusFictionalText;}
    public void setArtist(String artist) {properties[16] = artist;}
    public void setDate(String date) {properties[17] = date;}


    private String readFromFile() {
        StringBuilder st = new StringBuilder();
        try {
            FileReader fr = new FileReader(file);
            Scanner sc = new Scanner(fr);
            while (sc.hasNextLine()) {
                st.append(sc.nextLine()).append("\n");
            }
            sc.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return st.toString();
    }

    private void parseEngRusName() {
        Elements elements = doc.select("h2");
        String st = elements.text();
        if (st.contains(" // ")) {
            Pattern p = Pattern.compile(".+ //");
            Matcher m = p.matcher(st);
            if (m.find()) {
                setEngName(st.substring(0, m.end() - 3));
                setRusName(st.substring(m.end() + 1));
            }
        } else setEngName(st);
    }

    private void parseEngRusType() {
        Elements elements = doc.select("#noOracle_Type");
        String st = elements.text();
        if (st.contains(" // ")) {
            Pattern p = Pattern.compile(".+ //");
            Matcher m = p.matcher(st);
            if (m.find()) {
                setEngType(st.substring(0, m.end() - 3));
                setRusType(st.substring(m.end() + 1));
            }
        } else setEngType(st);
    }

    private void parseSummoningCost() {
        String st = readFromFile();
        String result = "";
        int total = 0;
        ArrayList<String> au = new ArrayList<>();
        Pattern p = Pattern.compile("Вызов.+Mana\">");
        Matcher m = p.matcher(st);
        if (m.find()) st = st.substring(m.start(), m.end());
        else return;
        p = Pattern.compile("alt=\".{1,3}\"");
        m = p.matcher(st);
        while (m.find()) au.add(st.substring(m.start() + 5, m.end() - 1));
        if (au.size() > 0) {
            result = au.get(0);
            if (Character.isDigit(au.get(0).charAt(0))) total = Integer.parseInt(au.get(0));
            else total++;
            for (int i = 1; i < au.size(); i++) {
                result += "_" + au.get(i);
                total++;
            }
        }
        setSummoningCost(result);
        setTotalSummoningCost(total);
    }

    private void parsePowerToughness() {
        String st = readFromFile();
        Pattern p = Pattern.compile("Сила/Защита:.+\n");
        Matcher m = p.matcher(st);
        if (m.find()) st = st.substring(m.start() + 18, m.end() - 1);
        else return;
        while (st.length() > 1 & !Character.isDigit(st.charAt(0)) & st.charAt(0) != '*') st = st.substring(1);
        p = Pattern.compile(" / ");
        m = p.matcher(st);
        if (m.find()) {
            setPower(st.substring(0, m.start()));
            setToughness(st.substring(m.end()));
        }
    }

    private void parseLoyalty() {
        String st = readFromFile();
        Pattern p = Pattern.compile("Верность.+\n");
        Matcher m = p.matcher(st);
        if (m.find()) setLoyalty(st.substring(m.start() + 15, m.end() - 1));

    }

    private void parseRarity() {
        Elements elements = doc.select("img.rarity");
        Pattern p = Pattern.compile("alt=\".+\"");
        Matcher m = p.matcher(elements.toString());
        if (m.find()) setRarity(elements.toString().substring(m.start() + 5, m.end() - 1));
    }

    private void parseEngRusEdition() {
        Elements elements = doc.select("td.EditionsList");
        StringBuilder sb = new StringBuilder(elements.text().substring(9));
        Pattern p = Pattern.compile(" В разных изданиях");
        Matcher m = p.matcher(sb);
        if (m.find()) sb.replace(m.start(), m.end(), "");
        p = Pattern.compile(" Варианты оформления.+]");
        m = p.matcher(sb);
        if (m.find()) sb.replace(m.start(), m.end(), "");
        if (sb.toString().contains(" // ")) {
            p = Pattern.compile(".+ //");
            m = p.matcher(sb);
            if (m.find()) {
                setEngEdition(sb.substring(0, m.end() - 3));
                setRusEdition(sb.substring(m.end() + 1));
            }
        } else setEngEdition(sb.toString());
    }

    public void parseRule() {
        Elements elements = doc.select("div.SearchCardInfoText");
        StringBuilder sb = new StringBuilder(elements.toString());
        Document aux = Jsoup.parse(sb.toString());
        List<String> list = aux.select("img").eachAttr("alt");
        while (sb.indexOf("<img") > -1) {
            Pattern p = Pattern.compile("<img.+?>");
            Matcher m = p.matcher(sb);
            if (m.find()) {
                sb.replace(m.start(), m.end(), "{" + list.get(0) + "}");
                list.remove(0);
            }
        }
        aux = Jsoup.parse(sb.toString());
        elements = aux.select("div");
        if (elements.size() > 1) {
            setEngRule(elements.get(0).text());
            setRusRule(elements.get(1).text());
        } else setEngRule(elements.text());
    }

    private void parseFictionalText() {
        Elements elements = doc.select("td.SearchCardFlavor > i");
        if (elements.size() > 1) {
            setEngFictionalText(elements.get(0).text());
            setRusFictionalText(elements.get(1).text());
        } else setEngFictionalText(elements.text());
    }

    private void parseArtist() {
        String st = readFromFile();
        Pattern p = Pattern.compile("Художник</tt>: .+</td>");
        Matcher m = p.matcher(st);
        if (m.find()) setArtist(st.substring(m.start() + 15, m.end() - 5));
    }

    private void parseDate() {
        String st = readFromFile();
        Pattern p = Pattern.compile("Дата выпуска</tt>: .+</td>");
        Matcher m = p.matcher(st);
        if (m.find()) setDate(st.substring(m.start() + 19, m.end() - 5));
    }

    public void runParsing() {
        parseEngRusName();
        parseEngRusType();
        parseSummoningCost();
        parseEngRusEdition();
        parsePowerToughness();
        parseRarity();
        parseLoyalty();
        parseRule();
        parseFictionalText();
        parseArtist();
        parseDate();
    }

    public String toString() {
        StringBuilder st = new StringBuilder();
        for (int i = 0; i < properties.length; i++) {
            if (properties[i] != null) {
                st.append(propertiesNames[i]).append(" = ").append(properties[i]).append("; ");
                st.append("\n");
            }
        }
        return st.toString();
    }

}
