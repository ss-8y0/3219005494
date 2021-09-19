import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class FileToString {

    public  String toString(String s){

        String str="";
        //文件路径
        String fileDirName = s;
        //  创建file对象 fileDirName可以为文件也可以为文件夹
        File file = new File(fileDirName);
        try {

            FileInputStream in=new FileInputStream(file);

            // size  为字串的长度 ，这里一次性读完

            int size=in.available();

            byte[] buffer=new byte[size];

            in.read(buffer);

            in.close();

            str=new String(buffer,"GB2312");

        } catch (IOException e) {

            // TODO Auto-generated catch block

            return null;


        }

        return str;

    }


}
