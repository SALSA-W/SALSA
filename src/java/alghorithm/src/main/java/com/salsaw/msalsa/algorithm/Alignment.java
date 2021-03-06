/**
 * Copyright 2015 Alessandro Daniele, Fabio Cesarato, Andrea Giraldin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.salsaw.msalsa.algorithm;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.salsaw.msalsa.algorithm.enums.EmbeddedScoringMatrix;
import com.salsaw.msalsa.algorithm.enums.MatrixSerie;
import com.salsaw.msalsa.algorithm.enums.TerminalGAPsStrategy;
import com.salsaw.msalsa.algorithm.exceptions.SALSAException;

/**
 * 
 * class that represents the alignment.
 * <p>
 * Theoretical notes:
 * <ul>
 * <li>Provides methods to move the GAP.</li>
 * <li>It does not keep inside the scores of the individual columns
 * (X<small><sub>k</sub></small>) and the value of the objective function, thus
 * avoiding the calculations to keep these updated variables. In fact, for the
 * purposes of the operation of the algorithm, it is not necessary to know at
 * each iteration the value of the WSP-Score, but only calculate the
 * improvements obtained by moving the GAP (the δ).</li>
 * <li>Another optimization is the implementation of a preprocessor that
 * converts the characters of the alignment to integers values. There is a
 * bijective correspondence between the characters and the integers generated.
 * <p>
 * The pre-processing is used only for efficiency purposes: the integers
 * correspond to correct indices in the matrix of class SubstitutionMatrix. In
 * this manner, when asked the score of the pair (α, β), the method of
 * SubstitutionMatrix score should not seek the position in memory, but simply
 * make a direct access to the position [α; β] of matrix.
 * </p>
 * <p>
 * The save method is the inverse operation of the pre-processing, bringing the
 * characters to their classical representation.
 * </p>
 * </li>
 * </ul>
 * </p>
 * 
 * @author Alessandro Daniele, Fabio Cesarato, Andrea Giraldin
 *
 */
public final class Alignment {
	// FIELDS
	private int numberOfSequences;
	private int length;
	/**
	 * The alignment
	 */
	private final int[] alignMatrix;
	private final Alphabet alphabet;
	private SubstitutionMatrix substitution;
	/**
	 * Sequences name and properties (found in FASTA files)
	 */
	private String[] properties;
	private double[] weights;
	private double weightsSUM;
	private final ArrayList<GAP> GAPS;

	private double[] countersMatrix;

	/**
	 * GAP opening penalty
	 */
	private final double GOP;

	private final TerminalGAPsStrategy terminal;

	// CONSTRUCTORS
	
	public Alignment(final String inputFilePath, final String treeFileName,
			final SubstitutionMatrix s, final double gop, final TerminalGAPsStrategy tgs)
			throws IOException, SALSAException {
		this(inputFilePath, treeFileName, s, gop, tgs, EmbeddedScoringMatrix.NONE, MatrixSerie.NONE, null);
	}
	
	public Alignment(final String inputFilePath, final String treeFileName,
			final MatrixSerie matrixSerie, final double gep, 
			final double gop, final TerminalGAPsStrategy tgs)
			throws IOException, SALSAException {		
		this(inputFilePath, treeFileName, null, gop, tgs, EmbeddedScoringMatrix.NONE , matrixSerie, gep);			
	}
	
	public Alignment(final String inputFilePath, final String treeFileName,
			final EmbeddedScoringMatrix scoringMatrix, final double gep, 
			final double gop, final TerminalGAPsStrategy tgs)
			throws IOException, SALSAException {		
		this(inputFilePath, treeFileName, null, gop, tgs, scoringMatrix, MatrixSerie.NONE, gep);			
	}	
	
	private Alignment(final String inputFilePath, final String treeFileName,
			final SubstitutionMatrix substitutionMatrix, final double gop, 
			final TerminalGAPsStrategy tgs, final EmbeddedScoringMatrix scoringMatrix, final MatrixSerie matrixSerie, final Double gep)
			throws IOException, SALSAException {		
		this.GOP = gop;
		this.terminal = tgs;
		ArrayList<String> sequences = readInputSequences(inputFilePath);

		if (substitutionMatrix == null) {
			// Create SubstitutionMatrix from input data
			if (matrixSerie == MatrixSerie.NONE && scoringMatrix == EmbeddedScoringMatrix.NONE) {
				throw new SALSAException("Missing data to generate scoringMatrix");
			}
			if (matrixSerie != MatrixSerie.NONE && scoringMatrix != EmbeddedScoringMatrix.NONE) {
				throw new SALSAException("Invalid input data for generate scoringMatrix");
			}
			if (matrixSerie != MatrixSerie.NONE) {				
				double pid = getAverageIdentityScore();
				this.substitution = SubstitutionMatrix
						.getSubstitutionMatrix(SubstitutionMatrix.getEmbeddedSubstitutionMatrix(matrixSerie, pid), gep);
			} else {
				this.substitution = SubstitutionMatrix.getSubstitutionMatrix(scoringMatrix, gep);
			}
		} else {
			this.substitution = substitutionMatrix;
		}
		this.alphabet = this.substitution.getAlphabet();
		this.alignMatrix = new int[this.numberOfSequences * this.length];
		this.GAPS = new ArrayList<>();

		createWeights(treeFileName);
		preprocessing(sequences);
		createCounters();
	}

	// GET / SET
	public final int getNumberOfSequences() {
		return this.numberOfSequences;
	}

	public final int getLength() {
		return this.length;
	}

	public final ArrayList<GAP> getGAPS() {
		return this.GAPS;
	}

	public final TerminalGAPsStrategy getTerminalGAPStrategy() {
		return this.terminal;
	}

	// METHODS	
	/**
	 * It calculates the identity score of two sequences (the percentage of identical residues found in the pairwise alignment)
	 * 
	 * @param firstRow
	 * @param secondRow
	 * @return
	 * @throws SALSAException
	 */
	private double getIdentityScore(final int firstRow, final int secondRow) throws SALSAException{
		if (firstRow < 0 || 
			firstRow >= numberOfSequences || 
			secondRow < 0 || 
			secondRow >= numberOfSequences){
			throw new SALSAException("Error: the two rows are not inside the correct range");		
		}

		int count = 0;
		int lengthFirstRow = 0;
		int lengthSecondRow = 0;

		for (int column = 0; column < length; column++){
			if (this.alignMatrix[firstRow * this.length + column] != this.alphabet.INDEL()) {
				lengthFirstRow++;
			}
			if (this.alignMatrix[firstRow * this.length + column] != this.alphabet.INDEL()) {
				lengthSecondRow++;
			}
			if (this.alignMatrix[firstRow * this.length + column] != this.alphabet.INDEL() && 
					this.alignMatrix[firstRow * this.length + column] == this.alignMatrix[secondRow * this.length + column]) {
				count++;
			}
		}

		if (lengthFirstRow < lengthSecondRow) {
			return count / ((float) lengthFirstRow);
		}
		else {
			return count / ((float) lengthSecondRow);
		}
	}
	
	/**
	 * It calculates the mean of the identity scores of all couples of sequences in the alignment
	 * 
	 * @return
	 * @throws SALSAException 
	 */
	private double getAverageIdentityScore() throws SALSAException{
		double sum = 0.0f;

		for (int i = 0; i < numberOfSequences - 1; i++){
			for (int j = i + 1; j < numberOfSequences; j++){
				sum += getIdentityScore(i, j);
			}
		}

		return 2 * sum / ((float) numberOfSequences * (numberOfSequences - 1));
	}
	
	/**
	 * It calculate the distance between two sequences
	 * 
	 * @param firstRow
	 * @param secondRow
	 * @return
	 * @throws SALSAException 
	 */
	protected final double getPairwiseDistance(int firstRow, int secondRow) throws SALSAException{
		return (1.0f - getIdentityScore(firstRow, secondRow)) * 100;		
	}
	
	/**
	 * Calculate the WSP-score (in the classic way, without using the counters)
	 * 
	 * @return
	 */
	public final double WSP() {
		double objval = 0.0f;
		// Int array already initialize at 0
		int[] numberOfGAPS = new int[numberOfSequences];
		GAP g;

		for (int i = 0; i < this.GAPS.size(); i++) {
			g = this.GAPS.get(i);

			if (this.terminal == TerminalGAPsStrategy.BOTH_PENALTIES
					|| !g.terminalGAP()) {
				numberOfGAPS[g.getRow()]++;
			}
		}

		for (int r1 = 0; r1 < this.numberOfSequences - 1; r1++) {
			for (int r2 = r1 + 1; r2 < this.numberOfSequences; r2++) {
				objval += this.weights[r1] * this.weights[r2]
						* pairwise(r1, r2, numberOfGAPS[r1], numberOfGAPS[r2]);
			}
		}

		return objval;
	}

	/**
	 * They move a GAP of one position and return the improvement in the
	 * WSP-Score due to this change
	 * 
	 * @param g
	 * @return
	 * @throws SALSAException
	 */
	public final double moveLeft(GAP g) throws SALSAException {
		int leftColumn = g.getBegin() - 1;
		int rightColumn = g.getEnd();
		int row = g.getRow();

		double delta = changeCell(row, rightColumn, this.alignMatrix[row
				* this.length + leftColumn]);
		delta += changeCell(row, leftColumn, this.alphabet.INDEL());

		g.moveLeft();
		return delta;
	}

	public final double moveRight(GAP g) throws SALSAException {
		int leftColumn = g.getBegin();
		int rightColumn = g.getEnd() + 1;
		int row = g.getRow();

		double delta = changeCell(row, leftColumn, this.alignMatrix[row
				* this.length + rightColumn]);
		delta += changeCell(row, rightColumn, this.alphabet.INDEL());

		g.moveRight();
		return delta;
	}

	/**
	 * Same as moveLeft and moveRight, but the improvement is not returned (not
	 * even calculated). Used to do the backtracking and restore the cell as
	 * before the movement of the GAP g.
	 * 
	 * @param g
	 * @throws SALSAException
	 */
	public final void goBackToLeft(GAP g) throws SALSAException {
		int leftColumn = g.getBegin() - 1;
		int rightColumn = g.getEnd();
		int row = g.getRow();

		restoreCell(row, rightColumn, this.alignMatrix[row * this.length
				+ leftColumn]);
		restoreCell(row, leftColumn, this.alphabet.INDEL());

		g.moveLeft();
	}

	/**
	 * Same as moveLeft and moveRight, but the improvement is not returned (not
	 * even calculated). Used to do the backtracking and restore the cell as
	 * before the movement of the GAP g.
	 * 
	 * @param g
	 * @throws SALSAException
	 */
	public final void goBackToRight(GAP g) throws SALSAException {
		int leftColumn = g.getBegin();
		int rightColumn = g.getEnd() + 1;
		int row = g.getRow();

		restoreCell(row, leftColumn, this.alignMatrix[row * this.length
				+ rightColumn]);
		restoreCell(row, rightColumn, this.alphabet.INDEL());

		g.moveRight();
	}

	/**
	 * It returns the penalty of adding a GOP in the specified row (it depends
	 * on the weight of the row).
	 * 
	 * @param row
	 * @return
	 */
	public final double getGOP(int row) {
		return this.GOP * this.weights[row]
				* (this.weightsSUM - this.weights[row]);
	}

	public final void save(String destinationFileNamePath) throws IOException,
			SALSAException {

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(
				destinationFileNamePath))) {

			// Write FASTA file
			for (int r = 0; r < this.numberOfSequences; r++) {
				// Add header indicator
				bw.write(">");
				bw.write(this.properties[r]);
				bw.newLine();

				for (int c = 0; c < this.length; c++) {
					// align
					bw.write(alphabet.intToChar(this.alignMatrix[r * this.length + c]));
					if (((c + 1) % 60) == 0) {
						// add new line every 60 characters
						bw.newLine();
					}
				}

				bw.newLine();
				bw.flush();
			}
		}
	}

	// PRIVATE METHODS
	/**
	 * Given the file containing the guide tree, it generates the sequences'
	 * weights
	 * 
	 * @param fileName
	 * @throws SALSAException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void createWeights(String fileName) throws SALSAException,
			FileNotFoundException, IOException {
		Tree t = new Tree(fileName, this.numberOfSequences);

		t.changeRoot();

		this.weights = new double[this.numberOfSequences];
		this.weightsSUM = t.generateWeights(this.properties, this.weights);
	}

	/**
	 * It reads sequences list (as a strings' vector) from a FASTA file
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	private ArrayList<String> readInputSequences(String filePath)
			throws IOException {
		FastaFileReader fastaReader = new FastaFileReader(filePath);

		this.numberOfSequences = fastaReader.getSequences().size();
		this.properties = fastaReader.getSequencesHeaders().toArray(new String[fastaReader.getSequencesHeaders()
				.size()]);
		this.length = fastaReader.getSequences().get(0).length();

		return fastaReader.getSequences();
	}

	/**
	 * Converts the character alignment in an integer alignment for efficiency
	 * reasons
	 * 
	 * @param seq
	 * @throws SALSAException
	 */
	private void preprocessing(ArrayList<String> seq)
			throws SALSAException {
		// If it is NULL, there are no GAPs opened
		GAP g = null;
		GAP previous;

		String currentSequence;
		int[] convertedSequence;

		for (int row = 0; row < this.numberOfSequences; row++) {
			previous = null;
			currentSequence = seq.get(row);
			convertedSequence = convert(currentSequence);

			for (int column = 0; column < this.length; column++) {
				this.alignMatrix[row * this.length + column] = convertedSequence[column];
				if (convertedSequence[column] == this.alphabet.INDEL()) {
					if (g == null) {
						g = new GAP(row, column, this.length, previous, null);
						if (previous != null) {
							previous.setNext(g);
						}
					} else
						g.extend();
				} else if (g != null) {
					// A GAP has been found and it is finished
					this.GAPS.add(g);
					previous = g;
					g = null;
				}
			}
			if (g != null) {
				// A GAP has been found and it is finished
				this.GAPS.add(g);
				g = null;
			}
		}
	}

	/**
	 * Used by pre-processing
	 * 
	 * @param s
	 * @return
	 * @throws SALSAException
	 */
	private int[] convert(String s) throws SALSAException {
		int[] sequenceOfNumbers = new int[this.length];
		
		// Avoid case errors
		String upperInput = s.toUpperCase(); 
		for (int c = 0; c < this.length; c++) {
			sequenceOfNumbers[c] = this.alphabet.charToInt(upperInput.charAt(c));
		}

		return sequenceOfNumbers;
	}

	private void createCounters() {
		this.countersMatrix = new double[(this.alphabet.dimension() + 1)
				* this.length];

		// Java already initialize at 0f :
		// http://docs.oracle.com/javase/specs/jls/se7/html/jls-4.html#jls-4.12.5
		// Initialize at 0
		int character;
		for (int column = 0; column < length; column++) {
			for (int row = 0; row < numberOfSequences; row++) {
				// align method
				character = this.alignMatrix[row * this.length + column];
				// counters method
				this.countersMatrix[character * this.length + column] += weights[row];
			}
		}
	}

	/**
	 * Score of two sequences in the specified rows (used by WSP). It requires
	 * also the number of GAPS inside the rows
	 * 
	 * @param row1
	 * @param row2
	 * @param numberOfGAPSr1
	 * @param numberOfGAPSr2
	 * @return
	 */
	private double pairwise(int row1, int row2, int numberOfGAPSr1,
			int numberOfGAPSr2) {
		double value = 0;
		int alpha, beta;
		for (int column = 0; column < this.length; column++) {
			alpha = this.alignMatrix[row1 * this.length + column];
			beta = this.alignMatrix[row2 * this.length + column];
			value += this.substitution.score(alpha, beta);
		}

		value -= this.GOP * (numberOfGAPSr1 + numberOfGAPSr2);

		return value;
	}

	/**
	 * It modify the character in position (row, column) and returns the
	 * improvement obtained by changing it.
	 * 
	 * @param row
	 * @param column
	 * @param newCharacter
	 * @return
	 */
	private double changeCell(int row, int column, int newCharacter) {
		// align
		int oldCharacter = this.alignMatrix[row * this.length + column];
		// counters
		this.countersMatrix[oldCharacter * this.length + column] -= weights[row];

		double delta = 0.0f;
		for (int alpha = 0; alpha <= this.alphabet.dimension(); alpha++) {
			delta +=
			// counters
			this.countersMatrix[alpha * this.length + column]
					* (this.substitution.score(newCharacter, alpha) - this.substitution
							.score(oldCharacter, alpha));
		}
		delta *= weights[row];

		// counters
		this.countersMatrix[newCharacter * this.length + column] += weights[row];
		// align
		this.alignMatrix[row * this.length + column] = newCharacter;

		return delta;
	}

	/**
	 * Same as changeCell, but here the improvement is not calculated
	 * 
	 * @param row
	 * @param column
	 * @param newCharacter
	 */
	private void restoreCell(int row, int column, int newCharacter) {
		// align
		int oldCharacter = this.alignMatrix[row * this.length + column];
		// counters
		this.countersMatrix[oldCharacter * this.length + column] -= weights[row];

		// counters
		this.countersMatrix[newCharacter * this.length + column] += weights[row];
		// align
		this.alignMatrix[row * this.length + column] = newCharacter;
	}
}
