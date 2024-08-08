package de.tum.bgu.msm.io;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.util.List;

public class LinkVolumesWriter {
    public static CSVWriter writer;
    public LinkVolumesWriter(){
        try {
            writer = new CSVWriter(new FileWriter("C:\\Users\\lukeg\\Documents\\SiloMontreal\\useCases\\munich\\test\\gma\\scenOutput\\GMAtest\\outputLinkVolumes.csv"));
        }
        catch(Exception e){
            System.out.println("BROKEN writer");
        }
    }

    public static void write(List<String[]> data){
        writer.writeAll(data);
    }
}
