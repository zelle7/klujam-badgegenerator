import java.util.HashMap;

/**
 * @author zelle (christian@zellot.at)
 */
public class Jammer {

    public static String[] skillNames = new String[]{"2dart", "art3d", "music", "programming", "gamedesign", "story", "management", "support"};

    private String name;
    private HashMap<String, Integer> skills = new HashMap<String, Integer>();

    private int fri;
    private int sat;
    private int su;

    public Jammer(){
        this.skills = new HashMap<String, Integer>();
        for(String skill : skillNames){
            this.skills.put(skill, 0);
        }
    }

    public static String[] getSkillNames() {
        return skillNames;
    }

    public static void setSkillNames(String[] skillNames) {
        Jammer.skillNames = skillNames;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Integer> getSkills() {
        return skills;
    }

    public Integer getSkill(String key) {
        return skills.get(key);
    }

    public void setSkills(HashMap<String, Integer> skills) {
        this.skills = skills;
    }

    public int getFri() {
        return fri;
    }

    public void setFri(int fri) {
        this.fri = fri;
    }

    public int getSat() {
        return sat;
    }

    public void setSat(int sat) {
        this.sat = sat;
    }

    public int getSu() {
        return su;
    }

    public void setSu(int su) {
        this.su = su;
    }
}
