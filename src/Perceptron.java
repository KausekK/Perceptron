public class Perceptron {

    private final double[] weights;
    private double biasWeight;
    private final double learningRate;

    public Perceptron(double learningRate) {
        weights = new double[4];
        initializeWeights();
        this.learningRate = learningRate;
    }


    private void initializeWeights() {
        for (int i = 0; i < weights.length; i++) {
            weights[i] = Math.random();
        }
        biasWeight = Math.random();
    }

    /**
     * funkcja aktywacji
     *
     * @param sum Suma ważona.
     * @return Wynik funkcji aktywacji (0 lub 1).
     */
    private double activationFunction(double sum) {
        return sum >= 0 ? 1 : 0;
    }

    /**
     * Metoda klasyfikująca wejście.
     *
     * @param inputs Wejście do klasyfikacji.
     * @return Wynik klasyfikacji (0 lub 1).
     */
    public double classify(double[] inputs) {
        double sum = biasWeight;
        for (int i = 0; i < inputs.length; i++) {
            sum += weights[i] * inputs[i];
        }
        return activationFunction(sum);
    }

    /**
     * Metoda trenująca perceptron na podstawie wejścia i oczekiwanego wyjścia.
     *
     * @param inputs       Wejście treningowe.
     * @param targetOutput Oczekiwane wyjście.
     */
    public void train(double[] inputs, double targetOutput) {
        double output = classify(inputs);
        double error = targetOutput - output;
        for (int i = 0; i < weights.length; i++) {
            weights[i] += learningRate * error * inputs[i];
        }
        biasWeight += learningRate * error;
    }

}
