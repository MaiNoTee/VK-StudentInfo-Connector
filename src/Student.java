import java.util.ArrayList;
import java.util.List;

public class Student extends Person {
    private List<Course> courses;

    public Student(String name, String surname) {
        super(name, surname);
        courses = new ArrayList<>();
    }

    public Student(Person person, String group, List<Course> courses) {
        super(person.getName(), person.getSurname());
        this.courses = courses;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void addCourse(Course courses) {
        this.courses.add(courses);
    }
}
