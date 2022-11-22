package net.luis.utils.math;

/**
 *
 * @author Luis-st
 *
 */

public enum MathOperation {
	
	ADDITION() {
		@Override
		public byte calculate(byte first, byte second) {
			return (byte) (first + second);
		}
		
		@Override
		public short calculate(short first, short second) {
			return (short) (first + second);
		}
		
		@Override
		public int calculate(int first, int second) {
			return first + second;
		}
		
		@Override
		public long calculate(long first, long second) {
			return first + second;
		}
		
		@Override
		public float calculate(float first, float second) {
			return first + second;
		}
		
		@Override
		public double calculate(double first, double second) {
			return first + second;
		}
	},
	SUBTRACTION() {
		@Override
		public byte calculate(byte first, byte second) {
			return (byte) (first - second);
		}
		
		@Override
		public short calculate(short first, short second) {
			return (short) (first - second);
		}
		
		@Override
		public int calculate(int first, int second) {
			return first - second;
		}
		
		@Override
		public long calculate(long first, long second) {
			return first - second;
		}
		
		@Override
		public float calculate(float first, float second) {
			return first - second;
		}
		
		@Override
		public double calculate(double first, double second) {
			return first - second;
		}
	},
	MULTIPLICATION() {
		@Override
		public byte calculate(byte first, byte second) {
			return (byte) (first * second);
		}
		
		@Override
		public short calculate(short first, short second) {
			return (short) (first * second);
		}
		
		@Override
		public int calculate(int first, int second) {
			return first * second;
		}
		
		@Override
		public long calculate(long first, long second) {
			return first * second;
		}
		
		@Override
		public float calculate(float first, float second) {
			return first * second;
		}
		
		@Override
		public double calculate(double first, double second) {
			return first * second;
		}
	},
	DIVISION() {
		@Override
		public byte calculate(byte first, byte second) {
			return (byte) (first / second);
		}
		
		@Override
		public short calculate(short first, short second) {
			return (short) (first / second);
		}
		
		@Override
		public int calculate(int first, int second) {
			return first / second;
		}
		
		@Override
		public long calculate(long first, long second) {
			return first / second;
		}
		
		@Override
		public float calculate(float first, float second) {
			return first / second;
		}
		
		@Override
		public double calculate(double first, double second) {
			return first / second;
		}
	},
	MODULUS() {
		@Override
		public byte calculate(byte first, byte second) {
			return (byte) (first % second);
		}
		
		@Override
		public short calculate(short first, short second) {
			return (short) (first % second);
		}
		
		@Override
		public int calculate(int first, int second) {
			return first % second;
		}
		
		@Override
		public long calculate(long first, long second) {
			return first % second;
		}
		
		@Override
		public float calculate(float first, float second) {
			return first % second;
		}
		
		@Override
		public double calculate(double first, double second) {
			return first % second;
		}
	};
	
	public abstract byte calculate(byte first, byte second);
	
	public abstract short calculate(short first, short second);
	
	public abstract int calculate(int first, int second);
	
	public abstract long calculate(long first, long second);
	
	public abstract float calculate(float first, float second);
	
	public abstract double calculate(double first, double second);
	
}
