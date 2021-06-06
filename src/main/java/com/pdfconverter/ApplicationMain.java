package com.pdfconverter;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;



public class ApplicationMain {
    public static Logger logger = Logger.getLogger(ApplicationMain.class.getName());
    //private HashMap<String,String> products=new HashMap<>();
    private List<String> products=new ArrayList<>();
   // private Pair<String,String> products1=new Pair<>();
    public static void main(String args[]) {

        if(args==null || args.length==0){
            logger.log(Level.WARNING,"Please input pdf file");
            return;

        }
        String inputFileName=args[0];
        ApplicationMain am=new ApplicationMain();
        try {
            am.generateTestToPdf(inputFileName);
            am.parseTextFile("pdf.txt");
            am.writeProductToCsv("final.csv");

        } catch(Exception e){
            logger.log(Level.WARNING,e.getMessage());
        }
    }

    private void writeProductToCsv(String filename){

        try (PrintWriter writer = new PrintWriter(new File(filename))) {
            for (int i=0;i<products.size();i++){
                writer.write(products.get(i));
            }
        } catch(Exception e){
            logger.log(Level.WARNING,e.getMessage());
        }

        System.out.println("Excel file created: "+filename);

    }

    private void parseTextFile(String filename){
        boolean productstarts= false;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(productstarts){
                    parseProduct(line);
                }
                if(line.startsWith("Catalog#")){
                    productstarts=true;
                }
            }
        }catch (Exception e){
            logger.log(Level.WARNING,e.getMessage());
        }
    }
    private void parseProduct(String productLine){
        int seriesIndex=productLine.indexOf("series");
        if(seriesIndex>0){
            String key=productLine.substring(0,seriesIndex-1);
            String value=productLine.substring(seriesIndex+7,productLine.length()-1);
            products.add(key+","+appendDQ(value)+"\n");
        }

    }

    private static String appendDQ(String str) {
        return "\"" + str + "\"";
    }

    private void generateTestToPdf(String filename) throws FileNotFoundException, UnsupportedEncodingException,IOException {
      //  try{
        File f = new File(filename);
        String parsedText;
        PDFParser parser = new PDFParser(new RandomAccessFile(f, "r"));
        parser.parse();
        logger.log(Level.INFO, "pdf parsed " );
        COSDocument cosDoc = parser.getDocument();
        PDFTextStripper pdfStripper = new PDFTextStripper();
        PDDocument pdDoc = new PDDocument(cosDoc);
        parsedText = pdfStripper.getText(pdDoc);

        PrintWriter pw = new PrintWriter("pdf.txt");
        pw.print(parsedText);
        logger.log(Level.INFO, "pdf.txt file created " );
        pw.close();
    }
}
