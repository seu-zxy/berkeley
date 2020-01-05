package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
/**the class commit which reprsents the commit.
 * @author josiah*/
public class Commit implements Serializable {
    /**the message for commit.*/
    private String _message;
    /**the time for commit.*/
    private Date time;
    /**the direct parent of commit. */
    private Commit _parent;
    /**the shal1 code of commit.*/
    private String _hashcode;
    /**the merge parent of commit.*/
    private Commit _secondparent;
    /**the map between file name and realfile saved as name. */
    private Map<String, String> filename;
    /**the map between file name and real blob saved name. */
    private Map<String, String> blobname;
    /**the number helps to name file save name and blob save name. */
    private static int numforblob = 0;
    /**time format. */
    static final SimpleDateFormat FORMATT = new SimpleDateFormat(
            "EEE MMM d HH:mm:ss yyyy Z");
    /**current stage place.*/
    private Stage currstage;
    /**for merge, whether have gone to it. */
    private boolean marked;
    /**normal commit. */
    Commit() {
        filename = new HashMap<>();
        blobname = new HashMap<>();
        _secondparent = null;
        marked = false;
    }
    /**commit2.
     * @param par parent
     * @param message  the message
     * @param sss the stage*/
    Commit(Commit par, Stage sss, String message) {
        filename = new HashMap<>();
        blobname = new HashMap<>();
        _parent = par;
        currstage = sss;
        _message = message;
        _secondparent = null;
        marked = false;
    }
    /**commit3.
     * @param message  message
     * @param par  paremt
     * @param par2  merge parent
     * @param sss  the stage*/
    Commit(Commit par, Commit par2, Stage sss, String message) {
        filename = new HashMap<>();
        blobname = new HashMap<>();
        _parent = par;
        currstage = sss;
        _message = message;
        _secondparent = par2;
        marked = false;
    }
    /**commit4.
     * @param messgae message
     * @param ti the time*/
    Commit(String messgae, Date ti) {
        _message = messgae;
        filename = new HashMap<>();
        blobname = new HashMap<>();
        time = ti;
        _hashcode = hashc();
        _secondparent = null;
        marked = false;
    }
    /**set currentstage.
     * @param ss the stage ss*/
    public void setCurrstage(Stage ss) {
        currstage = ss;
    }
    /**for commit function.
     * @return 0 1*/
    public int commitMess() {
        filename.putAll(_parent.filename);
        ArrayList<String> remlist = currstage.getRemB();
        blobname.putAll(_parent.blobname);
        HashMap stagetemp = currstage.getFilemap();
        HashMap blobtemp = currstage.getBlobmap();
        if (stagetemp.size() == 0 && remlist.size() == 0) {
            System.out.println("No changes added to the commit.");
            return 0;
        }
        ArrayList names = currstage.getAddB();
        for (Object namfi : names) {
            String filenreal = (String) stagetemp.get(namfi);
            String blobreal = (String) blobtemp.get(namfi);
            if (blobname.containsKey(namfi)) {
                blobname.remove(namfi);
                filename.remove(namfi);
            }
            blobname.put((String) namfi, blobreal);
            filename.put((String) namfi, filenreal);
        }
        for (String filedd: remlist) {
            if (filename.containsKey(filedd)) {
                filename.remove(filedd);
                blobname.remove(filedd);
            }
        }
        time = new Date();
        _hashcode = hashc();
        return 1;
    }
    /**calculate shal1 code.
     * @return  hashcode */
    public String hashc() {
        List<Object> temp = new ArrayList<>();
        if (_message != null) {
            temp.add(_message);
        }
        if (time != null) {
            temp.add(time.toString());
        }
        if (filename.size() != 0) {
            temp.add(filename.toString());
        }
        if (blobname.size() != 0) {
            temp.add(blobname.toString());
        }

        String val = Utils.sha1(temp);
        return val;
    }
    /**get map filename.
     * @return map filename*/
    public Map<String, String> getFilename() {
        return filename;
    }
    /**get map blobname.
     * @return  map blobname*/
    public Map<String, String> getBlobname() {
        return blobname;
    }
    /**get shal1 code.
     * @return  String shal1 */
    public String gethashcode() {
        return _hashcode;
    }
    /**get parent commit.
     * @return  commit parent */
    public Commit getparent() {
        return _parent;
    }
    /**save commit in to disk. */
    public void savecommit() {
        String name = "commit1";
        File savefile = Utils.join(Main.MAINFOLDER, name);
        Utils.writeObject(savefile, this);
    }
    /**read commit from disk.
     * @return jj*/
    public static Commit readfromfile() {
        String name = "commit1";
        File savefile = Utils.join(Main.MAINFOLDER, name);
        return Utils.readObject(savefile, Commit.class);
    }
    /**get commit message.
     * @return  message*/
    public String getmessage() {
        return _message;
    }
    /**get commite time.
     * @return time*/
    public Date getTime() {
        return time;
    }
    /**get merge parent.
     * @return  merge parent*/
    public Commit getsecondparent() {
        return _secondparent;
    }
    /**as a symbol means alread comes. */
    public void changemarked() {
        marked = true;
    }
    /**get whether already counts.
     * @return  marked s */
    public boolean getmark() {
        return marked;
    }
}
