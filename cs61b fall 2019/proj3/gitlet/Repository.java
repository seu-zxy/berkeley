package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
/**the class represents repository.
 * @author josiath */
public class Repository implements Serializable {
    /**a map between shal1 and commit. */
    private  HashMap<String, Commit> represent;
    /**a map between branch name and shal1 code. */
    private  HashMap<String, String> branch;
    /**current commit. */
    private  Commit currcommit;
    /**current branch. */
    private  String currentbranch;
    /**normal repository. */
    Repository() {
        represent = new HashMap<>();
        branch = new HashMap<>();
        currcommit = null;
    }
   /**add commit and branch.
    * @param commit1 commit
    * @param branch1  the breanch*/
    void add(Commit commit1, String branch1) {
        String code = commit1.gethashcode();
        represent.put(code, commit1);
        Commit oldcommit = commit1.getparent();
        String old = branch.get(branch1);
        if (old != null) {
            branch.remove(branch1);
        }
        branch.put(branch1, code);
        currcommit = commit1;
        currentbranch = branch1;
    }
    /**get current commit.
     * @return  currcommit */
    public Commit getCurrcommit() {
        return currcommit;
    }
    /**save the repository to disk. */
    public void saverepository() {
        String name = "repository1";
        File savename = Utils.join(Main.MAINFOLDER, name);
        Utils.writeObject(savename, this);
    }
    /**get repository from disk.
     * @return  repository */
    public static Repository readobject() {
        String name = "repository1";
        File savename = Utils.join(Main.MAINFOLDER, name);
        return Utils.readObject(savename, Repository.class);
    }
    /**get current branch.
     * @return  currentbranch */
    public String getCurrentbranch() {
        return currentbranch;
    }
    /**get the map between shal1 and commit.
     * @return  represent */
    public HashMap<String, Commit> getRepresent() {
        return represent;
    }
    /**get the branchmap.
     * @return branch map */
    public HashMap<String, String> getBranch() {
        return branch;
    }

    /**set the current branch.
     * @param branch1 branch */
    public void setCurrentbranch(String branch1) {
        currentbranch = branch1;
    }

    /**add branch name.
     * @param name the branch name*/
    public void addbranch(String name) {
        branch.put(name, currcommit.gethashcode());
    }
    /**remove the branch.
     * @param name  the name */
    public void removebranch(String name) {
        branch.remove(name);
    }
    /**set the current commit.
     * @param commit1 the commit */
    public void setCurrcommit(Commit commit1) {
        currcommit = commit1;
    }
    /**change the branch. */
    public void changeBranch() {
        String hashccc = currcommit.gethashcode();
        for (HashMap.Entry<String, String> entry: branch.entrySet()) {
            String temphac = entry.getValue();
            if (temphac.equals(hashccc)) {
                currentbranch = entry.getKey();
                break;
            }
        }
    }
}
