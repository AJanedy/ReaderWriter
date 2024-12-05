import java.util.concurrent.Semaphore;
import java.util.Random;

public class ReaderWriter { // ReaderWriter class contains both Reader and Writer threads

    private static int database = 0; // Shared database
    private static int readerCount = 0; // Number of readers
    private static final Semaphore mutex = new Semaphore(1); // Ensures one thread can access readerCount at a time
    private static final Semaphore writePriority = new Semaphore(1); // Ensures exclusion when writer is writing

    static class Reader extends Thread {
        private final char name;
        private final int numAccesses;

        Reader(char name, int numAccesses) {
            this.name = name;
            this.numAccesses = numAccesses;
        }
        @Override
        public void run() {
            try {
                for (int i = 0; i < numAccesses; i++) {
                    acquireSemaphores();
                    retrieveData();
                    releaseSemaphores();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        private static void acquireSemaphores() throws InterruptedException {
            Thread.sleep(new Random().nextInt(1000)); // Simulated processing time
            mutex.acquire(); // Ensures only one thread can update readerCount at a time
            readerCount++;

            if (readerCount == 1) { // If this is the first reader...
                writePriority.acquire(); // Prevent writers from accessing the database
            }
        }
        private void retrieveData() {

            mutex.release(); // Releases lock, allowing other threads to enter this section
            System.out.println("Reader " + name + " retrieved " + database);  // Print which writer retrieved data
        }
        private static void releaseSemaphores() throws InterruptedException {

            mutex.acquire(); // Ensures only one thread can update readerCount at a time
            readerCount--;

            if (readerCount == 0) { // If this is the last reader...
                writePriority.release(); // Allow writers access
            }
            mutex.release(); // Releases lock allowing others access
        }
    }
    static class Writer extends Thread {
        private final char name;
        private final int numAccesses;

        Writer(char name, int numAccesses) {
            this.name = name;
            this.numAccesses = numAccesses;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < numAccesses; i++) {
                    acquireSemaphore();
                    updateDatabase();
                    releaseSemaphore();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private static void acquireSemaphore() throws InterruptedException {
            Thread.sleep(new Random().nextInt(1000)); // Simulate processing time
            writePriority.acquire(); // Block other readers and writers
        }

        private void updateDatabase() {
            database = new Random().nextInt(10); // Add random int <10 to the database
            System.out.println("Writer " + name + " set buffer to " + database);
        }
        private static void releaseSemaphore() {
            writePriority.release();
        }
    }
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java ReaderWriter <# of readers> <# of writers> <# of accesses>");
            return;
        }

        int numReaders = Integer.parseInt(args[0]);
        int numWriters = Integer.parseInt(args[1]);
        int numAccesses = Integer.parseInt(args[2]);

        Thread[] readers = new Thread[numReaders];
        Thread[] writers = new Thread[numWriters];

        // Create readers A, B, C, etc...
        for (int i = 0; i < numReaders; i++) {
            readers[i] = new Reader((char)('A' + i), numAccesses); // Naming readers A, B, C, etc...
        }
        // Create writers F, G, H, etc...
        for (int i = 0; i < numWriters; i++) {
            writers[i] = new Writer((char)('F' + i), numAccesses); // Naming writers F, G, H, etc...
        }
        System.out.println("Reader/Writer is starting...");

        for (Thread reader : readers) {
            reader.start();
        }
        for (Thread writer : writers) {
            writer.start();
        }
    }
}