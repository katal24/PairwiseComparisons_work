package main.out.pl.edu.agh.talaga;

import com.github.rcaller.scriptengine.RCallerScriptEngine;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by dawid on 07.11.16.
 */
public class PairwiseComparisons {

    private ScriptEngineManager manager;
    private RCallerScriptEngine engine;
    private boolean keepOpenConnection;
    private boolean engineIsOpen;

    public PairwiseComparisons(){
        createTempFile();
        makeEngine();
        keepOpenConnection = false;
        engineIsOpen = false;
    }

    public PairwiseComparisons(boolean keepOpenConnection){
        makeEngine();
        this.keepOpenConnection = keepOpenConnection;
        engineIsOpen = false;
        if(keepOpenConnection){
            makeCaller();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if(engineIsOpen){
            engine.close();
        }
    }

    private void createTempFile(){
//        InputStream is = getClass().getResourceAsStream("main/pairwiseComparisons.R");
//        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//        try {
//            String line = reader.readLine();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        URL fileResource = getClass().getResource("pairwiseComparisons.R");
//        File tempFile;
//        try {
//            tempFile = File.createTempFile(FilenameUtils.getBaseName(fileResource.getFile()),
//                            FilenameUtils.getExtension(fileResource.getFile()));
//            IOUtils.copy(fileResource.openStream(), FileUtils.openOutputStream(tempFile));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    void makeEngine() {
        manager = new ScriptEngineManager();
    }

    void makeCaller(){
        engine = (RCallerScriptEngine) manager.getEngineByName("RCaller");
        try {
            engine.eval("source(\"pairwiseComparisons.R\")");
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        engineIsOpen = true;
    }

    public void close(){
            engine.close();
            engineIsOpen = false;
    }

    public void open(){
        if(!engineIsOpen) {
            makeCaller();
        }
    }

    private void openConnection(){
        if(!keepOpenConnection && !engineIsOpen){
            makeCaller();
            engineIsOpen = true;
        }
    }

    private void closeConnection(){
        if(!keepOpenConnection && engineIsOpen){
            engine.close();
            engineIsOpen = false;
        }
    }

    public void setKeepOpenConnection(boolean keepOpenConnection){
        if(!this.keepOpenConnection && keepOpenConnection && !engineIsOpen){
            makeCaller();
        }
        this.keepOpenConnection = keepOpenConnection;
    }

    public boolean getKeepOpenConnection(){
        return keepOpenConnection;
    }

    // // VALIDATION

   private void validateDimOfMatrix(double[][] matrix) throws PcMatrixException {
        int dimension1 = matrix.length;

        for(int i=0; i<dimension1; i++){
            int dimension2 = matrix[i].length;
             if(dimension1 != dimension2){
                throw new PcMatrixException("Dimension of matrix is incorrect.");
            }
        }

    }

    private void validateMatrix(double[][] matrix) throws PcMatrixException {
        validateDimOfMatrix(matrix);
        int dimension1 = matrix.length;
        int dimension2 = matrix[0].length;

        for(int i=0; i<dimension1; i++){
            for(int j=0; j<dimension2; j++){
                if(i==j && matrix[i][j]!=1){
                    throw new PcMatrixException("One or more values on diagonal is incorrect");
                }
                if(matrix[i][j] <=0 ){
                    throw new PcMatrixException("One or more values in matrix is not positive.");
                }
            }
        }
    }

    private void validateMatrixAndVector(double[][] matrix, double[] vector) throws PcMatrixException {
        validateMatrix(matrix);
        int matrixDimension = matrix.length;
        int vectorDimension = vector.length;

        if(matrixDimension != vectorDimension){
            throw new PcMatrixException("Dimension of matrix or vector is incorrect.");
        }
    }

    private void validateIntVector(int[] vector) throws PcValueException {
        for(int i=0; i<vector.length; i++){
           if(vector[i]<0){
                throw new PcValueException("One or more values in vector is not positive.");
            }
        }
    }

    private boolean isDuplicate(int[] vector){
        Set<Integer> set = new HashSet<>();
        for(int i=0; i<vector.length; i++){
                set.add(vector[i]);
        }
        if(set.size() != vector.length){
            return true;
        }
        return false;
    }

    private void validateIntVectorToRowDelete(double[][] matrix, int[] vector) throws PcValueException {
        if(isDuplicate(vector)){
            throw new PcValueException("Duplicate in vector.");
        }
        for(int i=0; i<matrix.length; i++){
            if(vector.length > matrix[i].length){
                throw new PcValueException("One or more values in vector is incorrect.");
            }
        }
        for(int i=0; i<vector.length; i++){
            if(vector[i] > matrix.length){
                throw new PcValueException("One or more values in vector is incorrect.");
            }
        }
    }

    private void validateIntVectorToColumnDelete(double[][] matrix, int[] vector) throws PcValueException {
        if(isDuplicate(vector)){
            throw new PcValueException("Duplicate in vector.");
        }
        for(int i=0; i<matrix.length; i++){
            if(vector.length > matrix[i].length){
                throw new PcValueException("One or more values in vector is incorrect.");
            }
        }
        for(int i=0; i<vector.length; i++){
            if(vector[i] > matrix[0].length){
                throw new PcValueException("One or more values in vector is incorrect.");
            }
        }
    }

    private void validateIntVectorToRowAndColumnDelete(double[][] matrix, int[] vector) throws PcValueException {
        if(isDuplicate(vector)){
            throw new PcValueException("Duplicate in vector.");
        }
        for(int i=0; i<matrix.length; i++){
            if(vector.length > matrix[i].length){
                throw new PcValueException("One or more values in vector is incorrect.");
            }
        }
        for(int i=0; i<vector.length; i++){
            if(vector[i] > matrix.length || vector[i] > matrix[0].length){
                throw new PcValueException("One or more values in vector is incorrect.");
            }
        }
    }

    private void validateTwoDoubleVectorToKendall(double[] vectorA, double[] vectorB) throws PcValueException {
        if(vectorA.length != vectorB.length){
            throw new PcValueException("Vectors must be the same size");
        }
        for(int i=0; i<vectorA.length; i++){
            if(!isValueInVector(vectorB, vectorA[i]) || !isValueInVector(vectorA, vectorB[i]) ){
                throw new PcValueException("Vectors must have the same values");
            }
        }
    }

    private void validateDoubleVector(double[] vector) throws PcValueException {
        for(int i=0; i<vector.length; i++){
            if(vector[i]<0){
                throw new PcValueException("One or more values in vector is not positive.");
            }
        }
    }

    private boolean isValueInVector(double[] vector, double value){
        for(int i=0; i<vector.length; i++){
            if(vector[i] == value){
                return true;
            }
        }
        return false;
    }

    private void validateTriad(double[] triad) throws PcValueException {
        if(triad.length != 3){
            throw new PcValueException("Number of triad elements is not equal 3.");
        }
    }

    private void validateInt(int value) throws PcValueException {
            if(value<0){
                throw new PcValueException("Value is not positive.");
            }
    }

    private void validateGetFromMatrix(double[][] matrix, int row, int column) throws PcMatrixException {
        if(row > matrix.length || column > matrix[0].length){
            throw new PcMatrixException("Matrix index out of bounds");
        }
    }

    private void validateCheckSize(int count){
        if(count != 1){
            throw new PcValueException("Vectors must have the same length.");
        }
    }


    //// PAIRWISE COMPARISONS METHODS

    public double principalEigenValue(double[][] matrix){
        openConnection();
        validateMatrix(matrix);
        engine.put("m", matrix);
        try {
            engine.eval("res <- principalEigenValue(m)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double result = ((double[]) engine.get("res"))[0];
        closeConnection();
        return result;
    }


    public double principalEigenValueSym(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        engine.put("m", matrix);
        try {
            //timer: metoda + argumenty
            engine.eval("res <- principalEigenValueSym(m)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double result = ((double[]) engine.get("res"))[0];
        closeConnection();
        return result;
    }


    public double[] principalEigenVector(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        engine.put("m", matrix);
        try {
            engine.eval("res <- principalEigenVector(m)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[] vector = (double[]) engine.get("res");
        closeConnection();
        return vector;
    }


    public double[] principalEigenVectorSym(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        engine.put("m", matrix);
        try {
            engine.eval("res <- principalEigenVectorSym(m)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[] vector = (double[]) engine.get("res");
        closeConnection();
        return vector;
    }


    public double saatyIdx(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        engine.put("m", matrix);
        try {
            engine.eval("res <- saatyIdx(m)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double result = ((double[]) engine.get("res"))[0];
        closeConnection();
        return result;
    }


    public double saatyIdxSym(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        engine.put("m", matrix);
        try {
            engine.eval("res <- saatyIdxSym(m)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double result = ((double[]) engine.get("res"))[0];
        closeConnection();
        return result;
    }


    public double[] eigenValueRank(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        engine.put("m", matrix);
        try {
            engine.eval("res <- eigenValueRank(m)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[] vector = (double[]) engine.get("res");
        closeConnection();
        return vector;
    }


    public double[] eigenValueRankSym(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        engine.put("m", matrix);
        try {
            engine.eval("res <- eigenValueRankSym(m)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[] vector = (double[]) engine.get("res");
        closeConnection();
        return vector;
    }


    public double[] ahp(double[][] mainMatrix, double[][]... matrices){
        validateMatrix(mainMatrix);
        validateDimOfMatrix(mainMatrix);
        Set<Integer> countLenth = new HashSet<>();
        countLenth.add(mainMatrix.length);
        for(double[][] matrix : matrices){
            validateMatrix(matrix);
            validateDimOfMatrix(matrix);
            countLenth.add(matrix.length);
        }
        validateCheckSize(countLenth.size());
        countLenth.clear();
        countLenth.add(mainMatrix.length);
        countLenth.add(matrices.length);
        validateCheckSize(countLenth.size());

        double[][] matrix = rbindMatrices(matrices);
        openConnection();
        engine.put("M", mainMatrix);
        engine.put("matrices", matrix);

        try {
            engine.eval("res <- ahpFromVector(M,matrices)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[] vector = (double[]) engine.get("res");
        closeConnection();
        return vector;
    }


    public double[] geometricRank(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        engine.put("m", matrix);
        try {
            engine.eval("res <- geometricRank(m)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[] vector = (double[]) engine.get("res");
        closeConnection();
        return vector;
    }


    public double[] geometricRescaledRank(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        engine.put("m", matrix);
        try {
            engine.eval("res <- geometricRescaledRank(m)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[] vector = (double[]) engine.get("res");
        closeConnection();
        return vector;
    }


    public double getMatrixEntry(double[][] matrix, int row, int column){
        validateInt(row);
        validateInt(column);
        validateGetFromMatrix(matrix, row, column);
        openConnection();
        engine.put("m", matrix);
        engine.put("r", row);
        engine.put("c", column);
        try {
            engine.eval("res <- getMatrixEntry(m, r, c)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double result = ((double[]) engine.get("res"))[0];
        closeConnection();
        return result;
    }


    public double[][] recreatePCMatrix(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        engine.put("m", matrix);
        try {
            engine.eval("res <- t(recreatePCMatrix(m))");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[][] matrixResult = (double[][]) engine.get("res");
        closeConnection();
        return matrixResult;
    }


    public double[][] deleteRows(double[][] matrix, int[] listOfRows){
        validateMatrix(matrix);
        validateIntVector(listOfRows);
        validateIntVectorToRowDelete(matrix, listOfRows);

        openConnection();
        engine.put("m", matrix);
        engine.put("l", listOfRows);
        try {
            engine.eval("res <- deleteRows(m,l)");
            engine.eval("res1 <- c(deleteRows(m,l))");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[] temp = (double[]) engine.get("res1");
        double[][] m = new double[temp.length/matrix[0].length][matrix[0].length];

        ArrayList<Double> l = new ArrayList<Double>();
        for(int i=0; i< temp.length; i++){
                l.add(temp[i]);
        }

        for(int i=0; i<m[0].length; i++){
            for(int j=0; j<m.length; j++){
                m[j][i] = l.get(0);
                l.remove(0);
            }
        }

        closeConnection();
        return m;
    }


    public double[][] deleteColumns(double[][] matrix, int[] listOfColumns){
        validateMatrix(matrix);
        validateIntVector(listOfColumns);
        validateIntVectorToColumnDelete(matrix, listOfColumns);

        openConnection();
        engine.put("m", matrix);
        engine.put("l", listOfColumns);
        try {
            engine.eval("res <- deleteColumns(m,l)");
            engine.eval("res1 <- c(deleteColumns(m,l))");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[] temp = (double[]) engine.get("res1");
        double[][] m = new double[matrix.length][temp.length/matrix.length];


        ArrayList<Double> l = new ArrayList<Double>();
        for(int i=0; i< temp.length; i++){
            l.add(temp[i]);
        }

        for(int i=0; i<m[0].length; i++){
            for(int j=0; j<m.length; j++){
                m[j][i] = l.get(0);
                l.remove(0);
            }
        }

        closeConnection();
        return m;
    }


    public double[][] deleteRowsAndColumns(double[][] matrix, int[] listOfRowsColumns){
        validateMatrix(matrix);
        validateIntVector(listOfRowsColumns);
        validateIntVectorToRowAndColumnDelete(matrix, listOfRowsColumns);
        openConnection();
        engine.put("m", matrix);
        engine.put("l", listOfRowsColumns);
        try {
            engine.eval("res1 <- c(deleteRowsAndColumns(m,l))");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[] temp = (double[]) engine.get("res1");
        double[][] m = new double[matrix.length - listOfRowsColumns.length][matrix[0].length - listOfRowsColumns.length];


        ArrayList<Double> l = new ArrayList<Double>();
        for(int i=0; i< temp.length; i++){
            l.add(temp[i]);
        }

        for(int i=0; i<m[0].length; i++){
            for(int j=0; j<m.length; j++){
                m[j][i] = l.get(0);
                l.remove(0);
            }
        }
        closeConnection();
        return m;
    }


    public double[][] setDiagonal(double[][] matrix, double valueToSet){
        validateMatrix(matrix);
        openConnection();
        engine.put("m", matrix);
        engine.put("v", valueToSet);
        try {
            engine.eval("res <- t(setDiagonal(m,v))");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[][] matrixResult = (double[][]) engine.get("res");

        closeConnection();
        return matrixResult;
    }


    public double[][] HREmatrix(double[][] matrix, double[] knowVector){
        validateMatrixAndVector(matrix, knowVector);
        openConnection();
        engine.put("m", matrix);
        engine.put("v", knowVector);
        try {
            engine.eval("res <- t(HREmatrix(m,v))");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[][] matrixResult = (double[][]) engine.get("res");

        closeConnection();
        return matrixResult;
    }


    public double[] HREconstantTermVector(double[][] matrix, double[] knowVector){
        validateMatrixAndVector(matrix, knowVector);
        openConnection();
        engine.put("m", matrix);
        engine.put("v", knowVector);
        try {
            engine.eval("res <- HREconstantTermVector(m,v)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[] vector = (double[]) engine.get("res");

        closeConnection();
        return vector;
    }


    public double[] HREpartialRank(double[][] matrix, double[] knowVector){
        validateMatrixAndVector(matrix, knowVector);
        openConnection();
        engine.put("m", matrix);
        engine.put("v", knowVector);
        try {
            engine.eval("res <- HREpartialRank(m,v)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[] vector = (double[]) engine.get("res");

        closeConnection();
        return vector;
    }


    public double[] HREfullRank(double[][] matrix,  double[] knowVector){
        validateMatrixAndVector(matrix, knowVector);
        openConnection();
        engine.put("m", matrix);
        engine.put("v", knowVector);
        try {
            engine.eval("res <- HREfullRank(m,v)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[] vector = (double[]) engine.get("res");
        closeConnection();
        return vector;
    }


    public double[] HRErescaledRank(double[][] matrix,  double[] knowVector){
        validateMatrixAndVector(matrix, knowVector);
        openConnection();
        engine.put("m", matrix);
        engine.put("v", knowVector);
        try {
            engine.eval("res <- HRErescaledRank(m,v)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[] vector = (double[]) engine.get("res");
        closeConnection();
        return vector;
    }


    public double[][] HREgeomMatrix(double[][] matrix,  double[] knowVector){
        validateMatrixAndVector(matrix, knowVector);
        openConnection();
        engine.put("m", matrix);
        engine.put("v", knowVector);
        try {
            engine.eval("res <- t(HREgeomMatrix(m,v))");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[][] matrixResult = (double[][]) engine.get("res");
        closeConnection();
        return matrixResult;
    }


    public double[] HREgeomConstantTermVector(double[][] matrix,  double[] knowVector){
        validateMatrixAndVector(matrix, knowVector);
        openConnection();
        engine.put("m", matrix);
        engine.put("v", knowVector);
        try {
            engine.eval("res <- HREgeomConstantTermVector(m,v)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[] vector = (double[]) engine.get("res");
        closeConnection();
        return vector;
    }


    public double[] HREgeomIntermediateRank(double[][] matrix,  double[] knowVector){
        validateMatrixAndVector(matrix, knowVector);
        openConnection();
        engine.put("m", matrix);
        engine.put("v", knowVector);
        try {
            engine.eval("res <- HREgeomIntermediateRank(m,v)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[] vector = (double[]) engine.get("res");
        closeConnection();
        return vector;
    }


    public double[] HREgeomPartialRank(double[][] matrix,  double[] knowVector){
        validateMatrixAndVector(matrix, knowVector);
        openConnection();
        engine.put("m", matrix);
        engine.put("v", knowVector);
        try {
            engine.eval("res <- HREgeomPartialRank(m,v)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[] vector = (double[]) engine.get("res");
        closeConnection();
        return vector;
    }


    public double[] HREgeomFullRank(double[][] matrix,  double[] knowVector){
        validateMatrixAndVector(matrix, knowVector);
        openConnection();
        engine.put("m", matrix);
        engine.put("v", knowVector);
        try {
            engine.eval("res <- HREgeomFullRank(m,v)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[] vector = (double[]) engine.get("res");
        closeConnection();
        return vector;
    }


    public double[] HREgeomRescaledRank(double[][] matrix,  double[] knowVector){
        validateMatrixAndVector(matrix, knowVector);
        openConnection();
        engine.put("m", matrix);
        engine.put("v", knowVector);
        try {
            engine.eval("res <- HREgeomRescaledRank(m,v)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[] vector = (double[]) engine.get("res");
        closeConnection();
        return vector;
    }


    public double koczkodajTriadIdx(double[] triad){
        validateTriad(triad);
        openConnection();
        engine.put("v", triad);
        try {
            engine.eval("res <- koczkodajTriadIdx(v)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double result = ((double[]) engine.get("res"))[0];
        closeConnection();
        return result;
    }


    public double[] koczkodajTheWorstTriad(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        engine.put("m", matrix);
        try {
            engine.eval("res <- koczkodajTheWorstTriad(m)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[] vector = (double[]) engine.get("res");
        closeConnection();
        return vector;
    }


    public double[][] koczkodajTheWorstTriads(double[][] matrix,  int numberOfTriads){
        validateMatrix(matrix);
        validateInt(numberOfTriads);
        openConnection();
        engine.put("m", matrix);
        engine.put("v", numberOfTriads);
        try {
            engine.eval("res <- koczkodajTheWorstTriads(m,v)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[][] matrixResult = (double[][]) engine.get("res");
        closeConnection();
        return matrixResult;
    }


    public double koczkodajIdx(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        engine.put("m", matrix);
        try {
            engine.eval("res <- koczkodajIdx(m)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double result = ((double[]) engine.get("res"))[0];
        closeConnection();
        return result;
    }


    public double[] koczkodajConsistentTriad(double[] triad){
        validateTriad(triad);
        openConnection();
        engine.put("v", triad);
        try {
            engine.eval("res <- koczkodajConsistentTriad(v)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[] vector = (double[]) engine.get("res");
        closeConnection();
        return vector;
    }


    public double[][] koczkodajImprovedMatrixStep(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        engine.put("m", matrix);
        try {
            engine.eval("res <- t(koczkodajImprovedMatrixStep(m))");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[][] matrixResult = (double[][]) engine.get("res");
        closeConnection();
        return matrixResult;
    }


    public double[][] AIJadd(double[][]... matrices){
        Set<Integer> countLenth = new HashSet<>();
        for(double[][] matrix : matrices){
            validateMatrix(matrix);
            validateDimOfMatrix(matrix);
            countLenth.add(matrix.length);
        }
        validateCheckSize(countLenth.size());

        double[][] bigMatrix = rbindMatrices(matrices);
        openConnection();
        engine.put("m", bigMatrix);
        try {
            engine.eval("res <- t(AIJaddFromVector(m))");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[][] matrixResult = ((double[][]) engine.get("res"));
        closeConnection();
        return matrixResult;
    }


    public double[][] AIJgeom(double[][]... matrices){
        Set<Integer> countLenth = new HashSet<>();
        for(double[][] matrix : matrices){
            validateMatrix(matrix);
            validateDimOfMatrix(matrix);
            countLenth.add(matrix.length);
        }
        validateCheckSize(countLenth.size());

        double[][] bigMatrix = rbindMatrices(matrices);
        openConnection();
        engine.put("m", bigMatrix);
        try {
            engine.eval("res <- t(AIJgeomFromVector(m))");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[][] matrixResult = ((double[][]) engine.get("res"));
        closeConnection();
        return matrixResult;
    }

    public double[][] rbindMatrices(double[][]... matrices){
        double[][] matrix = new double[matrices.length * matrices[0].length][];
        int counter = 0;
        for(double[][] m : matrices){
            for(int row=0; row<m.length; row++){
                matrix[counter] = m[row];
                counter++;
            }
        }
        return matrix;
    }


    public double[] AIJadd(double[]... vectors){
        Set<Integer> countLenth = new HashSet<>();
        for(double[] vector : vectors){
            validateDoubleVector(vector);
            countLenth.add(vector.length);
        }
        validateCheckSize(countLenth.size());

        double[] bigVector = rbindVectors(vectors);
        openConnection();
        engine.put("m", bigVector);
        engine.put("len", vectors[0].length);
        try {
            engine.eval("res <- AIJvectorsAddFromVector(m, len)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[] vector = ((double[]) engine.get("res"));
        closeConnection();
        return vector;
    }


    public double[] AIJgeom(double[]... vectors){
        Set<Integer> countLenth = new HashSet<>();
        for(double[] vector : vectors){
            validateDoubleVector(vector);
            countLenth.add(vector.length);
        }
        validateCheckSize(countLenth.size());


        double[] bigVector = rbindVectors(vectors);
        openConnection();
        engine.put("m", bigVector);
        engine.put("len", vectors[0].length);
        try {
            engine.eval("res <- AIJvectorsGeomFromVector(m, len)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[] vector = ((double[]) engine.get("res"));
        closeConnection();
        return vector;
    }

    public double[] rbindVectors(double[]... vectors){
        double[] vector = new double[vectors.length * vectors[0].length];
        int counter = 0;
        for(double[] v : vectors){
            for(int el=0; el<v.length; el++){
                vector[counter] = v[el];
                counter++;
            }
        }
        return vector;
    }

    public double harkerMatrixPlaceHolderCount(double[][] matrix, int row){
        validateMatrix(matrix);
        validateInt(row);
        openConnection();
        engine.put("m", matrix);
        engine.put("r", row);
        try {
            engine.eval("res <- t(harkerMatrixPlaceHolderCount(m,r))");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double result = ((double[]) engine.get("res"))[0];
        closeConnection();
        return result;
    }


    public double[][] harkerMatrix(double[][] matrix){
        validateMatrix(matrix);
        openConnection();
        engine.put("m", matrix);
        try {
            engine.eval("res <- t(harkerMatrix(m))");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[][] matrixResult = (double[][]) engine.get("res");
        closeConnection();
        return matrixResult;
    }


    public double[][] errorMatrix(double[][] matrix, double[] rankingOfMatrix){
        validateMatrixAndVector(matrix, rankingOfMatrix);
        openConnection();
        engine.put("m", matrix);
        engine.put("r", rankingOfMatrix);
        try {
            engine.eval("res <- t(errorMatrix(m,r))");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[][] matrixResult = (double[][]) engine.get("res");
        closeConnection();
        return matrixResult;
    }


    public double[][] localDiscrepancyMatrix(double[][] matrix, double[] rankingOfMatrix){
        validateMatrixAndVector(matrix, rankingOfMatrix);
        openConnection();
        engine.put("m", matrix);
        engine.put("r", rankingOfMatrix);
        try {
            engine.eval("res <- t(localDiscrepancyMatrix(m,r))");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[][] matrixResult = (double[][]) engine.get("res");
        closeConnection();
        return matrixResult;
    }


    public double globalDiscrepancy(double[][] matrix, double[] rankingOfMatrix){
        validateMatrixAndVector(matrix, rankingOfMatrix);
        openConnection();
        engine.put("m", matrix);
        engine.put("r", rankingOfMatrix);
        try {
            engine.eval("res <- globalDiscrepancy(m,r)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double result = ((double[]) engine.get("res"))[0];
        closeConnection();
        return result;
    }


    private int[][] castToIntMatrix(double[][] doubleMatrix){
        int[][] intMatrix = new int[doubleMatrix.length][doubleMatrix[0].length];
        for(int i=0; i< doubleMatrix.length; i++){
            for(int j=0; j< doubleMatrix[0].length; j++) {
                intMatrix[i][j] = (int) doubleMatrix[i][j];
            }
        }
        return intMatrix;
    }


    public int[][] cop1ViolationList(double[][] matrix, double[] rankingOfMatrix){
        validateMatrixAndVector(matrix, rankingOfMatrix);
        boolean temp = cop1Check(matrix, rankingOfMatrix);
        if(temp){
            return new int[][]{};
        }
        openConnection();
        engine.put("m", matrix);
        engine.put("r", rankingOfMatrix);
        try {
            engine.eval("res <- cop1ViolationList(m,r)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }


        double[][] matrixResult = (double[][]) engine.get("res");
        int[][] intMatrix = castToIntMatrix(matrixResult);
        closeConnection();
        return intMatrix;
    }


    public boolean cop1Check(double[][] matrix, double[] rankingOfMatrix){
        validateMatrixAndVector(matrix, rankingOfMatrix);
        openConnection();
        engine.put("m", matrix);
        engine.put("r", rankingOfMatrix);
        try {
            engine.eval("res <- cop1CheckDetails(m,r)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double result = ((double[]) engine.get("res"))[0];
        boolean resBool = result == 0.0 ? false : true;
        closeConnection();
        return resBool;
    }


    public int[][] cop2ViolationList(double[][] matrix, double[] rankingOfMatrix){
        validateMatrixAndVector(matrix, rankingOfMatrix);
        boolean temp = cop2Check(matrix, rankingOfMatrix);
        if(temp){
            return new int[][]{};
        }
        openConnection();
        engine.put("m", matrix);
        engine.put("r", rankingOfMatrix);
        try {
            engine.eval("res <- cop2ViolationList(m,r)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[][] matrixResult = (double[][]) engine.get("res");
        int[][] intMatrix = castToIntMatrix(matrixResult);
        closeConnection();
        return intMatrix;
    }


    public boolean cop2Check(double[][] matrix, double[] rankingOfMatrix){
        validateMatrixAndVector(matrix, rankingOfMatrix);
        openConnection();
        engine.put("m", matrix);
        engine.put("r", rankingOfMatrix);
        try {
            engine.eval("res <- cop2CheckDetails(m,r)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double result = ((double[]) engine.get("res"))[0];
        boolean resBool = result == 0.0 ? false : true;
        closeConnection();
        return resBool;
    }


    public double kendallTauDistance(double[] vectorA, double[] vectorB){
        validateTwoDoubleVectorToKendall(vectorA, vectorB);
        openConnection();
        engine.put("v1", vectorA);
        engine.put("v2", vectorB);
        try {
            engine.eval("res <- kendallTauDistance(v1,v2)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double result = ((double[]) engine.get("res"))[0];
        closeConnection();
        return result;
    }


    public double normalizedKendallTauDistance(double[] vectorA, double[] vectorB){
        validateTwoDoubleVectorToKendall(vectorA, vectorB);
        openConnection();
        engine.put("v1", vectorA);
        engine.put("v2", vectorB);
        try {
            engine.eval("res <- normalizedKendallTauDistance(v1,v2)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double result = ((double[]) engine.get("res"))[0];
        closeConnection();
        return result;
    }


    public double[][] consistentMatrixFromRank(double[] rankingOfMatrix){
        validateDoubleVector(rankingOfMatrix);
        openConnection();
        engine.put("r", rankingOfMatrix);
        try {
            engine.eval("res <- t(consistentMatrixFromRank(r))");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[][] matrixResult = (double[][]) engine.get("res");
        closeConnection();
        return matrixResult;
    }


    public double[] rankOrder(double[] rankingOfMatrix){
        openConnection();
        engine.put("r", rankingOfMatrix);
        try {
            engine.eval("res <- rankOrder(r)");
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        double[] vector = (double[]) engine.get("res");
        closeConnection();
        return vector;
    }
}