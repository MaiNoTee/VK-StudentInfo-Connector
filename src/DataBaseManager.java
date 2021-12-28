import java.sql.*;
import java.util.*;


public class DataBaseManager {
    private static Connection conn;
    private static Statement statmt;

    // --------ПОДКЛЮЧЕНИЕ К БАЗЕ ДАННЫХ--------
    public static void connect() throws ClassNotFoundException, SQLException {
        conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:database.s3db");
        statmt = conn.createStatement();
        System.out.println("База Подключена!");
    }

    // --------СОЗДАНИЕ ТАБЛИЦ--------
    public static void createDB() throws SQLException {

        statmt.execute("CREATE TABLE IF NOT EXISTS courses\n" +
                "(\n" +
                "    id          integer not null\n" +
                "        primary key autoincrement,\n" +
                "    courseName  text    not null,\n" +
                "    maxScore    integer not null,\n" +
                "    courseGroup text    not null,\n" +
                "    studentId   integer not null\n" +
                ");");
        statmt.execute("CREATE TABLE IF NOT EXISTS people(\n" +
                "    personId  integer not null\n" +
                "        primary key autoincrement,\n" +
                "    name      text    not null,\n" +
                "    surname   text    not null,\n" +
                "    city      text    not null,\n" +
                "    birthdate date,\n" +
                "    image     text    not null,\n" +
                "    vkId      integer not null,\n" +
                "    gender    integer default 0 not null\n" +
                ");");

        statmt.execute("CREATE TABLE IF NOT EXISTS students(\n" +
                "    student_id integer not null\n" +
                "        references person,\n" +
                "    course_id  integer not null\n" +
                "        references course\n" +
                ");");
        statmt.execute("CREATE TABLE IF NOT EXISTS tasks(\n" +
                "    id        integer not null\n" +
                "        primary key autoincrement,\n" +
                "    task_name text    not null,\n" +
                "    score     integer default 0 not null,\n" +
                "    theme_id  integer not null\n" +
                "        references theme,\n" +
                "    max_score integer default 0 not null\n" +
                ");");
        statmt.execute("CREATE TABLE IF NOT EXISTS themes(\n" +
                "    theme_name      text    not null,\n" +
                "    studentMaxPoint integer not null,\n" +
                "    maxPoint        integer not null,\n" +
                "    course_id       integer not null\n" +
                "        references course,\n" +
                "    themeId         integer not null\n" +
                "        primary key autoincrement\n" +
                ");");
        System.out.println("Таблицы созданы или уже существуют.");
    }

    // --------ЗАПОЛНЕНИЕ ТАБЛИЦ--------

    public static boolean writeDB(Student student) throws SQLException {
        var courses = student.getCourses();

        fillPeople(new String[]{
                student.getName(), student.getSurname(), student.getCity(), student.getBdate(), student.getPhoto(),
                String.valueOf(student.getVkID()), String.valueOf(student.getGender())});
        var student_id = statmt.executeQuery("SELECT * FROM people WHERE name='" + student.getName()
                        + "' and surname= '" + student.getSurname() + "' and vkId=" + student.getVkID() + ";")
                .getInt("personId");
        var coursesIds = fillCourses(courses, student_id);

        //-------------Students-------------
        for (var id : coursesIds) {
            var query = "INSERT INTO students (student_id, course_id)\n" +
                    "values (" + student_id + ", " + id + ");";
            try {
                statmt.execute(query);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return false;
    }

    //-------------People-------------
    private static void fillPeople(String[] person) {
        var builder = new StringBuilder();
        for (var i : person) {
            builder.append("'").append(i).append("'").append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
        var query = "INSERT INTO people (name, surname, city, birthdate, image, vkId, gender)\n" +
                "values (" + builder.toString() + ");";
        try {
            statmt.execute(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    private static List<Integer> fillCourses(List<Course> courses, int student_id) throws SQLException {
        var result = new ArrayList<Integer>();

        //-------------Courses-------------
        for (var course : courses) {
            var courseName = course.getName();
            var maxScore = course.getMaxScore();
            var group = course.getGroup();
            var courseQuery = "INSERT INTO courses (courseName, maxScore, courseGroup, studentId)\n" +
                    "values ('" + courseName + "', " + maxScore + ", '" + group + "', " + student_id + ");";
            try {
                statmt.execute(courseQuery);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            var courseId = statmt.executeQuery("SELECT * FROM courses WHERE courseName='" + courseName +
                    "' and studentId=" + student_id + ";").getInt("id");
            result.add(courseId);

            //-------------Themes-------------
            for (var theme : course.getThemes()) {
                var themeName = theme.getName();
                var studentMaxPoint = theme.getStudentMaxPoint();
                var maxPoint = theme.getMaxPoint();
                var themeQuery = "INSERT INTO themes (theme_name, studentMaxPoint, maxPoint, course_id)\n"
                        + "values ('" + themeName + "', " + studentMaxPoint + ", " + maxPoint + ", " + courseId + ");";
                try {
                    statmt.execute(themeQuery);
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
                var themeId = statmt.executeQuery("SELECT * FROM themes WHERE theme_name='" + themeName
                        + "' and course_id =" + courseId + ";").getInt("themeId");

                //-------------Tasks-------------
                for (var task : theme.getTasks()) {
                    var taskScore = task.getScore();
                    var taskName = task.getName();
                    var taskQuery = "INSERT INTO tasks (task_name, score, theme_id, max_score)\n" +
                            "values ('" + taskName + "', " + taskScore + ", '" + themeId + "', " + task.getMaxScore() + ");";
                    try {
                        statmt.execute(taskQuery);
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
        return result;
    }

    public static void cleanDb() {
        try {
            statmt.execute("delete from courses");
            statmt.execute("delete from people");
            statmt.execute("delete from students");
            statmt.execute("delete from tasks");
            statmt.execute("delete from themes");
            System.out.println("Таблицы очищены");
        } catch (SQLException e) {
            e.getErrorCode();
        }
    }

    // --------Закрытие БД--------
    public static void closeDB() throws SQLException {
        statmt.close();
        conn.close();
        System.out.println("Соединения закрыты");
    }
}