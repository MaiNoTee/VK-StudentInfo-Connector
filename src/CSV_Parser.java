import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CSV_Parser {

    private static List<List<String>> readCSV(String filename) {
        List<List<String>> records = new ArrayList<>();
        try (var reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                var values = line.split(";");
                records.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return records;
    }

    public static List<Student> parseStudentsFromCSV(String filename) {
        List<List<String>> parsedCSV = readCSV(filename);
        List<Student> students = new ArrayList<>();
        List<String> themes = getThemes(parsedCSV);
        List<String> tasksName = new ArrayList<>(parsedCSV.get(1));

        for (var i : parsedCSV.subList(3, parsedCSV.size())) {
            var nameAndSurname = i.get(0).split(" ");
            var person = new Person(nameAndSurname[1], nameAndSurname[0]);
            var courses = new ArrayList<Course>();
            var studentTasks = new ArrayList<Theme>();
            var tasksCount = Arrays.asList(1, 7, 9, 9, 11, 8, 13, 16, 7, 10, 11, 3, 2, 1, 1);
            var startIndex = 2;
            for (var j = 0; j < themes.size(); j++) {
                var tasks = new ArrayList<Task>();
                var tasksOfTheme = tasksName.subList(startIndex, startIndex + tasksCount.get(j));
                var taskValuePosition = startIndex;
                var taskMaxScorePosition = 0;
                for (var taskName : tasksOfTheme) {
                    tasks.add(new Task(taskName, Integer.parseInt(i.get(taskValuePosition)), Integer.parseInt(parsedCSV.get(2)
                            .get(startIndex + taskMaxScorePosition))));
                    taskValuePosition++;
                    taskMaxScorePosition++;
                }
                studentTasks.add(new Theme(themes.get(j), tasks, tasks.get(0).getScore(), Integer.parseInt(parsedCSV.get(2)
                        .get(startIndex))));
                startIndex = startIndex + tasksCount.get(j);
            }
            var course = new Course("Java-rtf", studentTasks, Integer.parseInt(parsedCSV.get(2)
                    .get(2)), i.get(1));
            courses.add(course);
            students.add(new Student(person, i.get(1), courses));
        }
        return students;
    }

    private static List<String> getThemes(List<List<String>> parsedCSV) {
        var themes = new ArrayList<String>();
        for (var theme : parsedCSV.get(0)) {
            if (theme.length() > 1)
                themes.add(theme);
        }
        return themes;
    }

    public static List<Student> createStudentsFromVK(List<JsonObject> vkData, String filename) {
        List<Student> students = CSV_Parser.parseStudentsFromCSV(filename);
        for (var student : students) {
            var response = Optional.<JsonObject>empty();
            for (var i : vkData) {
                if (i.get("first_name").getAsString().equals(student.getName())
                        && i.get("last_name").getAsString().equals(student.getSurname())) {
                    response = Optional.of(i);
                    break;
                }
            }
            if (response.isPresent()) {
                var value = response.get();
                if (value.has("bdate")) {
                    var bdate = value.get("bdate").getAsString();
                    if (bdate != null && !bdate.equals("")) {
                        student.setBdate(bdate);
                    }
                }

                if (value.has("city")) {
                    var city = value.get("city").getAsJsonObject().get("title").getAsString();
                    if (!city.equals("")) {
                        student.setCity(city);
                    }
                }

                if (value.has("sex")) {
                    var gender = value.get("sex").getAsInt();
                    if (gender == 1) {
                        student.setGender("Female");
                    }
                    if (gender == 2) {
                        student.setGender("Male");
                    }
                }

                var photo = value.get("photo_max").getAsString();
                if (!photo.equals("")) {
                    student.setPhoto(photo);
                }
                student.setVkID(value.get("id").getAsInt());
            }
        }
        return students;
    }
}