
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class SimHash {

    private String tokens;

    //�ַ�������
    private BigInteger intSimHash;

    private String strSimHash;

    //���庣��λ��
    private int hashbits = 64;

    public SimHash(String tokens) {
        this.tokens = tokens;
        this.intSimHash = this.simHash();
    }

    public SimHash(String tokens, int hashbits) {
        this.tokens = tokens;
        this.hashbits = hashbits;
        this.intSimHash = this.simHash();
    }

    //����ÿƪ�ĵ�����SimHash ���ǩ��
    public BigInteger simHash() {
        // ������������
        int[] v = new int[this.hashbits];
        // ���ı�ȥ����ʽ��, �ִ�
        StringTokenizer stringTokens = new StringTokenizer(this.tokens);
        while (stringTokens.hasMoreTokens()) {
            String temp = stringTokens.nextToken();
            // ��ÿһ���ִ�hashΪһ��̶����ȵ�����.���� 64bit ��һ������
            BigInteger t = this.hash(temp);
            for (int i = 0; i < this.hashbits; i++) {
                BigInteger bitmask = new BigInteger("1").shiftLeft(i);
                // ����һ������Ϊ64����������(����Ҫ����64λ������ָ��,Ҳ��������������),
                // ��ÿһ���ִ�hash������н����ж�,�����1000...1,��ô����ĵ�һλ��ĩβһλ��1,
                // �м��62λ��һ,Ҳ����˵,��1��1,��0��1.һֱ�������еķִ�hash����ȫ���ж����.
                if (t.and(bitmask).signum() != 0) {
                	// �����Ǽ��������ĵ�������������������
                    // ����ʵ��ʹ������Ҫ +- Ȩ�أ������Ǽ򵥵� +1/-1
                    v[i] += 1;
                } else {
                    v[i] -= 1;
                }
            }

        }
        BigInteger fingerprint = new BigInteger("0");
        StringBuffer simHashBuffer = new StringBuffer();
        for (int i = 0; i < this.hashbits; i++) {
            // ������������ж�,����0�ļ�Ϊ1,С�ڵ���0�ļ�Ϊ0,�õ�һ�� 64bit ������ָ��
            if (v[i] >= 0) {
                fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
                simHashBuffer.append("1");
            } else {
                simHashBuffer.append("0");
            }
        }
        this.strSimHash = simHashBuffer.toString();
       // System.out.println(this.strSimHash + " length " + this.strSimHash.length());
        return fingerprint;
    }

    //��ÿһ������ӳ��Ϊfά�ռ��һ������
    public BigInteger hash(String source) {
        if (source == null || source.length() == 0) {
            return new BigInteger("0");
        } else {
            char[] sourceArray = source.toCharArray();
            BigInteger x = BigInteger.valueOf(((long) sourceArray[0]) << 7);
            BigInteger m = new BigInteger("1000003");
            BigInteger mask = new BigInteger("2").pow(this.hashbits).subtract(
                    new BigInteger("1"));
            for (char item : sourceArray) {
                BigInteger temp = BigInteger.valueOf((long) item);
                x = x.multiply(m).xor(temp).and(mask);
            }
            x = x.xor(new BigInteger(String.valueOf(source.length())));
            if (x.equals(new BigInteger("-1"))) {
                x = new BigInteger("-2");
            }
            return x;
        }
    }


    //��������ǩ���ĺ�������
    public int hammingDistance(SimHash other) {

        BigInteger x = this.intSimHash.xor(other.intSimHash);
        int tot = 0;

       //ͳ��x�ж�����λ��Ϊ1�ĸ���
       
        while (x.signum() != 0) {
            tot += 1;
            x = x.and(x.subtract(new BigInteger("1")));
        }
        return tot;
    }


    public int getDistance(String str1, String str2) {
        int distance;
        if (str1.length() != str2.length()) {
            distance = -1;
        } else {
            distance = 0;
            for (int i = 0; i < str1.length(); i++) {
                if (str1.charAt(i) != str2.charAt(i)) {
                    distance++;
                }
            }
        }
        return distance;
    }


    public List subByDistance(SimHash simHash, int distance) {
        // ������
        int numEach = this.hashbits / (distance + 1);
        List characters = new ArrayList();

        StringBuffer buffer = new StringBuffer();

        int k = 0;
        for (int i = 0; i < this.intSimHash.bitLength(); i++) {
            // ���ҽ���������ָ����λ�ǣ�����true
            boolean sr = simHash.intSimHash.testBit(i);
            if (sr) {
                buffer.append("1");
            } else {
                buffer.append("0");
            }

            if ((i + 1) % numEach == 0) {
                // ��������תΪBigInteger
                BigInteger eachValue = new BigInteger(buffer.toString(), 2);
                //System.out.println("----" + eachValue);
                buffer.delete(0, buffer.length());
                characters.add(eachValue);
            }
        }

        return characters;
    }


    public static void main(String[] args) {
        FileToString fileToString1 = new FileToString();
        String s = fileToString1.toString("D:/�����ı�2/orig.txt");
        SimHash hash = new SimHash(s, 64);
        //System.out.println(hash1.intSimHash + "  " + hash1.intSimHash.bitLength());
        hash.subByDistance(hash, 3);
        System.out.println("\n");

             String[] str = {"D:/�����ı�2/orig.txt", "D:/�����ı�2/orig_0.8_add.txt", "D:/�����ı�2/orig_0.8_add.txt",
                "D:/�����ı�2/orig_0.8_dis_1.txt", "D:/�����ı�2/orig_0.8_dis_10.txt", "D:/�����ı�2/orig_0.8_dis_15.txt"};

        StringBuffer sb = new StringBuffer();
        FileToString fileToString = new FileToString();

        for (int i = 1; i < str.length; i++) {
            String s1 = fileToString.toString(str[i]);
            SimHash hash1 = new SimHash(s1, 64);
            hash1.subByDistance(hash1, 3);

            int dis = hash.getDistance(hash.strSimHash,hash1.strSimHash);
            int hammingDistance=hash.hammingDistance(hash1);
            System.out.println("��"+i+"ƪ"
//                    +hammingDistance + " "+ dis
                    +"���ƶ�Ϊ"+((100-dis*100/128)+"%"));


        }





    }
}
