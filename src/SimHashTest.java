import org.junit.Test;

public class SimHashTest {
    SimHash simHash;
    @Test
    public void simHashTest(){
            SimHash simHash = new SimHash("123");
            simHash.simHash();
    }
    @Test
    public void hashTest(){
        SimHash simHash = new SimHash("123");
        simHash.hash("123");
    }

    @Test
    public void subByDistanceTest(){
        SimHash simHash=new SimHash("123");
        simHash.subByDistance(simHash,3);
    }


    @Test
    public void getDistanceTest(){
        SimHash simHash=new SimHash("123");
        simHash.getDistance("123","234");
    }

    @Test
    public void hammingDistanceTest(){
        SimHash simHash=new SimHash("123");
        simHash.hammingDistance(simHash);
    }




}