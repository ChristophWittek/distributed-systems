public class SimpleThread {

  static class Threads extends Thread {
      private int number;

      public Threads(int number) {
          this.number = number;

      }

      @Override
      public void run() {
          while(number > 0) {
              System.out.println(getName() + ": " + number--);
          }
      }
  }

    public static void main(String[] args) {
        Threads number = new Threads(40);
        Threads number2 = new Threads(5);
        number.start();
        number2.start();
    }


}
