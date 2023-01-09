package bepo.passsave.util;

import bepo.passsave.model.Category;
import bepo.passsave.model.PassSave;
import bepo.passsave.model.Settings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Serialization {
    //For newEncrypt
    private static final String strKey = "ztxezilcdg";
    private static final String salt = "wuefiuhiufhsjfd";

    //For keygen()
    private static final String cryptKey = "UEZEOEOR75";
    private static String secretKey = "";

    //All relevant paths
    private static final String passSavePath = "application-files/PassSave.ser";
    private static final String authPath = "application-files/Auth.ser";
    private static final String auth2Path = "application-files/Auth2.ser";
    private static final String categoryPath = "application-files/Cat.ser";
    private static final String settingsPath = "application-files/Settings.ser";

    public static void deserializeKey() throws Exception {
        byte[] encrypted = {};

        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(auth2Path))) {
            encrypted = (byte[]) in.readObject();
        } catch(Exception e) {
            System.err.println("Could not read secretKey: " + e);
        }

        secretKey = decrypt(encrypted, cryptKey);

        if(secretKey == null || secretKey.isEmpty()) {
            System.out.println("Entered Keygen because no key could be found");
            Random random = new Random();

            String alphabet = "0123456789QWERTZUIOPASDFGHJKLYXCVBNM";
            String key = "";
            for (int i = 0; i <= 10; i++) {
                key += (alphabet.charAt(random.nextInt(alphabet.length())));
            }

            serializeKey(key);
        }

        System.out.println("Deserializing key complete");
    }

    private static void serializeKey(String key) throws Exception {
        byte [] encrypted = encrypt(key, cryptKey);

        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(auth2Path))) {
            out.writeObject(encrypted);
        }

        System.out.println("Serializing key complete");
    }

    public static Settings deserializeSettings() throws Exception {
        deserializeKey();

        ArrayList<byte[]> encrypted;
        Settings settings = new Settings(true, true, true, "name");

        //When the inputStream is empty it returns an EOF Exception instead of null, that's why this try-block is here
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(settingsPath))) {
            encrypted = (ArrayList<byte[]>) in.readObject();
        } catch(Exception e) {
            return settings;
        }

        settings.setCategoryClose(Boolean.parseBoolean(decrypt(encrypted.get(0), secretKey)));
        settings.setPassSaveClose(Boolean.parseBoolean(decrypt(encrypted.get(1), secretKey)));
        settings.setExportScan(Boolean.parseBoolean(decrypt(encrypted.get(2), secretKey)));
        settings.setSortProperty(decrypt(encrypted.get(3), secretKey));

        return settings;
    }

    public static void serializeSettings(Settings settings) throws Exception {
        deserializeKey();

        ArrayList<byte[]> encrypted = new ArrayList<>();

        encrypted.add(encrypt(String.valueOf(settings.isCategoryClose()), secretKey));
        encrypted.add(encrypt(String.valueOf(settings.isPassSaveClose()), secretKey));
        encrypted.add(encrypt(String.valueOf(settings.isExportScan()), secretKey));
        encrypted.add(encrypt(settings.getSortProperty(), secretKey));

        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(settingsPath))) {
            out.writeObject(encrypted);
        }

        System.out.println("Serialize Complete");
    }

    public static <T> ObservableList<PassSave> deserialize() throws Exception {
        deserializeKey();

        ArrayList<ArrayList<byte[]>> allEncryptedList;
        ObservableList<PassSave> decryptedList = FXCollections.observableArrayList();

        //When the inputStream is empty it returns an EOF Exception instead of null, that's why this try-block is here
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(passSavePath))) {
            allEncryptedList = (ArrayList<ArrayList<byte[]>>) in.readObject();
        } catch(Exception e) {
            return decryptedList;
        }

        for (ArrayList<byte[]> byteList : allEncryptedList) {
            String name = decrypt(byteList.get(0), secretKey);
            String username = decrypt(byteList.get(1), secretKey);
            String mail = decrypt(byteList.get(2), secretKey);
            String password = decrypt(byteList.get(3), secretKey);
            String category = decrypt(byteList.get(4), secretKey);
            String date = decrypt(byteList.get(5), secretKey);
            String addInfo = decrypt(byteList.get(6), secretKey);

            decryptedList.add(new PassSave(name, username, mail, password, category, LocalDate.parse(date), addInfo));
        }
        System.out.println("PassSave: Deserialize Complete");

        return decryptedList;
    }

    public static void serialize(ObservableList<PassSave> passSaveList) throws Exception {
        deserializeKey();

        ArrayList<ArrayList<byte[]>> allEncryptedList = new ArrayList<>();

        for (PassSave passSave : passSaveList) {
            ArrayList<byte[]> encryptedList = new ArrayList<>();

            encryptedList.add(encrypt(passSave.getName(), secretKey));
            encryptedList.add(encrypt(passSave.getUsername(), secretKey));
            encryptedList.add(encrypt(passSave.getMail(), secretKey));
            encryptedList.add(encrypt(passSave.getPassword(), secretKey));
            encryptedList.add(encrypt(passSave.getCategory(), secretKey));
            encryptedList.add(encrypt(passSave.getDate().toString(), secretKey));
            encryptedList.add(encrypt(passSave.getAddInfo(), secretKey));

            allEncryptedList.add(encryptedList);
        }

        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(passSavePath))) {
            out.writeObject(allEncryptedList);
        }

        System.out.println("PassSave: Serialize Complete");
    }

    public static String deserializePin() throws Exception {
        deserializeKey();

        String pin = "";
        byte[] encrypted;

        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(authPath))) {
            encrypted = (byte[]) in.readObject();
        } catch(Exception e) {
            return pin;
        }

        pin = decrypt(encrypted, secretKey);

        System.out.println("Pin: Deserialize Complete");

        return pin;
    }

    public static void serializePin(String pin) throws Exception {
        deserializeKey();

        byte [] encrypted = encrypt(pin, secretKey);

        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(authPath))) {
            out.writeObject(encrypted);
        }

        System.out.println("Pin: Serialize Complete");
    }

    public static ObservableList<Category> deserializeCategories() throws Exception {
        deserializeKey();

        ArrayList<ArrayList<byte[]>> allEncryptedList;
        ObservableList<Category> decryptedList = FXCollections.observableArrayList();

        //When the inputStream is empty it returns an EOF Exception instead of null, that's why this try-block is here
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(categoryPath))) {
            allEncryptedList = (ArrayList<ArrayList<byte[]>>) in.readObject();
        } catch(Exception e) {
            return decryptedList;
        }

        for (ArrayList<byte[]> byteList : allEncryptedList) {
            String name = decrypt(byteList.get(0), secretKey);
            String description = decrypt(byteList.get(1), secretKey);
            String color = decrypt(byteList.get(2), secretKey);

            decryptedList.add(new Category(name, description, Color.valueOf(color)));
        }
        System.out.println("Categories: Deserialize Complete");

        return decryptedList;
    }

    public static void serializeCategories(ObservableList<Category> categoryList) throws Exception {
        deserializeKey();

        ArrayList<ArrayList<byte[]>> allEncryptedList = new ArrayList<>();

        for (Category category : categoryList) {
            ArrayList<byte[]> encryptedList = new ArrayList<>();

            encryptedList.add(encrypt(category.getName(), secretKey));
            encryptedList.add(encrypt(category.getDescription(), secretKey));
            encryptedList.add(encrypt(category.getColor().toString(), secretKey));

            allEncryptedList.add(encryptedList);
        }

        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(categoryPath))) {
            out.writeObject(allEncryptedList);
        }

        System.out.println("Categories: Serialize Complete");
    }

    //Uses triple DES encryption to encrypt each String of data
    private static byte[] encrypt(String data, String key) throws Exception {
        final MessageDigest md = MessageDigest.getInstance("md5");
        final byte[] digestOfPassword = md.digest(key.getBytes("utf-8"));
        final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

        for (int j = 0, k = 16; j < 8;) {
            keyBytes[k++] = keyBytes[j++];
        }

        final SecretKey keyy = new SecretKeySpec(keyBytes, "DESede");
        final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keyy, iv);

        final byte[] plainTextBytes = data.getBytes("utf-8");
        final byte[] cipherText = cipher.doFinal(plainTextBytes);

        return cipherText;
    }

    //Decrypts Triple DES encryption for each byte[] of data
    private static String decrypt(byte[] data, String key) throws Exception {
        final MessageDigest md = MessageDigest.getInstance("md5");
        final byte[] digestOfPassword = md.digest(key.getBytes("utf-8"));
        final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

        for (int j = 0, k = 16; j < 8;) {
            keyBytes[k++] = keyBytes[j++];
        }

        final SecretKey keyy = new SecretKeySpec(keyBytes, "DESede");
        final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        final Cipher decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        decipher.init(Cipher.DECRYPT_MODE, keyy, iv);

        final byte[] plainText = decipher.doFinal(data);

        return new String(plainText, "UTF-8");
    }





    private static byte[] newencrypt(String data) throws Exception {
        final MessageDigest md = MessageDigest.getInstance("md5");
        final byte[] digestOfPassword = md.digest("HG58YZ3CR9".getBytes("utf-8"));
        final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

        for (int j = 0, k = 16; j < 8;) {
            keyBytes[k++] = keyBytes[j++];
        }

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(strKey.toCharArray(), salt.getBytes(), 65536, 256);
        final SecretKey key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        final IvParameterSpec iv = new IvParameterSpec(new byte[16]);
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        final byte[] plainTextBytes = data.getBytes("utf-8");
        final byte[] cipherText = cipher.doFinal(plainTextBytes);

        return cipherText;
    }

    //Decrypts AES encryption for each byte[] of data
    private static String newdecrypt(byte[] data) throws Exception {
        final MessageDigest md = MessageDigest.getInstance("md5");
        final byte[] digestOfPassword = md.digest("HG58YZ3CR9".getBytes("utf-8"));
        final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

        for (int j = 0, k = 16; j < 8;) {
            keyBytes[k++] = keyBytes[j++];
        }

        final SecretKey key = new SecretKeySpec(keyBytes, "DESede");; /*= new AESUtil.generateKey(128);*/
        final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        final Cipher decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        decipher.init(Cipher.DECRYPT_MODE, key, iv);

        final byte[] plainText = decipher.doFinal(data);

        return new String(plainText, "UTF-8");
    }

    public static void createApplicationFiles() throws IOException {
        //Creates all necessary directories/ files
        (new File("application-files")).mkdirs();
        (new File("application-files/Auth2.ser")).createNewFile();
        (new File("application-files/PassSave.ser")).createNewFile();
        (new File("application-files/Auth.ser")).createNewFile();
        (new File("application-files/Cat.ser")).createNewFile();
        (new File("application-files/Settings.ser")).createNewFile();
    }
}