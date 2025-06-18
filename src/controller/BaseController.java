package controller;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public abstract class BaseController {
    protected static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    protected boolean isValidString(String value, int minLength, int maxLength) {
        return value != null && !value.trim().isEmpty() 
            && value.length() >= minLength && value.length() <= maxLength;
    }
    
    protected boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            sdf.setLenient(false);
            Date date = sdf.parse(dateStr);
            return date != null;
        } catch (ParseException e) {
            return false;
        }
    }
    
    protected boolean isValidTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            sdf.setLenient(false);
            Date time = sdf.parse(timeStr);
            return time != null;
        } catch (ParseException e) {
            return false;
        }
    }
    
    protected boolean isValidIPAddress(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return false;
        }
        String[] parts = ipAddress.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        for (String part : parts) {
            try {
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }
    
    protected boolean isValidDuration(String duration) {
        if (duration == null || duration.trim().isEmpty()) {
            return false;
        }
        try {
            int value = Integer.parseInt(duration);
            return value > 0 && value <= 8; // Assuming max duration is 8 hours
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    protected String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        // Remove any potentially harmful characters
        return input.replaceAll("[<>\"'&]", "");
    }
} 