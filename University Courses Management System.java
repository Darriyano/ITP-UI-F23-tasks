import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public final class UniversityCourseManagementSystem {
    public static void main(String[] args) {
        try {
            /**
             * Variables we accept from user or rewriting in the code
             * */
            String command;
            String courseName;
            String courseLevel;
            String memberName;
            String memberId;
            String courseId;
            CourseLevel courseLev = CourseLevel.BACHELOR;
            /**
             * Prohibited names
             * */
            List<String> subrtring = Arrays.asList("course", "student", "professor", "enroll",
                    "drop", "teach", "exempt");
            Scanner scan = new Scanner(System.in);
            /**
             * Filling with initial data
             * */
            fillnitialData();
            /**
             * Infinite scan of the lines
             * */
            while (scan.hasNextLine()) {
                command = scan.nextLine().toLowerCase();
                if (command.isEmpty()) {
                    dropError("Wrong inputs");
                }
                /**
                 * handler of the commands we receive
                 */
                if (command.equals("course")) {
                    courseName = scan.nextLine();
                    /**
                     * handler of all potential mistakes in the input
                     */
                    mistakeInName(courseName);
                    courseName = courseName.toLowerCase();
                    ifCourseExist(courseName);
                    hasNextLine(scan);
                    courseLevel = scan.nextLine();
                    if (courseLevel.isEmpty()) {
                        System.exit(0);
                    }
                    equalsLevel(courseLevel);
                    if (courseLevel.toLowerCase().equals("master")) {
                        courseLev = CourseLevel.MASTER;
                    } else {
                        courseLev = CourseLevel.BACHELOR;
                    }
                    Course addCourse = new Course(courseName.toLowerCase(), courseLev);
                    System.out.println("Added successfully");
                } else if (command.equals("student")) {
                    memberName = scan.nextLine();
                    /**
                     * handler of all potential mistakes in the input
                     */
                    mistakeName(memberName);
                    memberName = memberName.toLowerCase();
                    Student student = new Student(memberName);
                    System.out.println("Added successfully");
                } else if (command.equals("professor")) {
                    memberName = scan.nextLine();
                    /**
                     * handler of all potential mistakes in the input
                     */
                    mistakeName(memberName);
                    memberName = memberName.toLowerCase();
                    Professor professor = new Professor(memberName);
                    System.out.println("Added successfully");
                } else if (command.equals("enroll")) {
                    memberId = scan.nextLine();
                    /**
                     * handler of all potential mistakes in the input
                     */
                    mistakeCatcher(memberId);
                    courseId = scan.nextLine();
                    mistakeCatcher(courseId);
                    /**
                     * checking if all conditions of the inputs met
                     */
                    entered(memberId);
                    isAlreadyEnrolled(courseId, memberId);
                    maxEnrollemnt(memberId);
                    courseIsFull(courseId);
                    toEnroll(courseId, memberId);
                } else if (command.equals("drop")) {
                    memberId = scan.nextLine();
                    /**
                     * handler of all potential mistakes in the input
                     */
                    mistakeCatcher(memberId);
                    courseId = scan.nextLine();
                    mistakeCatcher(courseId);
                    /**
                     * checking if all conditions of the inputs met
                     */
                    entered(memberId);
                    toDrop(courseId, memberId);
                } else if (command.equals("exempt")) {
                    memberId = scan.nextLine();
                    /**
                     * handler of all potential mistakes in the input
                     */
                    mistakeCatcher(memberId);
                    courseId = scan.nextLine();
                    mistakeCatcher(courseId);
                    /**
                     * checking if all conditions of the inputs met
                     */
                    checkEnteredId(memberId);
                    toExempt(courseId, memberId);
                } else if (command.equals("teach")) {
                    /**
                     * handler of all potential mistakes in the input
                     */
                    memberId = scan.nextLine();
                    mistakeCatcher(memberId);
                    courseId = scan.nextLine();
                    mistakeCatcher(courseId);
                    /**
                     * checking if all conditions of the inputs met
                     */
                    checkProfId(memberId);
                    checkLoad(memberId);
                    List<Course> gettedCourses = Course.getCourses();
                    isAlreadyTeaching(courseId, memberId);
                    toAssign(courseId, memberId);
                } else {
                    dropError("Wrong inputs");
                }
            }
        } catch (Exception exception) {
            dropError("Wrong inputs");
        }
    }

    /**
     * Function to enroll the student to the course
     */
    public static void toEnroll(String courseId, String memberId) {
        List<Course> gettedCourses = Course.getCourses();
        for (Course course : gettedCourses) {
            if (course.getCourseId() == Integer.parseInt(courseId)) {
                for (Student student : Student.getAllStudent()) {
                    if (student.getMemberId() == Integer.parseInt(memberId)) {
                        student.enroll(course);
                        System.out.println("Enrolled successfully");
                        break;
                    }
                }
            }
        }
    }

    /**
     * Function which checks if student is already enrolled
     */
    public static void isAlreadyEnrolled(String courseId, String memberId) {
        List<Course> gettedCourses = Course.getCourses();
        boolean neverEntered = true;
        for (Course course : gettedCourses) {
            if (course.getCourseId() == Integer.parseInt(courseId)) {
                for (Student student : course.getEnrolledStudents()) {
                    if (student.getMemberId() == Integer.parseInt(memberId)) {
                        dropError("Student is already enrolled in this course");
                    }
                }
                neverEntered = false;
                break;
            }
        }
        isNotTrue(!neverEntered, "Wrong inputs");
    }

    /**
     * Function which exempts member to a course
     */
    public static void toExempt(String courseId, String memberId) {
        boolean isEnrolled = false;
        List<Course> gettedCourses = Course.getCourses();
        for (Course course : gettedCourses) {
            if (course.getCourseId() == Integer.parseInt(courseId)) {
                for (Professor professor : course.getEnrolledProfs()) {
                    if (professor.getMemberId() == Integer.parseInt(memberId)) {
                        isEnrolled = true;
                        professor.exempt(course);
                        System.out.println("Professor is exempted");
                        break;
                    }
                }
            }
        }
        isNotTrue(isEnrolled, "Professor is not teaching this course");
    }

    /**
     * Checker if the member is a professor
     */
    public static void checkEnteredId(String memberId) {
        boolean entered = false;
        for (Professor prof : Professor.getAllProfs()) {
            if (prof.getMemberId() == Integer.parseInt(memberId)) {
                entered = true;
                break;
            }
        }
        isNotTrue(entered, "Wrong inputs");
    }

    /**
     * Function which assign professor to the course
     */
    public static void toAssign(String courseId, String memberId) {
        List<Course> gettedCourses = Course.getCourses();
        for (Course course : gettedCourses) {
            if (course.getCourseId() == Integer.parseInt(courseId)) {
                for (Professor professor : Professor.getAllProfs()) {
                    if (professor.getMemberId() == Integer.parseInt(memberId)) {
                        professor.teach(course);
                        System.out.println("Professor is successfully assigned to teach this course");
                        break;
                    }
                }
            }
        }
    }

    /**
     * Function which drop
     */
    public static void toDrop(String courseId, String memberId) {
        boolean isEnrolled = false;
        List<Course> gettedCourses = Course.getCourses();
        for (Course course : gettedCourses) {
            if (course.getCourseId() == Integer.parseInt(courseId)) {
                for (Student student : course.getEnrolledStudents()) {
                    if (student.getMemberId() == Integer.parseInt(memberId)) {
                        isEnrolled = true;
                        student.drop(course);
                        System.out.println("Dropped successfully");
                        break;
                    }
                }
            }
        }
        isNotTrue(isEnrolled, "Student is not enrolled in this course");
    }

    /**
     * Function which checks if professor is teaching
     */
    public static void isAlreadyTeaching(String courseId, String memberId) {
        List<Course> gettedCourses = Course.getCourses();
        boolean neverEntered = true;
        for (Course course : gettedCourses) {
            if (course.getCourseId() == Integer.parseInt(courseId)) {
                for (Professor professor : course.getEnrolledProfs()) {
                    if (professor.getMemberId() == Integer.parseInt(memberId)) {
                        dropError("Professor is already teaching this course");
                    }
                }
                neverEntered = false;
                break;
            }
        }
        isNotTrue(!neverEntered, "Wrong inputs");
    }

    /**
     * Function which checks if professor exists
     */
    public static void checkProfId(String memberId) {
        boolean entered = false;
        for (Professor prof : Professor.getAllProfs()) {
            if (prof.getMemberId() == Integer.parseInt(memberId)) {
                entered = true;
                break;
            }
        }
        isNotTrue(entered, "Wrong inputs");
    }

    /**
     * Function which checks maximum enrollment
     */
    public static void maxEnrollemnt(String memberId) {
        for (Student student : Student.getAllStudent()) {
            if (student.getMemberId() == Integer.parseInt(memberId)) {
                if (!student.canBeEnrolled()) {
                    dropError("Maximum enrollment is reached for the student");
                }
            }
        }
    }

    /**
     * Function which checks if the course is full
     */
    public static void courseIsFull(String courseId) {
        List<Course> gettedCourses = Course.getCourses();
        for (Course course : gettedCourses) {
            if (course.getCourseId() == Integer.parseInt(courseId)) {
                if (course.isFull()) {
                    dropError("Course if full");
                }
            }
        }
    }

    /**
     * Function which checks if the level is correct
     */
    public static boolean equalsLevel(String courseLevel) {
        if (!courseLevel.toLowerCase().equals("master") & !courseLevel.toLowerCase().equals("bachelor")) {
            dropError("Wrong inputs");
            return false;
        }
        return true;
    }

    /**
     * Function which checks if course exists
     */
    public static void ifCourseExist(String courseName) {
        List<Course> gettedNames = Course.getCourses();
        for (Course course : gettedNames) {
            if (course.getCourseName().equals(courseName.toLowerCase())) {
                dropError("Course exists");
            }
        }
    }

    /**
     * Function which checks load of the profressor
     */
    public static void checkLoad(String memberId) {
        for (Professor professor : Professor.getAllProfs()) {
            if (professor.getMemberId() == Integer.parseInt(memberId)) {
                if (!professor.canProfBeLoadedMore()) {
                    dropError("Professor's load is complete");
                }
            }
        }
    }

    /**
     * Checking if the next input != empty string
     */
    public static void hasNextLine(Scanner scan) {
        if (!scan.hasNextLine()) {
            dropError("Wrong inputs");
        }
    }

    /**
     * Flag checker for the main function
     */
    public static void isNotTrue(boolean variable, String error) {
        if (!variable) {
            dropError(error);
        }
    }

    /**
     * Checker of the mistakes in the received name
     */
    public static void mistakeName(String name) {
        List<String> subrtring = Arrays.asList("course", "student", "professor", "enroll", "drop", "teach", "exempt");
        if (name.isEmpty()) {
            System.exit(0);
        }
        name = name.toLowerCase();
        if (!name.matches("[a-z]+")) {
            dropError("Wrong inputs");
        }
        for (String strin : subrtring) {
            if (strin.equals(name)) {
                dropError("Wrong inputs");
            }
        }
    }

    /**
     * Checker of the mistakes in received name of course
     */
    public static void mistakeInName(String name) {
        if (name.isEmpty()) {
            System.exit(0);
        }

        name = name.toLowerCase();

        for (String coursePart : name.split("_+")) {
            if (!(coursePart.matches("[a-z]+"))) {
                dropError("Wrong inputs");
            }
        }
        List<String> subrtring = Arrays.asList("course", "student", "professor", "enroll", "drop", "teach", "exempt");
        for (String strin : subrtring) {
            if (strin.equals(name)) {
                dropError("Wrong inputs");
            }
        }

        if (name.contains("__")) {
            dropError("Wrong inputs");
        }
        if (name.charAt(name.length() - 1) == '_' || name.charAt(0) == '_') {
            dropError("Wrong inputs");
        }
    }

    /**
     * Checker of the mistakes in the indexes
     */
    public static void mistakeCatcher(String someId) {
        List<String> subrtring = Arrays.asList("course", "student", "professor", "enroll", "drop", "teach", "exempt");
        if (someId.isEmpty()) {
            System.exit(0);
        }
        for (String strin : subrtring) {
            if (strin.equals(someId)) {
                dropError("Wrong inputs");
            }
        }
        if (!(someId.matches("[0-9]+"))) {
            dropError("Wrong inputs");
        }
    }

    /**
     * Checker of entes: if not, dropping an error
     */
    public static void entered(String memberId) {
        boolean entered = false;
        for (Student student : Student.getAllStudent()) {
            if (student.getMemberId() == Integer.parseInt(memberId)) {
                entered = true;
                break;
            }
        }
        if (!entered) {
            dropError("Wrong inputs");
        }
    }

    /**
     * Function which outputs an error and terminates the program
     */
    public static void dropError(String err) {
        System.out.println(err);
        System.exit(0);
    }

    /**
     * Filling data with starting info
     */
    public static void fillnitialData() {
        Course javaBeginner = new Course("java_beginner", CourseLevel.BACHELOR);
        Course javaIntermediate = new Course("java_intermediate", CourseLevel.BACHELOR);
        Course pythonBasics = new Course("python_basics", CourseLevel.BACHELOR);
        Course algorithms = new Course("algorithms", CourseLevel.MASTER);
        Course advancedProgramming = new Course("advanced_programming", CourseLevel.MASTER);
        Course mathematicalAnalysis = new Course("mathematical_analysis", CourseLevel.MASTER);
        Course computerVision = new Course("computer_vision", CourseLevel.MASTER);

        Student alice = new Student("Alice");
        alice.enroll(javaBeginner);
        alice.enroll(javaIntermediate);
        alice.enroll(pythonBasics);
        Student bob = new Student("Bob");
        bob.enroll(javaBeginner);
        bob.enroll(algorithms);
        Student alex = new Student("Alex");
        alex.enroll(advancedProgramming);

        Professor ali = new Professor("Ali");
        ali.teach(javaBeginner);
        ali.teach(javaIntermediate);

        Professor ahmed = new Professor("Ahmed");
        ahmed.teach(pythonBasics);
        ahmed.teach(advancedProgramming);

        Professor andrey = new Professor("Andrey");
        andrey.teach(mathematicalAnalysis);
    }
}


class Course {
    /**
     * Variables for class Course
     */
    private static final int CAPACITY = 3;
    private static int numberOfCourses = 0;
    private int courseId;
    private String courseName;
    private CourseLevel courseLevel;
    /**
     * Students, enrolled in each course
     */
    private List<Student> enrolledStudents = new ArrayList<>();
    /**
     * Professors, enrolled in each course
     */
    private List<Professor> enrolledProfs = new ArrayList<>();
    /**
     * Array of all courses
     */
    private static final List<Course> ALLCOURSES = new ArrayList<>();

    /**
     * Course constructor
     */
    public Course(String courseName, CourseLevel courseLevel) {
        this.courseName = courseName;
        this.courseLevel = courseLevel;
        numberOfCourses = numberOfCourses + 1;
        this.courseId = numberOfCourses;
        ALLCOURSES.add(this);
    }

    /**
     * Checking if the course is full
     */
    public boolean isFull() {
        return this.enrolledStudents.size() == CAPACITY;
    }

    /**
     * Adding student to the course
     */
    public void addStudent(Student student) {
        this.enrolledStudents.add(student);
    }

    /**
     * Dropping student from the course
     */
    public void removeStudent(Student student) {
        this.enrolledStudents.remove(student);
    }

    /**
     * Adding professor to the course
     */
    public void addProf(Professor professor) {
        this.enrolledProfs.add(professor);
    }

    /**
     * Removing student from the course
     */
    public void removeProf(Professor professor) {
        this.enrolledProfs.remove(professor);
    }

    /**
     * Getter of enrolled students to this course
     */
    public List<Student> getEnrolledStudents() {
        return this.enrolledStudents;
    }

    /**
     * Getter of enrolled professors to this course
     */
    public List<Professor> getEnrolledProfs() {
        return this.enrolledProfs;
    }

    /**
     * Getter of all corses
     */
    public static List<Course> getCourses() {
        return ALLCOURSES;
    }

    /**
     * Getter of course ID
     */
    public int getCourseId() {
        return this.courseId;
    }

    /**
     * Getter of course name
     */
    public String getCourseName() {
        return this.courseName;
    }
}

enum CourseLevel {
    BACHELOR,
    MASTER;
}

class Student extends UniversityMember implements Enrollable {
    /**
     * Variables for Student class
     */
    private static final int MAX_ENROLLMENT = 3;
    /**
     * Array of courses for the student
     */
    private List<Course> enrolledCourses = new ArrayList<>();
    /**
     * Array of all existing students
     */
    private static final List<Student> ALLSTUDENTS = new ArrayList<>();

    /**
     * Constructor of the class Student
     */
    public Student(String memberName) {
        super(memberName);
        ALLSTUDENTS.add(this);
    }

    /**
     * Function to drop
     */
    @Override
    public boolean drop(Course course) {
        this.enrolledCourses.remove(course);
        course.removeStudent(this);
        return true;
    }

    /**
     * Function to enroll
     */
    @Override
    public boolean enroll(Course course) {
        this.enrolledCourses.add(course);
        course.addStudent(this);
        return true;
    }

    /**
     * Function which checks if student can be enrolled
     */
    public boolean canBeEnrolled() {
        return (this.enrolledCourses.size() != MAX_ENROLLMENT);
    }

    /**
     * Function which gets enrolled courses
     */
    public List<Course> getEnrolledCourses() {
        return this.enrolledCourses;
    }

    /**
     * Function which gets all students
     */
    public static List<Student> getAllStudent() {
        return ALLSTUDENTS;
    }
}

interface Enrollable {
    boolean drop(Course course);

    boolean enroll(Course course);
}


abstract class UniversityMember {
    /**
     * Variables for UniversityMember
     */
    private static int numberOfMembers = 0;
    private int memberId;
    private String memberName;

    /**
     * UniversityMember constructor
     */
    public UniversityMember(String memberName) {
        this.memberName = memberName;
        numberOfMembers = numberOfMembers + 1;
        this.memberId = numberOfMembers;
    }

    /**
     * Functions which gets the ID
     */
    public int getMemberId() {
        return this.memberId;
    }

    /**
     * Functions which gets the name
     */
    public String getMemberName() {
        return this.memberName;
    }
}

class Professor extends UniversityMember {
    /**
     * Variables for the Professor class
     */
    private static final int MAX_LOAD = 2;
    /**
     * Array of assigned courses for the professor
     */
    private List<Course> assignedCourses = new ArrayList<>();
    /**
     * Array of all professors
     */
    private static final List<Professor> ALLPROFS = new ArrayList<>();

    /**
     * Professor constructor
     */
    public Professor(String memberName) {
        super(memberName);
        ALLPROFS.add(this);
    }

    /**
     * Function which makes professor teach the course
     */
    public boolean teach(Course course) {
        this.assignedCourses.add(course);
        course.addProf(this);
        return true;
    }

    /**
     * Function which checks if professor can teach the course
     */
    public boolean canProfBeLoadedMore() {
        return this.assignedCourses.size() != MAX_LOAD;
    }

    /**
     * Function which drops professor from course
     */
    public boolean exempt(Course course) {
        this.assignedCourses.remove(course);
        course.removeProf(this);
        return true;
    }

    /**
     * Getter of the assigned courses
     */
    public List<Course> getAssignedCourses() {
        return this.assignedCourses;
    }

    /**
     * Getter of the professors
     */
    public static List<Professor> getAllProfs() {
        return ALLPROFS;
    }
}
