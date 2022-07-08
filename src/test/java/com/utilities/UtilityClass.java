package com.utilities;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.restassured.path.json.JsonPath;

import java.math.BigDecimal;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Random;

public class UtilityClass {

    public static String getAlphaNumericString(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }

    public static int getRandomNumber(Integer limit) {
        Random random = new Random();
        Integer number;
        do {
            number = random.nextInt(limit);
        } while (number == 0);
        return number;
    }

    public static double getRandomDouble(Double upperRange, Double lowerRange) {
        Random myrandom = new Random();
        Double myvalue;
        do {
            myvalue = myrandom.nextDouble() * (upperRange - lowerRange) + lowerRange;
        } while (myvalue <= lowerRange);
        return myvalue;
    }

    public static Long getRandomLong(Long upperRange, Long lowerRange) {
        Long myvalue;

        myvalue = lowerRange + (long) (Math.random() * (upperRange - lowerRange));
        return myvalue;
    }

    public static String getJsonParameterByPath(String JsonString, String path) {
        JsonPath js = new JsonPath(JsonString);
        return (js.get(path)).toString();
    }

    public static Double getJsonParameterByPathDouble(String JsonString, String path) {
        JsonPath js = new JsonPath(JsonString);
        return js.getDouble(path);
    }

    public static String getJsonParameterLargeDecimal(String JsonString, String path) {
        JsonObject jObject = new JsonParser().parse(JsonString).getAsJsonObject();
        if (path.contains(".")) {
            String[] pathArray = path.split("\\.");
            for (int i = 0; i < pathArray.length - 1; i++) {
                jObject = jObject.get(pathArray[i]).getAsJsonObject();
            }
            return jObject.get(pathArray[pathArray.length - 1]).getAsString();
        } else {
            return jObject.get(path).getAsString();
        }
    }

    public static String convertUnitTimeStamp(Long unixTimeStamp) {
        Integer length = unixTimeStamp.toString().length();

        // convert seconds to milliseconds
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDtm = null;

        if (length == 13) {
            formattedDtm = Instant.ofEpochSecond(unixTimeStamp / 1000).atZone(ZoneId.of("GMT-4")).format(formatter);
        } else if (length == 10) {
            formattedDtm = Instant.ofEpochSecond(unixTimeStamp).atZone(ZoneId.of("GMT-4")).format(formatter);
        }
        //System.out.println(formattedDtm);
        return formattedDtm;
    }

    /**
     * This method convert Unix timestamp as desired format. Ue a valud format as
     * the argument value. Such as: "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" or
     * "yyyy-MM-dd"T'HH:mm:ss'Z'" or "yyyy-MM-dd HH:mm:ss
     */
    public static String convertUnixTimeStamptoDesiredFormat(Long unixTimeStamp, String Format) {
        Integer length = unixTimeStamp.toString().length();

        // convert seconds to milliseconds
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Format);
        String formattedDtm = null;

        if (length == 13) {
            formattedDtm = Instant.ofEpochSecond(unixTimeStamp / 1000).atZone(ZoneId.of("GMT-4")).format(formatter);
        } else if (length == 10) {
            formattedDtm = Instant.ofEpochSecond(unixTimeStamp).atZone(ZoneId.of("GMT-4")).format(formatter);
        }
        System.out.println(formattedDtm);
        return formattedDtm;
    }

    public static <T> Boolean compareArrayEqual(T[] array1, T[] array2) {
        Boolean flag = true;

        if (array1.length != array2.length) {
            flag = false;
        } else {
            for (int i = 0; i < array1.length; i++) {
                boolean isDouble = isDouble(array1[i].toString());

                if (isDouble) {
                    if (BigDecimal.valueOf(Double.parseDouble(array1[i].toString())).subtract(BigDecimal.valueOf(Double.parseDouble(array2[i].toString()))).abs().compareTo(BigDecimal.valueOf(0.01d)) > 0) {
                        flag = false;
                        break;
                    }
                } else if (!array1[i].toString().equalsIgnoreCase(array2[i].toString())) {
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }

    public static <T> Boolean compareArrayEqual(T[][] array1, T[][] array2) {
        Boolean flag = true;
        if (array1.length != array2.length) {
            flag = false;
        } else {
            for (int i = 0; i < array1.length; i++) {
                if (array1[i].length != array2[i].length) {
                    flag = false;
                    break;
                } else {
                }
                for (int j = 0; j < array1[i].length; j++) {
                    String first = array1[i][j].toString();
                    String second = array2[i][j].toString();
                    if (!first.equalsIgnoreCase(second)) {
                        flag = false;
                        break;
                    }
                }
            }
        }
        return flag;
    }

    private static boolean isDouble(String value) {
        boolean flag = false;
        try {
            Double.parseDouble(value);
            flag = true;
        } catch (NumberFormatException e) {
            System.out.println(e.getStackTrace());
        }
        return flag;

    }

    public static String encryptXOR(String message, String key) {

        try {
            if (message == null || key == null)
                return null;
            char[] keys = key.toCharArray();
            char[] mesg = message.toCharArray();
            int ml = mesg.length;
            int kl = keys.length;
            char[] newmsg = new char[ml];
            for (int i = 0; i < ml; i++) {
                newmsg[i] = (char) (mesg[i] ^ keys[i % kl]);
            }
            mesg = null;
            keys = null;
            return new String(Base64.getEncoder().encode(new String(newmsg).getBytes()));
            // (new BASE64Encoder ().encode Buffer (new String (newmsg).getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decryptXOR(String message, String key) {
        try {
            if (message == null || key == null)
                return null;
            char[] keys = key.toCharArray();
            char[] mesg = new String(Base64.getDecoder().decode(message)).toCharArray();
            int ml = mesg.length;
            int kl = keys.length;
            char[] newmsg = new char[ml];
            for (int i = 0; i < ml; i++) {
                newmsg[i] = (char) (mesg[i] ^ keys[i % kl]);
            }
            mesg = null;
            keys = null;
            return new String(newmsg);
        } catch (Exception e) {
            return null;
        }
    }

    public static void deleteFilesForPathByPrefix(Path path, String prefix) {

        try (DirectoryStream<Path> newDirectoryStream = Files.newDirectoryStream(path, prefix + "*")) {
            for (final Path newDirectoryStreamItem : newDirectoryStream) {
                Files.delete(newDirectoryStreamItem);
            }
        } catch (final Exception e) { // empty
            System.out.println(e.getMessage());
        }
    }

    public static void deleteFilesForPathByExtension(Path path, String extension) {
        try (DirectoryStream<Path> newDirectoryStream = Files.newDirectoryStream(path, "*." + extension)) {
            for (final Path newDirectoryStreamItem : newDirectoryStream) {
                Files.delete(newDirectoryStreamItem);
            }
        } catch (final Exception e) { // empty
            System.out.println(e.getMessage());
        }
    }
}


