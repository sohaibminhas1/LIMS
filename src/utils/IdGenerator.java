package utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class for generating unique IDs for various entities
 */
public class IdGenerator {
    
    private static final AtomicInteger counter = new AtomicInteger(1);
    private static final Random random = new Random();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    
    /**
     * Generate unique computer ID
     * @return Unique computer ID in format PC-YYYYMMDD-XXX
     */
    public static String generateComputerId() {
        String date = dateFormat.format(new Date());
        int sequence = counter.getAndIncrement();
        return String.format("PC-%s-%03d", date, sequence % 1000);
    }
    
    /**
     * Generate unique complaint ID
     * @return Unique complaint ID in format COMP-YYYYMMDD-XXX
     */
    public static String generateComplaintId() {
        String date = dateFormat.format(new Date());
        int sequence = counter.getAndIncrement();
        return String.format("COMP-%s-%03d", date, sequence % 1000);
    }
    
    /**
     * Generate unique software request ID
     * @return Unique software request ID in format SW-YYYYMMDD-XXX
     */
    public static String generateSoftwareRequestId() {
        String date = dateFormat.format(new Date());
        int sequence = counter.getAndIncrement();
        return String.format("SW-%s-%03d", date, sequence % 1000);
    }
    
    /**
     * Generate unique feedback ID
     * @return Unique feedback ID in format FB-YYYYMMDD-XXX
     */
    public static String generateFeedbackId() {
        String date = dateFormat.format(new Date());
        int sequence = counter.getAndIncrement();
        return String.format("FB-%s-%03d", date, sequence % 1000);
    }
    
    /**
     * Generate unique lab reservation ID
     * @return Unique lab reservation ID in format LAB-YYYYMMDD-XXX
     */
    public static String generateLabReservationId() {
        String date = dateFormat.format(new Date());
        int sequence = counter.getAndIncrement();
        return String.format("LAB-%s-%03d", date, sequence % 1000);
    }
    
    /**
     * Generate random test ID for testing purposes
     * @param prefix Prefix for the ID
     * @return Random test ID
     */
    public static String generateTestId(String prefix) {
        int randomNum = random.nextInt(9999) + 1;
        return String.format("%s-TEST-%04d", prefix, randomNum);
    }
    
    /**
     * Generate unique ID with custom prefix
     * @param prefix Custom prefix
     * @return Unique ID with custom prefix
     */
    public static String generateCustomId(String prefix) {
        String date = dateFormat.format(new Date());
        int sequence = counter.getAndIncrement();
        return String.format("%s-%s-%03d", prefix, date, sequence % 1000);
    }
    
    /**
     * Generate simple sequential ID
     * @return Sequential integer ID
     */
    public static int generateSequentialId() {
        return counter.getAndIncrement();
    }
    
    /**
     * Reset counter (for testing purposes)
     */
    public static void resetCounter() {
        counter.set(1);
    }
    
    /**
     * Get current counter value
     * @return Current counter value
     */
    public static int getCurrentCounter() {
        return counter.get();
    }
}
