import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String folderPath = "src/main/resources/vocabulary";
        File categoriesFolder = new File(folderPath);
        while (true) {
            String category = selectCategory(categoriesFolder);
            if (category.equalsIgnoreCase("exit")) {
                break;
            } else {
                playCategory(category);
            }
        }
        System.out.println("BYE!");
    }

    private static String selectCategory(File f) {
        Scanner keyboard = new Scanner(System.in);

        System.out.println("Select a category: ");

        String[] categories = f.list();
        String[] categoryNames = new String[categories.length];
        int i = 1;
        for (String category : categories) {
            String categoryName = category.split("\\.")[0];
            System.out.println(i + ".- " + categoryName);
            categoryNames[i - 1] = categoryName;
            i++;
        }
        System.out.println(categoryNames.length + 1 + ".- All");
        System.out.println(categoryNames.length + 2 + ".- EXIT");
        String option = "";
        boolean validOption = false;
        while (!validOption) {
            option = keyboard.nextLine();
            for (int j = 1; j <= categories.length + 2; j++) {
                if ((j + "").equalsIgnoreCase(option)) {
                    validOption = true;
                }
            }
            if (!validOption) {
                System.out.println("Please select a valid option [1-" + categories.length + "]: ");
            }
        }

        int index = Integer.parseInt(option) - 1;
        if (index == categories.length) {
            return "all";
        }
        if (index == categories.length + 1) {
            return "exit";
        }
        return categoryNames[index];
    }

    private static void playCategory(String category) {
        Scanner keyboard = new Scanner(System.in);

        System.out.println(category.toUpperCase(Locale.ROOT) + ": ");

        Map<String, String> vocabulary = new HashMap<>();
        fillVocabulary(category, vocabulary);
        int words = vocabulary.size();

        //Separacion para no ver las respuestas mientras se juega
        System.out.println("Press any key to start (" + words + " words) . . .");
        keyboard.nextLine();
        for (int i = 0; i < 50; i++) {
            System.out.println("* * *");
        }

        int mistakes;

        do {
            mistakes = 0;
            List<String> englishWords = new ArrayList<>(vocabulary.keySet());
            Collections.shuffle(englishWords);

            for (String englishWord : englishWords) {
                System.out.print(englishWord + " : ");
                String userGuess = keyboard.nextLine();
                String polishWord = vocabulary.get(englishWord);
                if (userGuess.equalsIgnoreCase(polishWord)) {
                    System.out.println("\033[32mCORRECT\033[0m");
                    vocabulary.remove(englishWord);
                } else {
                    mistakes++;
                    System.out.println("\033[31mWRONG\033[0m (" + polishWord + ")");
                }
            }

            if (mistakes == 0) {
                System.out.println("You have finished succesfully");
            } else if (mistakes == 1){
                System.out.println("You only made a mistake (Out of " + words + " words). Let's try again.");
            } else {
                System.out.println("You made " + mistakes + " mistakes (Out of " + words + " words). Let's try those again.");
            }
        } while (mistakes != 0);

        System.out.println("Press any key to continue . . . ");
        keyboard.nextLine();

    }

    //Si la categoria es all se llena de forma recursiva
    private static Map fillVocabulary(String category, Map vocabulary) {
        Scanner keyboard = new Scanner(System.in);

        String folderPath = "src/main/resources/vocabulary/";
        File folder = new File(folderPath);

        if (category.equalsIgnoreCase("all")) {
            File[] files = folder.listFiles();
                for (File file : files) {
                    String c = file.getName().split("\\.")[0];
                    fillVocabulary(c, vocabulary);
                }
        } else {
            File file = new File(folderPath + category + ".csv");
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        String english = parts[0].trim();
                        String polish = parts[1].trim();
                        vocabulary.put(english, polish);
                        System.out.printf("%-20s -   %-20s \n", english, polish);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
        }

        return vocabulary;
    }
}
