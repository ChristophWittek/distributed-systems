public class ExerciseOneTwo {
    public void main(String[] args) throws InterruptedException {
        // // interference error:
        Counter counter = new Counter();
        System.out.println("=======================================================");
        System.out.println("Test Interference Error unsynchronized!!!!!!!!!!!!!!!!!");
        System.out.println("=======================================================");

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                counter.increment();
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                counter.decrement();
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Expected value: 0 - Acutal value: " + counter.value());


        // same without interference error:
        System.out.println("\n");
        System.out.println("=======================================================");
        System.out.println("Test Interference Error synchronized!!!!!!!!!!!!!!!!!!!");
        System.out.println("=======================================================");
        SynchronizedCounter synched_counter = new SynchronizedCounter();
        Thread t3 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                synched_counter.increment();
            }
        });
        Thread t4 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                synched_counter.decrement();
            }
        });

        t3.start();
        t4.start();
        t3.join();
        t4.join();

        System.out.println("Expected value: 0 - Acutal value: " + synched_counter.value());
    }
}