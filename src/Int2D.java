public class Int2D {
	public static final Int2D UP = new Int2D(0, -1);
	public static final Int2D DOWN = new Int2D(0, 1);
	public static final Int2D LEFT = new Int2D(-1, 0);
	public static final Int2D RIGHT = new Int2D(1, 0);

	public static final Int2D UP_LEFT = UP.add(LEFT);
	public static final Int2D UP_RIGHT = UP.add(RIGHT);
	public static final Int2D DOWN_LEFT = DOWN.add(LEFT);
	public static final Int2D DOWN_RIGHT = DOWN.add(RIGHT);

	public int x;
	public int y;

	public Int2D(int x, int y) {
		this.x = x;
		this.y = y;
	}

	// return a new Int2D that is this Int2D added to another Int2D
	public Int2D add(Int2D other) {
		return new Int2D(
						this.x + other.x,
						this.y + other.y
		);
	}

	// return a new Int2D that is this Int2D multiplied by a scalar
	public Int2D multiply(int scalar) {
		return new Int2D(
						this.x * scalar,
						this.y * scalar
		);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Int2D) {
			Int2D ptr = (Int2D)other;
			return this.x == ptr.x && this.y == ptr.y;
		}

		return false;
	}

	// chatgpt
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + x;
		result = 31 * result + y;
		return result;
	}
	public String toString() {
		return "Int2D(x=" + x + ", y=" + y + ")";
	}
}