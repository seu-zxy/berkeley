package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map;
/**the class stage.
 * @author josiath */
public class Stage implements Serializable {
    /**a list save all the add file name. */
    private  ArrayList<String> addB;
    /**a list save all the remove file name. */
    private  ArrayList<String> remB;
    /**a map between filename and save filename. */
    private  HashMap<String, String> filemap;
    /**a map between file name and save blob name. */
    private  HashMap<String, String> blobmap;
    /**the current commit. */
    private  Commit _current;
    /**the directory for stage save. */
    public static final String DIRFS = ".stage";
    /**the directory for blob save.*/
    public static final String DIRFB = ".blob";
    /**the directory for filesave. */
    public static final String FORF = ".file";
    /**the file for blob save. */
    public static final File BLOBSAVE = Utils.join(Main.MAINFOLDER, DIRFB);
    /**the directory for stage save.*/
    public static final File STAGESAVE = Utils.join(Main.MAINFOLDER, DIRFS);
    /**the directory for file save. */
    public static final File FILESAVE = Utils.join(Main.MAINFOLDER, FORF);
    /**the num for file count. */
    private int numforfile = 0;
    /**stage. */
    Stage() {
        addB = new ArrayList<>();
        remB = new ArrayList<>();
        filemap = new HashMap<>();
        blobmap = new HashMap<>();
    }

    /**add file.
     * @param file1 the name of the file */
    public  void add(String file1) {
        File newfile = Utils.join(".", file1);
        if (!newfile.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        String blobn = _current.getBlobname().get(file1);
        byte[] bfile1 = Utils.readContents(newfile);
        String hac1 = Utils.sha1(bfile1);
        if (blobn == null) {
            if (!addB.contains(file1)) {
                addB.add(file1);
                numforfile++;
                savefile(file1);
            } else {
                String oldname = filemap.get(file1);
                File oldf = Utils.join(FILESAVE, oldname);
                byte[] cold = Utils.readContents(oldf);
                String oldhash = Utils.sha1(cold);
                if (oldhash.equals(hac1)) {
                    return;
                } else {
                    Utils.writeContents(oldf, bfile1);
                    String oldb = blobmap.get(file1);
                    File oldbf = Utils.join(BLOBSAVE, oldb);
                    Utils.writeContents(oldbf, hac1);
                }
            }
        } else {
            File blobff = Utils.join(BLOBSAVE, blobn);
            String savedhac = Utils.readContentsAsString(blobff);
            if (hac1.equals(savedhac)) {
                if (addB.contains(file1)) {
                    delete(file1);
                }
                return;
            } else {
                if (addB.contains(file1)) {
                    File oldf = Utils.join(FILESAVE, file1);
                    File newf = Utils.join(".", file1);
                    byte[] cold = Utils.readContents(oldf);
                    String oldhash = Utils.sha1(cold);
                    if (oldhash.equals(hac1)) {
                        return;
                    }
                    Utils.writeContents(oldf, bfile1);
                    String oldb = blobmap.get(file1);
                    File oldbf = Utils.join(BLOBSAVE, oldb);
                    Utils.writeContents(oldbf, hac1);
                } else {
                    addB.add(file1);
                    numforfile++;
                    savefile(file1);
                }
            }
        }
        if (remB.contains(file1)) {
            remB.remove(file1);
        }
    }
    /**remove file.
     * @param file1 the name of file */
    public void remove(String file1) {
        Map<String, String> fn = _current.getFilename();
        if (!addB.contains(file1)
                && !_current.getFilename().containsKey(file1)) {
            System.out.println(" No reason to remove the file.");
            return;
        }
        if (addB.contains(file1)) {
            addB.remove(file1);
        }
        if (!remB.contains(file1) && fn.containsKey(file1)) {
            remB.add(file1);
        }
        if (fn.containsKey(file1)) {
            File def1 = Utils.join(".", file1);
            if (def1.exists()) {
                Utils.restrictedDelete(def1);
            }
        }
    }
    /**save file to disk.
     * @param file1 the name of the file */
    private  void savefile(String file1) {
        File file1read = Utils.join(".", file1);
        byte[] filebyte = Utils.readContents(file1read);
        String file1ren = "file" + Integer.toString(numforfile);
        String blob1ren = "blob" + Integer.toString(numforfile);
        File filewrite = Utils.join(FILESAVE, file1ren);
        File blobwrite = Utils.join(BLOBSAVE, blob1ren);
        Utils.writeContents(filewrite, filebyte);
        String hashblob = Utils.sha1(filebyte);
        Utils.writeContents(blobwrite, hashblob);
        filemap.put(file1, file1ren);
        blobmap.put(file1, blob1ren);
    }
    /**get filemap.
     * @return  filemap */
    public  HashMap<String, String> getFilemap() {
        return filemap;
    }
    /**delete file in the stage area.
     * @param file1 the file name */
    public  void delete(String file1) {
        String filenr = filemap.get(file1);
        String blobnr = blobmap.get(file1);
        filemap.remove(file1);
        blobmap.remove(file1);
        File fr = Utils.join(FILESAVE, filenr);
        File br = Utils.join(BLOBSAVE, blobnr);
        Utils.restrictedDelete(fr);
        Utils.restrictedDelete(br);
    }
    /**get addB list.
     * @return addB */
    public  ArrayList<String> getAddB() {
        return addB;
    }
    /**get blobmap.
     * @return blobmap */
    public  HashMap<String, String> getBlobmap() {
        return blobmap;
    }
    /**set current commit.
     * @param commit1 commit */
    public  void setcurrent(Commit commit1) {
        _current = commit1;
    }
    /**get stage from the disk.
     * @return  stage*/
    public static Stage readfromfile() {
        String name = "stage1";
        File savename = Utils.join(STAGESAVE, name);
        return Utils.readObject(savename, Stage.class);
    }
    /**save stage to disk. */
    public void saveStage() {
        String name = "stage1";
        File savename = Utils.join(STAGESAVE, name);
        Utils.writeObject(savename, this);
    }
    /**clear the stage. */
    public void clear() {
        addB.clear();
        filemap.clear();
        blobmap.clear();
        remB.clear();
    }

    /**get the remb list.
     * @return  remb */
    public ArrayList<String> getRemB() {
        return remB;
    }
    /**add from merge.
     * @param name s
     * @param blob1  blib
     * @param file1  the file name*/
    public void addfrommerge(String name, String blob1, String file1) {
        addB.add(name);
        blobmap.put(name, blob1);
        filemap.put(name, file1);
    }
}
