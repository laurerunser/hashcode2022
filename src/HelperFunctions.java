import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

public class HelperFunctions {

    //split line selon reg
    public static String[] splitString(String reg, String line) {
        return line.split(reg);
    }

    /***
     *
     * LECTURE DE FICHIER
     *
     ***/

    /**
     * Parses the file into a HashMap
     * The lines of the file must be of the format : key value (separated by a space)
     *
     * @param filePath path to the file
     * @return a HashMap containing the contents of the file
     * @throws FileNotFoundException if the file is not found
     */
    public static HashMap<String, String> readFileToHashMap(String filePath) throws FileNotFoundException {
        InputStream ins = new FileInputStream(filePath);
        Scanner obj = new Scanner(ins);
        HashMap<String, String> map = new HashMap<>();
        while (obj.hasNextLine()) {
            String[] str = splitString(" ", obj.nextLine());
            map.put(str[0], str[1]);
        }
        return map;
    }

    /**
     * Parses a file into a String array.
     * The fist line of the file contains an int = the length of the array
     *
     * @param filePath path to the file
     * @return a String array
     * @throws FileNotFoundException if the file is not found
     */
    public static String[] readFileToTab(String filePath) throws FileNotFoundException {
        InputStream ins = new FileInputStream(filePath);
        Scanner obj = new Scanner(ins);
        String[] tab = new String[Integer.parseInt(obj.nextLine())];
        int compt = 0;
        while (obj.hasNextLine()) {
            tab[compt] = obj.nextLine();
            compt = compt + 1;
        }
        return tab;
    }

    /**
     * Parses a file and returns a linked list (each line in a node)
     *
     * @param filePath path to the file
     * @return a Linked list
     * @throws FileNotFoundException if the file is not found
     */
    public static LinkedList<String> readFileToList(String filePath) throws FileNotFoundException {
        InputStream ins = new FileInputStream(filePath);
        Scanner obj = new Scanner(ins);
        LinkedList<String> list = new LinkedList<>();
        while (obj.hasNextLine()) {
            list.add(obj.nextLine());
        }
        return list;
    }


    /***
     *
     * ECRITURE DE FICHIER
     *
     ***/

    public static void writeArrayToFile(String[] tab, String nomDuFichier) throws IOException {
        PrintWriter writer = new PrintWriter(nomDuFichier, StandardCharsets.UTF_8);
        for (String data : tab) {
            writer.println(data);
        }
        writer.close();
    }

    public static void writeListToFile(LinkedList<String> list, String nomDuFichier) throws IOException {
        PrintWriter writer = new PrintWriter(nomDuFichier, StandardCharsets.UTF_8);
        for (String data : list) {
            writer.println(data);
        }
        writer.close();
    }

    public static void writeHashmapToFile(HashMap<String, Integer> map, String nomDuFichier) throws IOException {
        PrintWriter writer = new PrintWriter(nomDuFichier, StandardCharsets.UTF_8);
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            writer.println(entry.getKey() + " : " + entry.getValue());
        }
        writer.close();
    }

}
