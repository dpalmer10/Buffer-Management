import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BufMgr {
    private BufHashTbl bufTbl;
    private Frame[] pool;
    private int poolSize;
    private List<Integer> lruQueue;
    private int used = 0;

    public BufMgr(int poolSize) {
        bufTbl = new BufHashTbl();
        this.poolSize = poolSize;
        this.pool = new Frame[poolSize];
        lruQueue = new ArrayList<>();
    }

    public void pin(int pageNum) {
        //if page in pool, increase pin count
        int fnum = bufTbl.lookup(pageNum);
        if(fnum != -1){
            pool[fnum].incPin();
        }
        else{
            if(lruQueue.size() < poolSize){
                updateQueue(pageNum);
                bufTbl.insert(pageNum, lruQueue.size()-1);
                readPage(lruQueue.size()-1);
            }
            else{
                int page = getNextEmptyFrame();
                if(pool[page].isDirty()){
                    writePage(lruQueue.get(page));
                    bufTbl.remove(lruQueue.get(page));
                }
                updateQueue(pageNum);
                bufTbl.insert(pageNum, page);
                readPage(page);
            }
        }
    }

    public void unpin(int pageNum) {
        int fnum = bufTbl.lookup(pageNum);
        if(fnum != -1)
            pool[fnum].decPin();
    }

    public void createPage(int pageNum) {
        String name = getPageFileName(pageNum);
        String contents = "This is page " + pageNum + ".";
        FileWriter writer = null;

        try {
            writer = new FileWriter(name, false);
            writer.write(contents);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Something went wrong while creating the page");
        }
    }

    public void readPage(int pageNum) {
        try{
            String fname = getPageFileName(lruQueue.get(lruQueue.size()-1));
            File inf = new File(fname);
            Scanner fin = new Scanner(inf);
            String data = fin.nextLine();
            while (fin.hasNext()){
                data += "\n" + fin.nextLine();
            }
            pool[pageNum] = new Frame(data);
            pool[pageNum].incPin();
            pool[pageNum].setDirty(false);

            fin.close();
        }
        catch (Exception e){
            throw new IllegalArgumentException("Error, file does not exist");
        }
    }

    public void writePage(int pageNum) {
        String fname = getPageFileName(pageNum);
        int fnum = bufTbl.lookup(pageNum);
        String data = pool[fnum].getContent();
        FileWriter writer = null;
        try{
            writer = new FileWriter(fname, false);
            writer.write(data);
            writer.close();
        }
        catch (Exception e){
            throw new IllegalArgumentException("Error, file does not exist");
        }
    }

    public void displayPage(int pageNum) {
        Integer frameNum = bufTbl.lookup(pageNum);
        if (frameNum == null) throw new IllegalArgumentException("Cannot display page that is not in memory");

        pool[frameNum].displayPage();
    }

    public void updatePage(int pageNum, String toAppend) {
        Integer frameNum = bufTbl.lookup(pageNum);
        if (frameNum == null) throw new IllegalArgumentException("Cannot update page that is not in memory");

        pool[frameNum].updatePage(toAppend);
    }

    private String getPageFileName(int pageNum) {
        return pageNum + ".txt";
    }

    public void updateQueue(int pageNum){
        boolean exists = false;
        int i;
        for(i = 0; i < lruQueue.size(); i++){
            if(lruQueue.get(i) == pageNum) {
                exists = true;
                break;
            }
        }
        if(!exists && lruQueue.size() < poolSize){
            lruQueue.add(pageNum);
            return;
        }
        for(int j = 0; j < i -1; j++){
            lruQueue.set(j, lruQueue.get(j+1));
        }
        lruQueue.set(i-1, pageNum);
    }

    public int getNextEmptyFrame(){
        for(int i = 0; i < lruQueue.size()-1; i++){
            int num = bufTbl.lookup(lruQueue.get(i));
            if(pool[num].getPin() == 0)
                return num;
        }
        return -1;
    }
}
