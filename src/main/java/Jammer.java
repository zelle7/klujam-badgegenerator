import java.util.HashMap;

/**
 * @author zelle (christian@zellot.at)
 */
public class Jammer {

    public static final String ART2D = "2dart";
    public static final String ART3D = "art3d";
    public static final String MUSIC = "music";
    public static final String PROGRAMMING = "programming";
    public static final String GAMEDESIGN = "gamedesign";
    public static final String STORY = "story";
    public static final String MANAGEMENT = "management";
    public static final String SUPPORT = "support";


    public static String[] skillNames = new String[]{ART2D, ART3D, MUSIC, PROGRAMMING, GAMEDESIGN, STORY, MANAGEMENT , SUPPORT };

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

    public void setSkill(String key, boolean val){
        this.skills.put(key, val ? 3 : 0);
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
