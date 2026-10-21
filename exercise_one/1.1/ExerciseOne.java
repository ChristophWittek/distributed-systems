public class ExerciseOne{
    public static void main(String[] args) {
        (new Thread(new ThreadOne())).start();
        (new Thread(new ThreadTwo())).start();
        (new Thread(new ThreadThree())).start();
    }
}
// Der Output ist nicht immer in selber Reihenfolge.