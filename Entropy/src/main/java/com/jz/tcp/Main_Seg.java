package com.jz.tcp;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.sun.deploy.util.StringUtils;
import com.jz.seg.Segmentor;
import com.jz.utility.MyPair;

public class Main_Seg {
    public static void main(String[] args) throws IOException{
        String fileName = args[0];
        String outName = args[1];
        String line ="";
        Segmentor segger = null;
        segger = Segmentor.getInstance();
        BufferedReader in = new BufferedReader(new FileReader(fileName));
        FileWriter out = new FileWriter(new File(outName));
        BufferedWriter bout = new BufferedWriter(out);
        line = in.readLine();
        while (line!=null)
        {
            String[] fields = line.split("\t");
            String keyword = fields[0];
            ArrayList<List<MyPair<String,String>> > tags = segger.tag(keyword);
            List<String> list = new ArrayList<String>();
            List<Double> list_score = new ArrayList<Double>();
            Double score = 0.0;
            // all words
            for(MyPair<String,String> tag: tags.get(0))
            {
                list.add(tag.key);
            }

            //write into file
            bout.write(line + "\t" + StringUtils.join(list,",") +"\n");

            // left entropy (tag.key) and right entropy (tag.value)
//            for(MyPair<String,String> tag: tags.get(1))
//            {
//                list_score.add(Double.parseDouble(tag.key));
//                list_score.add(Double.parseDouble(tag.value));
//            }
//            for(double x : list_score)
//            {
//                score+=x;
//            }
//            score=score/2+1.0;
////            System.out.println(StringUtils.join(list,","));
//
//            if (list.size() > 1)
//            {
//                bout.write(line + "\t" + StringUtils.join(list,",") + '\t' + score.toString()+ '\t'+ score.toString()+"\n");
//            }
            line = in.readLine();
        }
        in.close();
        bout.flush();
        bout.close();
    }

}
