package org.example;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        File file = new File("src/main/resources/PageDivContents/M21/Adherent_of_Hope_M21_.html");
//        File file = new File("src/main/resources/PageDivContents/Azusa's_Many_Journeys___Likeness_of_the_Seeker_NEO.html");
//        File file = new File("src/main/resources/PageDivContents/Sol_Ring_MPS_KLD.html");

        //Planeswalker
//        File file = new File("src/main/resources/PageDivContents/Basri_Ket_1_m21_.html");
//
//        File file = new File("src/main/resources/PageDivContents/Crusader_of_Odric_M13.html");
//        File file = new File("src/main/resources/PageDivContents/Phyrexian_Metamorph_NPH.html");
//        File file = new File("src/main/resources/PageDivContents/Act_of_Aggression_NPH.html");
//        File file = new File("src/main/resources/PageDivContents/Kitchen_Finks_MMA.html");
//        File file = new File("src/main/resources/PageDivContents/Abandoned_Outpost_OD.html");
        CardsHTMLParser chp = new CardsHTMLParser(file);
        chp.runParsing();
        System.out.println(chp);

    }
}