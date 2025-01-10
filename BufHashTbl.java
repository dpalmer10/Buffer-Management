import java.util.ArrayList;
import java.util.List;

public class BufHashTbl {
    private final List<List<BufTblRecord>> records; //Forced to make double list of lists because java doesn't support arrays of generics for some dumb reason

    public BufHashTbl() {
        records = new ArrayList<>();
        int tableSize = 10;
        for (int i = 0; i < tableSize; i++) {
            records.add(new ArrayList<>());
        }
    }

    public void insert(int pageNum, int frameNum) {
        BufTblRecord rec = new BufTblRecord(pageNum, frameNum);
        ArrayList<BufTblRecord> entry = new ArrayList<>();
        entry.add(rec);

        records.set(pageNum, entry);
    }

    public int lookup(int pageNum) {
        try{
            return records.get(pageNum).get(0).frameNum;
        }
        catch (Exception e){
            return -1;
        }
    }

    public boolean remove(int pageNum) {
                records.set(pageNum, new ArrayList<>());
                return true;
    }

    private static class BufTblRecord {
        public int pageNum;
        public int frameNum;

        public BufTblRecord (int pageNum, int frameNum) {
            this.pageNum = pageNum;
            this.frameNum = frameNum;
        }
    }
}
