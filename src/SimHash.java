
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class SimHash {

    private String tokens;

    //字符串向量
    private BigInteger intSimHash;

    private String strSimHash;

    //定义海明位数
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

    //将对每篇文档根据SimHash 算出签名
    public BigInteger simHash() {
        // 定义特征向量
        int[] v = new int[this.hashbits];
        // 将文本去掉格式后, 分词
        StringTokenizer stringTokens = new StringTokenizer(this.tokens);
        while (stringTokens.hasMoreTokens()) {
            String temp = stringTokens.nextToken();
            // 将每一个分词hash为一组固定长度的数列.比如 64bit 的一个整数
            BigInteger t = this.hash(temp);
            for (int i = 0; i < this.hashbits; i++) {
                BigInteger bitmask = new BigInteger("1").shiftLeft(i);
                // 建立一个长度为64的整数数组(假设要生成64位的数字指纹,也可以是其它数字),
                // 对每一个分词hash后的数列进行判断,如果是1000...1,那么数组的第一位和末尾一位加1,
                // 中间的62位减一,也就是说,逢1加1,逢0减1.一直到把所有的分词hash数列全部判断完毕.
                if (t.and(bitmask).signum() != 0) {
                	// 这里是计算整个文档的所有特征的向量和
                    // 这里实际使用中需要 +- 权重，而不是简单的 +1/-1
                    v[i] += 1;
                } else {
                    v[i] -= 1;
                }
            }

        }
        BigInteger fingerprint = new BigInteger("0");
        StringBuffer simHashBuffer = new StringBuffer();
        for (int i = 0; i < this.hashbits; i++) {
            // 最后对数组进行判断,大于0的记为1,小于等于0的记为0,得到一个 64bit 的数字指纹
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

    //将每一个特征映射为f维空间的一个向量
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


    //计算两个签名的海明距离
    public int hammingDistance(SimHash other) {

        BigInteger x = this.intSimHash.xor(other.intSimHash);
        int tot = 0;

       //统计x中二进制位数为1的个数
       
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
        // 分组检查
        int numEach = this.hashbits / (distance + 1);
        List characters = new ArrayList();

        StringBuffer buffer = new StringBuffer();

        int k = 0;
        for (int i = 0; i < this.intSimHash.bitLength(); i++) {
            // 当且仅当设置了指定的位是，返回true
            boolean sr = simHash.intSimHash.testBit(i);
            if (sr) {
                buffer.append("1");
            } else {
                buffer.append("0");
            }

            if ((i + 1) % numEach == 0) {
                // 将二进制转为BigInteger
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
        String s = fileToString1.toString("D:/测试文本2/orig.txt");
        SimHash hash = new SimHash(s, 64);
        //System.out.println(hash1.intSimHash + "  " + hash1.intSimHash.bitLength());
        hash.subByDistance(hash, 3);
        System.out.println("\n");

             String[] str = {"D:/测试文本2/orig.txt", "D:/测试文本2/orig_0.8_add.txt", "D:/测试文本2/orig_0.8_add.txt",
                "D:/测试文本2/orig_0.8_dis_1.txt", "D:/测试文本2/orig_0.8_dis_10.txt", "D:/测试文本2/orig_0.8_dis_15.txt"};

        StringBuffer sb = new StringBuffer();
        FileToString fileToString = new FileToString();

        for (int i = 1; i < str.length; i++) {
            String s1 = fileToString.toString(str[i]);
            SimHash hash1 = new SimHash(s1, 64);
            hash1.subByDistance(hash1, 3);

            int dis = hash.getDistance(hash.strSimHash,hash1.strSimHash);
            int hammingDistance=hash.hammingDistance(hash1);
            System.out.println("第"+i+"篇"
//                    +hammingDistance + " "+ dis
                    +"相似度为"+((100-dis*100/128)+"%"));


        }





    }
}
