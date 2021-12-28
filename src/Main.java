import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;

import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws SQLException, ClassNotFoundException, ClientException, ApiException {

        var students = CSV_Parser.createStudentsFromVK(new VK_API().findUsers("iot_second_urfu"), "java-rtf.csv");
        DataBaseManager.connect();
        DataBaseManager.createDB();
        fillDb(students);
    }

    private static void fillDb(List<Student> students) throws SQLException {
        DataBaseManager.cleanDb();
        var count = 1;
        for (var i : students) {
            DataBaseManager.writeDB(i);
            System.out.println("Подгружено студентов: " + count);
            count++;
        }
        DataBaseManager.closeDB();
    }
}