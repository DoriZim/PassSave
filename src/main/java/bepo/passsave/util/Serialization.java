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
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class Serialization {
    private static final String strKey = "ztxezilcdg";
    private static final String salt = "wuefiuhiufhsjfd";

    private static final File passSaveFile = new File("application-files/PassSave.ser");
    private static final File authFile = new File("application-files/Auth.ser");
    private static final File categoryFile = new File("application-files/Cat.ser");
    private static final File settingsFile = new File("application-files/Settings.ser");

    public static <T> ObservableList<PassSave> deserialize() throws Exception {
        ArrayList<ArrayList<byte[]>> allEncryptedList;
        ObservableList<PassSave> decryptedList = FXCollections.observableArrayList();

        //When the inputStream is empty it returns an EOF Exception instead of null, that's why this try-block is here
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(passSaveFile))) {
            allEncryptedList = (ArrayList<ArrayList<byte[]>>) in.readObject();
        } catch(Exception e) {
            return decryptedList;
        }

        for (ArrayList<byte[]> byteList : allEncryptedList) {
            String name = decrypt(byteList.get(0));
            String username = decrypt(byteList.get(1));
            String mail = decrypt(byteList.get(2));
            String password = decrypt(byteList.get(3));
            String category = decrypt(byteList.get(4));
            String date = decrypt(byteList.get(5));
            String addInfo = decrypt(byteList.get(6));

            decryptedList.add(new PassSave(name, username, mail, password, category, LocalDate.parse(date), addInfo));
        }
        return decryptedList;
    }

    public static void serialize(ObservableList<PassSave> passSaveList) throws Exception {
        ArrayList<ArrayList<byte[]>> allEncryptedList = new ArrayList<>();

        for (PassSave passSave : passSaveList) {
            ArrayList<byte[]> encryptedList = new ArrayList<>();

            encryptedList.add(encrypt(passSave.getName()));
            encryptedList.add(encrypt(passSave.getUsername()));
            encryptedList.add(encrypt(passSave.getMail()));
            encryptedList.add(encrypt(passSave.getPassword()));
            encryptedList.add(encrypt(passSave.getCategory()));
            encryptedList.add(encrypt(passSave.getDate().toString()));
            encryptedList.add(encrypt(passSave.getAddInfo()));

            allEncryptedList.add(encryptedList);
        }

        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(passSaveFile));
        out.writeObject(allEncryptedList);
        out.close();

        System.out.println("Serialize Complete");
    }

    public static String deserializePin() throws Exception {
        String pin = "";
        byte[] encrypted;

        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(authFile))) {
            encrypted = (byte[]) in.readObject();
        } catch(Exception e) {
            return pin;
        }

        pin = decrypt(encrypted);

        System.out.println("Serialize Complete");

        return pin;
    }

    public static void serializePin(String pin) throws Exception {
        byte [] encrypted = encrypt(pin);

        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(authFile));
        out.writeObject(encrypted);
        out.close();

        System.out.println("Serialize Complete");
    }

    public static ObservableList<Category> deserializeCategories() throws Exception {
        ArrayList<ArrayList<byte[]>> allEncryptedList;
        ObservableList<Category> decryptedList = FXCollections.observableArrayList();

        //When the inputStream is empty it returns an EOF Exception instead of null, that's why this try-block is here
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(categoryFile))) {
            allEncryptedList = (ArrayList<ArrayList<byte[]>>) in.readObject();
        } catch(Exception e) {
            return decryptedList;
        }

        for (ArrayList<byte[]> byteList : allEncryptedList) {
            String name = decrypt(byteList.get(0));
            String description = decrypt(byteList.get(1));
            String color = decrypt(byteList.get(2));

            decryptedList.add(new Category(name, description, Color.valueOf(color)));
        }
        return decryptedList;
    }

    public static void serializeCategories(ObservableList<Category> categoryList) throws Exception {
        ArrayList<ArrayList<byte[]>> allEncryptedList = new ArrayList<>();

        for (Category category : categoryList) {
            ArrayList<byte[]> encryptedList = new ArrayList<>();

            encryptedList.add(encrypt(category.getName()));
            encryptedList.add(encrypt(category.getDescription()));
            encryptedList.add(encrypt(category.getColor().toString()));

            allEncryptedList.add(encryptedList);
        }

        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(categoryFile));
        out.writeObject(allEncryptedList);
        out.close();

        System.out.println("Serialize Complete");
    }

    public static Settings deserializeSettings() throws Exception {
        ArrayList<byte[]> encrypted;
        Settings settings = new Settings(true, true, true, "name");

        //When the inputStream is empty it returns an EOF Exception instead of null, that's why this try-block is here
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(settingsFile))) {
            encrypted = (ArrayList<byte[]>) in.readObject();
        } catch(Exception e) {
            return settings;
        }

        settings.setCategoryClose(Boolean.parseBoolean(decrypt(encrypted.get(0))));
        settings.setPassSaveClose(Boolean.parseBoolean(decrypt(encrypted.get(1))));
        settings.setExportScan(Boolean.parseBoolean(decrypt(encrypted.get(2))));
        settings.setSortProperty(decrypt(encrypted.get(3)));

        return settings;
    }

    public static void serializeSettings(Settings settings) throws Exception {
        ArrayList<byte[]> encrypted = new ArrayList<>();

        encrypted.add(encrypt(String.valueOf(settings.isCategoryClose())));
        encrypted.add(encrypt(String.valueOf(settings.isPassSaveClose())));
        encrypted.add(encrypt(String.valueOf(settings.isExportScan())));
        encrypted.add(encrypt(settings.getSortProperty()));

        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(settingsFile));
        out.writeObject(encrypted);
        out.close();

        System.out.println("Serialize Complete");
    }

    //Uses triple DES encryption to encrypt each String of data
    private static byte[] encrypt(String data) throws Exception {
        final MessageDigest md = MessageDigest.getInstance("md5");
        final byte[] digestOfPassword = md.digest("HG58YZ3CR9".getBytes("utf-8"));
        final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

        for (int j = 0, k = 16; j < 8;) {
            keyBytes[k++] = keyBytes[j++];
        }

        final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
        final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        final byte[] plainTextBytes = data.getBytes("utf-8");
        final byte[] cipherText = cipher.doFinal(plainTextBytes);

        return cipherText;
    }

    //Decrypts Triple DES encryption for each byte[] of data
    private static String decrypt(byte[] data) throws Exception {
        final MessageDigest md = MessageDigest.getInstance("md5");
        final byte[] digestOfPassword = md.digest("HG58YZ3CR9".getBytes("utf-8"));
        final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

        for (int j = 0, k = 16; j < 8;) {
            keyBytes[k++] = keyBytes[j++];
        }

        final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
        final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        final Cipher decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        decipher.init(Cipher.DECRYPT_MODE, key, iv);

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
        (new File("application-files/PassSave.ser")).createNewFile();
        (new File("application-files/Auth.ser")).createNewFile();
        (new File("application-files/Cat.ser")).createNewFile();
        (new File("application-files/Settings.ser")).createNewFile();
    }

    public static void deleteAllFiles() throws IOException {
        Files.deleteIfExists(Path.of("application-files/Settings.ser"));
        Files.deleteIfExists(Path.of("application-files/Cat.ser"));
        Files.deleteIfExists(Path.of("application-files/Auth.ser"));
        Files.deleteIfExists(Path.of("application-files/PassSave.ser"));
        Files.deleteIfExists(Path.of("application-files"));

        Alerts.infoAlert("Deletion successful", "Note that the files itself will be recreated. Only their content remains deleted.");
        createApplicationFiles();
    }
}