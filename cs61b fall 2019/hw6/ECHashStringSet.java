import net.sf.saxon.functions.Minimax;

import java.util.ArrayList;
import java.util.List;

/** A set of String values.
 *  @author
 */
class ECHashStringSet implements StringSet {
    public double Min_load = 0.2;
    public double Max_load = 5;
    private List[] Save;
    private int _Length;
    ECHashStringSet() {
        _Length = 0;
        int num = 5;
        Save = new List[5];
    }
    @Override
    public void put(String s) {
        if(s != null) {
            if(load() > Max_load) {
                reload();
            }
            int hashc = s.hashCode();
            hashc = hashchange(hashc);
            if (Save[hashc] == null) {
                Save[hashc] = new ArrayList<String>();
            }
            Save[hashc].add(s);
            _Length++;
        }
    }
    public int hashchange(int code) {
        if (code >= 0) {
            return code % Save.length;
        } else {
            code = code & 0x7fffffff;
            return code % Save.length;
        }
    }
    public double load() {
        return (double) _Length / (double)Save.length;
    }
    public int size() {
        return _Length;
    }
    public void reload() {
        List[] temp = Save;
        Save = new List[2*temp.length];
        _Length = 0;
        for (List<String> hh:temp) {
            if (hh != null) {
                for (String tt:hh) {
                    this.put(tt);
                }
            }
        }
    }
    @Override
    public boolean contains(String s) {
        int hc = hashchange(s.hashCode());
        if (Save[hc] == null) {
            return false;
        } else {
            return Save[hc].contains(s);
        }
    }

    @Override
    public List<String> asList() {
        List val = new ArrayList<String>();
        for (List<String> t1:Save) {
            if (t1 != null) {
                for (String kk : t1) {
                    val.add(kk);
                }
            }
        }
        return val;
    }
}
