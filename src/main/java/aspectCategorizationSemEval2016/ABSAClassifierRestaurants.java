package aspectCategorizationSemEval2016;

import de.bwaldvogel.liblinear.*;
import de.bwaldvogel.liblinear.LinearCopy;
import org.apache.commons.cli.BasicParser;

import java.io.*;
import java.util.*;


class ABSAClassifierHelperR {
    List<LinkedHashMap<Integer, Double>> featureList = new ArrayList<LinkedHashMap<Integer, Double>>();

    ABSAClassifierHelperR(String dataset) throws IOException {
        BufferedReader readerTrain = new BufferedReader(new FileReader(new File(dataset)));

        int count = 0;
        while (readerTrain.readLine() != null) {
            count++;
        }

        for (int i = 0; i < count; i++) {
            featureList.add(i, new LinkedHashMap<Integer, Double>());
        }

        System.out.println(count);
    }

    public void setHashMap(int start, List<LinkedHashMap<Integer, Double>> hMap) {
        for (int i = 0; i < hMap.size(); i++) {
            for (Map.Entry<Integer, Double> entry : hMap.get(i).entrySet()) {
                featureList.get(i).put(start + entry.getKey(), entry.getValue());
            }
        }
    }

    public List<LinkedHashMap<Integer, Double>> getList() {
        return featureList;
    }
}

public class ABSAClassifierRestaurants {
    static String rootDirectory;
    static List<LinkedHashMap<Integer, Double>> trainingFeature;
    static List<LinkedHashMap<Integer, Double>> testFeature;


    ABSAClassifierRestaurants(int option, String trainFile, String testFile, String ddgFile) throws IOException {

        rootDirectory = System.getProperty("user.dir");
        mainClassifierFunction(option, trainFile, testFile, ddgFile);

        //rootDirectory = "D:\\Course\\Semester VII\\Internship\\sentiment\\indian";
        //return generateFeature();
    }

    private int generateFeature(int option, String trainFile, String testFile, String ddgFile) throws IOException {

        //generateDataset(rootDirectory + "\\dataset\\dataset_sentimentClassification\\"+trainFile, rootDirectory + "\\dataset\\dataset_sentimentClassification\\Train_Restaurants_Contextual_Cleansed.txt");


        //TRAINING SET
        ABSAClassifierHelperR trainingObject = new ABSAClassifierHelperR(rootDirectory + "\\dataset\\dataset_aspectCategorization\\" + trainFile);

        //TESTING SET
        ABSAClassifierHelperR testObject = null;
        if (option == 2 || option == 3) {
            //generateDataset(rootDirectory + "\\dataset\\dataset_sentimentClassification\\"+testFile, rootDirectory + "\\dataset\\dataset_sentimentClassification\\Test_Restaurants_Contextual_Cleansed.txt");
            testObject = new ABSAClassifierHelperR(rootDirectory + "\\dataset\\dataset_aspectCategorization\\" + testFile);
        }
        //TRAINING SET
        /*ABSAABSAClassifierHelperRR trainingObject = new ABSAABSAClassifierHelperRR(rootDirectory + "\\dataset\\dataset_aspectCategorization\\Restaurants_Train_ABSA.txt");


        //TESTING SET
        ABSAABSAClassifierHelperRR testObject = new ABSAABSAClassifierHelperRR(rootDirectory + "\\dataset\\dataset_aspectCategorization\\Restaurants_Test_ABSA.txt");
*/
        //POS FEATURE
        int start = 0;
        //ABSAPOS posObject = new ABSAPOS(rootDirectory, rootDirectory + "\\dataset\\dataset_aspectCategorization\\"+trainFile, rootDirectory + "\\dataset\\dataset_aspectCategorization\\"+testFile);
        //trainingObject.setHashMap(start, posObject.getTrainingList());

        //testObject.setHashMap(start, posObject.getTestList());
        //System.out.println(trainingFeature.get(10).size());

        //BAG OF WORDS FEATURE
        ABSABagOfWords bagObject = new ABSABagOfWords(rootDirectory);
        trainingObject.setHashMap(start, bagObject.getTrainingList());
        if (option == 2 || option == 3) {
            testObject.setHashMap(start, bagObject.getTestList());
        }

        start += bagObject.getFeatureCount();
        TfIdfFeature tfObject = new TfIdfFeature(rootDirectory, ddgFile);
        trainingObject.setHashMap(start, tfObject.getTrainingList());
        if (option == 2 || option == 3) {
            testObject.setHashMap(start, tfObject.getTestList());
        }

        /*start += tfObject.getFeatureCount();
        DDGFeature ddgObject = new DDGFeature(rootDirectory, trainFile, testFile, ddgFile);
        trainingObject.setHashMap(start, ddgObject.getTrainingList());
        if (option == 2 || option == 3) {
            testObject.setHashMap(start, ddgObject.getTestList());
        }

        start += tfObject.getFeatureCount();
        TfIdfDTFeature tfDTObject = new TfIdfDTFeature(rootDirectory, ddgFile);
        trainingObject.setHashMap(start, tfDTObject.getTrainingList());
        if (option == 2 || option == 3) {
            testObject.setHashMap(start, tfDTObject.getTestList());
        }*/


        trainingFeature = trainingObject.getList();
        if (option == 2 || option == 3) {
            testFeature = testObject.getList();
        }

        int finalSize = start + tfObject.getFeatureCount();

        return finalSize;
    }


    private void mainClassifierFunction(int option, String trainFile, String testFile, String ddgFile) throws IOException {
        //SentimentClassifierHindi this = new SentimentClassifierHindi();
        //int finalSize = this.SentimentClassifierHindi();
        int finalSize = this.generateFeature(option, trainFile, testFile, ddgFile);
        System.out.println("Hello aspectCategorizationSemEval2016!");

        // Create features
        Problem problem = new Problem();

        // Save X to problem
        double a[] = new double[this.trainingFeature.size()];
        File file = new File(rootDirectory + "\\dataset\\trainingLabels.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String read;
        int count = 0;
        while ((read = reader.readLine()) != null) {
            //System.out.println(read);
            a[count++] = Double.parseDouble(read.toString());
        }

        //Feature[][] f = new Feature[][]{ {}, {}, {}, {}, {}, {} };

        //trainingFeature = trainingObject.getList();
        Feature[][] trainFeatureVector = new Feature[trainingFeature.size()][finalSize];

        System.out.println("Training Instances: " + trainingFeature.size());
        System.out.println("Feature Length: " + finalSize);

        for (int i = 0; i < trainingFeature.size(); i++) {
            //System.out.println();
            //System.out.println(trainingFeature.get(i));
            System.out.println(i + " trained.");
            for (int j = 0; j < finalSize; j++) {
                //System.out.print(trainingFeature.get(i).get(j + 1)+" ");
                //trainingFeature.get(i).
                if (trainingFeature.get(i).containsKey(j + 1)) {
                    //System.out.print(j + 1 + ", ");
                    trainFeatureVector[i][j] = new FeatureNode(j + 1, trainingFeature.get(i).get(j + 1));
                } else {
                    trainFeatureVector[i][j] = new FeatureNode(j + 1, 0.0);
                }
            }
            //System.out.println();
        }

        problem.l = trainingFeature.size(); // number of training examples
        problem.n = finalSize; // number of features
        problem.x = trainFeatureVector; // feature nodes
        problem.y = a; // target values ----

        BasicParser bp = new BasicParser();

        SolverType solver = SolverType.L2R_LR; // -s 7
        double C = 0.75;    // cost of constraints violation
        double eps = 0.0001; // stopping criteria

        Parameter parameter = new Parameter(solver, C, eps);
        Model model = Linear.train(problem, parameter);
        File modelFile = new File("model");
        model.save(modelFile);

        //PrintWriter write = new PrintWriter(new BufferedWriter(new FileWriter(rootDirectory + "\\dataset\\predictedLabels.txt")));
        PrintWriter write = new PrintWriter(new BufferedWriter(new FileWriter(rootDirectory + "\\dataset\\dataset_aspectCategorization\\predictedRestaurantsLabels.txt")));

        if (option == 1) {
            BufferedReader trainReader = new BufferedReader(new FileReader(new File(rootDirectory + "\\dataset\\dataset_aspectCategorization\\" + trainFile)));
            HashMap<String, Integer> id = new HashMap<String, Integer>();
            HashMap<String, String> review = new HashMap<String, String>();
            double[] val = new double[trainingFeature.size()];
            double[] tempVal = new double[trainingFeature.size()];
            LinearCopy.crossValidation(problem, parameter, 5, val, tempVal);

            model = Model.load(modelFile);
            int labelMap[] = new int[13];
            labelMap = model.getLabels();

            for (int i = 0; i < trainingFeature.size(); i++) {
                int flag = 0;
                String tokens[] = trainReader.readLine().split("\\|");
                if (id.containsKey(tokens[1]) == true || tokens[2].compareToIgnoreCase("True") == 0) {
                } else {
                    //System.out.println(tokens[1]);
                    /*int max = -1;
                    double probMax = -1.0;
                    for(int j=0; j<13; j++){
                        if(probMax<val[i][j]){
                            probMax = val[i][j];
                            max = j;
                        }
                    }*/
                    //System.out.println(tempVal[i]);
                    write.println((int) (val[i]));
                    write.println("next");
                    id.put(tokens[1], 1);
                    System.out.println(tokens[1] + "\t" + (int) (val[i]));
                    if (review.containsKey(tokens[1])) {
                        System.out.println(tokens[3]);
                        System.out.println(review.get(tokens[1]));
                    } else {
                        review.put(tokens[1], tokens[3]);
                    }
                }/*else{
                    int max = -1;
                    double maxProb = -1;
                    for (int j = 0; j < 13; j++) {
                        if(maxProb < tempVal[i][j]){
                            maxProb = tempVal[i][j];
                            max = j;
                        }
                        if (tempVal[i][j] >= 0.185) {
                            flag = 1;
                            //System.out.println("i");
                            write.println(labelMap[j]);
                        }
                    }
                    System.out.println("Max Index: "+max+", Actual Label: "+labelMap[max]);
                    if (flag == 1) {
                        write.println("next");
                    } else {
                        write.println("-1");
                        write.println("next");
                    }
                    //write.println(prediction);
                    id.put(tokens[1], 1);
                    //System.out.println();
                }*/
            }
            write.close();
            return;
        }

        if (option == 3) {
            BufferedReader testReader = new BufferedReader(new FileReader(new File(rootDirectory + "\\dataset\\dataset_aspectCategorization\\" + testFile)));
            HashMap<String, Integer> id = new HashMap<String, Integer>();
            model = Model.load(modelFile);
            for (int i = 0; i < testFeature.size(); i++) {
                //System.out.println(i+", "+testFeature.size()+", "+testFeature.get(i).size());
                Feature[] instance = new Feature[testFeature.get(i).size()];
                int j = 0;
                for (Map.Entry<Integer, Double> entry : testFeature.get(i).entrySet()) {
                    //System.out.print(entry.getKey() + ": " + entry.getValue() + ";   ");
                    //listOfMaps.get(i).put(start + entry.getKey(), entry.getValue());
                    // do stuff
                    instance[j++] = new FeatureNode(entry.getKey(), entry.getValue());
                }

                double d = LinearCopy.predict(model, instance);

                double[] predict = new double[13];
                double prediction = LinearCopy.predictProbability(model, instance, predict);

                int labelMap[] = new int[13];
                labelMap = model.getLabels();

                //System.out.println(prediction);
                //Arrays.sort(predict, Collections.reverseOrder());
                //System.out.println();
                //double prediction = LinearCopy.predict(model, instance);
                String tokens[] = testReader.readLine().split("\\|");

                if (id.containsKey(tokens[1]) == true || tokens[2].compareToIgnoreCase("True") == 0) {
                } else if (tokens[3].compareToIgnoreCase("abc") == 0) {
                    //System.out.println(tokens[1]);
                    write.println("-1");
                    write.println("next");
                    id.put(tokens[1], 1);
                } else {
                    int flag = 0;
                    for (int p = 0; p < 13; p++) {
                        if (predict[p] >= 0.185) {
                            flag = 1;
                            //System.out.println("Main Class: "+labelMap[p]);
                            write.println(labelMap[p]);
                        }
                    }
                    if (flag == 1) {
                        write.println("next");
                    } else {
                        write.println("-1");
                        write.println("next");
                    }

                    /*write.println((int)d);
                    write.println("next");*/

                    /*write.println(prediction);
                    write.println("next");*/
                    id.put(tokens[1], 1);
                }
            }

            write.close();
        }
    }

    private static LinkedHashMap sortHashMapByValuesD(HashMap passedMap) {
        List mapKeys = new ArrayList(passedMap.keySet());
        List mapValues = new ArrayList(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap sortedMap =
                new LinkedHashMap();

        Iterator valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Object val = valueIt.next();
            Iterator keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                Object key = keyIt.next();
                String comp1 = passedMap.get(key).toString();
                String comp2 = val.toString();

                if (comp1.equals(comp2)) {
                    passedMap.remove(key);
                    mapKeys.remove(key);
                    sortedMap.put(key, val);
                    break;
                }

            }

        }
        return sortedMap;
    }
}