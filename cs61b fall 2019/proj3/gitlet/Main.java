package gitlet;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.Queue;


/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author josiath
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    static final File CWD = new File(".");
    /**format of date and time. */
    static final SimpleDateFormat FORMATT =
            new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
    /**current branch. */
    private static String branch;
    /**the main git let directory. */
    static final String SINIT = ".gitlet";
    /**main folder. */
    static final File MAINFOLDER = Utils.join(CWD, SINIT);
    /**current stage. */
    private static Stage _stage;
    /**current repository. */
    private static Repository _repository;
    /**current commit. */
    private static Commit _commit;
    /**determine whether is initiated.*/
    private static boolean _isinitiated = false;
    /**main function.
     * @param args the args */
    public static void main(String... args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        initemain();
        String command = args[0];
        usecommand(command, args);
    }
    /**initite this main function
     * if inititaed just load every thing
     * or nothing.*/
    public static void initemain() {
        if (MAINFOLDER.exists()) {
            _isinitiated = true;
        } else {
            _isinitiated = false;
        }
        if (_isinitiated) {
            _repository = Repository.readobject();
            _commit = Commit.readfromfile();
            _stage = Stage.readfromfile();
            branch = _repository.getCurrentbranch();
        }
    }
    /**use command according to args[1].
     * @param args args
     * @param command  the command*/
    public static void usecommand(String command, String...args) {
        int leng = args.length;
        switch (command) {
        case "init": {
            init(leng);
            break;
        }
        case "add": {
            add(leng, args);
            break;
        }
        case "commit": {
            commitf(leng, args);
            break;
        }
        case "checkout" : {
            checkoutgeneral(leng, args);
            break;
        }
        case "log" : {
            log(leng);
            break;
        }
        case "rm": {
            removefile(leng, args);
            break;
        }
        case "global-log": {
            goloballog(leng);
            break;
        }
        case "find": {
            find(leng, args);
            break;
        }
        case "status" : {
            status(leng);
            break;
        }
        case  "branch" : {
            addbranch(leng, args);
            break;
        }
        case "rm-branch" : {
            removebranch(leng, args);
            break;
        }
        case "reset" : {
            reset(leng, args);
            break;
        }
        case "merge" : {
            merge(leng, args);
            break;
        }
        default: break;
        }
    }
    /**the command init.
     * @param length  the length*/
    public static void init(int length) {
        if (_isinitiated) {
            String o =
                    " A Gitlet version-control system already "
                            + "exists in the current directory.";
            System.out.println(o);
            return;
        }
        if (length > 1) {
            System.out.println("Incorrect operands.");
            return;
        }
        _isinitiated = true;
        MAINFOLDER.mkdir();
        Stage.BLOBSAVE.mkdir();
        Stage.FILESAVE.mkdir();
        Stage.STAGESAVE.mkdir();
        _stage = new Stage();
        _repository = new Repository();
        _commit = null;
        String initime = "Wed Dec 31 16:00:00 1969 -0800";
        try {
            Date initdate = FORMATT.parse(initime);
            Commit first = new Commit("initial commit", initdate);
            _repository.add(first, "master");
            _commit = _repository.getCurrcommit();
            _stage.setcurrent(_commit);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        _stage.saveStage();
        _commit.savecommit();
        _repository.saverepository();
    }
    /**the command for add.
     * @param  length length of command
     * @param args1  the args*/
    public  static void add(int length, String... args1) {
        if (!_isinitiated) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        if (length != 2) {
            System.out.println("Incorrect operands.");
            return;
        }
        _stage.setcurrent(_commit);
        _stage.add(args1[1]);
        _stage.saveStage();
    }
    /**the commit.
     * @param args1 a
     * @param length  the length*/
    public static void commitf(int length, String... args1) {
        if (!_isinitiated) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        if (length < 2) {
            System.out.println("Incorrect operands.");
            return;
        }
        String mess = "";
        for (int i = 1; i < length - 1; i++) {
            mess = mess + args1[i] + " ";
        }
        mess = mess + args1[length - 1];
        if (mess.equals("")) {
            System.out.println("Please enter a commit message. ");
            return;
        }
        Commit newcommit = new Commit(_commit, _stage, mess);
        int det = newcommit.commitMess();
        if (det == 1) {
            _commit = newcommit;
            _repository.add(_commit, branch);
            _commit.savecommit();
            _repository.saverepository();
            _stage.clear();
            _stage.setcurrent(_commit);
            _stage.saveStage();
        }
    }
    /**write a object to disk.
     * @param file1 file
     * @param obj1 object*/
    public void writetodisk(File file1, Object obj1) {
        Utils.writeContents(file1, obj1);
    }
    /**checkout large function.
     * @param length length
     * @param args1 args*/
    public static void checkoutgeneral(int length, String...args1) {
        if (!_isinitiated) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        if (length == 3) {
            if (args1[1].equals("--")) {
                String fname = args1[2];
                int sit = checkout1(fname);
                if (sit == 0) {
                    return;
                }
            } else {
                System.out.println("Incorrect operands.");
                return;
            }
        } else if (length == 4) {
            if (args1[2].equals("--")) {
                String id = args1[1];
                String fname = args1[3];
                checkout2(id, fname);
            } else {
                System.out.println("Incorrect operands.");
                return;
            }
        } else if (length == 2) {
            String branch1 = args1[1];
            checkout3(branch1);
        } else {
            System.out.println("Incorrect operands.");
            return;
        }
    }

    /**the function checkout + file.
     * @param file1 file
     * @return 1 is success 0 is not*/
    public  static int checkout1(String file1) {
        Commit oldc = _commit;
        Map<String, String> lfile = oldc.getFilename();
        if (lfile.containsKey(file1)) {
            String realf = lfile.get(file1);
            File savef = Utils.join(_stage.FILESAVE, realf);
            byte[] newcontens = Utils.readContents(savef);
            File oldf = Utils.join(".", file1);
            Utils.writeContents(oldf, newcontens);
            return 1;
        } else {
            System.out.println("File does not exist in that commit.");
            return 0;
        }
    }
    /**check out id + file.
     * @param  id commit id
     * @param file1 file name*/
    private static void checkout2(String id, String file1) {
        HashMap<String, Commit> chang1 = _repository.getRepresent();
        if (chang1.containsKey(id)) {
            Commit rcommit = chang1.get(id);
            Map<String, String> lfilen = rcommit.getFilename();
            if (lfilen.containsKey(file1)) {
                String realf = lfilen.get(file1);
                File savef = Utils.join(_stage.FILESAVE, realf);
                byte[] newcontens = Utils.readContents(savef);
                File oldf = Utils.join(".", file1);
                Utils.writeContents(oldf, newcontens);
            } else {
                System.out.println("File does not exist in that commit.");
                return;
            }
        } else {
            System.out.println("No commit with that id exists.");
            return;
        }
    }
    /**checkout branch.
     * @param branch1 the branch check to */
    public static void checkout3(String branch1) {
        if (branch1.equals(branch)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        HashMap<String, String> brachhash = _repository.getBranch();
        if (brachhash.containsKey(branch1)) {
            String hashc = brachhash.get(branch1);
            HashMap<String, Commit> commithash = _repository.getRepresent();
            Commit rcommit = commithash.get(hashc);
            Map<String, String> currentfiles = rcommit.getFilename();
            Map<String, String> oldfieles =  _commit.getFilename();
            for (Map.Entry<String, String> entry:oldfieles.entrySet()) {
                String name1 = entry.getKey();
                if (!currentfiles.containsKey(name1)) {
                    File deletef = Utils.join(".", name1);
                    Utils.restrictedDelete(deletef);
                }
            }
            for (Map.Entry<String, String> entry:currentfiles.entrySet()) {
                String name1 = entry.getKey();
                String realnamef = entry.getValue();
                File realf = Utils.join(Stage.FILESAVE, realnamef);
                File savef = Utils.join(".", name1);
                if (!oldfieles.containsKey(name1) && savef.exists()) {
                    System.out.println("There is an untracked file in the way; "
                            + "delete it or add it first.");
                    return;
                }
                byte[] contents11 = Utils.readContents(realf);
                Utils.writeContents(savef, contents11);
            }
            _commit = rcommit;
            _stage.clear();
            _commit.savecommit();
            _stage.saveStage();
            branch = branch1;
            _repository.setCurrentbranch(branch);
            _repository.saverepository();
        } else {
            System.out.println("No such branch exists.");
            return;
        }
    }
    /**the lof command.
     * @param length the length */
    public static void log(int length) {
        if (length != 1) {
            System.out.println("Incorrect operands.");
            return;
        }
        Commit temp = _commit;
        while (temp != null) {
            System.out.println("===");
            System.out.println("commit " + temp.gethashcode());
            System.out.println("Date: " + FORMATT.format(temp.getTime()));
            System.out.println(temp.getmessage());
            System.out.println();
            temp = temp.getparent();
        }
    }
    /**the command remove file.
     * @param length of command
     * @param args1  the arg*/
    public static void removefile(int length, String... args1) {
        if (length != 2) {
            System.out.println("Incorrect operands.");
            return;
        }
        _stage.setcurrent(_commit);
        _stage.remove(args1[1]);
        _stage.saveStage();
    }

    /**the command global log.
     * @param length the length of the command */
    public static void goloballog(int length) {
        if (length != 1) {
            System.out.println("Incorrect operands.");
            return;
        }
        HashMap<String, Commit> present = _repository.getRepresent();
        ArrayList<Commit> hasprint = new ArrayList<>();
        for (HashMap.Entry<String, Commit> entry: present.entrySet()) {
            Commit temp = entry.getValue();
            if (temp != null && !hasprint.contains(temp)) {
                System.out.println("===");
                System.out.println("commit " + temp.gethashcode());
                System.out.println("Date: " + FORMATT.format(temp.getTime()));
                System.out.println(temp.getmessage());
                System.out.println();
                hasprint.add(temp);
            }
        }
    }
    /**the command find.
     * @param args1  the arg
     * @param length  the length of the command*/
    public static void find(int length, String... args1) {
        if (length < 2) {
            System.out.println("Incorrect operands.");
            return;
        }
        boolean find = false;
        String mess = "";
        for (int i = 1; i < length - 1; i++) {
            mess = mess + args1[i] + " ";
        }
        mess = mess + args1[length - 1];
        HashMap<String, Commit> present = _repository.getRepresent();
        for (HashMap.Entry<String, Commit> entry:present.entrySet()) {
            Commit pre = entry.getValue();
            String messnow = pre.getmessage();
            if (mess.equals(messnow)) {
                find = true;
                System.out.println(entry.getKey());
            }
        }
        if (!find) {
            System.out.println("Found no commit with that message.");
            return;
        }
    }
    /**the command status.
     * @param length the length of the command. */
    public static void status(int length) {
        if (length != 1) {
            System.out.println("Incorrect operands.");
            return;
        }
        HashMap<String, String> branchall = _repository.getBranch();
        System.out.println("=== Branches ===");
        System.out.println("*" + branch);
        for (HashMap.Entry<String, String> entry:branchall.entrySet()) {
            String temp = entry.getKey();
            if (!temp.equals(branch)) {
                System.out.println(temp);
            }
        }
        System.out.println();
        ArrayList<String> stageadd = _stage.getAddB();
        ArrayList<String> stageremove = _stage.getRemB();
        System.out.println("=== Staged Files ===");
        for (String na: stageadd) {
            System.out.println(na);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (String na:stageremove) {
            System.out.println(na);
        }
        System.out.println();
        status2();
    }

    /**for modifeid not stage. */
    public static void status2() {
        ArrayList<String> stageadd = _stage.getAddB();
        ArrayList<String> stageremove = _stage.getRemB();
        Map<String, String> stageblobfile = _stage.getBlobmap();
        System.out.println("=== Modifications Not Staged For Commit ===");
        Map<String, String> commitblob = _commit.getBlobname();
        for (Map.Entry<String, String> entry:commitblob.entrySet()) {
            String fn1 = entry.getKey();
            String blobsavename = entry.getValue();
            File workfile = Utils.join(".", fn1);
            if (workfile.exists()) {
                File blobsave = Utils.join(Stage.BLOBSAVE, blobsavename);
                byte[] contentnow = Utils.readContents(workfile);
                String nowhac = Utils.sha1(contentnow);
                String commithac = Utils.readContentsAsString(blobsave);
                if (!commithac.equals(nowhac)) {
                    if (!stageadd.contains(fn1)) {
                        System.out.println(fn1 + " (modified)");
                    }
                }
            } else {
                if (!stageremove.contains(fn1)) {
                    System.out.println(fn1 + " (deleted)");
                }
            }
        }
        for (String fn1:stageadd) {
            File workfile = Utils.join(".", fn1);
            if (workfile.exists()) {
                byte[] content1 = Utils.readContents(workfile);
                String nowhac = Utils.sha1(content1);
                String blobname = stageblobfile.get(fn1);
                File blob1 = Utils.join(Stage.BLOBSAVE, blobname);
                String shal2 = Utils.readContentsAsString(blob1);
                if (!shal2.equals(nowhac)) {
                    System.out.println(fn1 + " (modified)");
                }
            } else {
                System.out.println(fn1 + " (deleted)");
            }
        }
        System.out.println();
        status3();
    }

    /**for untracked file. */
    public static void status3() {
        ArrayList<String> stageadd = _stage.getAddB();
        Map<String, String> stageblobfile = _stage.getBlobmap();
        Map<String, String> commitblob = _commit.getBlobname();
        System.out.println("=== Untracked Files ===");
        List<String> allfile = Utils.plainFilenamesIn(CWD);
        for (String name: allfile) {
            if (!stageblobfile.containsKey(name)
                    && !commitblob.containsKey(name)) {
                System.out.println(name);
            }
        }
        System.out.println();
    }
    /**the command branch.
     * @param length the length of the command
     * @param args1 the arg*/
    public static void addbranch(int length, String...args1) {
        if (length != 2) {
            System.out.println("Incorrect operands.");
            return;
        }
        boolean repeat = false;
        HashMap<String, String> branchall = _repository.getBranch();
        for (HashMap.Entry<String, String> entry:branchall.entrySet()) {
            String nameb = entry.getKey();
            if (args1[1].equals(nameb)) {
                repeat = true;
                break;
            }
        }
        if (repeat) {
            System.out.println(" A branch with that name already exists.");
            return;
        }
        _repository.addbranch(args1[1]);
        _repository.saverepository();
    }
    /**the command rm branch.
     * @param args1 the arg
     * @param length the length of the command*/
    public static void removebranch(int length, String...args1) {
        if (length != 2) {
            System.out.println("Incorrect operands.");
            return;
        }
        if (args1[1].equals(branch)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        HashMap<String, String> branchfile = _repository.getBranch();
        if (!branchfile.containsKey(args1[1])) {
            System.out.println(" A branch with that name does not exist.");
            return;
        }
        _repository.removebranch(args1[1]);
        _repository.saverepository();
    }
    /**the command reset.
     * @param length LENGTH
     * @param args1 need commit id*/
    public static void reset(int length, String...args1) {
        if (length != 2) {
            System.out.println("Incorrect operands.");
            return;
        }
        HashMap<String, Commit> commithash = _repository.getRepresent();
        int length2 = args1[1].length();
        Commit wantcommit = null;
        boolean have = false;
        for (HashMap.Entry<String, Commit> entry:commithash.entrySet()) {
            String compare11 = entry.getKey().substring(0, length2);
            if (compare11.equals(args1[1])) {
                have = true;
                wantcommit = entry.getValue();
                break;
            }
        }
        if (!have) {
            System.out.println(" No commit with that id exists.");
        }
        wantcommit = commithash.get(args1[1]);
        resetfunc(wantcommit);
    }

    /**reset function.
     * @param wantcommit  want commit*/
    public static void resetfunc(Commit wantcommit) {
        Map<String, String> filetoname1 = wantcommit.getFilename();
        Map<String, String> filetoname2 = _commit.getFilename();
        for (Map.Entry<String, String> entry:filetoname1.entrySet()) {
            String name1 = entry.getKey();
            if (!filetoname2.containsKey(name1)) {
                File file1 = Utils.join(".", name1);
                if (file1.exists()) {
                    String filename2 = entry.getValue();
                    File file2 = Utils.join(Stage.FILESAVE, filename2);
                    byte[] content1 = Utils.readContents(file1);
                    byte[] content2 = Utils.readContents(file2);
                    String hac1 = Utils.sha1(content1);
                    String hac2 = Utils.sha1(content2);
                    if (!hac1.equals(hac2)) {
                        System.out.println("There is an untracked file in"
                                + " the way; delete it or add it first.");
                        return;
                    }
                }
            }
        }
        for (Map.Entry<String, String> entry:filetoname2.entrySet()) {
            String name2 = entry.getKey();
            if (!filetoname1.containsKey(name2)) {
                File deletefi = Utils.join(".", name2);
                if (deletefi.exists()) {
                    Utils.restrictedDelete(deletefi);
                }
            } else {
                String realname = filetoname1.get(name2);
                File realf = Utils.join(Stage.FILESAVE, realname);
                byte[] contents = Utils.readContents(realf);
                File writeto = Utils.join(".", name2);
                Utils.writeContents(writeto, contents);
            }
        }
        _commit = wantcommit;
        _stage.clear();
        _repository.setCurrcommit(wantcommit);
        _repository.changeBranch();
        _commit.savecommit();
        _stage.saveStage();
        _repository.saverepository();
    }
    /**may not use.
     * @param wantcommit  commit*/
    public static void deltefiledir(Commit wantcommit) {
        List<String> workingfiles = Utils.plainFilenamesIn(CWD);
        Map<String, String> filetoname1 = wantcommit.getFilename();
        for (String name:workingfiles) {
            if (!filetoname1.containsKey(name)) {
                File delete = Utils.join(".", name);
                Utils.restrictedDelete(delete);
            }
        }
    }
    /**the command merge.
     * @param args1 the arg
     * @param length the length of the command */
    public static void merge(int length, String...args1) {
        if (!_isinitiated) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        if (length != 2) {
            System.out.println("Incorrect operands.");
            return;
        }
        int siez = _stage.getAddB().size() + _stage.getRemB().size();
        if (siez != 0) {
            System.out.println("You have uncommitted changes.");
            return;
        }
        HashMap<String, String> branchname = _repository.getBranch();
        if (!branchname.containsKey(args1[1])) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (args1.equals(branch)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        Commit split = findsplit(args1[1]);
        String givenstring = branchname.get(args1[1]);
        Commit givencomit = _repository.getRepresent().get(givenstring);
        if (split.gethashcode().equals(givencomit.gethashcode())) {
            String temp1 = "Given branch is an ancestor of the current branch.";
            System.out.println(temp1);
            return;
        }
        if (_commit.gethashcode().equals(split.gethashcode())) {
            resetfunc(givencomit);
            System.out.println("Current branch fast-forwarded.");
            return;
        }
        _stage.setcurrent(_commit);
        int cancommit = mergemain(split, givencomit);
        if (cancommit == 1) {
            String message = "Merged " + args1[1] + " into " + branch + ".";
            Commit newcommit = new Commit(_commit, givencomit, _stage, message);
            int det = newcommit.commitMess();
            if (det == 1) {
                _commit = newcommit;
                _repository.add(_commit, branch);
                _commit.savecommit();
                _repository.saverepository();
                _stage.clear();
                _stage.setcurrent(_commit);
                _stage.saveStage();
            }
        } else if (cancommit == 0) {
            System.out.println("Encountered a merge conflict.");
            return;
        } else if (cancommit == -1) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it or add it first.");
            return;
        }
    }
    /**just use to style check reduce.
     * @param givencomit commit
     * @param split  split
     * @return 1,0*/
    public static int mergetemp(Commit split, Commit givencomit) {
        Map<String, String> currentblobname = _commit.getBlobname();
        Map<String, String> givenblobname = givencomit.getBlobname();
        Map<String, String> givenfilename = givencomit.getFilename();
        ArrayList<String> addtogiven = getaddlist(split, givencomit);
        for (String name:addtogiven) {
            String blobname = givenblobname.get(name);
            String filename = givenfilename.get(name);
            File file1 = Utils.join(Stage.FILESAVE, filename);
            byte[] content = Utils.readContents(file1);
            File writeto = Utils.join(".", name);
            if (writeto.exists()) {
                if (!currentblobname.containsKey(name)) {
                    return -1;
                }
            }
            Utils.writeContents(writeto, content);
            _stage.addfrommerge(name, blobname, filename);
        }
        return 0;
    }
    /**the main function of merge.
     * @param givencomit the branch we want to go
     * @param split the split point
     * @return the status*/
    public static int mergemain(Commit split, Commit givencomit) {
        Map<String, String> currentblobname = _commit.getBlobname();
        Map<String, String> currentfilename = _commit.getFilename();
        Map<String, String> givenfilename = givencomit.getFilename();
        ArrayList<String> removelist = getremovelist(split, givencomit);
        ArrayList<String> rewritelist = getrewritelist(split, givencomit);
        int k = mergetemp(split, givencomit);
        if (k == -1) {
            return -1;
        }
        for (String name:removelist) {
            File writeto = Utils.join(".", name);
            if (writeto.exists()) {
                if (!currentblobname.containsKey(name)) {
                    return -1;
                }
            }
            _stage.remove(name);
        }
        for (String name:rewritelist) {
            String filename1 = currentfilename.get(name);
            String filename2 = givenfilename.get(name);
            File writeto = Utils.join(".", name);
            if (writeto.exists()) {
                if (!currentblobname.containsKey(name)) {
                    return -1;
                }
            }
            String content1 = null;
            String content2 = null;
            if (filename1 == null) {
                content1 = "";
            } else {
                File file1 = Utils.join(Stage.FILESAVE, filename1);
                content1 = Utils.readContentsAsString(file1);
            }
            if (filename2 == null) {
                content2 = "";
            } else {
                File file2 = Utils.join(Stage.FILESAVE, filename2);
                content2 = Utils.readContentsAsString(file2);
            }
            String val = "<<<<<<< HEAD\n";
            val = val.concat(content1);
            val = val.concat("=======\n");
            val = val.concat(content2);
            val = val.concat(">>>>>>>\n");
            Utils.writeContents(writeto, val);
            _stage.add(name);
        }
        if (rewritelist.isEmpty()) {
            return 1;
        } else {
            return 0;
        }
    }
   /**get rewrite list.
    * @param split the split commit
    * @param givencomit the givencommit
    * @return the list*/
    public static  ArrayList<String> getrewritelist(Commit split,
                                                    Commit givencomit) {
        ArrayList<String> rewritelist = new ArrayList<>();
        Map<String, String> currentblobname = _commit.getBlobname();
        Map<String, String> currentfilename = _commit.getFilename();
        Map<String, String> splitblobname = split.getBlobname();
        Map<String, String> givenblobname = givencomit.getBlobname();
        Map<String, String> givenfilename = givencomit.getFilename();
        for (Map.Entry<String, String> entry:currentblobname.entrySet()) {
            String name = entry.getKey();
            String blob1 = entry.getValue();
            if (splitblobname.containsKey(name)) {
                String blob2 = splitblobname.get(name);
                if (!blob1.equals(blob2)) {
                    if (givenblobname.containsKey(name)) {
                        String blob3 = givenblobname.get(name);
                        if (!blob1.equals(blob3)) {
                            rewritelist.add(name);
                        }
                    } else {
                        rewritelist.add(name);
                    }
                }
            } else {
                if (givenblobname.containsKey(name)) {
                    String blob4 = givenblobname.get(name);
                    if (!blob1.equals(blob4)) {
                        rewritelist.add(name);
                    }
                }
            }
        }
        for (Map.Entry<String, String> entry:givenblobname.entrySet()) {
            String name = entry.getKey();
            String blob1 = givenblobname.get(name);
            if (splitblobname.containsKey(name)) {
                String blob2 = splitblobname.get(name);
                if (!blob1.equals(blob2)) {
                    if (!currentblobname.containsKey(name)) {
                        rewritelist.add(name);
                    }
                }
            }
        }
        return rewritelist;
    }

   /**get removelist file.
    * @param givencomit the given commit
    * @param split the split point
    * @return  the arratlist*/
    public static ArrayList<String> getremovelist(Commit split,
                                                  Commit givencomit) {
        ArrayList<String> removelist = new ArrayList<>();
        Map<String, String> currentblobname = _commit.getBlobname();
        Map<String, String> currentfilename = _commit.getFilename();
        Map<String, String> splitblobname = split.getBlobname();
        Map<String, String> givenblobname = givencomit.getBlobname();
        Map<String, String> givenfilename = givencomit.getFilename();
        for (Map.Entry<String, String> entry:splitblobname.entrySet()) {
            String name = entry.getKey();
            String blob1 = entry.getValue();
            if (currentblobname.containsKey(name)) {
                String blob2 = currentblobname.get(name);
                if (blob2.equals(blob1)) {
                    if (!givenblobname.containsKey(name)) {
                        removelist.add(name);
                    }
                }
            }
        }
        return removelist;
    }

   /**get add list.
    * @param split the split commit
    * @param givencomit the given commit
    * @return the list*/
    public static ArrayList<String> getaddlist(Commit split,
                                               Commit givencomit) {
        ArrayList<String> addtogiven = new ArrayList<>();
        Map<String, String> currentblobname = _commit.getBlobname();
        Map<String, String> currentfilename = _commit.getFilename();
        Map<String, String> splitblobname = split.getBlobname();
        Map<String, String> givenblobname = givencomit.getBlobname();
        Map<String, String> givenfilename = givencomit.getFilename();
        for (Map.Entry<String, String> entry:givenblobname.entrySet()) {
            String name = entry.getKey();
            String blob1 = entry.getValue();
            if (splitblobname.containsKey(name)) {
                String blob2 = splitblobname.get(name);
                if (!blob2.equals(blob1)) {
                    if (currentblobname.containsKey(name)) {
                        String blob3 = currentblobname.get(name);
                        if (blob3.equals(blob2)) {
                            addtogiven.add(name);
                        }
                    }
                }
            } else {
                if (!currentblobname.containsKey(name)) {
                    addtogiven.add(name);
                }
            }
        }
        return addtogiven;
    }
   /**find the split commit.
    * @param branch1111 the branch name
    * @return  the split commit*/
    public static Commit findsplit(String branch1111) {
        Commit split = null;
        HashMap<String, String> branchname = _repository.getBranch();
        HashMap<String, Commit> commitcode = _repository.getRepresent();
        String givenstring = branchname.get(branch1111);
        Commit givencomit = _repository.getRepresent().get(givenstring);
        Queue<Commit> depth = new LinkedList<>();
        ArrayList<String> commitsave = new ArrayList<>();
        depth.add(givencomit);
        while (!depth.isEmpty()) {
            Commit temp = depth.poll();
            String shal1 = temp.gethashcode();
            if (temp.getparent() != null) {
                depth.offer(temp.getparent());
            }
            if (temp.getsecondparent() != null) {
                depth.offer(temp.getsecondparent());
            }
            if (!commitsave.contains(shal1)) {
                commitsave.add(shal1);
            }
        }
        Queue<Commit> currentsave = new LinkedList<>();
        currentsave.offer(_commit);
        while (!currentsave.isEmpty()) {
            Commit temp = currentsave.poll();
            String shal1 = temp.gethashcode();
            if (commitsave.contains(shal1)) {
                split = commitcode.get(shal1);
                break;
            }
            if (temp.getparent() != null && !temp.getmark()) {
                currentsave.offer(temp.getparent());
            }
            if (temp.getsecondparent() != null && !temp.getmark()) {
                currentsave.offer(temp.getsecondparent());
            }
            if (!temp.getmark() && temp != null) {
                temp.changemarked();
            }
        }
        return split;
    }
}
