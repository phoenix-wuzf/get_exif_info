package com.wuzufei.sudo;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Test {
    public static void main(String[] args) throws ImageProcessingException, IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String lat = "";
        String lon = "";
        ArrayList<String> files = new ArrayList<>();
        File file = new File("./source_jpg");
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            System.out.println("正处理第 "+ (i+1) +" 张, 共 "+tempList.length+"张图片");
            File jpegFile = new File(tempList[i].toString());
            Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);

            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    if (tag.getTagName() == "GPS Latitude") {
                        lat = tag.getDescription();
                    } else if (tag.getTagName() == "GPS Longitude") {
                        lon = tag.getDescription();
                    } else {
                        /* don‘t care other field, do nothing */
                    }
                }
            }

            String[] tmp = lat.split("°");
            //1 获取原图
            Mat src = Imgcodecs.imread(tempList[i].toString());
            if (src.empty()) {
                System.out.println("图片读取失败");
                return;
            }
            Font font = new Font("楷体", Font.PLAIN, 200);
            BufferedImage bufImg = Translate.Mat2BufImg(src,".jpg");
            Graphics2D g = bufImg.createGraphics();
            g.drawImage(bufImg, 0, 0, bufImg.getWidth(),bufImg.getHeight(), null);

            g.setFont(font);              //设置字体类型
            g.setColor(Color.RED);        //设置字体颜色
            g.drawString("经度: " + lon, 2800, 700); //设置经度书写位置
            //g.drawString("精度: 23°22'223" + tmp[0] + "°°"+tmp[1], 2800, 900);
            g.drawString("纬度: " + lat, 2800, 500); //设置纬度书写位置
            g.dispose();

            src = Translate.BufImg2Mat(bufImg, BufferedImage.TYPE_3BYTE_BGR, CvType.CV_8UC3);

            String[] spilt_tmp = tempList[i].toString().split("_jpg");
            Imgcodecs.imwrite("./result"+ spilt_tmp[1], src);
        }
        System.out.println("全部处理完成");
        return;
    }
}