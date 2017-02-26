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
		
		float[][] realXfd = new float[menge][N]; // Matrix Xfd (rows => frequencies; columns => samples)
		float[][] imagXfd = new float[menge][N]; //
		
		//float[] realDftCode = new float[N];
		//float[] imagDftCode = new float[N];
		
		float[][] realMatrix = new float[menge][N]; // Final matrix not inverted (rows => frequencies; columns => samples)
		float[][] imagMatrix = new float[menge][N]; //
		
		//float[][] invMatrix = new float[menge][N]; // Final matrix inverted (rows => frequencies; columns => samples)
		
		calculateXfd(realXfd, imagXfd); // Matrix Xfd
		
		calculateDft(realCode, imagCode, realSample, imagSample); // Array DFT(C)
		changeSignalElements(imagSample); // Calculate complex conjugate of elements of DFT(C)
		
		calculateArgumentOfIdft(realXfd, imagXfd, realSample, imagSample, realMatrix, imagMatrix); // Array DFT(Xfd)*complexConj(DFT(C))
																									 // for each fd
		calculateIdft(realMatrix, imagMatrix, realXfd);
		
		maxValue = findMaxValue(realXfd);
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
	
	private void calculateXfd(float[][] realXfd, float[][] imagXfd){
		int m = 0;
		
		for(int fd = minFrequenz; fd <= maxFrequenz; fd += stepFrequenz){
			for(int n = 0; n < N; n++){
				float cosx = PETrigonometry.cos(-2*pi*fd*n/sampleFrequenz); // e^(-j*2pi*fd*n/fs) = cos(-2pi*fd*n/fs) + jsen(-2pi*fd*n*fs)
				float sinx = PETrigonometry.sin(-2*pi*fd*n/sampleFrequenz); //
				
				realXfd[m][n] = realSample[n]*cosx - imagSample[n]*sinx;
				imagXfd[m][n] = realSample[n]*sinx + imagSample[n]*cosx;
			}
			m++;
		}
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
	
	private void calculateArgumentOfIdft(float[][] realXfd, float[][] imagXfd, float[] realDftCode, 
										 float[] imagDftCode, float[][] realOutput, float[][] imagOutput){
		
		for(int m = 0; m < menge; m++){ // For each fd
			calculateDft(realXfd[m], imagXfd[m], realOutput[m], imagOutput[m]);  // Array DFT(Xfd) for particular fd
				
			for(int n = 0; n < N; n++){
				float realElementXfd = realOutput[m][n];
				float imagElementXfd = imagOutput[m][n];
				
				realOutput[m][n] = realElementXfd*realDftCode[n] - imagElementXfd*imagDftCode[n]; // Array DFT(Xfd)*complexConj(DFT(C))
				imagOutput[m][n] = realElementXfd*imagDftCode[n] + imagElementXfd*realDftCode[n]; // for particular fd
			}
		}
	}
	
	private void calculateIdft(float[][] realInput, float[][] imagInput, float[][] matrixOutput){
		
		for(int m = 0; m < menge; m++){ // For each fd
			
			for(int k = 0; k < N; k++){ // For each output of IDFT
				float sumReal = 0;
				float sumImag = 0;
				
				for(int t = 0; t < N; t++){ // For each input of IDFT
					float cosx = PETrigonometry.cos(2*pi*k*t/N);
					float sinx = PETrigonometry.sin(2*pi*k*t/N);
					sumReal += realInput[m][t]*cosx - imagInput[m][t]*sinx;
					sumImag += realInput[m][t]*sinx + imagInput[m][t]*cosx;
				}
				sumReal = sumReal/N;
				sumImag = sumImag/N;
				
				matrixOutput[m][k] = sumReal*sumReal + sumImag*sumImag; // |R(f,τ)|ˆ2
			}
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
