package gps.acquisition;
import java.lang.Math;

import java.util.ArrayList;
import java.util.Arrays;
import cgra.pe.PETrigonometry;

public class Acquisition {
	
	float pi = (float) 3.14159265359;

	int N = 400;
	int nSample = 0;
	float[] realSample = new float[N];
	float[] imagSample = new float[N];

	int nCode = 0;	
	float[] realCode = new float[N];
	float[] imagCode = new float[N];
	
	int Dopplerverschiebung;
	int Codeverschiebung;
	
	int sampleFrequenz = 400000;		//fs
	int stepFrequenz = 1000;			//fstep
	int maxFrequenz = 5000;				//fmax
	int minFrequenz = -5000; 			//fmin
	float grenzwert = (float) 0.015;	//gama
	int menge = 11;						//numero de valores de frequencia
	
	float[] realXfd1 = new float[N];
	float[] realXfd2 = new float[N];
	float[] realXfd3 = new float[N];
	float[] realXfd4 = new float[N];
	float[] realXfd5 = new float[N];
	float[] realXfd6 = new float[N];
	float[] realXfd7 = new float[N];
	float[] realXfd8 = new float[N];
	float[] realXfd9 = new float[N];
	float[] realXfd10 = new float[N];
	float[] realXfd11 = new float[N];
	
	float[] imagXfd1 = new float[N];
	float[] imagXfd2 = new float[N];
	float[] imagXfd3 = new float[N];
	float[] imagXfd4 = new float[N];
	float[] imagXfd5 = new float[N];
	float[] imagXfd6 = new float[N];
	float[] imagXfd7 = new float[N];
	float[] imagXfd8 = new float[N];
	float[] imagXfd9 = new float[N];
	float[] imagXfd10 = new float[N];
	float[] imagXfd11 = new float[N];
	
	public boolean enterSample(float real, float imag){
		
		realSample[nSample] = real;
		imagSample[nSample] = imag;
		nSample++;
		
		if(nSample < N - 1) return false;
		else return true;
	}
	
	public boolean enterCode(float real, float imag){
	
		realCode[nCode] = real;
		imagCode[nCode] = imag;
		nCode++;
		
		if(nCode < N - 1) return false;
		else return true;
	}
	
	public int getDopplerverschiebung(){
		return Dopplerverschiebung;
	}
	
	public int getCodeVerschiebung(){
		return Codeverschiebung;
	}
	
	// Acquisition function and its auxiliary functions
	public boolean startAcquisition(){
		float Pin = calculateInputSignalEstimation();
		float maxValue;
		float glrt = 0; // gama maiusculo
		
		
		
		//float[] realDftCode = new float[N];
		//float[] imagDftCode = new float[N];
		
		float[][] realMatrix = new float[menge][N]; // Final matrix not inverted (rows => frequencies; columns => samples)
		float[][] imagMatrix = new float[menge][N]; //
		
		float[][] invMatrix = new float[menge][N]; // Final matrix inverted (rows => frequencies; columns => samples)
		
		for(int n = 0; n < N; n++){
			float cosx = PETrigonometry.cos(-2*pi*(-5000)*n/sampleFrequenz);
			float sinx = PETrigonometry.sin(-2*pi*(-5000)*n/sampleFrequenz);
			
			realXfd1[n] = realSample[n]*cosx - imagSample[n]*sinx;
			imagXfd1[n] = realSample[n]*sinx + imagSample[n]*cosx;
			
			cosx = PETrigonometry.cos(-2*pi*(-4000)*n/sampleFrequenz);
			sinx = PETrigonometry.sin(-2*pi*(-4000)*n/sampleFrequenz);
			
			realXfd2[n] = realSample[n]*cosx - imagSample[n]*sinx;
			imagXfd2[n] = realSample[n]*sinx + imagSample[n]*cosx;
			
			cosx = PETrigonometry.cos(-2*pi*(-3000)*n/sampleFrequenz);
			sinx = PETrigonometry.sin(-2*pi*(-3000)*n/sampleFrequenz);

			realXfd3[n] = realSample[n]*cosx - imagSample[n]*sinx;
			imagXfd3[n] = realSample[n]*sinx + imagSample[n]*cosx;
			
			cosx = PETrigonometry.cos(-2*pi*(-2000)*n/sampleFrequenz);
			sinx = PETrigonometry.sin(-2*pi*(-2000)*n/sampleFrequenz);
			
			realXfd4[n] = realSample[n]*cosx - imagSample[n]*sinx;
			imagXfd4[n] = realSample[n]*sinx + imagSample[n]*cosx;
			
			cosx = PETrigonometry.cos(-2*pi*(-1000)*n/sampleFrequenz);
			sinx = PETrigonometry.sin(-2*pi*(-1000)*n/sampleFrequenz);
			
			realXfd5[n] = realSample[n]*cosx - imagSample[n]*sinx;
			imagXfd5[n] = realSample[n]*sinx + imagSample[n]*cosx;
			
			cosx = PETrigonometry.cos(-2*pi*0*n/sampleFrequenz);
			sinx = PETrigonometry.sin(-2*pi*0*n/sampleFrequenz);
			
			realXfd6[n] = realSample[n]*cosx - imagSample[n]*sinx;
			imagXfd6[n] = realSample[n]*sinx + imagSample[n]*cosx;
			
			cosx = PETrigonometry.cos(-2*pi*1000*n/sampleFrequenz);
			sinx = PETrigonometry.sin(-2*pi*1000*n/sampleFrequenz);
			
			realXfd7[n] = realSample[n]*cosx - imagSample[n]*sinx;
			imagXfd7[n] = realSample[n]*sinx + imagSample[n]*cosx;
			
			cosx = PETrigonometry.cos(-2*pi*2000*n/sampleFrequenz);
			sinx = PETrigonometry.sin(-2*pi*2000*n/sampleFrequenz);
			
			realXfd8[n] = realSample[n]*cosx - imagSample[n]*sinx;
			imagXfd8[n] = realSample[n]*sinx + imagSample[n]*cosx;
			
			cosx = PETrigonometry.cos(-2*pi*3000*n/sampleFrequenz);
			sinx = PETrigonometry.sin(-2*pi*3000*n/sampleFrequenz);
			
			realXfd9[n] = realSample[n]*cosx - imagSample[n]*sinx;
			imagXfd9[n] = realSample[n]*sinx + imagSample[n]*cosx;
			
			cosx = PETrigonometry.cos(-2*pi*4000*n/sampleFrequenz);
			sinx = PETrigonometry.sin(-2*pi*4000*n/sampleFrequenz);
			
			realXfd10[n] = realSample[n]*cosx - imagSample[n]*sinx;
			imagXfd10[n] = realSample[n]*sinx + imagSample[n]*cosx;
			
			cosx = PETrigonometry.cos(-2*pi*5000*n/sampleFrequenz);
			sinx = PETrigonometry.sin(-2*pi*5000*n/sampleFrequenz);
			
			realXfd11[n] = realSample[n]*cosx - imagSample[n]*sinx;
			imagXfd11[n] = realSample[n]*sinx + imagSample[n]*cosx;
		}
		
		calculateDft(realCode, imagCode, realSample, imagSample); // Array DFT(C)
		changeSignalElements(imagSample); // Calculate complex conjugate of elements of DFT(C)
		
		calculateArgumentOfIdft(realSample, imagSample, realMatrix, imagMatrix); // Array DFT(Xfd)*complexConj(DFT(C))
																				 // for each fd
		calculateIdft(realMatrix, imagMatrix, invMatrix);
		
		maxValue = findMaxValue(invMatrix);
		glrt = 2*N*maxValue/Pin;
		
		if(glrt > grenzwert) return true;
		else return false;
	}
	
	private float calculateInputSignalEstimation(){
		int n = 0;
		float Pin = 0;
		
		for(n = 0; n < N; n++){
			Pin += realSample[n]*realSample[n] + imagSample[n]*imagSample[n];
		}
		Pin = Pin/N;
		return Pin;
	}
	
	private void calculateDft(float[] realInput, float[] imagInput, float[] realOutput, float[] imagOutput){
		for(int k = 0; k < N; k++){
			float sumReal = 0;
			float sumImag = 0;
			for(int t = 0; t < N; t++){  // For each input element
				float cosx = PETrigonometry.cos(-2*pi*t*k/N);
				float sinx = PETrigonometry.sin(-2*pi*t*k/N);
				sumReal += realInput[t]*cosx - imagInput[t]*sinx;
				sumImag += realInput[t]*sinx + imagInput[t]*cosx;
			}
			realOutput[k] = sumReal;
			imagOutput[k] = sumImag;
		}
	}
	
	private void changeSignalElements(float[] input){
		for(int i = 0; i < N; i++){
			input[i] = -input[i];
		}
	}
	
	private void calculateArgumentOfIdft(float[] realDftCode, float[] imagDftCode, float[][] realOutput, float[][] imagOutput){
		calculateDft(realXfd1, imagXfd1, realOutput[0], imagOutput[0]);
		for(int n = 0; n < N; n++){
			float realElementXfd = realOutput[0][n];
			float imagElementXfd = imagOutput[0][n];
			
			realOutput[0][n] = realElementXfd*realDftCode[n] - imagElementXfd*imagDftCode[n]; // Array DFT(Xfd)*complexConj(DFT(C))
			imagOutput[0][n] = realElementXfd*imagDftCode[n] + imagElementXfd*realDftCode[n]; // for particular fd
		}
		
		calculateDft(realXfd2, imagXfd2, realOutput[1], imagOutput[1]);
		for(int n = 0; n < N; n++){
			float realElementXfd = realOutput[1][n];
			float imagElementXfd = imagOutput[1][n];
			
			realOutput[1][n] = realElementXfd*realDftCode[n] - imagElementXfd*imagDftCode[n]; // Array DFT(Xfd)*complexConj(DFT(C))
			imagOutput[1][n] = realElementXfd*imagDftCode[n] + imagElementXfd*realDftCode[n]; // for particular fd
		}
		
		calculateDft(realXfd3, imagXfd3, realOutput[2], imagOutput[2]);
		for(int n = 0; n < N; n++){
			float realElementXfd = realOutput[2][n];
			float imagElementXfd = imagOutput[2][n];
			
			realOutput[2][n] = realElementXfd*realDftCode[n] - imagElementXfd*imagDftCode[n]; // Array DFT(Xfd)*complexConj(DFT(C))
			imagOutput[2][n] = realElementXfd*imagDftCode[n] + imagElementXfd*realDftCode[n]; // for particular fd
		}
		
		calculateDft(realXfd4, imagXfd4, realOutput[3], imagOutput[3]);
		for(int n = 0; n < N; n++){
			float realElementXfd = realOutput[3][n];
			float imagElementXfd = imagOutput[3][n];
			
			realOutput[3][n] = realElementXfd*realDftCode[n] - imagElementXfd*imagDftCode[n]; // Array DFT(Xfd)*complexConj(DFT(C))
			imagOutput[3][n] = realElementXfd*imagDftCode[n] + imagElementXfd*realDftCode[n]; // for particular fd
		}
		
		calculateDft(realXfd5, imagXfd5, realOutput[4], imagOutput[4]);
		for(int n = 0; n < N; n++){
			float realElementXfd = realOutput[4][n];
			float imagElementXfd = imagOutput[4][n];
			
			realOutput[4][n] = realElementXfd*realDftCode[n] - imagElementXfd*imagDftCode[n]; // Array DFT(Xfd)*complexConj(DFT(C))
			imagOutput[4][n] = realElementXfd*imagDftCode[n] + imagElementXfd*realDftCode[n]; // for particular fd
		}
		
		calculateDft(realXfd6, imagXfd6, realOutput[5], imagOutput[5]);
		for(int n = 0; n < N; n++){
			float realElementXfd = realOutput[5][n];
			float imagElementXfd = imagOutput[5][n];
			
			realOutput[5][n] = realElementXfd*realDftCode[n] - imagElementXfd*imagDftCode[n]; // Array DFT(Xfd)*complexConj(DFT(C))
			imagOutput[5][n] = realElementXfd*imagDftCode[n] + imagElementXfd*realDftCode[n]; // for particular fd
		}
		
		calculateDft(realXfd7, imagXfd7, realOutput[6], imagOutput[6]);
		for(int n = 0; n < N; n++){
			float realElementXfd = realOutput[6][n];
			float imagElementXfd = imagOutput[6][n];
			
			realOutput[6][n] = realElementXfd*realDftCode[n] - imagElementXfd*imagDftCode[n]; // Array DFT(Xfd)*complexConj(DFT(C))
			imagOutput[6][n] = realElementXfd*imagDftCode[n] + imagElementXfd*realDftCode[n]; // for particular fd
		}
		
		calculateDft(realXfd8, imagXfd8, realOutput[7], imagOutput[7]);
		for(int n = 0; n < N; n++){
			float realElementXfd = realOutput[7][n];
			float imagElementXfd = imagOutput[7][n];
			
			realOutput[7][n] = realElementXfd*realDftCode[n] - imagElementXfd*imagDftCode[n]; // Array DFT(Xfd)*complexConj(DFT(C))
			imagOutput[7][n] = realElementXfd*imagDftCode[n] + imagElementXfd*realDftCode[n]; // for particular fd
		}
		
		calculateDft(realXfd9, imagXfd9, realOutput[8], imagOutput[8]);
		for(int n = 0; n < N; n++){
			float realElementXfd = realOutput[8][n];
			float imagElementXfd = imagOutput[8][n];
			
			realOutput[8][n] = realElementXfd*realDftCode[n] - imagElementXfd*imagDftCode[n]; // Array DFT(Xfd)*complexConj(DFT(C))
			imagOutput[8][n] = realElementXfd*imagDftCode[n] + imagElementXfd*realDftCode[n]; // for particular fd
		}
		
		calculateDft(realXfd10, imagXfd10, realOutput[9], imagOutput[9]);
		for(int n = 0; n < N; n++){
			float realElementXfd = realOutput[9][n];
			float imagElementXfd = imagOutput[9][n];
			
			realOutput[9][n] = realElementXfd*realDftCode[n] - imagElementXfd*imagDftCode[n]; // Array DFT(Xfd)*complexConj(DFT(C))
			imagOutput[9][n] = realElementXfd*imagDftCode[n] + imagElementXfd*realDftCode[n]; // for particular fd
		}
		
		calculateDft(realXfd11, imagXfd11, realOutput[10], imagOutput[10]);
		for(int n = 0; n < N; n++){
			float realElementXfd = realOutput[10][n];
			float imagElementXfd = imagOutput[10][n];
			
			realOutput[10][n] = realElementXfd*realDftCode[n] - imagElementXfd*imagDftCode[n]; // Array DFT(Xfd)*complexConj(DFT(C))
			imagOutput[10][n] = realElementXfd*imagDftCode[n] + imagElementXfd*realDftCode[n]; // for particular fd
		}
	}
	
	private void calculateIdft(float[][] realInput, float[][] imagInput, float[][] matrixOutput){
		for(int k = 0; k < N; k++){ // For each output of IDFT
			float sumReal = 0;
			float sumImag = 0;
			
			for(int t = 0; t < N; t++){ // For each input of IDFT
				float cosx = PETrigonometry.cos(2*pi*k*t/N);
				float sinx = PETrigonometry.sin(2*pi*k*t/N);
				sumReal += realInput[0][t]*cosx - imagInput[0][t]*sinx;
				sumImag += realInput[0][t]*sinx + imagInput[0][t]*cosx;
			}
			sumReal = sumReal/N;
			sumImag = sumImag/N;
			
			matrixOutput[0][k] = sumReal*sumReal + sumImag*sumImag; // |R(f,τ)|ˆ2
		}
		
		for(int k = 0; k < N; k++){ // For each output of IDFT
			float sumReal = 0;
			float sumImag = 0;
			
			for(int t = 0; t < N; t++){ // For each input of IDFT
				float cosx = PETrigonometry.cos(2*pi*k*t/N);
				float sinx = PETrigonometry.sin(2*pi*k*t/N);
				sumReal += realInput[1][t]*cosx - imagInput[1][t]*sinx;
				sumImag += realInput[1][t]*sinx + imagInput[1][t]*cosx;
			}
			sumReal = sumReal/N;
			sumImag = sumImag/N;
			
			matrixOutput[1][k] = sumReal*sumReal + sumImag*sumImag; // |R(f,τ)|ˆ2
		}
		
		for(int k = 0; k < N; k++){ // For each output of IDFT
			float sumReal = 0;
			float sumImag = 0;
			
			for(int t = 0; t < N; t++){ // For each input of IDFT
				float cosx = PETrigonometry.cos(2*pi*k*t/N);
				float sinx = PETrigonometry.sin(2*pi*k*t/N);
				sumReal += realInput[2][t]*cosx - imagInput[2][t]*sinx;
				sumImag += realInput[2][t]*sinx + imagInput[2][t]*cosx;
			}
			sumReal = sumReal/N;
			sumImag = sumImag/N;
			
			matrixOutput[2][k] = sumReal*sumReal + sumImag*sumImag; // |R(f,τ)|ˆ2
		}
		
		for(int k = 0; k < N; k++){ // For each output of IDFT
			float sumReal = 0;
			float sumImag = 0;
			
			for(int t = 0; t < N; t++){ // For each input of IDFT
				float cosx = PETrigonometry.cos(2*pi*k*t/N);
				float sinx = PETrigonometry.sin(2*pi*k*t/N);
				sumReal += realInput[3][t]*cosx - imagInput[3][t]*sinx;
				sumImag += realInput[3][t]*sinx + imagInput[3][t]*cosx;
			}
			sumReal = sumReal/N;
			sumImag = sumImag/N;
			
			matrixOutput[3][k] = sumReal*sumReal + sumImag*sumImag; // |R(f,τ)|ˆ2
		}
		
		for(int k = 0; k < N; k++){ // For each output of IDFT
			float sumReal = 0;
			float sumImag = 0;
			
			for(int t = 0; t < N; t++){ // For each input of IDFT
				float cosx = PETrigonometry.cos(2*pi*k*t/N);
				float sinx = PETrigonometry.sin(2*pi*k*t/N);
				sumReal += realInput[4][t]*cosx - imagInput[4][t]*sinx;
				sumImag += realInput[4][t]*sinx + imagInput[4][t]*cosx;
			}
			sumReal = sumReal/N;
			sumImag = sumImag/N;
			
			matrixOutput[4][k] = sumReal*sumReal + sumImag*sumImag; // |R(f,τ)|ˆ2
		}
		
		for(int k = 0; k < N; k++){ // For each output of IDFT
			float sumReal = 0;
			float sumImag = 0;
			
			for(int t = 0; t < N; t++){ // For each input of IDFT
				float cosx = PETrigonometry.cos(2*pi*k*t/N);
				float sinx = PETrigonometry.sin(2*pi*k*t/N);
				sumReal += realInput[5][t]*cosx - imagInput[5][t]*sinx;
				sumImag += realInput[5][t]*sinx + imagInput[5][t]*cosx;
			}
			sumReal = sumReal/N;
			sumImag = sumImag/N;
			
			matrixOutput[5][k] = sumReal*sumReal + sumImag*sumImag; // |R(f,τ)|ˆ2
		}
		
		for(int k = 0; k < N; k++){ // For each output of IDFT
			float sumReal = 0;
			float sumImag = 0;
			
			for(int t = 0; t < N; t++){ // For each input of IDFT
				float cosx = PETrigonometry.cos(2*pi*k*t/N);
				float sinx = PETrigonometry.sin(2*pi*k*t/N);
				sumReal += realInput[6][t]*cosx - imagInput[6][t]*sinx;
				sumImag += realInput[6][t]*sinx + imagInput[6][t]*cosx;
			}
			sumReal = sumReal/N;
			sumImag = sumImag/N;
			
			matrixOutput[6][k] = sumReal*sumReal + sumImag*sumImag; // |R(f,τ)|ˆ2
		}
		
		for(int k = 0; k < N; k++){ // For each output of IDFT
			float sumReal = 0;
			float sumImag = 0;
			
			for(int t = 0; t < N; t++){ // For each input of IDFT
				float cosx = PETrigonometry.cos(2*pi*k*t/N);
				float sinx = PETrigonometry.sin(2*pi*k*t/N);
				sumReal += realInput[7][t]*cosx - imagInput[7][t]*sinx;
				sumImag += realInput[7][t]*sinx + imagInput[7][t]*cosx;
			}
			sumReal = sumReal/N;
			sumImag = sumImag/N;
			
			matrixOutput[7][k] = sumReal*sumReal + sumImag*sumImag; // |R(f,τ)|ˆ2
		}
		
		for(int k = 0; k < N; k++){ // For each output of IDFT
			float sumReal = 0;
			float sumImag = 0;
			
			for(int t = 0; t < N; t++){ // For each input of IDFT
				float cosx = PETrigonometry.cos(2*pi*k*t/N);
				float sinx = PETrigonometry.sin(2*pi*k*t/N);
				sumReal += realInput[8][t]*cosx - imagInput[8][t]*sinx;
				sumImag += realInput[8][t]*sinx + imagInput[8][t]*cosx;
			}
			sumReal = sumReal/N;
			sumImag = sumImag/N;
			
			matrixOutput[8][k] = sumReal*sumReal + sumImag*sumImag; // |R(f,τ)|ˆ2
		}
		
		for(int k = 0; k < N; k++){ // For each output of IDFT
			float sumReal = 0;
			float sumImag = 0;
			
			for(int t = 0; t < N; t++){ // For each input of IDFT
				float cosx = PETrigonometry.cos(2*pi*k*t/N);
				float sinx = PETrigonometry.sin(2*pi*k*t/N);
				sumReal += realInput[9][t]*cosx - imagInput[9][t]*sinx;
				sumImag += realInput[9][t]*sinx + imagInput[9][t]*cosx;
			}
			sumReal = sumReal/N;
			sumImag = sumImag/N;
			
			matrixOutput[9][k] = sumReal*sumReal + sumImag*sumImag; // |R(f,τ)|ˆ2
		}
		
		for(int k = 0; k < N; k++){ // For each output of IDFT
			float sumReal = 0;
			float sumImag = 0;
			
			for(int t = 0; t < N; t++){ // For each input of IDFT
				float cosx = PETrigonometry.cos(2*pi*k*t/N);
				float sinx = PETrigonometry.sin(2*pi*k*t/N);
				sumReal += realInput[10][t]*cosx - imagInput[10][t]*sinx;
				sumImag += realInput[10][t]*sinx + imagInput[10][t]*cosx;
			}
			sumReal = sumReal/N;
			sumImag = sumImag/N;
			
			matrixOutput[10][k] = sumReal*sumReal + sumImag*sumImag; // |R(f,τ)|ˆ2
		}
	}
	
	private float findMaxValue(float[][] matrix){
		float maxValue = 0;
		int findedRow = 0, findedCol = 0;
		
		for(int i = 0; i < menge; i++){
			for(int j = 0; j < N; j++){
				if(maxValue < matrix[i][j]){
					maxValue = matrix[i][j];
					findedRow = i;
					findedCol = j;
				}
			}
		}
		
		Dopplerverschiebung = (maxFrequenz-minFrequenz)*findedRow/(menge-1) + minFrequenz;
		Codeverschiebung = findedCol;
		return maxValue;
	}

}
