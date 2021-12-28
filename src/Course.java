import java.util.List;

public class Course {
    private final String name;
    private final List<Theme> themes;
    private int maxScore;
    private final String group;

    public Course(String name, List<Theme> themes, int maxScore, String group) {
        this.name = name;
        this.themes = themes;
        this.maxScore = maxScore;
        this.group = group;

    }

    public String getName() {
        return name;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public String getGroup() {
        return group;
    }

    public List<Theme> getThemes() {
        return themes;
    }
}
