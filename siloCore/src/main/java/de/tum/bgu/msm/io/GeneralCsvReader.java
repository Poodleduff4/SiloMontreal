package de.tum.bgu.msm.io;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.util.List;

public class GeneralCsvReader {
    public static CSVReader reader;
    public GeneralCsvReader(String filename) {
        try {
            reader = new CSVReader(new FileReader(filename));
        }catch(Exception e) {
            System.out.println("BROKEN");
        }
    }

    public int[] readInts(String columnName){
        try {
            List<String[]> data = reader.readAll();
            int[] res = new int[data.size()-1];
            int colIndex=0;

            for (int i=0;i<data.get(0).length;i++){
                if(data.get(0)[i].equals(columnName)){
                    colIndex=i;
                }
            }

            for(int i=1;i<data.size();i++){
                    res[i-1] = Integer.parseInt(data.get(i)[colIndex]);
            }

            System.out.println(data.size());

            return res;
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public String[] readStrings(String columnName){
        try {
            List<String[]> data = reader.readAll();
            String[] res = new String[data.size()-1];
            int colIndex=0;
            for (int i=0;i<data.get(0).length;i++){
                if(data.get(0)[i].equals(columnName)){
                    colIndex=i;
                }
            }

            for(int i=1;i<data.size();i++){
                res[i-1] = data.get(i)[colIndex];
            }

            System.out.println(data.size());

            return res;
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
}
