import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Perceptron perceptron = new Perceptron(0.2);

        System.out.println("Podaj kwiat, który chcesz wyszukać (setosa, versicolor, virginica):");
        String flower = scanner.nextLine();

        List<String> data = loadData();

        double trainPercentage = 0.8;
        int dataSize = data.size();
        int trainSize = (int) (dataSize * trainPercentage);

        Collections.shuffle(data);
        List<String> trainingData = data.subList(0, trainSize);
        List<String> testingData = data.subList(trainSize, dataSize);

        try {
            BufferedWriter br = new BufferedWriter(new FileWriter("src/data/training_data"));
            changeLabelTo1ForSearchingFlower(trainingData, br, flower);
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            BufferedWriter br = new BufferedWriter(new FileWriter("src/data/test_data"));
            changeLabelTo1ForSearchingFlower(testingData, br, flower);
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        trainPerceptron(perceptron, trainingData, flower);

        double accuracy = testPerceptron(perceptron, testingData, flower);
        System.out.println("Dokładność perceptronu na danych testowych: " + accuracy * 100 + "%");

        System.out.println("Podaj wektor testowy (petal length, petal width, sepal length, sepal width) lub wpisz 'koniec' aby zakończyć:");
        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("koniec")) break;
            double[] vector = parseVector(input);
            double result = perceptron.classify(vector);
            System.out.println("Klasyfikacja: " + (result == 1 ? flower : "inne gatunki"));
            break;
        }
    }

    /**
     * Zmienia etykietę na 1, jeśli kwiat jest tym, którego szukamy, w przeciwnym razie na 0.
     *
     * @param testingData Lista danych testowych.
     * @param br           Writer do zapisu zmienionych danych.
     * @param flower      Kwiat, który jest poszukiwany.
     * @throws IOException Wyrzucany w przypadku problemów z zapisem danych.
     */
    private static void changeLabelTo1ForSearchingFlower(List<String> testingData, BufferedWriter br, String flower) throws IOException {
        for (String i : testingData) {
            String[] parts = i.split(";");
            if (parts[parts.length - 1].equals(flower)) {
                parts[parts.length - 1] = "1";
            } else {
                parts[parts.length - 1] = "0";
            }
            String modifiedLine = String.join(";", parts);
            br.write(modifiedLine);
            br.newLine();
        }
    }
    /**
     * Wczytuje dane z pliku i zwraca je jako listę.
     *
     * @return Lista wczytanych danych.
     */
    private static List<String> loadData() {
        List<String> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("src/data/Iris data"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace(",", ".");
                data.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    /**
     * Trenuje perceptron na podstawie danych treningowych.
     *
     * @param perceptron   Perceptron do trenowania.
     * @param trainingData Lista danych treningowych.
     * @param flower       Kwiat, który jest poszukiwany.
     */
    private static void trainPerceptron(Perceptron perceptron, List<String> trainingData, String flower) {
        for (String entry : trainingData) {
            double[] vector = parseVector(entry);
            double targetOutput = extractTargetOutput(entry, flower);
            perceptron.train(vector, targetOutput);
        }
    }

    /**
     * Testuje perceptron na danych testowych i zwraca dokładność klasyfikacji.
     *
     * @param perceptron   Perceptron do testowania.
     * @param testingData  Lista danych testowych.
     * @param flower       Kwiat, który jest poszukiwany.
     * @return Dokładność klasyfikacji perceptronu.
     */
    private static double testPerceptron(Perceptron perceptron, List<String> testingData, String flower) {
        int correct = 0;
        int total = 0;
        for (String entry : testingData) {
            double[] vector = parseVector(entry);
            double targetOutput = extractTargetOutput(entry, flower);
            double result = perceptron.classify(vector);
            if (result == targetOutput) {
                correct++;
            }
            total++;
        }
        return (double) correct / total;
    }

    /**
     * Parsuje dane wejściowe i zwraca wektor cech.
     *
     * @param data Dane wejściowe.
     * @return Wektor cech.
     */
    private static double[] parseVector(String data) {
        String[] parts = data.split(";");
        double[] featureVector = new double[4];
        for (int i = 0; i < parts.length - 1; i++) {
            featureVector[i] = Double.parseDouble(parts[i]);
        }
        return featureVector;
    }

    private static double extractTargetOutput(String data, String flower) {
        String[] parts = data.split(";");
        String label = parts[parts.length - 1].trim();
        return label.equalsIgnoreCase(flower) ? 1 : 0;
    }
}

